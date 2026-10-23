@echo off
setlocal
echo ========================================================
echo Starting Laboratory Management System on Apache Tomcat...
echo ========================================================
echo.
set "CATALINA_HOME=C:\Users\nitai\apache-tomcat-10.1.20"
set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.12"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo Deploying latest WAR file to Tomcat...
copy /Y "%~dp0target\LaboratoryManagementSystem.war" "%CATALINA_HOME%\webapps\LaboratoryManagementSystem.war" > nul

echo.
echo Application will be available at:
echo http://localhost:8080/LaboratoryManagementSystem/
echo.
start "" http://localhost:8080/LaboratoryManagementSystem/
call "%CATALINA_HOME%\bin\catalina.bat" run
