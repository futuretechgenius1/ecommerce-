# E-Commerce Mini-Platform

A complete microservices-based e-commerce platform built with Spring Boot backend and Angular frontend.

## Architecture

### Backend Services
- **Discovery Service** (Port 8761) - Eureka Server for service discovery
- **Gateway Service** (Port 8080) - API Gateway with JWT authentication and routing
- **Auth Service** (Port 8081) - User authentication and management
- **Catalogue Service** (Port 8082) - Product catalog and inventory management
- **Order Service** (Port 8083) - Shopping cart, orders, and payment processing

### Frontend
- **Angular Application** (Port 4200) - Single Page Application with Material Design

## Technology Stack

### Backend
- Java 8
- Spring Boot 2.7.18
- Spring Cloud 2021.0.8
- Spring Security with JWT
- Spring Data JPA
- H2 Database (in-memory)
- Netflix Eureka
- Spring Cloud Gateway

### Frontend
- Angular 16+
- Angular Material
- RxJS
- TypeScript

## Features

### User Features
- User registration and login
- Product browsing with search and filters
- Shopping cart management
- Checkout with Indian payment methods (UPI, COD, Card)
- Order history and tracking
- Profile and address management

### Admin Features
- Product catalog management
- Order management and tracking
- Payment method configuration
- Sales analytics and KPIs

### Indian E-commerce Specific
- GST calculations by product category
- Multiple payment methods (UPI, COD, Card)
- Shipping fee calculations
- Indian address format support

## Getting Started

### Prerequisites
- Java 8 or higher
- Maven 3.6+
- Node.js 16+ and npm
- Git

### Backend Setup

1. **Navigate to the project directory**
   ```bash
   cd d:/App/ecm
   ```

2. **Start services in order (use separate terminal windows):**

   **Step 1: Start Discovery Service**
   ```bash
   cd discovery-service
   mvn spring-boot:run
   ```
   Wait for Eureka to start at http://localhost:8761

   **Step 2: Start Auth Service**
   ```bash
   cd auth-service
   mvn spring-boot:run
   ```

   **Step 3: Start Catalogue Service**
   ```bash
   cd catalogue-service
   mvn spring-boot:run
   ```

   **Step 4: Start Order Service**
   ```bash
   cd order-service
   mvn spring-boot:run
   ```

   **Step 5: Start Gateway Service**
   ```bash
   cd gateway-service
   mvn spring-boot:run
   ```

3. **Alternative: Use startup scripts**
   - **Windows PowerShell**: `.\start-services.bat`
   - **Linux/Mac**: `./start-services.sh`

4. **Verify services are running:**
   - Eureka Dashboard: http://localhost:8761
   - API Gateway: http://localhost:8080
   - All services should be registered in Eureka

### Frontend Setup

1. **Install Node.js and Angular CLI (if not installed)**
   ```bash
   npm install -g @angular/cli
   ```

2. **Navigate to frontend directory and install dependencies**
   ```bash
   cd frontend
   npm install
   ```

3. **Start the Angular application**
   ```bash
   npx ng serve
   ```
   Or if Angular CLI is installed globally:
   ```bash
   ng serve
   ```
   The application will be available at http://localhost:4200

### Alternative: Start All Services

You can also start all backend services from the root directory:
```bash
# Start all services (requires multiple terminals)
mvn spring-boot:run -pl discovery-service
mvn spring-boot:run -pl auth-service
mvn spring-boot:run -pl catalogue-service
mvn spring-boot:run -pl order-service
mvn spring-boot:run -pl gateway-service
```

## API Documentation

### Authentication Endpoints
- `POST /auth/register` - User registration
- `POST /auth/login` - User login

### Catalog Endpoints
- `GET /catalog/items` - Get all products
- `GET /catalog/items/{id}` - Get product details
- `GET /catalog/categories` - Get all categories

### Cart & Order Endpoints (Authenticated)
- `GET /cart` - Get user's cart
- `POST /cart/items` - Add item to cart
- `PUT /cart/items/{id}` - Update cart item quantity
- `DELETE /cart/items/{id}` - Remove item from cart
- `POST /order/checkout` - Place order
- `GET /order/history` - Get order history

### Admin Endpoints (Admin Role Required)
- `POST /admin/items` - Create product
- `PUT /admin/items/{id}` - Update product
- `GET /admin/orders` - Get all orders
- `GET /admin/kpis` - Get sales analytics

## Default Users

The system comes with pre-configured users:

### Admin User
- Email: `admin@ecommerce.com`
- Password: `admin123`
- Role: ADMIN

### Regular User
- Email: `user@ecommerce.com`
- Password: `user123`
- Role: USER

## Sample Data

The application includes sample data:
- 3 product categories (Electronics, Home Appliances, Fashion)
- Sample products with attributes and inventory
- Payment methods (UPI, COD, Card)
- GST tax rates by category

## Database Access

H2 Console is available for each service:
- Auth Service: http://localhost:8081/h2-console
- Catalogue Service: http://localhost:8082/h2-console
- Order Service: http://localhost:8083/h2-console

**Connection Details:**
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

## Testing

### Manual Testing Flow

1. **Start all services** as described above
2. **Access the application** at http://localhost:4200
3. **Register a new user** or login with default credentials
4. **Browse products** in the catalog
5. **Add items to cart** and proceed to checkout
6. **Place an order** using different payment methods
7. **View order history** and track orders
8. **Admin features** - login as admin to manage products and view analytics

### API Testing

You can test the APIs using tools like Postman or curl:

```bash
# Register a new user
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123","firstName":"Test","lastName":"User","phone":"9876543210"}'

# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

# Get products (no auth required)
curl http://localhost:8080/catalog/items

# Get cart (requires JWT token)
curl -H "Authorization: Bearer <your-jwt-token>" http://localhost:8080/cart
```

## Troubleshooting

### Common Issues

1. **Services not registering with Eureka**
   - Ensure Discovery Service is started first
   - Check if ports are available
   - Verify application.yml configurations

2. **CORS errors in frontend**
   - Ensure Gateway Service is running
   - Check CORS configuration in gateway-service/application.yml

3. **JWT authentication issues**
   - Verify JWT secret is consistent across services
   - Check token expiration settings

4. **Database connection issues**
   - H2 is in-memory, data resets on service restart
   - Check H2 console for data verification

### Port Conflicts

If you encounter port conflicts, you can change the ports in the respective `application.yml` files:
- Discovery Service: `server.port: 8761`
- Gateway Service: `server.port: 8080`
- Auth Service: `server.port: 8081`
- Catalogue Service: `server.port: 8082`
- Order Service: `server.port: 8083`

## Development

### Adding New Features

1. **Backend**: Add new endpoints in the appropriate service
2. **Frontend**: Create new components and services
3. **Database**: Update JPA entities and add migration scripts
4. **Security**: Configure access control in SecurityConfig classes

### Code Structure

```
ecommerce-platform/
├── discovery-service/          # Eureka Server
├── gateway-service/           # API Gateway
├── auth-service/             # Authentication Service
├── catalogue-service/        # Product Catalog Service
├── order-service/           # Order Management Service
├── frontend/               # Angular Application
└── pom.xml                # Parent POM
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.
