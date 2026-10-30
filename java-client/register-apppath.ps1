$exePath = "C:\Windows\System32\examclient.bat"
$keyPath = "HKCU:\Software\Microsoft\Windows\CurrentVersion\App Paths\172.16.32.73"
New-Item -Path $keyPath -Force | Out-Null
Set-ItemProperty -Path $keyPath -Name "(Default)" -Value $exePath
Write-Output "App Path registered for 172.16.32.73"
