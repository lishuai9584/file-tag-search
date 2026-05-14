#!/bin/bash

# 标签管理系统启动脚本

echo "========================================"
echo "标签管理系统 - 快速启动"
echo "========================================"

# 1. 检查Java版本
echo -n "检查Java版本... "
java -version 2>&1 | head -n 1
if [ $? -ne 0 ]; then
    echo "错误: 未找到Java，请先安装JDK 17+"
    exit 1
fi

# 2. 检查Maven
echo -n "检查Maven... "
mvn -version 2>&1 | head -n 1
if [ $? -ne 0 ]; then
    echo "错误: 未找到Maven，请先安装Maven"
    exit 1
fi

# 3. 检查PostgreSQL
echo -n "检查PostgreSQL连接... "
psql -U postgres -c "SELECT 1" > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "错误: 无法连接到PostgreSQL，请检查配置"
    echo "提示: 修改 src/main/resources/application.yml 中的数据库连接信息"
    exit 1
fi
echo "✓"

# 4. 检查数据库
echo "检查数据库初始化状态..."
psql -U postgres -d label_code -c "SELECT COUNT(*) FROM label_library" > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "数据库尚未初始化，正在执行初始化脚本..."
    psql -U postgres -f src/main/resources/db/init-schema.sql
    if [ $? -ne 0 ]; then
        echo "错误: 数据库初始化失败"
        exit 1
    fi
    echo "✓ 数据库初始化完成"
else
    echo "✓ 数据库已存在"
fi

# 5. 启动应用
echo ""
echo "启动应用..."
echo "========================================"
mvn spring-boot:run
