@echo off
setlocal
rem Starts DB (Docker), server and client from jar files.
rem Put this file in the project root (next to pharmacy-client and pharmacy-server).
rem First run builds the jars. Force rebuild: run.bat --rebuild

set "ROOT_DIR=%~dp0"
set "SERVER_DIR=%ROOT_DIR%pharmacy-server"
set "CLIENT_DIR=%ROOT_DIR%pharmacy-client"

rem --- Maven wrapper needs JAVA_HOME: detect it from java on PATH if not set ---
if defined JAVA_HOME goto java_ok
for /f "tokens=1,* delims==" %%a in ('java -XshowSettings:properties -version 2^>^&1 ^| findstr /C:"java.home"') do set "JAVA_HOME=%%b"
if not defined JAVA_HOME (
    echo [ERROR] Java not found. Install JDK 21 and add it to PATH.
    pause
    exit /b 1
)
set "JAVA_HOME=%JAVA_HOME:~1%"
:java_ok
echo Using JAVA_HOME=%JAVA_HOME%

set NEED_BUILD=0
if "%1"=="--rebuild" set NEED_BUILD=1
if not exist "%SERVER_DIR%\target\pharmacy-server-*.jar" set NEED_BUILD=1
if not exist "%CLIENT_DIR%\target\pharmacy-client-*.jar" set NEED_BUILD=1
if "%NEED_BUILD%"=="0" goto run

echo ==^> Building server...
pushd "%SERVER_DIR%"
call mvnw.cmd -q package -DskipTests
if errorlevel 1 goto build_failed
popd

echo ==^> Building client...
pushd "%CLIENT_DIR%"
call mvnw.cmd -q package -DskipTests
if errorlevel 1 goto build_failed
popd
goto run

:build_failed
popd
echo [ERROR] Build failed. See the Maven output above.
pause
exit /b 1

:run
set "SERVER_JAR="
set "CLIENT_JAR="
for %%f in ("%SERVER_DIR%\target\pharmacy-server-*.jar") do set "SERVER_JAR=%%f"
for %%f in ("%CLIENT_DIR%\target\pharmacy-client-*.jar") do set "CLIENT_JAR=%%f"
if not defined SERVER_JAR (echo [ERROR] Server jar not found. & pause & exit /b 1)
if not defined CLIENT_JAR (echo [ERROR] Client jar not found. & pause & exit /b 1)

echo ==^> Starting database...
pushd "%SERVER_DIR%"
docker compose up -d
if errorlevel 1 (echo [ERROR] Docker failed. Is Docker Desktop running? & popd & pause & exit /b 1)
popd

echo ==^> Starting server (log: server.log)...
start "pharmacy-server" /min cmd /c java -jar "%SERVER_JAR%" ^> "%ROOT_DIR%server.log" 2^>^&1

echo ==^> Starting client (it waits for the server)...
java -jar "%CLIENT_JAR%"

echo ==^> Stopping server...
taskkill /FI "WINDOWTITLE eq pharmacy-server*" /T /F >nul 2>&1
endlocal
