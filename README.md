# 标签管理系统

基于 Directus 设计思想的标签管理系统实现，使用 Spring Boot + MyBatis-Plus + PostgreSQL。
文件标签检索
## 技术栈

- **后端框架**: Spring Boot 3.2.0
- **ORM框架**: MyBatis-Plus 3.5.5
- **数据库**: PostgreSQL
- **工具**: Lombok, Jackson
- **JDK版本**: 17

## 项目结构

```
label-code/
├── src/
│   ├── main/
│   │   ├── java/com/labelcode/
│   │   │   ├── config/              # 配置类
│   │   │   ├── controller/          # REST API控制器
│   │   │   ├── dto/                 # 数据传输对象
│   │   │   ├── entity/              # 实体类
│   │   │   ├── mapper/              # MyBatis Mapper接口
│   │   │   └── service/             # 业务逻辑层
│   └── resources/
│       ├── mapper/                  # MyBatis XML映射文件
│       └── db/                      # 数据库脚本
└── pom.xml
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

### 1. 环境要求

- JDK 17+
- PostgreSQL 12+
- Maven 3.6+

### 2. 数据库初始化

```bash
# 执行数据库初始化脚本
psql -U postgres -f src/main/resources/db/init-schema.sql
```

### 3. 应用配置

修改 `src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/label_code
    username: postgres
    password: postgres
```

### 4. 启动应用

```bash
mvn spring-boot:run
```

### 5. 测试接口

#### 创建标签
```bash
curl -X POST http://localhost:8080/api/labels \
  -H "Content-Type: application/json" \
  -d '{"labelName":"测试标签","color":"#FF0000"}'
```

#### 查询标签列表
```bash
curl http://localhost:8080/api/labels
```

#### 为文件添加标签
```bash
curl -X POST "http://localhost:8080/api/file-tags?fileId=1&labelId=1"
```

#### 多标签交集查询
```bash
curl -X POST http://localhost:8080/api/file-tags/search/intersection \
  -H "Content-Type: application/json" \
  -d '{"labelIds":[1,2]}'
```

#### 标签合并
```bash
curl -X POST http://localhost:8080/api/labels/merge \
  -H "Content-Type: application/json" \
  -d '{"oldLabelId":1,"newLabelId":2}'
```

## API 接口文档

### 标签管理

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/labels | 创建标签 |
| GET | /api/labels | 查询标签列表（分页） |
| GET | /api/labels/{id} | 获取单个标签 |
| PUT | /api/labels/{id} | 更新标签 |
| DELETE | /api/labels/{id} | 删除标签 |
| POST | /api/labels/merge | 合并标签 |
| GET | /api/labels/statistics | 标签统计 |

### 文件标签关联

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/file-tags | 为文件添加标签 |
| POST | /api/file-tags/batch | 批量添加标签 |
| DELETE | /api/file-tags/{fileId}/{labelId} | 移除文件标签 |
| DELETE | /api/file-tags/{fileId}/batch | 批量移除标签 |
| GET | /api/file-tags/{fileId} | 查询文件标签列表 |
| GET | /api/file-tags/files-by-label/{labelId} | 查询具有某标签的文件 |
| GET | /api/file-tags/file/{fileId} | 获取文件标签详情 |
| GET | /api/file-tags/files?fileIds=1,2,3 | 批量查询文件标签 |
| POST | /api/file-tags/search/intersection | 多标签交集查询 |
| GET | /api/file-tags/count/{labelId} | 统计标签使用次数 |

## 核心功能说明

### 1. 多标签关联查询

使用 PostgreSQL 的 JSONB 查询功能：

```sql
-- 查询同时具有多个标签的文件
SELECT * FROM file_meta
WHERE tags @> '["标签1", "标签2"]'::jsonb;
```

### 2. 标签合并

支持批量将一个标签的所有关联关系合并到另一个标签：

```java
// 将 labelId=1 的所有关联合并到 labelId=2
labelService.mergeTags(1L, 2L);
```

### 3. 标签统计

自动维护每个标签的 tag_count 字段：

```sql
-- 触发器会在插入/删除 file_tag_relation 时自动更新
```

## 性能优化

1. **GIN 索引**: 为 JSONB 字段创建 GIN 索引，加速多标签查询
2. **复合索引**: 为 file_id 和 label_id 创建索引
3. **批量操作**: 支持批量插入和删除，减少数据库交互次数
4. **事务管理**: 所有写操作使用事务，保证数据一致性

## 测试验证

执行以下 SQL 验证功能：

```sql
-- 1. 查看标签统计
SELECT * FROM v_label_statistics;

-- 2. 查询文件标签
SELECT * FROM file_meta;

-- 3. 多标签交集查询
SELECT * FROM file_meta
WHERE tags @> '["重要", "待处理"]'::jsonb;
```

## 扩展方向

1. **缓存优化**: 对热点标签数据进行缓存
2. **软删除**: 添加 deleted 字段支持数据恢复
3. **权限控制**: 基于标签的权限隔离
4. **数据导出**: 支持标签统计报表导出
5. **标签分组**: 实现标签的层级分类

## 许可证

MIT License
