# Registers a custom "CBTExamApp" protocol handler that launches Edge in --app
# (kiosk-like, no address bar/tabs) mode for http/https links, then sets it as
# the default handler for http/https on this machine. Run as Administrator.
#
# To REVERT: run SetDefaultBrowserHandler.ps1 -Revert
# (this re-opens Windows' default apps settings so you can pick your normal
#  browser again — Windows doesn't allow scripting that part directly).

param(
    [switch]$Revert
)

if ($Revert) {
    Write-Output "Opening Windows Default Apps settings so you can restore your normal browser..."
    Start-Process "ms-settings:defaultapps"
    exit
}

$edgePath = "C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe"
if (-not (Test-Path $edgePath)) {
    $edgePath = "C:\Program Files\Microsoft\Edge\Application\msedge.exe"
}
if (-not (Test-Path $edgePath)) {
    Write-Error "Edge not found at expected locations. Aborting."
    exit 1
}

$progId = "CBTExamApp"
$examUrl = "http://172.16.32.73:8080/login.jsp"

# Register a ProgID that opens any http(s) URL in Edge --app mode.
New-Item -Path "HKCU:\Software\Classes\$progId" -Force | Out-Null
Set-ItemProperty -Path "HKCU:\Software\Classes\$progId" -Name "(Default)" -Value "CBT Exam App"
New-Item -Path "HKCU:\Software\Classes\$progId\shell\open\command" -Force | Out-Null
Set-ItemProperty -Path "HKCU:\Software\Classes\$progId\shell\open\command" -Name "(Default)" -Value "`"$edgePath`" --app=%1"

Write-Output "Registered '$progId' to open URLs via Edge --app mode."
Write-Output ""
Write-Output "Now set it as default for HTTP/HTTPS:"
Write-Output "  Settings > Apps > Default apps > search 'CBT Exam App' > set for HTTP and HTTPS"
Write-Output ""
Write-Output "Opening Default Apps settings now..."
Start-Process "ms-settings:defaultapps"
