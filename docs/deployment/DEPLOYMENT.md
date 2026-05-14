# 部署指南

## 环境准备

### 1. 安装Java 17+

```bash
# Windows
# 下载并安装 JDK 17: https://adoptium.net/

# Linux/Mac
brew install openjdk@17
# 或
sudo apt install openjdk-17-jdk
```

### 2. 安装Maven

```bash
# Windows
# 下载 Maven: https://maven.apache.org/download.cgi

# Linux/Mac
brew install maven
# 或
sudo apt install maven
```

### 3. 安装PostgreSQL 12+

```bash
# Windows
# 下载 PostgreSQL: https://www.postgresql.org/download/windows/

# Linux/Mac
brew install postgresql@14
# 或
sudo apt install postgresql-14
```

### 4. 启动PostgreSQL

```bash
# Linux/Mac
brew services start postgresql@14
# 或
sudo systemctl start postgresql

# Windows
# 启动PostgreSQL服务
```

## 数据库初始化

### 1. 创建数据库

```sql
CREATE DATABASE label_code
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8';
```

### 2. 执行初始化脚本

```bash
# Windows
psql -U postgres -f src\main\resources\db\init-schema.sql

# Linux/Mac
psql -U postgres -f src/main/resources/db/init-schema.sql
```

或使用Spring Boot自动初始化（应用启动时会自动执行）。

## 配置修改

### 修改数据库连接信息

编辑 `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/label_code
    username: postgres
    password: 你的密码
```

## 编译项目

```bash
# 清理并编译
mvn clean compile

# 打包（跳过测试）
mvn clean package -DskipTests

# 打包（包含测试）
mvn clean package
```

## 运行项目

### 方式1: 使用启动脚本（推荐）

**Windows:**
```bash
run.bat
```

**Linux/Mac:**
```bash
chmod +x run.sh
./run.sh
```

### 方式2: 使用Maven命令

```bash
# 开发模式运行
mvn spring-boot:run

# 指定配置文件运行
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=dev

# 指定端口运行
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### 方式3: 使用JAR文件

```bash
# 运行JAR文件
java -jar target/label-code-1.0.0.jar

# 指定配置运行
java -jar target/label-code-1.0.0.jar --spring.config.location=application-prod.yml
```

## 验证部署

### 1. 检查应用是否启动成功

访问: http://localhost:8080/api/labels

应该返回标签列表数据。

### 2. 测试API接口

```bash
# 查询标签列表
curl http://localhost:8080/api/labels

# 创建标签
curl -X POST http://localhost:8080/api/labels \
  -H "Content-Type: application/json" \
  -d "{\"labelName\":\"测试标签\",\"color\":\"#FF0000\"}"

# 查询标签统计
curl http://localhost:8080/api/labels/statistics
```

### 3. 查看日志

应用启动后，查看控制台输出，确认：

- 数据库连接成功
- GIN索引创建成功
- 触发器创建成功
- 测试数据插入成功

## 生产环境部署

### 1. 配置优化

编辑 `application.yml`:

```yaml
server:
  port: 8080

spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl  # 使用日志而非控制台
```

### 2. 环境配置

创建 `application-prod.yml`:

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

  jackson:
    default-property-inclusion: non_null

logging:
  level:
    com.labelcode: INFO
  file:
    name: logs/label-code.log
```

### 3. 使用环境变量

```bash
export DB_URL=jdbc:postgresql://localhost:5432/label_code
export DB_USERNAME=postgres
export DB_PASSWORD=your_password

java -jar target/label-code-1.0.0.jar --spring.profiles.active=prod
```

### 4. 使用Docker部署（可选）

创建 `Dockerfile`:

```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/label-code-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

构建和运行：

```bash
docker build -t label-code .
docker run -p 8080:8080 --env-file .env label-code
```

### 5. 使用JAR包方式运行

```bash
java -Xms512m -Xmx1024m \
     -Dspring.profiles.active=prod \
     -jar label-code-1.0.0.jar
```

## 监控和维护

### 1. 健康检查

```bash
curl http://localhost:8080/api/labels
```

### 2. 查看日志

```bash
# Linux/Mac
tail -f logs/label-code.log

# Windows
type logs\label-code.log | findstr /i "error"
```

### 3. 数据库备份

```bash
# 备份数据库
pg_dump -U postgres label_code > backup_$(date +%Y%m%d).sql

# 恢复数据库
psql -U postgres label_code < backup_20240101.sql
```

## 故障排查

### 问题1: 数据库连接失败

**症状**: `SQLException: Connection refused`

**解决**:
1. 检查PostgreSQL是否启动
2. 检查连接配置（URL、用户名、密码）
3. 检查防火墙设置

### 问题2: GIN索引创建失败

**症状**: `Index creation failed`

**解决**:
```sql
-- 手动删除索引后重启应用
DROP INDEX IF EXISTS idx_file_meta_tags_gin;
```

### 问题3: 触发器未创建

**症状**: tag_count数据不准确

**解决**:
```sql
-- 手动重新创建触发器
DROP TRIGGER IF EXISTS trigger_update_tag_count ON file_tag_relation;
-- 重启应用，自动触发器会重新创建
```

## 性能优化建议

1. **连接池优化**: 根据并发量调整HikariCP连接数
2. **查询优化**: 避免全表扫描，使用合适索引
3. **缓存**: 对热点标签数据添加缓存
4. **分页**: 大数据量查询使用分页

## 安全建议

1. 使用SSL连接数据库
2. 设置强密码
3. 限制数据库访问IP
4. 定期备份数据
5. 使用环境变量管理敏感配置

## 联系支持

如有问题，请查看日志或联系开发团队。
