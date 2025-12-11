# Quick Reference - Communication Protocols

## 🚀 Start Everything
```powershell
.\start-all-services.ps1
```
Wait 60 seconds for services to register with Eureka.

## 🧪 Test Everything
```powershell
.\test-all-protocols.ps1
```

## 📡 Protocol Examples

### 1️⃣ REST API

#### Register User
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method POST -ContentType "application/json" -Body (@{
    username = "john"
    email = "john@example.com"
    password = "password123"
    role = "DEVELOPER"
} | ConvertTo-Json)
```

#### Create Team
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/teams" -Method POST -ContentType "application/json" -Body (@{
    name = "Team A"
    description = "Development team"
    managerId = 1
} | ConvertTo-Json)
```

#### Create Project
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/projects" -Method POST -ContentType "application/json" -Body (@{
    name = "Project X"
    description = "New project"
    createdBy = 1
    teamId = 1
} | ConvertTo-Json)
```

---

### 2️⃣ GraphQL

#### Open GraphiQL Playground
```
http://localhost:8080/graphiql
```

#### Create Activity Log (Mutation)
```graphql
mutation {
  logActivity(
    userId: 1
    action: "User created project"
    entity: "PROJECT"
  ) {
    id
    userId
    action
    timestamp
  }
}
```

#### Query Activity Logs
```graphql
query {
  activityLogs {
    id
    userId
    action
    entity
    timestamp
  }
}
```

#### Query with Username Enrichment
```graphql
query {
  activityLogs {
    id
    action
    userName
    timestamp
  }
}
```

---

### 3️⃣ SOAP

#### View WSDL
```
http://localhost:8080/ws/notifications.wsdl
```

#### Send Notification
```powershell
$soap = @"
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:not="http://workhive.com/notification">
    <soapenv:Header/>
    <soapenv:Body>
        <not:sendNotificationRequest>
            <not:userId>1</not:userId>
            <not:message>Welcome to WorkHive!</not:message>
        </not:sendNotificationRequest>
    </soapenv:Body>
</soapenv:Envelope>
"@

Invoke-WebRequest -Uri "http://localhost:8080/ws" -Method POST -ContentType "text/xml" -Body $soap
```

#### Get Notifications
```powershell
$soap = @"
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:not="http://workhive.com/notification">
    <soapenv:Header/>
    <soapenv:Body>
        <not:getNotificationsRequest>
            <not:userId>1</not:userId>
        </not:getNotificationsRequest>
    </soapenv:Body>
</soapenv:Envelope>
"@

Invoke-WebRequest -Uri "http://localhost:8080/ws" -Method POST -ContentType "text/xml" -Body $soap
```

#### Mark as Read
```powershell
$soap = @"
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:not="http://workhive.com/notification">
    <soapenv:Header/>
    <soapenv:Body>
        <not:markAsReadRequest>
            <not:notificationId>1</not:notificationId>
        </not:markAsReadRequest>
    </soapenv:Body>
</soapenv:Envelope>
"@

Invoke-WebRequest -Uri "http://localhost:8080/ws" -Method POST -ContentType "text/xml" -Body $soap
```

---

### 4️⃣ gRPC

#### Dashboard Analytics (Triggers all gRPC calls)
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/analytics/dashboard" -Method GET
```

**This internally makes gRPC calls to:**
- USER-SERVICE (port 9091): GetUserCount()
- PROJECT-SERVICE (port 9093): GetProjectCount(), GetCompletedProjects()
- TEAM-SERVICE (port 9092): GetTeamCount(), GetAverageTeamSize()
- LOGS-SERVICE (port 9094): GetActivityCount()

#### Individual Analytics
```powershell
# User analytics (gRPC to USER-SERVICE)
Invoke-RestMethod -Uri "http://localhost:8080/api/analytics/users" -Method GET

# Project analytics (gRPC to PROJECT-SERVICE)
Invoke-RestMethod -Uri "http://localhost:8080/api/analytics/projects" -Method GET

# Team analytics (gRPC to TEAM-SERVICE)
Invoke-RestMethod -Uri "http://localhost:8080/api/analytics/teams" -Method GET
```

#### Direct gRPC Testing with grpcurl
```bash
# Install grpcurl first: https://github.com/fullstorydev/grpcurl

# USER-SERVICE
grpcurl -plaintext localhost:9091 user.UserStatisticsService/GetUserCount

# PROJECT-SERVICE
grpcurl -plaintext localhost:9093 project.ProjectStatisticsService/GetProjectCount
grpcurl -plaintext localhost:9093 project.ProjectStatisticsService/GetCompletedProjects

# TEAM-SERVICE
grpcurl -plaintext localhost:9092 team.TeamStatisticsService/GetTeamCount
grpcurl -plaintext localhost:9092 team.TeamStatisticsService/GetAverageTeamSize

# LOGS-SERVICE
grpcurl -plaintext localhost:9094 logs.LogsStatisticsService/GetActivityCount
```

---

## 📊 Service Ports

| Service | HTTP | gRPC | Protocol |
|---------|------|------|----------|
| Gateway | 8080 | - | Router |
| Eureka | 8761 | - | Discovery |
| USER-SERVICE | 8081 | 9091 | REST + gRPC |
| TEAM-SERVICE | 8082 | 9092 | REST + gRPC |
| PROJECT-SERVICE | 8083 | 9093 | REST + gRPC |
| Logs-service | 8084 | 9094 | GraphQL + gRPC |
| NOTIFICATION-SERVICE | 8085 | - | SOAP |
| ANALYTICS-SERVICE | 8086 | - | gRPC Client |

---

## 🔗 Important URLs

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **GraphiQL**: http://localhost:8080/graphiql
- **SOAP WSDL**: http://localhost:8080/ws/notifications.wsdl
- **H2 Console**: http://localhost:808X/h2-console (X = 1-6)

---

## 🎯 Demo Flow for Exam

### Step 1: Start Services
```powershell
.\start-all-services.ps1
```
Wait 60 seconds.

### Step 2: Check Eureka
Open `http://localhost:8761` - all 8 services should be registered.

### Step 3: REST Demo
```powershell
# Create user
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method POST -ContentType "application/json" -Body '{"username":"demo","email":"demo@test.com","password":"pass","role":"DEVELOPER"}' | ConvertTo-Json
```

### Step 4: GraphQL Demo
Open `http://localhost:8080/graphiql` and run:
```graphql
mutation {
  logActivity(userId: 1, action: "Demo test", entity: "USER") {
    id
    action
  }
}
```

### Step 5: SOAP Demo
```powershell
$soap = '<?xml version="1.0"?><soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:not="http://workhive.com/notification"><soapenv:Body><not:sendNotificationRequest><not:userId>1</not:userId><not:message>Test</not:message></not:sendNotificationRequest></soapenv:Body></soapenv:Envelope>'
Invoke-WebRequest -Uri "http://localhost:8080/ws" -Method POST -ContentType "text/xml" -Body $soap
```

### Step 6: gRPC Demo
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/analytics/dashboard" -Method GET | ConvertTo-Json
```

### Step 7: Run Full Test Suite
```powershell
.\test-all-protocols.ps1
```

---

## ✅ All 4 Protocols Implemented!

1. ✅ **REST**: USER, TEAM, PROJECT services with OpenFeign
2. ✅ **GraphQL**: Logs-service with queries & mutations
3. ✅ **SOAP**: NOTIFICATION-SERVICE with WSDL
4. ✅ **gRPC**: ANALYTICS-SERVICE aggregates from 4 servers

---

**Ready for exam! 🎓**
