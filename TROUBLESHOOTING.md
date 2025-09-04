# E-Commerce Platform Troubleshooting Guide

## Common Issues and Solutions

### 1. Gateway Service Not Starting (ERR_CONNECTION_REFUSED on port 8080)

**Symptoms:**
- `net::ERR_CONNECTION_REFUSED` when accessing `http://localhost:8080`
- Frontend cannot connect to backend APIs

**Solutions:**
1. **Check if gateway service is running:**
   ```bash
   # Check if port 8080 is in use
   netstat -an | findstr :8080
   ```

2. **Restart gateway service:**
   ```bash
   cd gateway-service
   mvn spring-boot:run
   ```

3. **Check gateway logs for errors:**
   - Look for bean conflicts or CORS configuration issues
   - Ensure Eureka client is connecting to discovery service

### 2. CORS Issues

**Symptoms:**
- `Access to XMLHttpRequest blocked by CORS policy`
- `No 'Access-Control-Allow-Origin' header is present`

**Solutions:**
1. **Verify CORS configuration in gateway:**
   - Check `gateway-service/src/main/java/com/ecommerce/gateway/config/CorsConfig.java`
   - Ensure reactive CORS filter is properly configured

2. **Test CORS with curl:**
   ```bash
   curl -H "Origin: http://localhost:4200" \
        -H "Access-Control-Request-Method: POST" \
        -H "Access-Control-Request-Headers: Content-Type" \
        -X OPTIONS \
        http://localhost:8080/api/auth/login
   ```

### 3. Authentication Issues

**Symptoms:**
- `401 Unauthorized` when accessing catalog items
- JWT token not being accepted

**Solutions:**
1. **Check JWT filter configuration:**
   - Ensure catalog endpoints are whitelisted for public access
   - Verify OPTIONS requests are handled for CORS preflight

2. **Test authentication flow:**
   ```bash
   # Test login
   curl -X POST http://localhost:8080/api/auth/login \
        -H "Content-Type: application/json" \
        -d '{"email":"user@ecommerce.com","password":"user123"}'
   
   # Test catalog access (should work without auth)
   curl http://localhost:8080/api/catalog/items
   ```

### 4. Service Discovery Issues

**Symptoms:**
- Services not appearing in Eureka dashboard
- Gateway cannot route to services

**Solutions:**
1. **Check Eureka dashboard:** `http://localhost:8761`
2. **Verify service registration:**
   - All services should appear in Eureka dashboard
   - Check application names match routing configuration

3. **Restart services in order:**
   ```bash
   # 1. Start discovery service first
   cd discovery-service && mvn spring-boot:run
   
   # 2. Wait for Eureka to start, then start other services
   cd auth-service && mvn spring-boot:run
   cd catalogue-service && mvn spring-boot:run
   cd order-service && mvn spring-boot:run
   cd gateway-service && mvn spring-boot:run
   ```

### 5. Database Issues

**Symptoms:**
- Empty product catalog
- User registration/login failures

**Solutions:**
1. **Check H2 console:** `http://localhost:8082/h2-console`
   - JDBC URL: `jdbc:h2:mem:cataloguedb`
   - Username: `sa`, Password: (empty)

2. **Verify seed data loading:**
   - Check `data.sql` files in each service
   - Look for SQL execution errors in service logs

## Quick Health Checks

### 1. Service Health Endpoints
```bash
# Discovery Service
curl http://localhost:8761/actuator/health

# Gateway Service
curl http://localhost:8080/health

# Auth Service
curl http://localhost:8081/actuator/health

# Catalogue Service
curl http://localhost:8082/actuator/health

# Order Service
curl http://localhost:8083/actuator/health
```

### 2. API Endpoint Tests
```bash
# Test catalog (public)
curl http://localhost:8080/api/catalog/items

# Test login
curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email":"user@ecommerce.com","password":"user123"}'

# Test with authentication
TOKEN="your-jwt-token-here"
curl -H "Authorization: Bearer $TOKEN" \
     http://localhost:8080/api/cart
```

## Port Configuration

| Service | Port | URL |
|---------|------|-----|
| Discovery (Eureka) | 8761 | http://localhost:8761 |
| Gateway | 8080 | http://localhost:8080 |
| Auth Service | 8081 | http://localhost:8081 |
| Catalogue Service | 8082 | http://localhost:8082 |
| Order Service | 8083 | http://localhost:8083 |
| Angular Frontend | 4200 | http://localhost:4200 |

## Demo Credentials

- **User:** user@ecommerce.com / user123
- **Admin:** admin@ecommerce.com / admin123
