@echo off
echo ============================================
echo BookMyShow Backend - Starting Application
echo ============================================
echo.

cd backend

echo Checking Java version...
java -version
echo.

echo Building project with Maven...
call mvn clean install -DskipTests
echo.

if %ERRORLEVEL% NEQ 0 (
    echo Build failed! Please check errors above.
    pause
    exit /b 1
)

echo Build successful!
echo.
echo Starting Spring Boot application...
echo Backend will be available at: http://localhost:8080
echo H2 Console: http://localhost:8080/h2-console
echo.
echo Press Ctrl+C to stop the server
echo.

call mvn spring-boot:run
