@echo off
echo Stopping any existing gateway service...
taskkill /f /im java.exe 2>nul

echo Starting Gateway Service...
cd gateway-service
start "Gateway Service" mvn spring-boot:run
cd ..

echo Gateway service is starting...
timeout /t 5 /nobreak >nul
echo Done!
