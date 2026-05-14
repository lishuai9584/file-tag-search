# 变更日志

本项目遵循[语义化版本](https://semver.org/lang/zh-CN/)规范。

## [Unreleased]

### Added
- 初始版本发布
- 标签管理功能（创建、查询、更新、删除）
- 文件标签关联功能
- 多标签交集查询
- 标签合并功能
- 标签统计功能
- PostgreSQL JSONB + GIN索引优化
- 自动统计更新触发器
- RESTful API接口
- 前端可视化测试界面
- 批量操作支持
- 完整的项目文档

### Changed
- 项目结构重组，符合开源项目标准
- 文档整理到docs目录
- 前端文件独立到frontend目录

### Fixed
- 初始版本，无修复记录

## [1.0.0] - 2024-01-01

### Added
- 核心标签管理系统
- 基于Directus设计思想
- Spring Boot 3.2.0 + MyBatis-Plus 3.5.5
- PostgreSQL数据库支持
- 完整的CRUD操作
- 事务管理
- 参数校验
- 错误处理
- 数据库初始化脚本
- Maven构建配置
- 启动脚本（Windows/Linux）

---

## 版本说明

### 版本号格式：MAJOR.MINOR.PATCH

- **MAJOR**: 不兼容的API变更
- **MINOR**: 向后兼容的功能新增
- **PATCH**: 向后兼容的问题修正

### 变更类型说明

- **Added**: 新增功能
- **Changed**: 现有功能的变更
- **Deprecated**: 即将废弃的功能
- **Removed**: 已删除的功能
- **Fixed**: Bug修复
- **Security**: 安全相关的修复或改进

---

**注意**: 此CHANGELOG.md将在后续版本更新时持续维护。
