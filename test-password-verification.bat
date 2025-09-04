@echo off
echo Testing Password Verification...
echo.

echo Testing with PowerShell BCrypt verification:
powershell -Command "Add-Type -AssemblyName 'System.Web'; $hash1 = '$2a$10$Qji2/icFWIGGQEAv8bbwNuKGrSZyiJfDOTKqBJqVPPFdIbG/lSG96'; $hash2 = '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a'; Write-Host 'Hash 1 (user123):' $hash1; Write-Host 'Hash 2 (admin123):' $hash2"

echo.
echo Testing login with different credentials...
echo.

echo 1. Testing with user@ecommerce.com / user123:
powershell -Command "try { Invoke-RestMethod -Uri 'http://localhost:8081/login' -Method POST -ContentType 'application/json' -Body '{\"email\":\"user@ecommerce.com\",\"password\":\"user123\"}' } catch { Write-Host 'Error:' $_.Exception.Message }"

echo.
echo 2. Testing with admin@ecommerce.com / admin123:
powershell -Command "try { Invoke-RestMethod -Uri 'http://localhost:8081/login' -Method POST -ContentType 'application/json' -Body '{\"email\":\"admin@ecommerce.com\",\"password\":\"admin123\"}' } catch { Write-Host 'Error:' $_.Exception.Message }"

echo.
echo 3. Testing with wrong password:
powershell -Command "try { Invoke-RestMethod -Uri 'http://localhost:8081/login' -Method POST -ContentType 'application/json' -Body '{\"email\":\"user@ecommerce.com\",\"password\":\"wrongpassword\"}' } catch { Write-Host 'Error:' $_.Exception.Message }"

echo.
pause
