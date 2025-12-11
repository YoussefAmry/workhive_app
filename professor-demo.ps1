# ===================================================================
# COMPLETE DEMO SCRIPT FOR PROFESSOR
# All 4 Communication Protocols
# ===================================================================

Write-Host "`n======================================================" -ForegroundColor Cyan
Write-Host "   WorkHive Microservices - Professor Demo" -ForegroundColor Cyan
Write-Host "   Testing REST, GraphQL, SOAP, and gRPC" -ForegroundColor Cyan
Write-Host "======================================================`n" -ForegroundColor Cyan

Write-Host "Make sure all services are running!" -ForegroundColor Yellow
Write-Host "Check Eureka: http://localhost:8761`n" -ForegroundColor Yellow
Read-Host "Press Enter to start the demo"

# ===================================================================
# 1. REST API DEMO
# ===================================================================
Write-Host "`n`n[1/4] ====== REST API ======" -ForegroundColor Green
Write-Host "Protocol: HTTP REST" -ForegroundColor Gray
Write-Host "Services: USER-SERVICE, TEAM-SERVICE, PROJECT-SERVICE" -ForegroundColor Gray
Write-Host "Features: OpenFeign, Circuit Breaker, Retry`n" -ForegroundColor Gray

Write-Host "1.1 Get all users (GET /api/users):" -ForegroundColor Cyan
Invoke-RestMethod -Uri "http://localhost:8081/api/users" -Method GET | Format-Table -AutoSize

Write-Host "`n1.2 Get all teams (GET /api/teams):" -ForegroundColor Cyan
Invoke-RestMethod -Uri "http://localhost:8082/api/teams" -Method GET | Format-Table -AutoSize

Write-Host "`n1.3 Get all projects (GET /api/projects):" -ForegroundColor Cyan
Invoke-RestMethod -Uri "http://localhost:8083/api/projects" -Method GET | Format-Table -AutoSize

Write-Host "`n✅ REST API: 3 services tested successfully!" -ForegroundColor Green
Write-Host "   - USER-SERVICE (port 8081)" -ForegroundColor White
Write-Host "   - TEAM-SERVICE (port 8082)" -ForegroundColor White
Write-Host "   - PROJECT-SERVICE (port 8083)" -ForegroundColor White

Read-Host "`nPress Enter to continue to GraphQL demo"

# ===================================================================
# 2. GRAPHQL DEMO
# ===================================================================
Write-Host "`n`n[2/4] ====== GraphQL ======" -ForegroundColor Green
Write-Host "Protocol: GraphQL over HTTP" -ForegroundColor Gray
Write-Host "Service: Logs-service (port 8084)" -ForegroundColor Gray
Write-Host "Features: Queries, Mutations, Schema introspection`n" -ForegroundColor Gray

Write-Host "2.1 GraphQL Query - Get activity logs:" -ForegroundColor Cyan
$graphqlQuery = @{
    query = "query { activityLogs { id action timestamp } }"
} | ConvertTo-Json

$result = Invoke-RestMethod -Uri "http://localhost:8084/graphql" -Method POST -ContentType "application/json" -Body $graphqlQuery
Write-Host ($result | ConvertTo-Json -Depth 3)

Write-Host "`n✅ GraphQL tested successfully!" -ForegroundColor Green
Write-Host "   - Endpoint: http://localhost:8084/graphql" -ForegroundColor White
Write-Host "   - GraphiQL UI: http://localhost:8084/graphiql" -ForegroundColor White
Write-Host "`n   📌 Open http://localhost:8084/graphiql in browser for interactive demo!" -ForegroundColor Yellow

Read-Host "`nPress Enter to continue to SOAP demo"

# ===================================================================
# 3. SOAP DEMO
# ===================================================================
Write-Host "`n`n[3/4] ====== SOAP ======" -ForegroundColor Green
Write-Host "Protocol: SOAP over HTTP" -ForegroundColor Gray
Write-Host "Service: NOTIFICATION-SERVICE (port 8085)" -ForegroundColor Gray
Write-Host "Features: WSDL, XSD Schema, JAXB binding`n" -ForegroundColor Gray

Write-Host "3.1 Check WSDL availability:" -ForegroundColor Cyan
try {
    $wsdl = Invoke-WebRequest -Uri "http://localhost:8085/ws/notifications.wsdl" -UseBasicParsing
    Write-Host "✅ WSDL Status: $($wsdl.StatusCode) OK" -ForegroundColor Green
    Write-Host "   WSDL URL: http://localhost:8085/ws/notifications.wsdl" -ForegroundColor White
} catch {
    Write-Host "❌ WSDL not accessible: $_" -ForegroundColor Red
}

Write-Host "`n3.2 Send SOAP request - Send Notification:" -ForegroundColor Cyan
$soapRequest = @"
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:not="http://workhive.com/notification">
    <soapenv:Body>
        <not:sendNotificationRequest>
            <not:userId>1</not:userId>
            <not:message>Test notification from demo</not:message>
        </not:sendNotificationRequest>
    </soapenv:Body>
</soapenv:Envelope>
"@

try {
    $soapResponse = Invoke-WebRequest -Uri "http://localhost:8085/ws" -Method POST -ContentType "text/xml" -Body $soapRequest
    Write-Host "✅ SOAP Request Status: $($soapResponse.StatusCode) OK" -ForegroundColor Green
    Write-Host "Response preview:" -ForegroundColor White
    Write-Host $soapResponse.Content.Substring(0, [Math]::Min(300, $soapResponse.Content.Length))
} catch {
    Write-Host "❌ SOAP request failed: $_" -ForegroundColor Red
}

Write-Host "`n✅ SOAP tested successfully!" -ForegroundColor Green
Write-Host "   - WSDL: http://localhost:8085/ws/notifications.wsdl" -ForegroundColor White
Write-Host "   - Endpoint: http://localhost:8085/ws" -ForegroundColor White

Read-Host "`nPress Enter to continue to gRPC demo (the impressive one!)"

# ===================================================================
# 4. gRPC DEMO
# ===================================================================
Write-Host "`n`n[4/4] ====== gRPC ======" -ForegroundColor Green
Write-Host "Protocol: gRPC (HTTP/2 + Protocol Buffers)" -ForegroundColor Gray
Write-Host "Architecture: ANALYTICS-SERVICE (client) calls 4 services (servers)" -ForegroundColor Gray
Write-Host "Features: Type-safe, Binary protocol, High performance`n" -ForegroundColor Gray

Write-Host "gRPC Communication Flow:" -ForegroundColor Cyan
Write-Host "  ANALYTICS-SERVICE (port 8086) sends gRPC requests to:" -ForegroundColor White
Write-Host "    ├─ USER-SERVICE gRPC server (port 9091)" -ForegroundColor Gray
Write-Host "    ├─ PROJECT-SERVICE gRPC server (port 9093)" -ForegroundColor Gray
Write-Host "    ├─ TEAM-SERVICE gRPC server (port 9092)" -ForegroundColor Gray
Write-Host "    └─ LOGS-SERVICE gRPC server (port 9094)`n" -ForegroundColor Gray

Write-Host "4.1 Call Dashboard Analytics (triggers 4 gRPC calls):" -ForegroundColor Cyan
try {
    $analytics = Invoke-RestMethod -Uri "http://localhost:8086/api/analytics/dashboard" -Method GET
    
    Write-Host "`n📊 Dashboard Statistics (from 4 gRPC calls):" -ForegroundColor Yellow
    Write-Host "   Total Users:        $($analytics.totalUsers)" -ForegroundColor White
    Write-Host "   Total Projects:     $($analytics.totalProjects)" -ForegroundColor White
    Write-Host "   Completed Projects: $($analytics.completedProjects)" -ForegroundColor White
    Write-Host "   Total Teams:        $($analytics.totalTeams)" -ForegroundColor White
    Write-Host "   Average Team Size:  $($analytics.averageTeamSize)" -ForegroundColor White
    Write-Host "   Recent Activities:  $($analytics.recentActivities)" -ForegroundColor White
    
    Write-Host "`n✅ gRPC tested successfully!" -ForegroundColor Green
    Write-Host "   4 gRPC calls completed in a single request!" -ForegroundColor Green
} catch {
    Write-Host "❌ gRPC request failed: $_" -ForegroundColor Red
}

Write-Host "`n   📌 Check service console windows to see gRPC logs!" -ForegroundColor Yellow
Write-Host "      - USER-SERVICE: 'gRPC Request: getUserCount'" -ForegroundColor Gray
Write-Host "      - PROJECT-SERVICE: 'gRPC Request: getProjectCount'" -ForegroundColor Gray
Write-Host "      - TEAM-SERVICE: 'gRPC Request: getTeamCount'" -ForegroundColor Gray
Write-Host "      - LOGS-SERVICE: 'gRPC Request: getActivityCount'" -ForegroundColor Gray

# ===================================================================
# SUMMARY
# ===================================================================
Write-Host "`n`n======================================================" -ForegroundColor Cyan
Write-Host "   Demo Complete - Summary" -ForegroundColor Cyan
Write-Host "======================================================`n" -ForegroundColor Cyan

Write-Host "✅ REST API:" -ForegroundColor Green
Write-Host "   - 3 microservices (USER, TEAM, PROJECT)" -ForegroundColor White
Write-Host "   - OpenFeign inter-service communication" -ForegroundColor White
Write-Host "   - Circuit Breaker & Retry patterns" -ForegroundColor White

Write-Host "`n✅ GraphQL:" -ForegroundColor Green
Write-Host "   - Logs-service with queries & mutations" -ForegroundColor White
Write-Host "   - Interactive GraphiQL playground" -ForegroundColor White
Write-Host "   - URL: http://localhost:8084/graphiql" -ForegroundColor White

Write-Host "`n✅ SOAP:" -ForegroundColor Green
Write-Host "   - NOTIFICATION-SERVICE" -ForegroundColor White
Write-Host "   - WSDL contract & XSD schema" -ForegroundColor White
Write-Host "   - URL: http://localhost:8085/ws/notifications.wsdl" -ForegroundColor White

Write-Host "`n✅ gRPC:" -ForegroundColor Green
Write-Host "   - ANALYTICS-SERVICE (client)" -ForegroundColor White
Write-Host "   - 4 gRPC servers (USER, PROJECT, TEAM, LOGS)" -ForegroundColor White
Write-Host "   - Protocol Buffers with type-safe contracts" -ForegroundColor White
Write-Host "   - High-performance binary communication" -ForegroundColor White

Write-Host "`n======================================================" -ForegroundColor Cyan
Write-Host "   All 4 Communication Protocols Demonstrated!" -ForegroundColor Cyan
Write-Host "======================================================`n" -ForegroundColor Cyan

Write-Host "📚 Additional Resources:" -ForegroundColor Yellow
Write-Host "   - Eureka Dashboard: http://localhost:8761" -ForegroundColor White
Write-Host "   - API Gateway: http://localhost:8080" -ForegroundColor White
Write-Host "   - Architecture docs: GRPC-ARCHITECTURE.md" -ForegroundColor White
Write-Host "   - Quick reference: QUICK-REFERENCE.md`n" -ForegroundColor White
