# WorkHive Microservices Architecture

Complete microservices architecture demonstrating **REST, GraphQL, SOAP, and gRPC** communication protocols for exam requirements.

## 🏗️ Architecture Overview

### Services (9 Total)

| Service | Port | gRPC Port | Protocol | Description |
|---------|------|-----------|----------|-------------|
| discoveryservice | 8761 | - | - | Eureka Server for service discovery |
| Config-Server | 8888 | - | - | Centralized configuration management |
| Gateway-service | 8080 | - | - | API Gateway (routes all external traffic) |
| USER-SERVICE | 8081 | 9091 | REST + gRPC | User management & authentication |
| TEAM-SERVICE | 8082 | 9092 | REST + gRPC | Team management |
| PROJECT-SERVICE | 8083 | 9093 | REST + gRPC | Project and task management |
| Logs-service | 8084 | 9094 | GraphQL + gRPC | Activity logging with GraphQL |
| NOTIFICATION-SERVICE | 8085 | - | SOAP | Notification system via SOAP |
| ANALYTICS-SERVICE | 8086 | - | gRPC Client | Statistics aggregation via gRPC |

## 📡 Communication Protocols

### 1. REST API
**Services**: USER-SERVICE, TEAM-SERVICE, PROJECT-SERVICE

- **Technology**: Spring Boot Web, OpenFeign
- **Resilience**: Circuit Breaker (Resilience4J), Retry patterns
- **Example**:
  ```bash
  # Create user
  POST http://localhost:8080/api/auth/register
  
  # Create team
  POST http://localhost:8080/api/teams
  
  # Create project
  POST http://localhost:8080/api/projects
  ```

### 2. GraphQL
**Service**: Logs-service

- **Technology**: Spring GraphQL
- **Endpoint**: `http://localhost:8080/graphql`
- **Playground**: `http://localhost:8080/graphiql`
- **Example**:
  ```graphql
  mutation {
    logActivity(userId: 1, action: "User logged in", entity: "USER") {
      id
      action
      timestamp
    }
  }
  
  query {
    activityLogs {
      id
      action
      timestamp
    }
  }
  ```

### 3. SOAP
**Service**: NOTIFICATION-SERVICE

- **Technology**: Spring Web Services, JAXB
- **Endpoint**: `http://localhost:8080/ws`
- **WSDL**: `http://localhost:8080/ws/notifications.wsdl`
- **Example**:
  ```xml
  <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                    xmlns:not="http://workhive.com/notification">
    <soapenv:Body>
      <not:sendNotificationRequest>
        <not:userId>1</not:userId>
        <not:message>Welcome!</not:message>
      </not:sendNotificationRequest>
    </soapenv:Body>
  </soapenv:Envelope>
  ```

### 4. gRPC
**Client**: ANALYTICS-SERVICE  
**Servers**: USER-SERVICE, PROJECT-SERVICE, TEAM-SERVICE, Logs-service

- **Technology**: gRPC Spring Boot Starter 3.1.0, Protocol Buffers 3.25.1
- **Architecture**: ANALYTICS-SERVICE calls 4 services via gRPC to aggregate statistics
- **Ports**:
  - USER-SERVICE: `localhost:9091`
  - PROJECT-SERVICE: `localhost:9093`
  - TEAM-SERVICE: `localhost:9092`
  - LOGS-SERVICE: `localhost:9094`
- **Example**:
  ```bash
  # Via REST endpoint (triggers gRPC calls internally)
  GET http://localhost:8080/api/analytics/dashboard
  ```

## 🚀 Quick Start

### Prerequisites
- Java 17
- Maven 3.8+
- PowerShell (Windows)

### Build All Services
```powershell
# From workspace root
mvn clean install -DskipTests
```

### Start Services
```powershell
# Automated startup (opens each service in new terminal)
.\start-all-services.ps1
```

**OR** Manual startup in order:
```powershell
# 1. Discovery Service
cd discoveryservice
mvn spring-boot:run

# 2. Config Server
cd Config-Server
mvn spring-boot:run

# 3. Gateway
cd Gateway-service
mvn spring-boot:run

# 4-9. All other services
cd USER-SERVICE
mvn spring-boot:run
# ... repeat for each service
```

### Test All Protocols
```powershell
# Wait 60 seconds for services to register, then:
.\test-all-protocols.ps1
```

## 🧪 Testing Endpoints

### REST Examples
```powershell
# Register user
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method POST -ContentType "application/json" -Body '{"username":"test","email":"test@example.com","password":"pass123","role":"DEVELOPER"}'

# Get users
Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Method GET
```

### GraphQL Examples
```powershell
$body = @{query="mutation { logActivity(userId: 1, action: `"test`", entity: `"USER`") { id } }"} | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/graphql" -Method POST -ContentType "application/json" -Body $body
```

### SOAP Examples
```powershell
$soap = @"
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:not="http://workhive.com/notification">
  <soapenv:Body>
    <not:sendNotificationRequest>
      <not:userId>1</not:userId>
      <not:message>Test notification</not:message>
    </not:sendNotificationRequest>
  </soapenv:Body>
</soapenv:Envelope>
"@
Invoke-WebRequest -Uri "http://localhost:8080/ws" -Method POST -ContentType "text/xml" -Body $soap
```

### gRPC Examples
```powershell
# Via REST (triggers gRPC internally)
Invoke-RestMethod -Uri "http://localhost:8080/api/analytics/dashboard" -Method GET
```

## 📊 gRPC Architecture Details

### Communication Flow
```
Client → Gateway → ANALYTICS-SERVICE (gRPC Client)
                        ↓ gRPC calls
    ┌──────────────────┼──────────────────┬──────────────────┐
    ↓                  ↓                  ↓                  ↓
USER-SERVICE      PROJECT-SERVICE    TEAM-SERVICE      LOGS-SERVICE
(Port 9091)       (Port 9093)        (Port 9092)       (Port 9094)
GetUserCount()    GetProjectCount()  GetTeamCount()    GetActivityCount()
GetUserById()     GetCompleted()     GetAvgSize()      
```

### Proto Files
All services have `.proto` files in `src/main/proto/`:
- **USER-SERVICE**: `user_service.proto` → UserStatisticsService
- **PROJECT-SERVICE**: `project_service.proto` → ProjectStatisticsService
- **TEAM-SERVICE**: `team_service.proto` → TeamStatisticsService
- **LOGS-SERVICE**: `logs_service.proto` → LogsStatisticsService

### gRPC Testing with grpcurl
```bash
# Install grpcurl first
grpcurl -plaintext localhost:9091 user.UserStatisticsService/GetUserCount
grpcurl -plaintext localhost:9093 project.ProjectStatisticsService/GetProjectCount
grpcurl -plaintext localhost:9092 team.TeamStatisticsService/GetTeamCount
grpcurl -plaintext localhost:9094 logs.LogsStatisticsService/GetActivityCount
```

## 🛠️ Technology Stack

### Core Framework
- Spring Boot 3.2.5
- Spring Cloud 2023.0.1
- Java 17

### Communication
- **REST**: Spring Web, OpenFeign
- **GraphQL**: Spring GraphQL
- **SOAP**: Spring Web Services, JAXB
- **gRPC**: gRPC Spring Boot Starter 3.1.0, gRPC 1.60.0, Protobuf 3.25.1

### Infrastructure
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Database**: H2 (in-memory)
- **Resilience**: Resilience4J (Circuit Breaker, Retry)
- **Security**: Spring Security, JWT

### Build Tools
- Maven
- Protobuf Maven Plugin 0.6.1
- OS Maven Plugin 1.7.1 (platform detection)

## 📁 Project Structure

```
WorkHiveApp/
├── discoveryservice/          # Eureka Server
├── Config-Server/            # Config Server
├── Gateway-service/          # API Gateway
├── USER-SERVICE/             # User management (REST + gRPC server)
│   ├── src/main/proto/      # user_service.proto
│   └── grpc/                # UserStatisticsServiceImpl
├── TEAM-SERVICE/             # Team management (REST + gRPC server)
│   ├── src/main/proto/      # team_service.proto
│   └── grpc/                # TeamStatisticsServiceImpl
├── PROJECT-SERVICE/          # Project management (REST + gRPC server)
│   ├── src/main/proto/      # project_service.proto
│   └── grpc/                # ProjectStatisticsServiceImpl
├── Logs-service/             # Activity logs (GraphQL + gRPC server)
│   ├── src/main/proto/      # logs_service.proto
│   └── grpc/                # LogsStatisticsServiceImpl
├── NOTIFICATION-SERVICE/     # Notifications (SOAP)
│   ├── xsd/                 # notifications.xsd
│   └── endpoint/            # NotificationEndpoint
├── ANALYTICS-SERVICE/        # Analytics (gRPC client)
│   ├── src/main/proto/      # Proto files for all services
│   └── service/             # AnalyticsService (gRPC client)
├── start-all-services.ps1   # Startup script
├── test-all-protocols.ps1   # Testing script
└── GRPC-ARCHITECTURE.md     # gRPC documentation
```

## 🔧 Configuration

### Gateway Routes (application.properties)
```properties
# REST routes
/api/users/**      → USER-SERVICE
/api/teams/**      → TEAM-SERVICE
/api/projects/**   → PROJECT-SERVICE

# GraphQL
/graphql           → Logs-service
/graphiql          → Logs-service

# SOAP
/ws/**             → NOTIFICATION-SERVICE

# gRPC client (REST endpoint)
/api/analytics/**  → ANALYTICS-SERVICE
```

### gRPC Server Ports
```properties
# USER-SERVICE
grpc.server.port=9091

# TEAM-SERVICE
grpc.server.port=9092

# PROJECT-SERVICE
grpc.server.port=9093

# LOGS-SERVICE
grpc.server.port=9094
```

### gRPC Client Configuration (ANALYTICS-SERVICE)
```properties
grpc.client.user-service.address=static://localhost:9091
grpc.client.project-service.address=static://localhost:9093
grpc.client.team-service.address=static://localhost:9092
grpc.client.logs-service.address=static://localhost:9094
# All use plaintext (no TLS)
```

## 📈 Monitoring & Health

### Eureka Dashboard
```
http://localhost:8761
```
View all registered services and their status.

### H2 Consoles
Each service has an H2 console:
```
http://localhost:8081/h2-console  (USER-SERVICE)
http://localhost:8082/h2-console  (TEAM-SERVICE)
http://localhost:8083/h2-console  (PROJECT-SERVICE)
http://localhost:8084/h2-console  (Logs-service)
http://localhost:8085/h2-console  (NOTIFICATION-SERVICE)
http://localhost:8086/h2-console  (ANALYTICS-SERVICE)
```
**Credentials**: username=`sa`, password=``

### Actuator Endpoints
```
http://localhost:808X/actuator/health
http://localhost:808X/actuator/metrics
http://localhost:808X/actuator/circuitbreakers
```

## 🎯 Exam Demonstration Checklist

### ✅ REST Communication
- [x] USER-SERVICE REST API
- [x] TEAM-SERVICE REST API
- [x] PROJECT-SERVICE REST API
- [x] OpenFeign inter-service calls
- [x] Circuit Breaker pattern
- [x] Retry mechanism

### ✅ GraphQL Communication
- [x] Logs-service GraphQL schema
- [x] GraphQL queries
- [x] GraphQL mutations
- [x] GraphiQL playground

### ✅ SOAP Communication
- [x] NOTIFICATION-SERVICE SOAP endpoints
- [x] WSDL definition
- [x] XSD schema
- [x] JAXB data binding

### ✅ gRPC Communication
- [x] ANALYTICS-SERVICE gRPC client
- [x] USER-SERVICE gRPC server
- [x] PROJECT-SERVICE gRPC server
- [x] TEAM-SERVICE gRPC server
- [x] LOGS-SERVICE gRPC server
- [x] Protocol Buffers definitions
- [x] Bidirectional communication

## 🐛 Troubleshooting

### Services not registering with Eureka
- Wait 30-60 seconds after startup
- Check Eureka dashboard: `http://localhost:8761`
- Verify `eureka.client.service-url.defaultZone` in each service

### gRPC connection refused
- Ensure gRPC servers are running on correct ports (9091-9094)
- Check firewall settings
- Verify `grpc.server.port` in application.properties

### Gateway 503 errors
- Services must be registered with Eureka first
- Check service health: `/actuator/health`
- Verify Gateway routes in application.properties

### Build failures
- Clean Maven cache: `mvn clean`
- Update dependencies: `mvn dependency:resolve`
- Ensure Java 17 is used: `java -version`

## 📚 Additional Resources

- [gRPC Architecture Details](GRPC-ARCHITECTURE.md)
- [SOAP Testing Script](test-soap.ps1)
- [Full Protocol Testing](test-all-protocols.ps1)

## 👨‍💻 Development

### Adding a New Service
1. Create Spring Boot project
2. Add Eureka client dependency
3. Configure `application.properties`
4. Add Gateway route
5. Register with discovery service

### Adding gRPC Support
1. Add gRPC dependencies to `pom.xml`
2. Create `.proto` file in `src/main/proto/`
3. Add protobuf-maven-plugin
4. Implement service with `@GrpcService`
5. Configure gRPC port

## 📄 License
Educational project for exam demonstration.

---

**Note**: All services use H2 in-memory databases. Data is lost on restart.
