@echo off
chcp 65001 >nul

echo ========================================
echo 标签管理系统 - 快速启动
echo ========================================

REM 1. 检查Java
echo 检查Java版本...
java -version 2>&1 | findstr /i "version"
if errorlevel 1 (
    echo 错误: 未找到Java，请先安装JDK 17+
    pause
    exit /b 1
)

REM 2. 检查Maven
echo 检查Maven...
mvn -version 2>&1 | findstr /i "Apache Maven"
if errorlevel 1 (
    echo 错误: 未找到Maven，请先安装Maven
    pause
    exit /b 1
)

REM 3. 检查PostgreSQL连接
echo 检查PostgreSQL连接...
psql -U postgres -c "SELECT 1" >nul 2>&1
if errorlevel 1 (
    echo 错误: 无法连接到PostgreSQL，请检查配置
    echo 提示: 修改 src\main\resources\application.yml 中的数据库连接信息
    pause
    exit /b 1
)
echo ✓

REM 4. 检查数据库
echo 检查数据库初始化状态...
psql -U postgres -d label_code -c "SELECT COUNT(*) FROM label_library" >nul 2>&1
if errorlevel 1 (
    echo 数据库尚未初始化，正在执行初始化脚本...
    psql -U postgres -f src\main\resources\db\init-schema.sql
    if errorlevel 1 (
        echo 错误: 数据库初始化失败
        pause
        exit /b 1
    )
    echo ✓ 数据库初始化完成
) else (
    echo ✓ 数据库已存在
)

REM 5. 启动应用
echo.
echo 启动应用...
echo ========================================
call mvn spring-boot:run

pause
