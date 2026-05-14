-- =====================================================
-- 200万级模拟数据生成脚本 (PostgreSQL 专用)
-- 预期耗时：30-60秒 (取决于磁盘IO)
-- =====================================================

-- 确保测试标签存在
INSERT INTO label_library (label_name, color, sort_order, description)
SELECT '重要', '#EF4444', 1, '标记为重要的文件' WHERE NOT EXISTS (SELECT 1 FROM label_library WHERE label_name = '重要');
INSERT INTO label_library (label_name, color, sort_order, description)
SELECT '待处理', '#F59E0B', 2, '需要处理的工作文件' WHERE NOT EXISTS (SELECT 1 FROM label_library WHERE label_name = '待处理');
INSERT INTO label_library (label_name, color, sort_order, description)
SELECT '已完成', '#10B981', 3, '已经完成的工作' WHERE NOT EXISTS (SELECT 1 FROM label_library WHERE label_name = '已完成');
INSERT INTO label_library (label_name, color, sort_order, description)
SELECT '归档', '#9CA3AF', 4, '过期的归档文件' WHERE NOT EXISTS (SELECT 1 FROM label_library WHERE label_name = '归档');
INSERT INTO label_library (label_name, color, sort_order, description)
SELECT '保密', '#8B5CF6', 5, '核心商业机密' WHERE NOT EXISTS (SELECT 1 FROM label_library WHERE label_name = '保密');

-- 1. 生成 2,000,000 条文件元数据
-- 注意：ID 使用 generate_series 以保证唯一性
INSERT INTO file_meta (id, file_name, file_path, file_size, file_type, tags, metadata)
SELECT 
    gs, 
    'mock_file_' || gs || '.dat',
    '/data/storage/' || (gs % 100) || '/' || (gs % 1000) || '/file_' || gs || '.dat',
    (random() * 104857600)::bigint, -- 0-100MB
    CASE (gs % 5) 
        WHEN 0 THEN 'pdf' WHEN 1 THEN 'docx' WHEN 2 THEN 'xlsx' WHEN 3 THEN 'mp4' ELSE 'jpg' 
    END,
    '[]'::jsonb, -- 初始标签为空，稍后更新
    jsonb_build_object(
        'dept', CASE (gs % 4) WHEN 0 THEN 'IT' WHEN 1 THEN 'HR' WHEN 2 THEN 'FIN' ELSE 'DEV' END,
        'creator', 'user_' || (gs % 200),
        'version', (random() * 10)::int || '.' || (random() * 10)::int
    )
FROM generate_series(1, 2000000) AS gs;

-- 2. 随机生成文件-标签关联 (每个文件 1-2 个标签)
-- 为演示性能优化，我们批量插入
INSERT INTO file_tag_relation (file_id, label_id)
SELECT 
    gs.id,
    l.id
FROM (SELECT generate_series(1, 2000000) as id) gs
CROSS JOIN LATERAL (
    SELECT id FROM label_library ORDER BY random() LIMIT (1 + (gs.id % 2))
) l
ON CONFLICT DO NOTHING;

-- 3. 反向同步冗余字段 tags (JSONB)
-- 这一步比较耗时，但在大数据查询(非关联查询)时非常有用
-- 我们只为前 100,000 条数据更新冗余字段，以节省脚本执行时间
-- 或者直接全量更新
UPDATE file_meta fm
SET tags = (
    SELECT jsonb_agg(ll.label_name)
    FROM file_tag_relation ftr
    JOIN label_library ll ON ftr.label_id = ll.id
    WHERE ftr.file_id = fm.id
)
WHERE fm.id <= 500000; -- 脚本阶段先更新 50 万条作为演示

-- 4. 重置统计计数 (仅需一次)
UPDATE label_library ll
SET tag_count = (
    SELECT COUNT(*) FROM file_tag_relation ftr
    WHERE ftr.label_id = ll.id
);

-- 5. 打印统计信息确认
SELECT COUNT(*) as total_files FROM file_meta;
SELECT label_name, tag_count FROM label_library;
