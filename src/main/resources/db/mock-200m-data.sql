-- =========================================================================
-- PostgreSQL 2亿真实元数据压测造数脚本 (Partition-Aware + Jsonb + Trigger-Safe)
-- =========================================================================

-- 0. 【补丁自愈】：强制补齐丢失的物理关系分区表及触发器，确保后续入库万无一失
DO $$
DECLARE
    i INT;
BEGIN
    FOR i IN 0..63 LOOP
        EXECUTE 'CREATE TABLE IF NOT EXISTS file_tag_relation_' || i || ' PARTITION OF file_tag_relation FOR VALUES WITH (MODULUS 64, REMAINDER ' || i || ')';
        EXECUTE 'CREATE TABLE IF NOT EXISTS file_dataset_relation_' || i || ' PARTITION OF file_dataset_relation FOR VALUES WITH (MODULUS 64, REMAINDER ' || i || ')';
        EXECUTE 'CREATE OR REPLACE FUNCTION fn_update_tag_count() RETURNS TRIGGER AS $func$ BEGIN IF (TG_OP = ''INSERT'') THEN UPDATE label_library SET tag_count = tag_count + 1 WHERE id = NEW.label_id; RETURN NEW; ELSIF (TG_OP = ''DELETE'') THEN UPDATE label_library SET tag_count = tag_count - 1 WHERE id = OLD.label_id; RETURN OLD; END IF; RETURN NULL; END; $func$ LANGUAGE plpgsql;';
        -- 注意：由于 CREATE TRIGGER 没有 IF NOT EXISTS，所以我们进行异常捕获静默处理
        BEGIN
            EXECUTE 'CREATE TRIGGER trg_label_count_' || i || ' AFTER INSERT OR DELETE ON file_tag_relation_' || i || ' FOR EACH ROW EXECUTE FUNCTION fn_update_tag_count()';
        EXCEPTION WHEN duplicate_object THEN
            NULL; -- 已存在则忽略
        END;
    END LOOP;
END $$;
-- 1. 创建高速造数存储过程 (通过多次 COMMIT 避免 WAL 撑爆和 OOM)
CREATE OR REPLACE PROCEDURE generate_200m_metadata(batch_size INT DEFAULT 100000, total_batches INT DEFAULT 2000)
LANGUAGE plpgsql
AS $$
DECLARE
    i INT;
    start_id BIGINT;
BEGIN
    FOR i IN 1..total_batches LOOP
        start_id := (i - 1) * batch_size + 1;
        
        -- A. 插入文件元数据 (包含2~3个随机标签，和自定义属性，以及打散到过去 365 天的分区时间)
        INSERT INTO file_meta (id, file_name, file_path, file_size, file_type, tags, metadata, created_at)
        SELECT 
            seq_id,
            'doc_' || seq_id || '_' || substr(md5(random()::text), 1, 8) || '.dat',
            '/data/store/vol' || (seq_id % 10) || '/file_' || seq_id,
            (random() * 1024 * 1024 * 100)::bigint, -- 0-100MB 随机
            CASE WHEN seq_id % 3 = 0 THEN 'pdf' WHEN seq_id % 3 = 1 THEN 'docx' ELSE 'mp4' END,
            -- 按照设定：随机2到3个标签 (基础库默认有 ID: 1, 2, 3)
            CASE WHEN seq_id % 2 = 0 THEN '[1, 2]'::jsonb ELSE '[1, 2, 3]'::jsonb END,
            -- 按照设定：填充对应数据集的自定义扩展属性 (JSONB)
            CASE 
                -- 对应 dataset_id = 1 (邮件)
                WHEN seq_id % 3 = 0 THEN ('{"sender": "user_' || (seq_id % 100) || '", "is_enc": true, "receiver": "org_' || (seq_id % 10) || '"}')::jsonb
                -- 对应 dataset_id = 2 (传媒)
                WHEN seq_id % 3 = 1 THEN ('{"recorder": "cam_' || (seq_id % 50) || '", "duration": ' || (random() * 3600)::int || ', "device": "iPhone15"}')::jsonb
                -- 对应 dataset_id = 3 (普通)
                ELSE '{"dept": "HR", "confidential": false}'::jsonb
            END,
            -- 均匀打散时间到 2025 ~ 2027 这 3 年内 (约1095天)，促使 2 亿数据完美覆盖所有物理分区表
            timestamp '2025-01-01 00:00:00' + (random() * 1095) * interval '1 day'
        FROM generate_series(start_id, start_id + batch_size - 1) AS seq_id
        ON CONFLICT (id, created_at) DO NOTHING;

        -- B. 插入标签关联表 (为了触发器或后续分析，需要物理化)
        INSERT INTO file_tag_relation (file_id, label_id)
        SELECT seq_id, 1 FROM generate_series(start_id, start_id + batch_size - 1) AS seq_id
        UNION ALL
        SELECT seq_id, 2 FROM generate_series(start_id, start_id + batch_size - 1) AS seq_id
        UNION ALL
        SELECT seq_id, 3 FROM generate_series(start_id, start_id + batch_size - 1) AS seq_id WHERE seq_id % 2 = 1
        ON CONFLICT (file_id, label_id) DO NOTHING;

        -- C. 插入数据集关联表 (将文件均匀划归至 1, 2, 3 号数据集, 对应上面 metadata 的格式)
        INSERT INTO file_dataset_relation (file_id, dataset_id)
        SELECT 
            seq_id, 
            (seq_id % 3) + 1
        FROM generate_series(start_id, start_id + batch_size - 1) AS seq_id
        ON CONFLICT (file_id, dataset_id) DO NOTHING;

        -- 提交事务，释放内存和行锁
        COMMIT;
        
        RAISE NOTICE '已完成第 % 批数据注入, 每批写入 % 条物理记录', i, batch_size;
    END LOOP;
END;
$$;

-- 2. 预检：临时禁用所有分区触发器防锁死 (非常关键：2亿级高并发插表，如果频繁触发更新 label_library，会导致死锁和性能崩溃)
DO $$
DECLARE
    i INT;
BEGIN
    FOR i IN 0..63 LOOP
        EXECUTE 'ALTER TABLE file_tag_relation_' || i || ' DISABLE TRIGGER ALL';
    END LOOP;
END $$;


-- 3. 发起调用造数据 (2000批次 * 10万条 = 2亿条)
-- 注意‼️ 本机测试建议先只跑 100万 条：调用参数改为 (100000, 10) 即可。若要完整 2 亿，请解开下句注释执行。

CALL generate_200m_metadata(100000, 1374);  -- 执行完整的 2 亿数据注入 (建议在服务器中通过 nohup psql 运行)
-- CALL generate_200m_metadata(100000, 10);       -- 仅仅生成 100 万条用于快速功能测试


-- 4. 收尾：数据造完后恢复触发器
DO $$
DECLARE
    i INT;
BEGIN
    FOR i IN 0..63 LOOP
        EXECUTE 'ALTER TABLE file_tag_relation_' || i || ' ENABLE TRIGGER ALL';
    END LOOP;
END $$;


-- 5. 收尾：重新计算全量标签计数
UPDATE label_library l 
SET tag_count = (SELECT COUNT(*) FROM file_tag_relation f WHERE f.label_id = l.id);

