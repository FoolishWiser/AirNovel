$ErrorActionPreference = "Stop"
$gradle = "$env:USERPROFILE\.gradle\wrapper\dists\gradle-8.13-bin\ap7pdhvhnjtc6mxtzz89gkh0c\gradle-8.13\bin\gradle.bat"
$logFile = "e:\Airnovel\build_log.txt"
$errFile = "e:\Airnovel\build_err.txt"

Write-Host "Starting build..."
$proc = Start-Process -FilePath $gradle -ArgumentList @("assembleDebug", "--no-daemon", "--no-build-cache") -WorkingDirectory "e:\Airnovel" -NoNewWindow -RedirectStandardOutput $logFile -RedirectStandardError $errFile -PassThru
Write-Host "Build PID: $($proc.Id)"
$proc.WaitForExit()
Write-Host "Exit code: $($proc.ExitCode)"
