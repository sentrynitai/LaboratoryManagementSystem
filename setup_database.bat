@echo off
setlocal
echo ========================================================
echo Laboratory Management System - MySQL Database Setup
echo ========================================================
echo.
set /p DBPASS="Enter your MySQL root password: "
echo Initializing database schema and seed data...
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p%DBPASS% < "%~dp0database.sql"
if %ERRORLEVEL% EQU 0 (
    echo.
    echo [SUCCESS] Database 'laboratory_management' and seed data initialized successfully!
) else (
    echo.
    echo [ERROR] Failed to execute database.sql. Please verify your MySQL password and ensure MySQL service is running.
)
echo.
pause
