@echo off
echo ========================================
echo Restarting Auth Service with Fixed Passwords
echo ========================================
echo.
echo This will restart the Auth Service to load the corrected password hashes.
echo.

echo Stopping Auth Service if running...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8081') do (
    taskkill /f /pid %%a 2>nul
)
timeout /t 2 > nul

echo Starting Auth Service with corrected passwords...
cd auth-service
start "Auth Service" cmd /k "mvn spring-boot:run"
cd ..

echo.
echo ========================================
echo Auth Service Restarted!
echo ========================================
echo.
echo Wait about 15-20 seconds for the service to fully start.
echo.
echo Then try logging in with:
echo   User: user@ecommerce.com / user123
echo   Admin: admin@ecommerce.com / admin123
echo.
pause
