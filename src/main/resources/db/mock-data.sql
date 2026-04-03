-- =====================================================
-- 2亿级数据集及动态属性功能验证数据脚本
-- =====================================================

-- 1. 清空旧的历史测试数据 (可选)
TRUNCATE file_meta CASCADE;
TRUNCATE file_tag_relation CASCADE;
TRUNCATE file_dataset_relation CASCADE;

-- 2. 插入文件元数据 (file_meta)
-- ID 采用大整数，模拟均衡分布
INSERT INTO file_meta (id, file_name, file_type, file_path, file_size, tags, metadata) VALUES 
(1001, '内部调薪方案_0402.eml', 'email', '/storage/emails/2026/04/02/001.eml', 15240, '[1]', '{"sender": "HR-Alice", "receiver": "Boss-Bob", "subject": "2026调薪方案", "is_enc": true}'),
(1002, '机房监控录像_20260402.mp4', 'video', '/storage/media/cctv/cam01_20260402.mp4', 850400, '[3]', '{"recorder": "SYS_ADMIN", "duration": 3600, "device": "海康威视-V2"}'),
(1003, '财务第一季度报表.pdf', 'document', '/storage/docs/finance/Q1_report.pdf', 24500, '[2]', '{"dept": "财务部"}'),
(1004, '紧急会议录屏_项目A.mp4', 'video', '/storage/media/recordings/meeting_A.mp4', 125000, '[1]', '{"recorder": "JohnDoe", "duration": 1800, "device": "Zoom-Record"}');

-- 3. 建立文件与数据集的关联 (物理层：解决 404 检索库为空的问题)
-- 1: 内网邮件存证, 2: 手机传媒存档, 3: 通用办公文件
INSERT INTO file_dataset_relation (file_id, dataset_id) VALUES 
(1001, 1), -- 邮件归属数据集 1
(1002, 2), -- 视频归属数据集 2
(1003, 3), -- 文档归属数据集 3
(1004, 2); -- 视频2归属数据集 2

-- 4. 建立文件与标签的关联 (物理层：支持 EXISTS 高性能检索)
-- 1: 重要涉密, 2: 归档完结, 3: 待审计
INSERT INTO file_tag_relation (file_id, label_id) VALUES 
(1001, 1), -- 调薪方案 -> 重要涉密
(1002, 3), -- 监控录像 -> 待审计
(1003, 2), -- 财务报表 -> 归档完结
(1004, 1); -- 会议录屏 -> 重要涉密

-- =====================================================
-- 数据验证指南：
-- 1. 点击左侧“全部文件”：应看到 4 条记录。
-- 2. 点击左侧“内网邮件存证”：应只看到 1 条记录，且表头自动出现“发件人”、“收件人”。
-- 3. 点击左侧“手机传媒存档”：应看到 2 条记录，且表头自动出现“拍摄师”、“时长(s)”。
-- =====================================================
