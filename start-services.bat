@echo off
echo Starting E-Commerce Platform Services...
echo.

echo Starting Discovery Service (Eureka)...
start "Discovery Service" cmd /k "cd discovery-service && mvn spring-boot:run"
timeout /t 30

echo Starting Auth Service...
start "Auth Service" cmd /k "cd auth-service && mvn spring-boot:run"
timeout /t 15

echo Starting Catalogue Service...
start "Catalogue Service" cmd /k "cd catalogue-service && mvn spring-boot:run"
timeout /t 15

echo Starting Order Service...
start "Order Service" cmd /k "cd order-service && mvn spring-boot:run"
timeout /t 15

echo Starting Gateway Service...
start "Gateway Service" cmd /k "cd gateway-service && mvn spring-boot:run"
timeout /t 15

echo.
echo All services are starting up...
echo.
echo Service URLs:
echo - Eureka Dashboard: http://localhost:8761
echo - API Gateway: http://localhost:8080
echo - Auth Service: http://localhost:8081
echo - Catalogue Service: http://localhost:8082
echo - Order Service: http://localhost:8083
echo.
echo To start the Angular frontend:
echo cd frontend
echo npm install
echo ng serve
echo.
echo Frontend will be available at: http://localhost:4200
echo.
pause
