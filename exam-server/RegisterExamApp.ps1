# Registers "CBT Exam App" as a proper Windows default-app candidate for
# HTTP/HTTPS (so it shows up in the "Choose an application" picker), then
# launches the picker directly on the HTTP protocol.
#
# This uses the same registration shape Windows expects from real browsers:
# a Capabilities key + an entry under RegisteredApplications.

$edgePath = "C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe"
if (-not (Test-Path $edgePath)) {
    $edgePath = "C:\Program Files\Microsoft\Edge\Application\msedge.exe"
}
if (-not (Test-Path $edgePath)) {
    Write-Error "Edge not found. Aborting."
    exit 1
}

$progId = "CBTExamApp"
$appName = "CBT Exam App"

# 1. The ProgID itself: how to open a URL with it.
New-Item -Path "HKCU:\Software\Classes\$progId" -Force | Out-Null
Set-ItemProperty -Path "HKCU:\Software\Classes\$progId" -Name "(Default)" -Value $appName
New-Item -Path "HKCU:\Software\Classes\$progId\DefaultIcon" -Force | Out-Null
Set-ItemProperty -Path "HKCU:\Software\Classes\$progId\DefaultIcon" -Name "(Default)" -Value "$edgePath,0"
New-Item -Path "HKCU:\Software\Classes\$progId\shell\open\command" -Force | Out-Null
Set-ItemProperty -Path "HKCU:\Software\Classes\$progId\shell\open\command" -Name "(Default)" -Value "`"$edgePath`" --app=%1"

# 2. Capabilities block: describes the app and which protocols it can own.
$capPath = "HKCU:\Software\$appName\Capabilities"
New-Item -Path $capPath -Force | Out-Null
Set-ItemProperty -Path $capPath -Name "ApplicationName" -Value $appName
Set-ItemProperty -Path $capPath -Name "ApplicationDescription" -Value "Launches the CBT exam portal in a clean app window"

New-Item -Path "$capPath\URLAssociations" -Force | Out-Null
Set-ItemProperty -Path "$capPath\URLAssociations" -Name "http" -Value $progId
Set-ItemProperty -Path "$capPath\URLAssociations" -Name "https" -Value $progId

New-Item -Path "$capPath\StartMenu" -Force | Out-Null
Set-ItemProperty -Path "$capPath\StartMenu" -Name "StartMenuInternet" -Value $appName

# 3. Register it so Windows' app picker and Default Apps settings see it.
New-Item -Path "HKCU:\Software\RegisteredApplications" -Force | Out-Null
Set-ItemProperty -Path "HKCU:\Software\RegisteredApplications" -Name $appName -Value "Software\$appName\Capabilities"

Write-Output "Registered '$appName' as a selectable default-app candidate for HTTP/HTTPS."
Write-Output "Opening the 'Choose an application' picker for HTTP now..."
Start-Sleep -Seconds 1
Start-Process "ms-settings:defaultapps"
