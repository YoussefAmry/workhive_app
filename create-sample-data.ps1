# Create Sample Data for WorkHive Demo

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   Creating Sample Data for WorkHive   " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Wait for services to be ready
Start-Sleep -Seconds 2

# 1. Create Users
Write-Host "`n[1] Creating Users..." -ForegroundColor Yellow

$user1 = @{
    username = "john_doe"
    email = "john@workhive.com"
    password = "password123"
    firstName = "John"
    lastName = "Doe"
    role = "DEVELOPER"
} | ConvertTo-Json

$user2 = @{
    username = "jane_manager"
    email = "jane@workhive.com"
    password = "password123"
    firstName = "Jane"
    lastName = "Smith"
    role = "MANAGER"
} | ConvertTo-Json

$user3 = @{
    username = "bob_dev"
    email = "bob@workhive.com"
    password = "password123"
    firstName = "Bob"
    lastName = "Johnson"
    role = "DEVELOPER"
} | ConvertTo-Json

try {
    Write-Host "   Creating John (Developer)..." -NoNewline
    $u1 = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/register" -Method POST -ContentType "application/json" -Body $user1 -ErrorAction Stop
    Write-Host " OK (ID: $($u1.id))" -ForegroundColor Green
} catch {
    Write-Host " Already exists or error" -ForegroundColor DarkYellow
}

try {
    Write-Host "   Creating Jane (Manager)..." -NoNewline
    $u2 = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/register" -Method POST -ContentType "application/json" -Body $user2 -ErrorAction Stop
    Write-Host " OK (ID: $($u2.id))" -ForegroundColor Green
} catch {
    Write-Host " Already exists or error" -ForegroundColor DarkYellow
}

try {
    Write-Host "   Creating Bob (Developer)..." -NoNewline
    $u3 = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/register" -Method POST -ContentType "application/json" -Body $user3 -ErrorAction Stop
    Write-Host " OK (ID: $($u3.id))" -ForegroundColor Green
} catch {
    Write-Host " Already exists or error" -ForegroundColor DarkYellow
}

# Wait a bit for services to sync
Start-Sleep -Seconds 3

# 2. Create Teams
Write-Host "`n[2] Creating Teams..." -ForegroundColor Yellow
Write-Host "   NOTE: If this fails, restart TEAM-SERVICE in IntelliJ first" -ForegroundColor DarkGray

$team1 = @{
    name = "Backend Development Team"
    description = "Team responsible for backend microservices"
    managerId = 2
    developerIds = @(1, 3)
} | ConvertTo-Json

$team2 = @{
    name = "Frontend Team"
    description = "UI/UX development team"
    managerId = 2
    developerIds = @(1)
} | ConvertTo-Json

try {
    Write-Host "   Creating Backend Team..." -NoNewline
    $t1 = Invoke-RestMethod -Uri "http://localhost:8082/api/teams" -Method POST -ContentType "application/json" -Body $team1 -ErrorAction Stop
    Write-Host " OK (ID: $($t1.id))" -ForegroundColor Green
} catch {
    Write-Host " FAILED" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "   Solution: Restart TEAM-SERVICE in IntelliJ and run this script again" -ForegroundColor Yellow
}

try {
    Write-Host "   Creating Frontend Team..." -NoNewline
    $t2 = Invoke-RestMethod -Uri "http://localhost:8082/api/teams" -Method POST -ContentType "application/json" -Body $team2 -ErrorAction Stop
    Write-Host " OK (ID: $($t2.id))" -ForegroundColor Green
} catch {
    Write-Host " FAILED" -ForegroundColor Red
}

# Wait for team creation
Start-Sleep -Seconds 2

# 3. Create Projects
Write-Host "`n[3] Creating Projects..." -ForegroundColor Yellow

$project1 = @{
    name = "E-Commerce Platform"
    description = "Online shopping platform with microservices architecture"
    createdBy = 1
    teamId = 1
} | ConvertTo-Json

$project2 = @{
    name = "Analytics Dashboard"
    description = "Real-time analytics and reporting system"
    createdBy = 3
    teamId = 1
} | ConvertTo-Json

try {
    Write-Host "   Creating E-Commerce Platform..." -NoNewline
    $p1 = Invoke-RestMethod -Uri "http://localhost:8083/api/projects" -Method POST -ContentType "application/json" -Body $project1 -ErrorAction Stop
    Write-Host " OK (ID: $($p1.id))" -ForegroundColor Green
} catch {
    Write-Host " FAILED" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
}

try {
    Write-Host "   Creating Analytics Dashboard..." -NoNewline
    $p2 = Invoke-RestMethod -Uri "http://localhost:8083/api/projects" -Method POST -ContentType "application/json" -Body $project2 -ErrorAction Stop
    Write-Host " OK (ID: $($p2.id))" -ForegroundColor Green
} catch {
    Write-Host " FAILED" -ForegroundColor Red
}

# 4. Display All Data
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "          Current Data Summary         " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

Write-Host "`n--- USERS ---" -ForegroundColor Yellow
try {
    $users = Invoke-RestMethod -Uri "http://localhost:8081/api/users"
    $users | Format-Table id, username, email, role, fullName -AutoSize
} catch {
    Write-Host "Failed to fetch users" -ForegroundColor Red
}

Write-Host "`n--- TEAMS ---" -ForegroundColor Yellow
try {
    $teams = Invoke-RestMethod -Uri "http://localhost:8082/api/teams"
    if ($teams -and $teams.Count -gt 0) {
        $teams | Format-Table id, name, description, managerName -AutoSize
    } else {
        Write-Host "No teams found. Please restart TEAM-SERVICE and run again." -ForegroundColor Red
    }
} catch {
    Write-Host "Failed to fetch teams" -ForegroundColor Red
}

Write-Host "`n--- PROJECTS ---" -ForegroundColor Yellow
try {
    $projects = Invoke-RestMethod -Uri "http://localhost:8083/api/projects"
    if ($projects -and $projects.Count -gt 0) {
        $projects | Format-Table id, name, description, teamName -AutoSize
    } else {
        Write-Host "No projects found" -ForegroundColor Red
    }
} catch {
    Write-Host "Failed to fetch projects" -ForegroundColor Red
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "Sample data creation complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
