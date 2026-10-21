$env:JAVA_HOME     = "C:\Program Files\Java\jdk-25.0.2"
$env:CATALINA_HOME = "C:\apache-tomcat-9.0.121"
$tomcatBin = "$env:CATALINA_HOME\bin"
$webapps   = "$env:CATALINA_HOME\webapps"

Write-Host "1. Deteniendo Tomcat..." -ForegroundColor Yellow
& "$tomcatBin\shutdown.bat" | Out-Null
Start-Sleep -Seconds 3

Write-Host "2. Compilando con Maven..." -ForegroundColor Cyan
mvn clean package
if ($LASTEXITCODE -ne 0) {
    Write-Host "La compilacion fallo, revisa el error de arriba." -ForegroundColor Red
    exit 1
}

Write-Host "3. Limpiando despliegue anterior..." -ForegroundColor Yellow
if (Test-Path "$webapps\Urent") { Remove-Item "$webapps\Urent" -Recurse -Force }
if (Test-Path "$webapps\Urent.war") { Remove-Item "$webapps\Urent.war" -Force }

Write-Host "4. Copiando el .war a Tomcat..." -ForegroundColor Cyan
Copy-Item "target\Urent-1.0-SNAPSHOT.war" "$webapps\Urent.war"

Write-Host "5. Iniciando Tomcat..." -ForegroundColor Green
& "$tomcatBin\startup.bat"

Write-Host "Despliegue completado. Abre localhost:8080/Urent/" -ForegroundColor Green