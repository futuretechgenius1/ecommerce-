@echo off
echo ========================================
echo Restarting Services to Apply Fixes
echo ========================================
echo.
echo This will restart:
echo 1. Auth Service - to load updated user credentials
echo 2. Catalogue Service - to apply CORS fixes
echo 3. Order Service - to apply security changes
echo.
echo Press any key to continue...
pause > nul

echo.
echo Step 1: Stopping all Java processes...
taskkill /f /im java.exe 2>nul
timeout /t 3 > nul

echo.
echo Step 2: Starting Discovery Service (Eureka)...
cd discovery-service
start "Discovery Service" cmd /k "mvn spring-boot:run"
cd ..
echo Waiting for Eureka to start...
timeout /t 15 > nul

echo.
echo Step 3: Starting Gateway Service...
cd gateway-service
start "Gateway Service" cmd /k "mvn spring-boot:run"
cd ..
timeout /t 10 > nul

echo.
echo Step 4: Starting Auth Service (with updated credentials)...
cd auth-service
start "Auth Service" cmd /k "mvn spring-boot:run"
cd ..
timeout /t 10 > nul

echo.
echo Step 5: Starting Catalogue Service (with CORS fix)...
cd catalogue-service
start "Catalogue Service" cmd /k "mvn spring-boot:run"
cd ..
timeout /t 10 > nul

echo.
echo Step 6: Starting Order Service (with security fix)...
cd order-service
start "Order Service" cmd /k "mvn spring-boot:run"
cd ..
timeout /t 10 > nul

echo.
echo ========================================
echo All services have been restarted!
echo ========================================
echo.
echo Services should be available at:
echo - Eureka Dashboard: http://localhost:8761
echo - Gateway: http://localhost:8080
echo - Auth Service: http://localhost:8081
echo - Catalogue Service: http://localhost:8082
echo - Order Service: http://localhost:8083
echo.
echo Frontend (if running): http://localhost:4200
echo.
echo Wait about 30 seconds for all services to fully register with Eureka.
echo.
echo Demo Credentials:
echo User: user@ecommerce.com / user123
echo Admin: admin@ecommerce.com / admin123
echo.
pause
