# File Tag Search - 快速参考

## 项目简介
一个基于Directus设计思想的高性能文件标签管理系统，支持灵活的标签管理和多标签查询。

## 技术栈
- **后端**: Spring Boot 3.2.0 + MyBatis-Plus 3.5.5
- **数据库**: PostgreSQL 12+ (JSONB + GIN索引)
- **前端**: HTML5 + CSS3 + JavaScript
- **构建**: Maven 3.6+
- **JDK**: 17+

## 快速开始

### 1. 环境准备
```bash
# 需要安装
- JDK 17+
- PostgreSQL 12+
- Maven 3.6+
```

### 2. 启动步骤
```bash
# Clone项目
git clone https://github.com/yourusername/file-tag-search.git
cd file-tag-search

# 初始化数据库
psql -U postgres -c "CREATE DATABASE file_tag_search;"
psql -U postgres -d file_tag_search -f src/main/resources/db/init-schema.sql

# 配置数据库（编辑 src/main/resources/application.yml）

# 启动应用
mvn spring-boot:run
# 或使用脚本
scripts/run.bat  # Windows
./scripts/run.sh # Linux/Mac
```

### 3. 访问界面
- **API**: http://localhost:8080/api/labels
- **前端**: 打开 `frontend/index.html` 或运行本地服务器

## 核心功能

### 标签管理
- ✅ 创建、查询、更新、删除标签
- ✅ 标签合并
- ✅ 标签统计

### 文件标签关联
- ✅ 单个/批量添加标签
- ✅ 单个/批量移除标签
- ✅ 查询文件标签

### 高级查询
- ✅ 多标签交集查询
- ✅ 按标签检索文件
- ✅ 标签使用统计

## API端点

### 标签管理
```
POST   /api/labels                    # 创建标签
GET    /api/labels                    # 查询标签列表
GET    /api/labels/{id}               # 获取标签详情
PUT    /api/labels/{id}               # 更新标签
DELETE /api/labels/{id}               # 删除标签
POST   /api/labels/merge              # 合并标签
GET    /api/labels/statistics         # 标签统计
```

### 文件标签
```
POST   /api/file-tags                 # 添加标签
POST   /api/file-tags/batch           # 批量添加
DELETE /api/file-tags/{fileId}/{labelId}  # 移除标签
GET    /api/file-tags/{fileId}        # 查询文件标签
POST   /api/file-tags/search/intersection  # 多标签查询
```

## 项目结构
```
file-tag-search/
├── .github/          # GitHub配置
├── docs/             # 文档
│   ├── api/         # API文档
│   ├── deployment/  # 部署指南
│   └── development/ # 开发文档
├── frontend/         # 前端界面
├── scripts/          # 脚本文件
├── src/              # 源代码
│   ├── main/
│   │   ├── java/    # Java代码
│   │   └── resources/ # 资源配置
│   └── test/        # 测试代码
├── LICENSE           # MIT许可证
├── README.md         # 项目说明
├── CONTRIBUTING.md   # 贡献指南
└── CHANGELOG.md      # 变更日志
```

## 常用命令

```bash
# 编译项目
mvn clean compile

# 运行测试
mvn test

# 打包应用
mvn clean package -DskipTests

# 运行应用
mvn spring-boot:run

# 清理构建
mvn clean
```

## 文档链接

- [完整README](README.md)
- [贡献指南](CONTRIBUTING.md)
- [部署指南](docs/deployment/DEPLOYMENT.md)
- [前端使用指南](docs/development/FRONTEND_GUIDE.md)
- [项目结构](docs/PROJECT_STRUCTURE.md)
- [变更日志](CHANGELOG.md)

## 支持

- 📖 查看[文档](docs/)
- 🐛 报告[问题](https://github.com/yourusername/file-tag-search/issues)
- 💡 提出[建议](https://github.com/yourusername/file-tag-search/issues)
- 🤝 参与[贡献](CONTRIBUTING.md)

## 许可证

MIT License - 详见 [LICENSE](LICENSE) 文件

---

**快速导航**: [首页](README.md) | [API](docs/api/) | [贡献](CONTRIBUTING.md) | [变更](CHANGELOG.md)
