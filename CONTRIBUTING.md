# 贡献指南

感谢您对 File Tag Search 项目的关注！我们欢迎各种形式的贡献，包括但不限于代码、文档、测试、bug报告和功能建议。

## 目录

- [行为准则](#行为准则)
- [如何贡献](#如何贡献)
  - [报告Bug](#报告bug)
  - [提出新功能](#提出新功能)
  - [提交代码](#提交代码)
- [开发环境设置](#开发环境设置)
- [代码规范](#代码规范)
- [提交Pull Request](#提交pull-request)
- [代码审查流程](#代码审查流程)

## 行为准则

本项目采用[Contributor Covenant](https://www.contributor-covenant.org/)行为准则。参与本项目即表示您同意遵守此准则。请尊重所有贡献者。

## 如何贡献

### 报告Bug

如果您发现了bug，请创建一个Issue并包含以下信息：

1. **清晰的标题**：简要描述问题
2. **复现步骤**：详细说明如何重现该问题
3. **预期行为**：描述您期望发生的行为
4. **实际行为**：描述实际发生的行为
5. **环境信息**：
   - Java版本
   - PostgreSQL版本
   - 操作系统
6. **相关日志**：如果有错误日志，请附上

### 提出新功能

如果您有新功能的想法，请先创建一个Issue讨论：

1. **功能描述**：清晰描述新功能
2. **使用场景**：说明为什么需要这个功能
3. **实现思路**：如果有技术实现的想法，可以分享
4. **替代方案**：是否考虑过其他解决方案

在开始编码之前，请先与 maintainer 讨论，以确保您的工作不会被重复或拒绝。

### 提交代码

1. Fork 本仓库
2. 创建您的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交您的更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启一个 Pull Request

## 开发环境设置

### 前置要求

- JDK 17+
- Maven 3.6+
- PostgreSQL 12+

### 快速开始

1. Clone 项目
```bash
git clone https://github.com/yourusername/file-tag-search.git
cd file-tag-search
```

2. 初始化数据库
```bash
psql -U postgres -f src/main/resources/db/init-schema.sql
```

3. 配置数据库连接
编辑 `src/main/resources/application.yml`

4. 运行项目
```bash
mvn spring-boot:run
```

详细指南请参考 [DEPLOYMENT.md](docs/deployment/DEPLOYMENT.md)

## 代码规范

### Java代码规范

- 遵循 [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- 使用有意义的变量和方法名
- 添加必要的注释，特别是复杂逻辑
- 保持方法简洁，单一职责
- 编写单元测试

### 提交信息规范

使用清晰的提交信息格式：

```
<type>: <subject>

<body>

<footer>
```

Type包括：
- `feat`: 新功能
- `fix`: Bug修复
- `docs`: 文档更新
- `style`: 代码格式（不影响代码运行）
- `refactor`: 重构
- `test`: 测试相关
- `chore`: 构建过程或辅助工具的变动

示例：
```
feat: 添加标签批量合并功能

- 支持一次合并多个标签
- 添加事务保证数据一致性
- 更新相关统计信息

Closes #123
```

### 测试要求

- 新功能必须包含单元测试
- Bug修复必须包含回归测试
- 确保所有测试通过：`mvn test`
- 保持测试覆盖率在合理水平

## 提交Pull Request

### PR标题

使用清晰的标题，说明PR的目的：
- ✅ `feat: 添加标签搜索功能`
- ✅ `fix: 修复多标签查询性能问题`
- ❌ `更新代码`
- ❌ `修改了一些东西`

### PR描述

请包含以下信息：

1. **变更类型**：
   - [ ] Bug修复
   - [ ] 新功能
   - [ ] 文档更新
   - [ ] 性能优化
   - [ ] 重构
   - [ ] 其他（请说明）

2. **相关Issue**：链接到相关的Issue（如：Closes #123）

3. **变更说明**：
   - 这次PR做了什么？
   - 为什么需要这些变更？
   - 如何测试这些变更？

4. **检查清单**：
   - [ ] 代码遵循项目规范
   - [ ] 添加了必要的测试
   - [ ] 所有测试通过
   - [ ] 更新了相关文档
   - [ ] 没有引入新的警告

### PR大小

- 保持PR小而专注
- 一个PR只做一件事
- 如果变更很大，考虑拆分成多个PR

## 代码审查流程

1. **自动检查**：
   - CI会自动运行测试
   - 检查代码风格
   - 验证构建

2. **人工审查**：
   - Maintainer会审查代码
   - 可能会提出修改建议
   - 讨论实现细节

3. **合并**：
   - 审查通过后合并
   - 可能会squash commits
   - 关闭相关Issue

### 审查标准

- 代码质量和可读性
- 功能正确性
- 测试覆盖
- 性能影响
- 向后兼容性
- 文档完整性

## 文档贡献

文档同样重要！您可以：

- 修正拼写和语法错误
- 改进文档结构
- 添加示例代码
- 翻译文档
- 补充缺失的说明

## 社区交流

- 通过Issue讨论技术问题
- 参与Code Review
- 帮助回答其他用户的问题

## 许可

贡献的代码将采用MIT许可证。

## 致谢

感谢所有为本项目做出贡献的开发者！

---

如有疑问，请创建Issue或联系maintainer。
