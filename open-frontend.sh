#!/bin/bash

echo "========================================"
echo "标签管理系统 - 打开前端页面"
echo "========================================"

echo ""
echo "检查 index.html 文件..."
if [ ! -f "index.html" ]; then
    echo "错误: 找不到 index.html 文件"
    echo "请确保在项目根目录运行此脚本"
    exit 1
fi

echo ""
echo "正在打开前端页面..."
echo ""

# 尝试使用默认浏览器打开
if command -v xdg-open &> /dev/null; then
    xdg-open index.html
elif command -v open &> /dev/null; then
    open index.html
else
    echo "无法自动打开浏览器，请手动运行: open index.html"
    exit 1
fi

echo ""
echo "========================================"
echo "提示：
echo 1. 确保后端服务已启动
echo 2. 访问地址: http://localhost:8080/api
echo 3. 浏览器应会自动打开
echo ========================================"
echo ""
