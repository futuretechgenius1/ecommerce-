@echo off
echo Testing E-Commerce Services...

echo.
echo 1. Testing Discovery Service (Eureka)...
curl -s http://localhost:8761/actuator/health
if %errorlevel% neq 0 (
    echo Discovery service not running on port 8761
) else (
    echo Discovery service is UP
)

echo.
echo 2. Testing Gateway Service...
curl -s http://localhost:8080/health
if %errorlevel% neq 0 (
    echo Gateway service not running on port 8080
) else (
    echo Gateway service is UP
)

echo.
echo 3. Testing Auth Service...
curl -s http://localhost:8081/actuator/health
if %errorlevel% neq 0 (
    echo Auth service not running on port 8081
) else (
    echo Auth service is UP
)

echo.
echo 4. Testing Catalogue Service...
curl -s http://localhost:8082/actuator/health
if %errorlevel% neq 0 (
    echo Catalogue service not running on port 8082
) else (
    echo Catalogue service is UP
)

echo.
echo 5. Testing Order Service...
curl -s http://localhost:8083/actuator/health
if %errorlevel% neq 0 (
    echo Order service not running on port 8083
) else (
    echo Order service is UP
)

echo.
echo 6. Testing Catalog API through Gateway...
curl -s http://localhost:8080/api/catalog/items
if %errorlevel% neq 0 (
    echo Catalog API not accessible through gateway
) else (
    echo Catalog API is accessible through gateway
)

echo.
echo 7. Testing Auth API through Gateway...
curl -s -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"email\":\"user@ecommerce.com\",\"password\":\"user123\"}"
if %errorlevel% neq 0 (
    echo Auth API not accessible through gateway
) else (
    echo Auth API is accessible through gateway
)

echo.
echo Service testing completed!
pause
