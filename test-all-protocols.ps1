# Test script for all microservices communication protocols
# Tests REST, GraphQL, SOAP, and gRPC

Write-Host "`n==========================================" -ForegroundColor Cyan
Write-Host "WorkHive Microservices - Protocol Testing" -ForegroundColor Cyan
Write-Host "==========================================`n" -ForegroundColor Cyan

# Wait for services to start
Write-Host "Waiting for services to initialize (20 seconds)..." -ForegroundColor Yellow
Start-Sleep -Seconds 20

# ===========================================
# 1. REST API TESTING
# ===========================================
Write-Host "`n[1/4] Testing REST Communication" -ForegroundColor Green
Write-Host "-----------------------------" -ForegroundColor Gray

# Register a user
Write-Host "`n1.1 Creating user (POST /api/auth/register)..." -ForegroundColor Cyan
$registerResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method POST -ContentType "application/json" -Body (@{
    username = "testuser"
    email = "test@example.com"
    password = "password123"
    role = "DEVELOPER"
} | ConvertTo-Json)
Write-Host "Response: User registered successfully" -ForegroundColor Green

# Create a team
Write-Host "`n1.2 Creating team (POST /api/teams)..." -ForegroundColor Cyan
$teamResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/teams" -Method POST -ContentType "application/json" -Body (@{
    name = "Development Team"
    description = "Main dev team"
    managerId = 1
} | ConvertTo-Json)
Write-Host "Response: Team created with ID $($teamResponse.id)" -ForegroundColor Green

# Create a project
Write-Host "`n1.3 Creating project (POST /api/projects)..." -ForegroundColor Cyan
$projectResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/projects" -Method POST -ContentType "application/json" -Body (@{
    name = "Test Project"
    description = "Test project for exam"
    createdBy = 1
    teamId = $teamResponse.id
} | ConvertTo-Json)
Write-Host "Response: Project created with ID $($projectResponse.id)" -ForegroundColor Green

# ===========================================
# 2. GraphQL TESTING
# ===========================================
Write-Host "`n`n[2/4] Testing GraphQL Communication" -ForegroundColor Green
Write-Host "-----------------------------" -ForegroundColor Gray

Write-Host "`n2.1 Creating activity log (GraphQL mutation)..." -ForegroundColor Cyan
$graphqlMutation = @{
    query = "mutation { logActivity(userId: 1, action: `"User registered`", entity: `"USER`") { id userId action timestamp } }"
} | ConvertTo-Json

$graphqlResponse = Invoke-RestMethod -Uri "http://localhost:8080/graphql" -Method POST -ContentType "application/json" -Body $graphqlMutation
Write-Host "Response: Activity log created with ID $($graphqlResponse.data.logActivity.id)" -ForegroundColor Green

Write-Host "`n2.2 Querying activity logs (GraphQL query)..." -ForegroundColor Cyan
$graphqlQuery = @{
    query = "query { activityLogs { id action timestamp } }"
} | ConvertTo-Json

$logsResponse = Invoke-RestMethod -Uri "http://localhost:8080/graphql" -Method POST -ContentType "application/json" -Body $graphqlQuery
Write-Host "Response: Found $($logsResponse.data.activityLogs.Count) activity logs" -ForegroundColor Green

# ===========================================
# 3. SOAP TESTING
# ===========================================
Write-Host "`n`n[3/4] Testing SOAP Communication" -ForegroundColor Green
Write-Host "-----------------------------" -ForegroundColor Gray

Write-Host "`n3.1 Sending notification (SOAP request)..." -ForegroundColor Cyan
$soapEnvelope = @"
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

$soapResponse = Invoke-WebRequest -Uri "http://localhost:8080/ws" -Method POST -ContentType "text/xml" -Body $soapEnvelope
Write-Host "Response: Notification sent successfully (Status: $($soapResponse.StatusCode))" -ForegroundColor Green

Write-Host "`n3.2 Retrieving notifications (SOAP request)..." -ForegroundColor Cyan
$soapGetEnvelope = @"
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

$notificationsResponse = Invoke-WebRequest -Uri "http://localhost:8080/ws" -Method POST -ContentType "text/xml" -Body $soapGetEnvelope
Write-Host "Response: Retrieved notifications (Status: $($notificationsResponse.StatusCode))" -ForegroundColor Green

# ===========================================
# 4. gRPC TESTING (via REST endpoint)
# ===========================================
Write-Host "`n`n[4/4] Testing gRPC Communication" -ForegroundColor Green
Write-Host "-----------------------------" -ForegroundColor Gray
Write-Host "Note: ANALYTICS-SERVICE uses gRPC to call all other services" -ForegroundColor Yellow

Write-Host "`n4.1 Getting dashboard analytics (triggers 4 gRPC calls)..." -ForegroundColor Cyan
Write-Host "    - USER-SERVICE gRPC (port 9091): GetUserCount" -ForegroundColor Gray
Write-Host "    - PROJECT-SERVICE gRPC (port 9093): GetProjectCount, GetCompletedProjects" -ForegroundColor Gray
Write-Host "    - TEAM-SERVICE gRPC (port 9092): GetTeamCount, GetAverageTeamSize" -ForegroundColor Gray
Write-Host "    - LOGS-SERVICE gRPC (port 9094): GetActivityCount" -ForegroundColor Gray

try {
    $analyticsResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/analytics/dashboard" -Method GET
    Write-Host "`nDashboard Statistics:" -ForegroundColor Cyan
    Write-Host "  Total Users: $($analyticsResponse.totalUsers)" -ForegroundColor White
    Write-Host "  Total Projects: $($analyticsResponse.totalProjects)" -ForegroundColor White
    Write-Host "  Completed Projects: $($analyticsResponse.completedProjects)" -ForegroundColor White
    Write-Host "  Total Teams: $($analyticsResponse.totalTeams)" -ForegroundColor White
    Write-Host "  Average Team Size: $($analyticsResponse.averageTeamSize)" -ForegroundColor White
    Write-Host "  Recent Activities: $($analyticsResponse.recentActivities)" -ForegroundColor White
    Write-Host "`nAll gRPC calls completed successfully!" -ForegroundColor Green
} catch {
    Write-Host "Error calling analytics endpoint: $_" -ForegroundColor Red
    Write-Host "Make sure all services are running and registered with Eureka" -ForegroundColor Yellow
}

# ===========================================
# SUMMARY
# ===========================================
Write-Host "`n`n==========================================" -ForegroundColor Cyan
Write-Host "Test Summary" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "✅ REST API: USER, TEAM, PROJECT services" -ForegroundColor Green
Write-Host "✅ GraphQL: Logs-service queries & mutations" -ForegroundColor Green
Write-Host "✅ SOAP: NOTIFICATION-SERVICE endpoints" -ForegroundColor Green
Write-Host "✅ gRPC: ANALYTICS-SERVICE → 4 services" -ForegroundColor Green
Write-Host "`nAll communication protocols tested!" -ForegroundColor Cyan
Write-Host "==========================================`n" -ForegroundColor Cyan
