#!/bin/bash

echo "Starting E-Commerce Platform Services..."
echo

# Function to start a service in a new terminal
start_service() {
    local service_name=$1
    local service_dir=$2
    local port=$3
    
    echo "Starting $service_name..."
    
    # For macOS
    if [[ "$OSTYPE" == "darwin"* ]]; then
        osascript -e "tell application \"Terminal\" to do script \"cd $(pwd)/$service_dir && mvn spring-boot:run\""
    # For Linux with gnome-terminal
    elif command -v gnome-terminal &> /dev/null; then
        gnome-terminal --title="$service_name" -- bash -c "cd $service_dir && mvn spring-boot:run; exec bash"
    # For Linux with xterm
    elif command -v xterm &> /dev/null; then
        xterm -title "$service_name" -e "cd $service_dir && mvn spring-boot:run; bash" &
    # Fallback - run in background
    else
        echo "No suitable terminal found. Starting $service_name in background..."
        cd $service_dir && mvn spring-boot:run > ../logs/$service_name.log 2>&1 &
        cd ..
    fi
    
    sleep 5
}

# Create logs directory if it doesn't exist
mkdir -p logs

echo "Starting services in order..."
echo

# Start Discovery Service first
start_service "Discovery Service" "discovery-service" "8761"
echo "Waiting for Discovery Service to start..."
sleep 25

# Start other services
start_service "Auth Service" "auth-service" "8081"
sleep 10

start_service "Catalogue Service" "catalogue-service" "8082"
sleep 10

start_service "Order Service" "order-service" "8083"
sleep 10

start_service "Gateway Service" "gateway-service" "8080"
sleep 10

echo
echo "All services are starting up..."
echo
echo "Service URLs:"
echo "- Eureka Dashboard: http://localhost:8761"
echo "- API Gateway: http://localhost:8080"
echo "- Auth Service: http://localhost:8081"
echo "- Catalogue Service: http://localhost:8082"
echo "- Order Service: http://localhost:8083"
echo
echo "To start the Angular frontend:"
echo "cd frontend"
echo "npm install"
echo "ng serve"
echo
echo "Frontend will be available at: http://localhost:4200"
echo
echo "Press any key to continue..."
read -n 1
