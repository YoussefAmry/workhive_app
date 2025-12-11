# WorkHive Microservices Startup Script
# Starts all services in the correct order

Write-Host "`n==========================================" -ForegroundColor Cyan
Write-Host "WorkHive Microservices - Startup" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

$services = @(
    @{Name="discoveryservice"; Port=8761; Path="discoveryservice"},
    @{Name="Config-Server"; Port=8888; Path="Config-Server"},
    @{Name="Gateway-service"; Port=8080; Path="Gateway-service"},
    @{Name="USER-SERVICE"; Port=8081; GrpcPort=9091; Path="USER-SERVICE"},
    @{Name="TEAM-SERVICE"; Port=8082; GrpcPort=9092; Path="TEAM-SERVICE"},
    @{Name="PROJECT-SERVICE"; Port=8083; GrpcPort=9093; Path="PROJECT-SERVICE"},
    @{Name="Logs-service"; Port=8084; GrpcPort=9094; Path="Logs-service"},
    @{Name="NOTIFICATION-SERVICE"; Port=8085; Path="NOTIFICATION-SERVICE"},
    @{Name="ANALYTICS-SERVICE"; Port=8086; Path="ANALYTICS-SERVICE"}
)

Write-Host "`nStarting services..." -ForegroundColor Yellow
Write-Host "Note: Each service will open in a new terminal window`n" -ForegroundColor Gray

foreach ($service in $services) {
    $portInfo = "Port: $($service.Port)"
    if ($service.GrpcPort) {
        $portInfo += " | gRPC: $($service.GrpcPort)"
    }
    
    Write-Host "Starting $($service.Name) ($portInfo)..." -ForegroundColor Cyan
    
    $servicePath = Join-Path $PSScriptRoot $service.Path
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$servicePath'; Write-Host 'Starting $($service.Name)...' -ForegroundColor Green; mvn spring-boot:run"
    
    Start-Sleep -Seconds 3
}

Write-Host "`n==========================================" -ForegroundColor Green
Write-Host "All services started!" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green

Write-Host "`nService URLs:" -ForegroundColor Yellow
Write-Host "  Eureka Discovery: http://localhost:8761" -ForegroundColor White
Write-Host "  API Gateway:      http://localhost:8080" -ForegroundColor White
Write-Host "  H2 Consoles:      http://localhost:808X/h2-console" -ForegroundColor Gray
Write-Host "  GraphiQL:         http://localhost:8080/graphiql" -ForegroundColor White
Write-Host "  SOAP WSDL:        http://localhost:8080/ws/notifications.wsdl" -ForegroundColor White

Write-Host "`ngRPC Servers:" -ForegroundColor Yellow
Write-Host "  USER-SERVICE:     localhost:9091" -ForegroundColor White
Write-Host "  TEAM-SERVICE:     localhost:9092" -ForegroundColor White
Write-Host "  PROJECT-SERVICE:  localhost:9093" -ForegroundColor White
Write-Host "  LOGS-SERVICE:     localhost:9094" -ForegroundColor White

Write-Host "`nCommunication Protocols:" -ForegroundColor Yellow
Write-Host "  ✅ REST:    USER, TEAM, PROJECT services" -ForegroundColor Green
Write-Host "  ✅ GraphQL: Logs-service" -ForegroundColor Green
Write-Host "  ✅ SOAP:    NOTIFICATION-SERVICE" -ForegroundColor Green
Write-Host "  ✅ gRPC:    ANALYTICS-SERVICE → 4 servers" -ForegroundColor Green

Write-Host "`nWait 60 seconds for all services to register with Eureka," -ForegroundColor Yellow
Write-Host "then run: .\test-all-protocols.ps1 to test all protocols`n" -ForegroundColor Yellow
