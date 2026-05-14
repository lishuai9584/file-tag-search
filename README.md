# File Tag Search

<div align="center">

![Java](https://img.shields.io/badge/Java-17+-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-12+-blue.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

文件标签管理系统，支持高效的文件标签检索和管理。


</div>

## 📋 目录

- [项目简介](#项目简介)
- [主要特性](#主要特性)
- [技术栈](#技术栈)
- [快速开始](#快速开始)
- [项目结构](#项目结构)
- [API文档](#api文档)
- [前端界面](#前端界面)
- [部署指南](#部署指南)
- [贡献指南](#贡献指南)
- [许可证](#许可证)

## 项目简介

File Tag Search 是一个高性能的文件标签管理系统，灵感来源于 Directus 的设计理念。它提供了完整的标签管理、文件标签关联、多标签查询等功能，特别适合需要灵活组织和检索大量文件的场景。

### 核心功能

- 🏷️ **标签管理**: 创建、更新、删除和合并标签
- 🔗 **文件标签关联**: 为文件添加、移除和管理标签
- 🔍 **多标签查询**: 支持多标签交集查询，快速定位文件
- 📊 **统计分析**: 自动维护标签使用统计
- ⚡ **高性能**: 基于 PostgreSQL JSONB + GIN 索引优化
## 主要特性

- ✅ **完整的标签管理**: CRUD操作、批量删除、标签合并
- ✅ **灵活的文件标签关联**: 单个/批量添加和移除标签
- ✅ **强大的查询功能**: 多标签交集查询、按标签检索文件
- ✅ **自动统计**: 通过数据库触发器自动维护标签使用次数
- ✅ **高性能优化**: JSONB + GIN索引，支持高效的多标签查询
- ✅ **事务安全**: 所有写操作支持事务回滚，保证数据一致性
- ✅ **RESTful API**: 标准的REST接口设计
- ✅ **前端界面**: 提供完整的可视化测试和管理界面
- ✅ **易于扩展**: 清晰的架构设计，便于功能扩展

## 技术栈

### 后端
- **框架**: Spring Boot 3.2.0
- **ORM**: MyBatis-Plus 3.5.5
- **数据库**: PostgreSQL 12+
- **工具**: Lombok, Jackson
- **JDK**: 17+

### 前端
- **技术**: HTML5, CSS3, JavaScript (ES6+)
- **API通信**: Fetch API
- **数据存储**: Local Storage

### 构建工具
- **依赖管理**: Maven 3.6+
- **测试**: JUnit, MockMvc

## 项目结构

```
file-tag-search/
├── .github/                    # GitHub相关配置
│   ├── ISSUE_TEMPLATE/        # Issue模板
│   └── PULL_REQUEST_TEMPLATE.md
├── docs/                       # 文档目录
│   ├── api/                   # API文档
│   ├── deployment/            # 部署指南
│   └── development/           # 开发文档
├── examples/                   # 示例代码
├── frontend/                   # 前端文件
│   ├── index.html             # 主页面
│   ├── advanced-features.html # 高级功能演示
│   └── ...
├── scripts/                    # 脚本文件
│   ├── run.bat                # Windows启动脚本
│   ├── run.sh                 # Linux/Mac启动脚本
│   └── *.sql                  # SQL脚本
├── src/                        # 源代码
│   ├── main/
│   │   ├── java/com/labelcode/
│   │   │   ├── config/        # 配置类
│   │   │   ├── controller/    # REST API控制器
│   │   │   ├── dto/           # 数据传输对象
│   │   │   ├── entity/        # 实体类
│   │   │   ├── mapper/        # MyBatis Mapper接口
│   │   │   ├── service/       # 业务逻辑层
│   │   │   └── util/          # 工具类
│   │   └── resources/
│   │       ├── db/            # 数据库脚本
│   │       └── mapper/        # MyBatis XML映射文件
│   └── test/                   # 测试代码
├── LICENSE                     # MIT许可证
├── README.md                   # 项目说明
├── CONTRIBUTING.md             # 贡献指南
├── CHANGELOG.md                # 变更日志
└── pom.xml                     # Maven配置
```

## 数据库设计

### 核心表

1. **label_library** - 标签库表
   - 存储标签的基本信息（名称、颜色、描述等）
   - 维护每个标签的关联文件数统计

2. **file_tag_relation** - 文件标签关联表
   - 存储文件与标签的多对多关系
   - 使用复合唯一索引保证关系唯一性

3. **file_meta** - 文件元数据表
   - 使用 JSONB 字段存储标签数组
   - 通过 GIN 索引支持快速多标签查询

### 关键特性

- **JSONB + GIN 索引**: 实现高效的多标签交集查询
- **自动统计**: 通过触发器自动维护 tag_count
- **标签合并**: 支持批量合并相同标签
- **事务支持**: 所有操作支持事务回滚

## 快速开始

### 环境要求

- JDK 17+
- PostgreSQL 12+
- Maven 3.6+

### 安装步骤

#### 1. Clone项目

```bash
git clone https://github.com/yourusername/file-tag-search.git
cd file-tag-search
```

#### 2. 初始化数据库

```bash
# 创建数据库
psql -U postgres -c "CREATE DATABASE file_tag_search;"

# 执行初始化脚本
psql -U postgres -d file_tag_search -f src/main/resources/db/init-schema.sql
```

#### 3. 配置数据库连接

编辑 `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/file_tag_search
    username: postgres
    password: your_password
```

#### 4. 启动应用

**方式一：使用Maven**
```bash
mvn spring-boot:run
```

**方式二：使用启动脚本**
```bash
# Windows
scripts\run.bat

# Linux/Mac
chmod +x scripts/run.sh
./scripts/run.sh
```

**方式三：打包运行**
```bash
mvn clean package -DskipTests
java -jar target/label-code-1.0.0.jar
```

#### 5. 访问前端界面

打开浏览器访问 `frontend/index.html` 文件，或启动本地服务器：

```bash
cd frontend
python -m http.server 8000
# 然后访问 http://localhost:8000
```

#### 6. 测试API

```bash
# 查询标签列表
curl http://localhost:8080/api/labels

# 创建标签
curl -X POST http://localhost:8080/api/labels \
  -H "Content-Type: application/json" \
  -d '{"labelName":"测试标签","color":"#FF0000"}'
```

## API文档

详细的API文档请查看 [docs/api](docs/api/) 目录。

### 主要接口概览

#### 标签管理

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/labels | 创建标签 |
| GET | /api/labels | 查询标签列表（分页） |
| GET | /api/labels/{id} | 获取单个标签 |
| PUT | /api/labels/{id} | 更新标签 |
| DELETE | /api/labels/{id} | 删除标签 |
| POST | /api/labels/merge | 合并标签 |
| GET | /api/labels/statistics | 标签统计 |

#### 文件标签关联

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/file-tags | 为文件添加标签 |
| POST | /api/file-tags/batch | 批量添加标签 |
| DELETE | /api/file-tags/{fileId}/{labelId} | 移除文件标签 |
| DELETE | /api/file-tags/{fileId}/batch | 批量移除标签 |
| GET | /api/file-tags/{fileId} | 查询文件标签列表 |
| GET | /api/file-tags/files-by-label/{labelId} | 查询具有某标签的文件 |
| POST | /api/file-tags/search/intersection | 多标签交集查询 |
| GET | /api/file-tags/count/{labelId} | 统计标签使用次数 |

完整API文档请参考：
- [API接口详细说明](docs/api/API_REFERENCE.md)
- [部署指南](docs/deployment/DEPLOYMENT.md)

## 前端界面

项目提供了完整的可视化测试和管理界面，位于 `frontend/` 目录。

### 功能特性

- 🎨 **美观的UI**: 现代化的卡片式设计，响应式布局
- 📑 **标签管理**: 创建、查询、删除和合并标签
- 🔗 **文件标签关联**: 添加、移除和管理文件标签
- 🔍 **多标签查询**: 可视化多标签交集查询
- 📊 **统计分析**: 查看标签使用统计

### 使用方法

```bash
# 方式一：直接打开HTML文件
# Windows
start frontend/index.html

# Linux/Mac
open frontend/index.html

# 方式二：使用本地服务器
cd frontend
python -m http.server 8000
# 然后访问 http://localhost:8000
```

详细使用说明请参考：[前端使用指南](docs/development/FRONTEND_GUIDE.md)

## 数据库设计

### 核心表结构

1. **label_library** - 标签库表
   - 存储标签的基本信息（名称、颜色、描述等）
   - 维护每个标签的关联文件数统计

2. **file_tag_relation** - 文件标签关联表
   - 存储文件与标签的多对多关系
   - 使用复合唯一索引保证关系唯一性

3. **file_meta** - 文件元数据表
   - 使用 JSONB 字段存储标签数组
   - 通过 GIN 索引支持快速多标签查询

### 关键特性

- **JSONB + GIN 索引**: 实现高效的多标签交集查询
- **自动统计**: 通过触发器自动维护 tag_count
- **标签合并**: 支持批量合并相同标签
- **事务支持**: 所有操作支持事务回滚

## 贡献指南

我们欢迎各种形式的贡献！在开始之前，请阅读 [CONTRIBUTING.md](CONTRIBUTING.md) 了解如何参与项目开发。

### 快速贡献步骤

1. Fork 本仓库
2. 创建您的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交您的更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启一个 Pull Request

### 开发资源

- [开发文档](docs/development/)
- [部署指南](docs/deployment/DEPLOYMENT.md)
- [实现总结](docs/development/IMPLEMENTATION_SUMMARY.md)

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 致谢

感谢所有为本项目做出贡献的开发者！

---

<div align="center">

**如果这个项目对您有帮助，请给个 ⭐️ Star 支持一下！**

Made with ❤️ by File Tag Search Contributors

</div>
