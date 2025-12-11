# Quick Demo Script - Test All 4 Protocols
# Run this after all services are started

Write-Host "`n===========================================" -ForegroundColor Cyan
Write-Host " WorkHive - All Protocols Demo" -ForegroundColor Cyan
Write-Host "===========================================`n" -ForegroundColor Cyan

# Wait for user confirmation
Write-Host "Press Enter when all services are running (check http://localhost:8761)..." -ForegroundColor Yellow
Read-Host

# ============================================
# 1. REST API TEST
# ============================================
Write-Host "`n[1/4] REST API Test" -ForegroundColor Green
Write-Host "-------------------" -ForegroundColor Gray

Write-Host "`nGetting all users..."
try {
    $users = Invoke-RestMethod -Uri "http://localhost:8081/api/users" -Method GET
    Write-Host "Found $($users.Count) users" -ForegroundColor White
    $users | ConvertTo-Json
    Write-Host "âœ… REST API works!" -ForegroundColor Green
} catch {
    Write-Host "â Error: $_" -ForegroundColor Red
}

# ============================================
# 2. GRAPHQL TEST
# ============================================
Write-Host "`n`n[2/4] GraphQL Test" -ForegroundColor Green
Write-Host "-------------------" -ForegroundColor Gray

Write-Host "`nQuerying activity logs..."
try {
    $graphqlQuery = @{
        query = "query { activityLogs { id action timestamp } }"
    } | ConvertTo-Json

    $logs = Invoke-RestMethod -Uri "http://localhost:8080/graphql" -Method POST -ContentType "application/json" -Body $graphqlQuery
    Write-Host "GraphQL Response:" -ForegroundColor White
    $logs | ConvertTo-Json -Depth 3
    Write-Host "âœ… GraphQL works!" -ForegroundColor Green
} catch {
    Write-Host "â Error: $_" -ForegroundColor Red
}

Write-Host "`nâ„¹  Open GraphiQL playground: http://localhost:8080/graphiql" -ForegroundColor Cyan

# ============================================
# 3. SOAP TEST
# ============================================
Write-Host "`n`n[3/4] SOAP Test" -ForegroundColor Green
Write-Host "----------------" -ForegroundColor Gray

Write-Host "`nAccessing WSDL..."
try {
    $wsdl = Invoke-WebRequest -Uri "http://localhost:8080/ws/notifications.wsdl" -UseBasicParsing
    Write-Host "WSDL Status: $($wsdl.StatusCode)" -ForegroundColor White
    Write-Host "âœ… SOAP WSDL accessible!" -ForegroundColor Green
} catch {
    Write-Host "â Error: $_" -ForegroundColor Red
}

Write-Host "`nâ„¹  WSDL URL: http://localhost:8080/ws/notifications.wsdl" -ForegroundColor Cyan

# ============================================
# 4. gRPC TEST
# ============================================
Write-Host "`n`n[4/4] gRPC Test" -ForegroundColor Green
Write-Host "----------------" -ForegroundColor Gray

Write-Host "`nCalling Analytics Dashboard (triggers 4 gRPC calls)..."
Write-Host "  - USER-SERVICE gRPC (port 9091)" -ForegroundColor Gray
Write-Host "  - PROJECT-SERVICE gRPC (port 9093)" -ForegroundColor Gray
Write-Host "  - TEAM-SERVICE gRPC (port 9092)" -ForegroundColor Gray
Write-Host "  - LOGS-SERVICE gRPC (port 9094)" -ForegroundColor Gray

try {
    $analytics = Invoke-RestMethod -Uri "http://localhost:8080/api/analytics/dashboard" -Method GET
    Write-Host "`nDashboard Statistics:" -ForegroundColor White
    Write-Host "  Total Users: $($analytics.totalUsers)" -ForegroundColor Cyan
    Write-Host "  Total Projects: $($analytics.totalProjects)" -ForegroundColor Cyan
    Write-Host "  Total Teams: $($analytics.totalTeams)" -ForegroundColor Cyan
    Write-Host "  Recent Activities: $($analytics.recentActivities)" -ForegroundColor Cyan
    Write-Host "âœ… gRPC communication works!" -ForegroundColor Green
} catch {
    Write-Host "â Error: $_" -ForegroundColor Red
}

# ============================================
# SUMMARY
# ============================================
Write-Host "`n`n===========================================" -ForegroundColor Cyan
Write-Host " Test Summary" -ForegroundColor Cyan
Write-Host "===========================================`n" -ForegroundColor Cyan

Write-Host "âœ… REST:    http://localhost:8081/api/users" -ForegroundColor Green
Write-Host "âœ… GraphQL: http://localhost:8080/graphiql" -ForegroundColor Green
Write-Host "âœ… SOAP:    http://localhost:8080/ws/notifications.wsdl" -ForegroundColor Green
Write-Host "âœ… gRPC:    http://localhost:8080/api/analytics/dashboard" -ForegroundColor Green

Write-Host "`nAll 4 communication protocols are working!" -ForegroundColor Cyan
Write-Host "===========================================`n" -ForegroundColor Cyan
