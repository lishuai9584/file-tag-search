# 项目结构说明

本文档详细说明了 File Tag Search 项目的目录结构和文件组织。

## 目录结构

```
file-tag-search/
├── .github/                    # GitHub相关配置
│   ├── ISSUE_TEMPLATE/        # Issue模板目录
│   │   ├── bug_report.md      # Bug报告模板
│   │   └── feature_request.md # 功能请求模板
│   └── PULL_REQUEST_TEMPLATE.md # PR模板
│
├── docs/                       # 文档目录
│   ├── api/                   # API文档
│   ├── deployment/            # 部署相关文档
│   │   └── DEPLOYMENT.md      # 部署指南
│   └── development/           # 开发相关文档
│       ├── FRONTEND_GUIDE.md          # 前端使用指南
│       ├── IMPLEMENTATION_SUMMARY.md  # 实现总结
│       ├── START_HERE.md              # 快速开始
│       └── frontend-complete.md       # 前端完成说明
│
├── examples/                   # 示例代码目录（待添加）
│
├── frontend/                   # 前端文件
│   ├── index.html             # 主页面（完整功能）
│   ├── advanced-features.html # 高级功能演示页面
│   ├── frontend-intro.html    # 前端介绍页面
│   ├── open-frontend.bat      # Windows打开前端脚本
│   └── open-frontend.sh       # Linux/Mac打开前端脚本
│
├── scripts/                    # 脚本文件
│   ├── run.bat                # Windows启动脚本
│   ├── run.sh                 # Linux/Mac启动脚本
│   ├── generate-mock-data.sql # 生成测试数据脚本
│   └── test-data.sql          # 测试数据脚本
│
├── src/                        # 源代码目录
│   ├── main/
│   │   ├── java/com/labelcode/
│   │   │   ├── config/        # 配置类
│   │   │   │   ├── DatabaseInitializer.java
│   │   │   │   ├── MybatisPlusConfig.java
│   │   │   │   └── WebConfig.java
│   │   │   ├── controller/    # REST API控制器
│   │   │   │   ├── AttributeConfigController.java
│   │   │   │   ├── DatasetConfigController.java
│   │   │   │   ├── FileMetaController.java
│   │   │   │   ├── FileTagRelationController.java
│   │   │   │   └── LabelLibraryController.java
│   │   │   ├── dto/           # 数据传输对象
│   │   │   │   ├── FileTagRelationQuery.java
│   │   │   │   ├── LabelDTO.java
│   │   │   │   └── TagMergeRequest.java
│   │   │   ├── entity/        # 实体类
│   │   │   │   ├── AttributeConfig.java
│   │   │   │   ├── DatasetConfig.java
│   │   │   │   ├── FileDatasetRelation.java
│   │   │   │   ├── FileMeta.java
│   │   │   │   ├── FileTagRelation.java
│   │   │   │   └── LabelLibrary.java
│   │   │   ├── mapper/        # MyBatis Mapper接口
│   │   │   │   ├── AttributeConfigMapper.java
│   │   │   │   ├── DatasetConfigMapper.java
│   │   │   │   ├── FileDatasetRelationMapper.java
│   │   │   │   ├── FileMetaMapper.java
│   │   │   │   ├── FileTagRelationMapper.java
│   │   │   │   └── LabelLibraryMapper.java
│   │   │   ├── service/       # 业务逻辑层
│   │   │   │   ├── impl/      # 服务实现
│   │   │   │   └── *.java     # 服务接口
│   │   │   ├── util/          # 工具类
│   │   │   │   └── LabelSystemValidator.java
│   │   │   └── LabelCodeApplication.java  # 主应用类
│   │   └── resources/
│   │       ├── db/            # 数据库脚本
│   │       │   ├── init-schema.sql        # 初始化脚本
│   │       │   ├── mock-200m-data.sql     # 2亿条模拟数据脚本
│   │       │   └── mock-data.sql          # 模拟数据脚本
│   │       ├── mapper/        # MyBatis XML映射文件
│   │       │   ├── FileMetaMapper.xml
│   │       │   ├── FileTagRelationMapper.xml
│   │       │   └── LabelLibraryMapper.xml
│   │       └── application.yml # 应用配置文件
│   └── test/                   # 测试代码
│       └── java/com/labelcode/
│           └── LabelCodeApiTest.java  # API测试
│
├── LICENSE                     # MIT许可证
├── README.md                   # 项目说明文档
├── CONTRIBUTING.md             # 贡献指南
├── CHANGELOG.md                # 变更日志
├── pom.xml                     # Maven配置文件
└── .gitignore                  # Git忽略文件配置
```

## 主要目录说明

### .github/
存放GitHub相关的配置文件，包括Issue模板和Pull Request模板，帮助规范社区的贡献流程。

### docs/
项目文档目录，按类型分类：
- **api/**: API接口文档
- **deployment/**: 部署相关文档
- **development/**: 开发相关文档

### frontend/
前端界面文件，提供可视化的标签管理和测试界面。可以直接在浏览器中打开使用。

### scripts/
各种脚本文件：
- 启动脚本（run.bat/run.sh）
- SQL脚本（测试数据、模拟数据等）

### src/
核心源代码目录，采用标准的Maven项目结构：
- **main/java**: Java源代码
- **main/resources**: 资源文件（配置、SQL、XML映射等）
- **test**: 测试代码

## 关键文件说明

### 根目录文件
- **README.md**: 项目主文档，包含项目介绍、快速开始、API说明等
- **LICENSE**: 项目许可证（MIT）
- **CONTRIBUTING.md**: 贡献指南，说明如何参与项目开发
- **CHANGELOG.md**: 版本变更日志
- **pom.xml**: Maven项目配置文件
- **.gitignore**: Git忽略文件配置

### 配置文件
- **src/main/resources/application.yml**: Spring Boot应用配置
- **src/main/resources/db/init-schema.sql**: 数据库初始化脚本

### 启动脚本
- **scripts/run.bat**: Windows系统启动脚本
- **scripts/run.sh**: Linux/Mac系统启动脚本

## 开发建议

1. **代码修改**: 主要在 `src/main/java` 目录下进行
2. **配置修改**: 编辑 `src/main/resources/application.yml`
3. **数据库变更**: 更新 `src/main/resources/db/` 下的SQL脚本
4. **文档更新**: 在 `docs/` 目录下添加或修改文档
5. **前端开发**: 修改 `frontend/` 目录下的HTML文件

## 注意事项

- 不要将IDE配置文件（如 `.idea/`）提交到Git
- 敏感信息（如数据库密码）应使用环境变量或外部配置
- 保持代码和文档的同步更新
- 遵循项目的代码规范和提交规范
