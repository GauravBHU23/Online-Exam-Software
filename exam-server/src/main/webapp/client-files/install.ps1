# CBT Exam Client - one-shot installer.
# Run as Administrator on each candidate PC. Downloads Java, JavaFX, and the
# locked-down exam client from the exam server, installs them, and registers
# the server IP as a Win+R command (both "examclient" and the raw IP itself).
#
# Usage (from an elevated PowerShell):
#   iex (irm http://192.168.138.235:8080/client-files/install.ps1)

$ErrorActionPreference = "Stop"

$serverIp = "192.168.138.235"
$serverPort = "8080"
$baseUrl = "http://$serverIp`:$serverPort/client-files"

$installDir = "C:\CBTExamClient"
New-Item -ItemType Directory -Force -Path $installDir | Out-Null

# --- Step 1: find or install Java ---
Write-Output "Checking for Java..."
$javawPath = $null

$javaHome = $env:JAVA_HOME
if ($javaHome -and (Test-Path "$javaHome\bin\javaw.exe")) {
    $javawPath = "$javaHome\bin\javaw.exe"
}

if (-not $javawPath) {
    $found = Get-ChildItem "C:\Program Files\Eclipse Adoptium" -Filter "jdk-*" -Directory -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($found -and (Test-Path "$($found.FullName)\bin\javaw.exe")) {
        $javawPath = "$($found.FullName)\bin\javaw.exe"
    }
}

if (-not $javawPath) {
    $localJre = "$installDir\jre17\bin\javaw.exe"
    if (Test-Path $localJre) {
        $javawPath = $localJre
    }
}

if (-not $javawPath) {
    Write-Output "Java not found. Downloading a portable JRE 17 from the exam server (~45 MB, local network)..."
    $jreZip = "$installDir\jre17.zip"
    Invoke-WebRequest -Uri "$baseUrl/jre17.zip" -OutFile $jreZip

    Write-Output "Extracting JRE..."
    $extractTemp = "$installDir\jre17_extract"
    Remove-Item -Recurse -Force $extractTemp -ErrorAction SilentlyContinue
    Expand-Archive -Path $jreZip -DestinationPath $extractTemp -Force

    $jreFolder = Get-ChildItem $extractTemp -Directory | Select-Object -First 1
    Remove-Item -Recurse -Force "$installDir\jre17" -ErrorAction SilentlyContinue
    Move-Item $jreFolder.FullName "$installDir\jre17"
    Remove-Item -Recurse -Force $extractTemp -ErrorAction SilentlyContinue
    Remove-Item $jreZip -ErrorAction SilentlyContinue

    $javawPath = "$installDir\jre17\bin\javaw.exe"
    if (-not (Test-Path $javawPath)) {
        Write-Error "JRE extraction failed - javaw.exe not found at $javawPath"
        exit 1
    }
    Write-Output "JRE installed at $installDir\jre17"
} else {
    Write-Output "Found existing Java: $javawPath"
}

# --- Step 2: download JavaFX (needed for the embedded, locked-down browser view) ---
# The zip contains top-level "bin" (native libraries - required for rendering)
# and "lib" (jars) folders, extracted side by side under javafx-sdk\.
$javafxDir = "$installDir\javafx-sdk"
$javafxLibDir = "$javafxDir\lib"
$javafxBinDir = "$javafxDir\bin"
if (-not (Test-Path "$javafxLibDir\javafx.web.jar") -or -not (Test-Path "$javafxBinDir\prism_d3d.dll")) {
    Write-Output "Downloading JavaFX runtime (~36 MB, local network)..."
    $javafxZip = "$installDir\javafx-sdk.zip"
    Invoke-WebRequest -Uri "$baseUrl/javafx-sdk.zip" -OutFile $javafxZip

    Write-Output "Extracting JavaFX..."
    New-Item -ItemType Directory -Force -Path $javafxDir | Out-Null
    Expand-Archive -Path $javafxZip -DestinationPath $javafxDir -Force
    Remove-Item $javafxZip -ErrorAction SilentlyContinue
} else {
    Write-Output "JavaFX already installed."
}

# --- Step 3: download the exam client jar ---
Write-Output "Downloading exam client..."
Invoke-WebRequest -Uri "$baseUrl/exam-client.jar" -OutFile "$installDir\exam-client.jar"

# --- Step 4: write the launcher batch file ---
$launcherContent = "@echo off`r`nstart `"`" `"$javawPath`" --module-path `"$javafxLibDir`" --add-modules javafx.controls,javafx.web,javafx.swing -Djava.library.path=`"$javafxBinDir`" -jar `"$installDir\exam-client.jar`"`r`n"
Set-Content -Path "$installDir\examclient.bat" -Value $launcherContent -NoNewline

Write-Output "Installing Win+R command 'examclient'..."
Copy-Item "$installDir\examclient.bat" "C:\Windows\System32\examclient.bat" -Force

# --- Step 5: register the raw IP as a Win+R command too ---
Write-Output "Registering '$serverIp' as a Win+R command too..."
$keyPath = "HKCU:\Software\Microsoft\Windows\CurrentVersion\App Paths\$serverIp"
New-Item -Path $keyPath -Force | Out-Null
Set-ItemProperty -Path $keyPath -Name "(Default)" -Value "C:\Windows\System32\examclient.bat"

Write-Output ""
Write-Output "Done. On this PC, Win+R -> 'examclient' or '$serverIp' will now open the locked-down exam client."
Write-Output "Reminder: only Ctrl+Alt+F12 exits the exam view once connected - that is the invigilator shortcut."
