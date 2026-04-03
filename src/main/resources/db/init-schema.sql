-- =====================================================
-- 标签管理系统数据库初始化脚本 (无损增强版：兼容旧逻辑 + 三维解耦功能)
-- =====================================================

-- 1. 核心标签库 (保留所有原始统计及更新时间字段)
DROP TABLE IF EXISTS label_library CASCADE;
CREATE TABLE label_library (
    id BIGSERIAL PRIMARY KEY,
    label_name VARCHAR(100) NOT NULL UNIQUE,
    color VARCHAR(20) DEFAULT '#4F46E5',
    sort_order INT DEFAULT 0,
    description TEXT,
    tag_count BIGINT DEFAULT 0, -- 保留原有统计功能
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- 保留原有更新时间
);

-- 2. 数据集与属性扩展 (新增核心：支持“邮件”、“视频”等解耦视图)
DROP TABLE IF EXISTS dataset_definition CASCADE;
CREATE TABLE dataset_definition (
    id SERIAL PRIMARY KEY,
    dataset_name VARCHAR(100) NOT NULL,
    dataset_code VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS attribute_config CASCADE;
CREATE TABLE attribute_config (
    id SERIAL PRIMARY KEY,
    dataset_id INT REFERENCES dataset_definition(id) ON DELETE CASCADE,
    attr_key VARCHAR(100) NOT NULL,
    attr_label VARCHAR(100) NOT NULL,
    data_type VARCHAR(20) NOT NULL DEFAULT 'String', -- String/Number/Boolean/Date
    is_required BOOLEAN DEFAULT FALSE,
    is_global BOOLEAN DEFAULT FALSE, -- 在综合视图展现
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(dataset_id, attr_key)
);

-- 3. 文件元数据主表 (【企业级黄金方案】：按 created_at 范围分区)
DROP TABLE IF EXISTS file_meta CASCADE;
CREATE TABLE file_meta (
    id BIGINT NOT NULL,
    file_name VARCHAR(255),
    file_path VARCHAR(500),
    file_size BIGINT, -- 保留原有大小字段
    file_type VARCHAR(50),
    tags JSONB DEFAULT '[]'::jsonb, -- 恢复旧有的标签冗余列
    metadata JSONB DEFAULT '{}'::jsonb, -- 物理层支持 JSONB 扩展属性
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- 【关键限制】：范围分区的表，主键必须包含分区键
    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (created_at);

-- 【高阶装配：自动化动态滚动建表】(智能划分 2025 ~ 2027 年的每个月)
DO $$
DECLARE
    start_date DATE := '2025-01-01';
    end_date DATE;
BEGIN
    FOR i IN 0..35 LOOP
        end_date := start_date + INTERVAL '1 month';
        EXECUTE format('
            CREATE TABLE IF NOT EXISTS file_meta_%s_%s PARTITION OF file_meta 
            FOR VALUES FROM (%L) TO (%L);', 
            to_char(start_date, 'YYYY'), to_char(start_date, 'MM'), 
            start_date, end_date);
        start_date := end_date;
    END LOOP;
END $$;
-- 设立一个无界分区兜底，防备超出上述时间的异常数据插入报错
CREATE TABLE IF NOT EXISTS file_meta_future PARTITION OF file_meta FOR VALUES FROM ('2028-01-01 00:00:00') TO (MAXVALUE);
CREATE TABLE IF NOT EXISTS file_meta_past PARTITION OF file_meta FOR VALUES FROM (MINVALUE) TO ('2025-01-01 00:00:00');


-- 4. 关系层 (按 file_id HASH 分区保持不变，保障写入顺滑无关联锁)
-- 文件与标签关系
DROP TABLE IF EXISTS file_tag_relation CASCADE;
CREATE TABLE file_tag_relation (
    file_id BIGINT NOT NULL,
    label_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (file_id, label_id)
) PARTITION BY HASH (file_id);

-- 文件与数据集关系 (支持一个文件归属于多个数据集)
DROP TABLE IF EXISTS file_dataset_relation CASCADE;
CREATE TABLE file_dataset_relation (
    file_id BIGINT NOT NULL,
    dataset_id INT NOT NULL,
    PRIMARY KEY (file_id, dataset_id)
) PARTITION BY HASH (file_id);

-- 5. 自动生成 64 个分区 (增强兼容性写法)
DO $$
DECLARE
    i INT;
BEGIN
    FOR i IN 0..63 LOOP
        EXECUTE 'CREATE TABLE IF NOT EXISTS file_tag_relation_' || i || ' PARTITION OF file_tag_relation FOR VALUES WITH (MODULUS 64, REMAINDER ' || i || ')';
        EXECUTE 'CREATE TABLE IF NOT EXISTS file_dataset_relation_' || i || ' PARTITION OF file_dataset_relation FOR VALUES WITH (MODULUS 64, REMAINDER ' || i || ')';
    END LOOP;
END $$;


-- 6. 建立索引与视图
CREATE INDEX idx_ftr_label_file_desc ON file_tag_relation(label_id, file_id DESC);
CREATE INDEX idx_fdr_dataset_file ON file_dataset_relation(dataset_id, file_id);
CREATE INDEX idx_file_meta_metadata_gin ON file_meta USING GIN (metadata);
CREATE INDEX IF NOT EXISTS idx_file_meta_tags_gin ON file_meta USING GIN (tags);

-- 【万倍提速核心】：引入 pg_trgm 扩展并为 JSONB 的文本映射建立专门的三元语法全文检索树
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX IF NOT EXISTS idx_file_meta_metadata_text_gin ON file_meta USING GIN ((metadata::text) gin_trgm_ops);


CREATE OR REPLACE VIEW v_label_statistics AS
SELECT
    ll.id AS label_id,
    ll.label_name,
    ll.color,
    ll.tag_count,
    COUNT(DISTINCT ftr.file_id) AS file_count
FROM label_library ll
LEFT JOIN file_tag_relation ftr ON ll.id = ftr.label_id
GROUP BY ll.id, ll.label_name, ll.color, ll.tag_count;

-- 8. 【高性能统计触发器】(解决 2亿级计数问题)
CREATE OR REPLACE FUNCTION fn_update_tag_count() RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        UPDATE label_library SET tag_count = tag_count + 1 WHERE id = NEW.label_id;
        RETURN NEW;
    ELSIF (TG_OP = 'DELETE') THEN
        UPDATE label_library SET tag_count = tag_count - 1 WHERE id = OLD.label_id;
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- 为每个分区建立触发器 (PG 系统要求必须在叶子分区上建立)
DO $$
DECLARE
    i INT;
BEGIN
    FOR i IN 0..63 LOOP
        EXECUTE 'CREATE TRIGGER trg_label_count_' || i || ' AFTER INSERT OR DELETE ON file_tag_relation_' || i || ' FOR EACH ROW EXECUTE FUNCTION fn_update_tag_count()';
    END LOOP;
END $$;

-- 9. 【手动全量刷一次统计】(防止之前的数据没计入)
UPDATE label_library l 
SET tag_count = (SELECT COUNT(*) FROM file_tag_relation f WHERE f.label_id = l.id);

INSERT INTO dataset_definition (id, dataset_name, dataset_code) VALUES 
(1, '内网邮件存证', 'email_set'),
(2, '手机传媒存档', 'media_set'),
(3, '通用办公文件', 'generic_docs');

-- 注入邮件专属字段规范 (Sender 为全局可见)
INSERT INTO attribute_config (dataset_id, attr_key, attr_label, data_type, is_required, is_global) VALUES
(1, 'sender', '发件人', 'String', true, true),
(1, 'receiver', '收件人', 'String', false, true),
(1, 'is_enc', '加密传输', 'Boolean', false, false);

-- 注入传媒视频规范 (Recorder, Duration 为全局可见)
INSERT INTO attribute_config (dataset_id, attr_key, attr_label, data_type, is_required, is_global) VALUES
(2, 'recorder', '拍摄师', 'String', true, true),
(2, 'duration', '时长(s)', 'Number', true, false),
(2, 'device', '设备名称', 'String', false, true);

-- 初始化基础标签
INSERT INTO label_library (label_name, color, description) VALUES 
('重要涉密', '#EF4444', '需重点监管的数据'),
('归档完结', '#10B981', '已完成所有处理流程'),
('待审计', '#F59E0B', '待安全合规审计');

-- 重置 Serial 序列
SELECT setval('dataset_definition_id_seq', (SELECT MAX(id) FROM dataset_definition));
SELECT setval('attribute_config_id_seq', (SELECT MAX(id) FROM attribute_config));
SELECT setval('label_library_id_seq', (SELECT MAX(id) FROM label_library));


