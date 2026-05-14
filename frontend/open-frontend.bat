@echo off
chcp 65001 >nul

echo ========================================
echo 标签管理系统 - 打开前端页面
echo ========================================

echo.
echo 检查 index.html 文件...
if not exist "index.html" (
    echo 错误: 找不到 index.html 文件
    echo 请确保在项目根目录运行此脚本
    pause
    exit /b 1
)

echo.
echo 正在打开前端页面...
echo.

REM 检测操作系统
if "%OS%"=="Windows_NT" (
    start index.html
) else (
    # 尝试打开 macOS 和 Linux
    if exist /Applications/Google\ Chrome.app/Contents/MacOS/Google\ Chrome then
        open -a "Google Chrome" index.html
    else if exist /usr/bin/open then
        open index.html
    else
        xdg-open index.html 2>nul || echo "无法打开浏览器，请手动打开 index.html"
)

echo.
echo ========================================
echo 提示：
echo 1. 确保后端服务已启动
echo 2. 访问地址: http://localhost:8080/api
echo 3. 浏览器应会自动打开
echo ========================================
echo.

pause
