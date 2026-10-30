# CBT Exam Client - one-shot installer.
# Run as Administrator on each candidate PC. Downloads the client jar and
# launcher from the exam server, installs them, and registers the server IP
# as a Win+R command (both "examclient" and the raw IP itself).
#
# Usage (from an elevated PowerShell):
#   iex (irm http://192.168.138.235:8080/client-files/install.ps1)

$serverIp = "192.168.138.235"
$serverPort = "8080"
$baseUrl = "http://$serverIp`:$serverPort/client-files"

$installDir = "C:\CBTExamClient"
New-Item -ItemType Directory -Force -Path $installDir | Out-Null

Write-Output "Downloading exam client..."
Invoke-WebRequest -Uri "$baseUrl/exam-client.jar" -OutFile "$installDir\exam-client.jar"

$javaHome = $env:JAVA_HOME
if (-not $javaHome -or -not (Test-Path "$javaHome\bin\javaw.exe")) {
    $found = Get-ChildItem "C:\Program Files\Eclipse Adoptium" -Filter "jdk-*" -Directory -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($found) { $javaHome = $found.FullName }
}
if (-not $javaHome -or -not (Test-Path "$javaHome\bin\javaw.exe")) {
    Write-Error "Java (JDK 17+) not found on this PC. Install it first, then re-run this script."
    exit 1
}
$javawPath = "$javaHome\bin\javaw.exe"

$launcherContent = "@echo off`r`nstart `"`" `"$javawPath`" -jar `"$installDir\exam-client.jar`"`r`n"
Set-Content -Path "$installDir\examclient.bat" -Value $launcherContent -NoNewline

Write-Output "Installing Win+R command 'examclient'..."
Copy-Item "$installDir\examclient.bat" "C:\Windows\System32\examclient.bat" -Force

Write-Output "Registering '$serverIp' as a Win+R command too..."
$keyPath = "HKCU:\Software\Microsoft\Windows\CurrentVersion\App Paths\$serverIp"
New-Item -Path $keyPath -Force | Out-Null
Set-ItemProperty -Path $keyPath -Name "(Default)" -Value "C:\Windows\System32\examclient.bat"

Write-Output ""
Write-Output "Done. On this PC, Win+R -> 'examclient' or '$serverIp' will now open the exam client."
