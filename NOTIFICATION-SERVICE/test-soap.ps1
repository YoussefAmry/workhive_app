# NOTIFICATION-SERVICE SOAP Testing Script
# This script tests the SOAP web service using PowerShell

# Define the SOAP endpoint URL
$soapUrl = "http://localhost:8080/ws"

Write-Host "=== Testing NOTIFICATION-SERVICE SOAP Web Service ===" -ForegroundColor Green
Write-Host ""

# Test 1: Send Notification
Write-Host "1. Testing sendNotification SOAP operation..." -ForegroundColor Yellow
$sendNotificationRequest = @"
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:not="http://workhive.com/notification">
   <soapenv:Header/>
   <soapenv:Body>
      <not:sendNotificationRequest>
         <not:userId>1</not:userId>
         <not:message>Your project has been updated</not:message>
         <not:type>INFO</not:type>
      </not:sendNotificationRequest>
   </soapenv:Body>
</soapenv:Envelope>
"@

try {
    $response1 = Invoke-WebRequest -Uri $soapUrl -Method POST -Body $sendNotificationRequest -ContentType "text/xml; charset=utf-8"
    Write-Host "Response Status: $($response1.StatusCode)" -ForegroundColor Green
    Write-Host "Response Body:" -ForegroundColor Cyan
    Write-Host $response1.Content
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "-------------------------------------------" -ForegroundColor Gray
Write-Host ""

# Test 2: Get Notifications
Write-Host "2. Testing getNotifications SOAP operation..." -ForegroundColor Yellow
$getNotificationsRequest = @"
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:not="http://workhive.com/notification">
   <soapenv:Header/>
   <soapenv:Body>
      <not:getNotificationsRequest>
         <not:userId>1</not:userId>
      </not:getNotificationsRequest>
   </soapenv:Body>
</soapenv:Envelope>
"@

try {
    $response2 = Invoke-WebRequest -Uri $soapUrl -Method POST -Body $getNotificationsRequest -ContentType "text/xml; charset=utf-8"
    Write-Host "Response Status: $($response2.StatusCode)" -ForegroundColor Green
    Write-Host "Response Body:" -ForegroundColor Cyan
    Write-Host $response2.Content
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "-------------------------------------------" -ForegroundColor Gray
Write-Host ""

# Test 3: Mark as Read
Write-Host "3. Testing markAsRead SOAP operation..." -ForegroundColor Yellow
$markAsReadRequest = @"
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:not="http://workhive.com/notification">
   <soapenv:Header/>
   <soapenv:Body>
      <not:markAsReadRequest>
         <not:notificationId>1</not:notificationId>
      </not:markAsReadRequest>
   </soapenv:Body>
</soapenv:Envelope>
"@

try {
    $response3 = Invoke-WebRequest -Uri $soapUrl -Method POST -Body $markAsReadRequest -ContentType "text/xml; charset=utf-8"
    Write-Host "Response Status: $($response3.StatusCode)" -ForegroundColor Green
    Write-Host "Response Body:" -ForegroundColor Cyan
    Write-Host $response3.Content
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "-------------------------------------------" -ForegroundColor Gray
Write-Host ""

# Test 4: View WSDL
Write-Host "4. Checking WSDL availability..." -ForegroundColor Yellow
try {
    $wsdlResponse = Invoke-WebRequest -Uri "$soapUrl/notifications.wsdl" -Method GET
    Write-Host "WSDL Status: $($wsdlResponse.StatusCode)" -ForegroundColor Green
    Write-Host "WSDL is available at: $soapUrl/notifications.wsdl" -ForegroundColor Cyan
} catch {
    Write-Host "Error accessing WSDL: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== Testing Complete ===" -ForegroundColor Green
Write-Host ""
Write-Host "Note: Make sure the following services are running:" -ForegroundColor Magenta
Write-Host "  1. discoveryservice (Port 8761)" -ForegroundColor White
Write-Host "  2. Gateway-service (Port 8080)" -ForegroundColor White
Write-Host "  3. USER-SERVICE (Port 8081)" -ForegroundColor White
Write-Host "  4. NOTIFICATION-SERVICE (Port 8085)" -ForegroundColor White
