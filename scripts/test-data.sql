-- =====================================
-- 测试数据导入脚本
-- 用于快速填充测试数据
-- =====================================

\c label_code;

-- 插入测试标签
INSERT INTO label_library (label_name, color, sort_order, description, tag_count) VALUES
    ('高优先级', '#EF4444', 1, '紧急且重要的任务', 0),
    ('中优先级', '#F59E0B', 2, '需要注意的任务', 0),
    ('低优先级', '#10B981', 3, '可以稍后处理', 0),
    ('待审核', '#3B82F6', 4, '需要审核的工作', 0),
    '草稿', '#6B7280', 5, '草稿文件', 0),
    '已完成', '#10B981', 6, '已经完成', 0),
    '已发布', '#8B5CF6', 7, '已发布到生产环境', 0),
    '归档', '#9CA3AF', 8, '归档的文件', 0);

-- 插入测试文件标签关联（示例数据）
INSERT INTO file_tag_relation (file_id, label_id) VALUES
    (1, 1),  -- 高优先级
    (1, 2),  -- 中优先级
    (2, 3),  -- 低优先级
    (2, 4),  -- 待审核
    (3, 5),  -- 草稿
    (4, 6),  -- 已完成
    (5, 7),  -- 已发布
    (6, 8);  -- 归档

-- 更新标签统计
UPDATE label_library ll
SET tag_count = (
    SELECT COUNT(*) FROM file_tag_relation ftr
    WHERE ftr.label_id = ll.id
);

-- 查看测试数据
SELECT ll.label_name, ll.color, ll.tag_count, ftr.file_id
FROM label_library ll
LEFT JOIN file_tag_relation ftr ON ll.id = ftr.label_id
ORDER BY ll.sort_order;

-- 验证数据
SELECT
    COUNT(*) as total_labels,
    SUM(tag_count) as total_relations,
    COUNT(DISTINCT ftr.file_id) as unique_files
FROM label_library ll
LEFT JOIN file_tag_relation ftr ON ll.id = ftr.label_id;
