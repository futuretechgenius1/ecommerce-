@echo off
echo Restarting Catalogue Service to apply CORS fixes...

echo Stopping catalogue service if running...
taskkill /f /im java.exe 2>nul

echo Starting catalogue service...
cd catalogue-service
start "Catalogue Service" cmd /k "mvn spring-boot:run"
cd ..

echo Catalogue service restarted!
echo Wait 30 seconds for service to fully start, then test the frontend again.
pause
