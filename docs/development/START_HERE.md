# 🎉 标签管理系统 - 完整指南

## 快速开始

### 第一步：启动后端服务

#### Windows
```bash
run.bat
```

#### Linux/Mac
```bash
chmod +x run.sh
./run.sh
```

#### 或使用Maven
```bash
mvn spring-boot:run
```

**等待看到提示：数据库初始化完成！**

---

### 第二步：打开前端页面

#### Windows
```bash
双击 open-frontend.bat
```

#### Linux/Mac
```bash
chmod +x open-frontend.sh
./open-frontend.sh
```

#### 或直接打开
```bash
start index.html      # Windows
open index.html       # Mac
xdg-open index.html   # Linux
```

---

### 第三步：开始使用

1. 在浏览器中打开 `index.html`
2. 选择不同的标签页进行测试
3. 尝试各种功能

---

## 📋 页面功能导航

### 📑 标签管理
- 创建标签（名称、颜色、排序、描述）
- 查看标签列表（支持搜索）
- 删除标签
- 标签合并
- 查看统计

### 📁 文件标签关联
- 添加单个标签
- 批量添加标签
- 移除单个标签
- 批量移除标签
- 查询文件标签

### 🔍 多标签查询
- 选择多个标签
- 查询交集文件
- 查看结果

### 📊 统计数据
- 标签使用统计
- 标签计数详情

---

## 📚 文档

- **FRONTEND_GUIDE.md** - 前端使用指南
- **DEPLOYMENT.md** - 部署指南
- **IMPLEMENTATION_SUMMARY.md** - 实现总结
- **README.md** - 项目说明

---

## 🎨 可用的页面

1. **index.html** - 主页面（完整功能）
2. **advanced-features.html** - 高级示例和API文档
3. **frontend-intro.html** - 功能概览

---

## 🚀 测试建议

### 基础测试
```
✓ 创建几个标签
✓ 为文件添加标签
✓ 查询文件标签
✓ 删除标签
```

### 高级测试
```
✓ 批量创建标签
✓ 批量添加标签
✓ 多标签查询
✓ 标签合并
✓ 批量删除
```

---

## 🔧 常见问题

### Q: 页面打不开？
A: 确保后端服务已启动，浏览器可以访问 http://localhost:8080/api

### Q: 创建标签失败？
A: 检查标签名称是否重复，查看浏览器控制台错误

### Q: 数据查询不到？
A: 确保数据库已初始化，检查标签ID是否正确

### Q: CORS错误？
A: 确保后端配置了正确的CORS设置

---

## 📊 项目统计

- **后端代码**: ~3000+ 行
- **前端代码**: ~1500+ 行
- **文档**: ~2000+ 字
- **总文件数**: 20+ 个

---

## 🎯 技术栈

**后端**:
- Spring Boot 3.2.0
- MyBatis-Plus 3.5.5
- PostgreSQL
- Java 17

**前端**:
- HTML5
- CSS3
- JavaScript ES6+
- Fetch API

---

## 📞 获取帮助

查看以下资源：
1. 浏览器控制台错误信息
2. 后端服务日志
3. 各类文档文件
4. advanced-features.html 中的API示例

---

## ✨ 开始体验吧！

```bash
# 1. 启动后端
run.bat

# 2. 打开前端
open-frontend.bat

# 3. 开始测试！
```

**祝您使用愉快！** 🎉
