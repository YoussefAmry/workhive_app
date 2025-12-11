# gRPC Communication Architecture Summary

## Overview
ANALYTICS-SERVICE acts as a **gRPC client** that aggregates statistics from 4 other microservices acting as **gRPC servers**.

## gRPC Servers

### 1. USER-SERVICE (Port 9091)
- **Proto File**: `user_service.proto`
- **Service**: `UserStatisticsService`
- **RPC Methods**:
  - `GetUserCount()` → Returns total and active user counts
  - `GetUserById()` → Returns user details by ID
- **Implementation**: `UserStatisticsServiceImpl.java`
- **Annotations**: `@GrpcService`

### 2. PROJECT-SERVICE (Port 9093)
- **Proto File**: `project_service.proto`
- **Service**: `ProjectStatisticsService`
- **RPC Methods**:
  - `GetProjectCount()` → Returns total and in-progress project counts
  - `GetCompletedProjects()` → Returns completed project count
- **Implementation**: `ProjectStatisticsServiceImpl.java`
- **Annotations**: `@GrpcService`

### 3. TEAM-SERVICE (Port 9092)
- **Proto File**: `team_service.proto`
- **Service**: `TeamStatisticsService`
- **RPC Methods**:
  - `GetTeamCount()` → Returns total team count
  - `GetAverageTeamSize()` → Returns average team size
- **Implementation**: `TeamStatisticsServiceImpl.java`
- **Annotations**: `@GrpcService`

### 4. LOGS-SERVICE (Port 9094)
- **Proto File**: `logs_service.proto`
- **Service**: `LogsStatisticsService`
- **RPC Methods**:
  - `GetActivityCount()` → Returns total and recent activity counts
- **Implementation**: `LogsStatisticsServiceImpl.java`
- **Annotations**: `@GrpcService`

## gRPC Client

### ANALYTICS-SERVICE (Port 8086)
- **Purpose**: Aggregate statistics from all services
- **gRPC Client Configuration**:
  ```properties
  # USER-SERVICE gRPC
  grpc.client.user-service.address=static://localhost:9091
  grpc.client.user-service.negotiationType=plaintext
  
  # PROJECT-SERVICE gRPC
  grpc.client.project-service.address=static://localhost:9093
  grpc.client.project-service.negotiationType=plaintext
  
  # TEAM-SERVICE gRPC
  grpc.client.team-service.address=static://localhost:9092
  grpc.client.team-service.negotiationType=plaintext
  
  # LOGS-SERVICE gRPC
  grpc.client.logs-service.address=static://localhost:9094
  grpc.client.logs-service.negotiationType=plaintext
  ```

- **Service Implementation**: `AnalyticsService.java`
- **Injected Stubs**:
  ```java
  @GrpcClient("user-service")
  private UserStatisticsServiceGrpc.UserStatisticsServiceBlockingStub userStub;
  
  @GrpcClient("project-service")
  private ProjectStatisticsServiceGrpc.ProjectStatisticsServiceBlockingStub projectStub;
  
  @GrpcClient("team-service")
  private TeamStatisticsServiceGrpc.TeamStatisticsServiceBlockingStub teamStub;
  
  @GrpcClient("logs-service")
  private LogsStatisticsServiceGrpc.LogsStatisticsServiceBlockingStub logsStub;
  ```

- **REST Controller**: `AnalyticsController.java`
  - `GET /api/analytics/dashboard` → Calls all 4 gRPC services
  - `GET /api/analytics/users` → Calls USER-SERVICE gRPC
  - `GET /api/analytics/projects` → Calls PROJECT-SERVICE gRPC
  - `GET /api/analytics/teams` → Calls TEAM-SERVICE gRPC

## Communication Flow

```
Client (Browser/Postman)
    ↓ HTTP REST
Gateway (Port 8080)
    ↓ Route: /api/analytics/**
ANALYTICS-SERVICE (Port 8086)
    ↓ gRPC (Port 9091)      ↓ gRPC (Port 9093)      ↓ gRPC (Port 9092)      ↓ gRPC (Port 9094)
USER-SERVICE            PROJECT-SERVICE          TEAM-SERVICE            LOGS-SERVICE
GetUserCount()          GetProjectCount()        GetTeamCount()          GetActivityCount()
GetUserById()           GetCompletedProjects()   GetAverageTeamSize()
```

## Technical Stack
- **gRPC Framework**: gRPC Spring Boot Starter 3.1.0.RELEASE
- **gRPC Version**: 1.60.0
- **Protocol Buffers**: 3.25.1
- **Maven Plugin**: protobuf-maven-plugin 0.6.1
- **Platform Detection**: os-maven-plugin 1.7.1

## Testing gRPC

### Via REST Endpoint (Easiest):
```powershell
# This triggers 4 gRPC calls internally
Invoke-RestMethod -Uri "http://localhost:8080/api/analytics/dashboard" -Method GET
```

### Direct gRPC Testing:
Use tools like:
- **grpcurl**: Command-line gRPC client
- **BloomRPC**: GUI for gRPC testing
- **Postman**: Supports gRPC (newer versions)

Example with grpcurl:
```bash
grpcurl -plaintext localhost:9091 user.UserStatisticsService/GetUserCount
grpcurl -plaintext localhost:9093 project.ProjectStatisticsService/GetProjectCount
grpcurl -plaintext localhost:9092 team.TeamStatisticsService/GetTeamCount
grpcurl -plaintext localhost:9094 logs.LogsStatisticsService/GetActivityCount
```

## Resilience Patterns
All gRPC calls in ANALYTICS-SERVICE use:
- **Circuit Breaker**: Fails fast if 50% of requests fail
- **Retry**: 3 attempts with 500ms delay
- **Fallback Methods**: Return empty data on failure

Configuration:
```properties
resilience4j.circuitbreaker.instances.analyticsService.slidingWindowSize=10
resilience4j.circuitbreaker.instances.analyticsService.failureRateThreshold=50
resilience4j.retry.instances.analyticsService.maxAttempts=3
resilience4j.retry.instances.analyticsService.waitDuration=500ms
```

## Build Verification
All services successfully built with gRPC support:
- ✅ USER-SERVICE: 32 source files compiled (including gRPC stubs)
- ✅ PROJECT-SERVICE: 40 source files compiled (including gRPC stubs)
- ✅ TEAM-SERVICE: 30 source files compiled (including gRPC stubs)
- ✅ Logs-service: 19 source files compiled (including gRPC stubs)
- ✅ ANALYTICS-SERVICE: 43 source files compiled (gRPC client stubs)

## Protocol Summary for Exam
1. **REST**: OpenFeign inter-service communication (USER ← TEAM ← PROJECT)
2. **GraphQL**: Logs-service with queries and mutations
3. **SOAP**: NOTIFICATION-SERVICE with WSDL/XSD
4. **gRPC**: ANALYTICS-SERVICE (client) aggregates from 4 services (servers)

All 4 communication protocols implemented and operational! ✅
