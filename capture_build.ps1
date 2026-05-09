$gradle = "$env:USERPROFILE\.gradle\wrapper\dists\gradle-8.13-bin\ap7pdhvhnjtc6mxtzz89gkh0c\gradle-8.13\bin\gradle.bat"
$p = Start-Process -FilePath $gradle -ArgumentList @("assembleDebug","--no-daemon") -WorkingDirectory "e:\Airnovel" -NoNewWindow -RedirectStandardOutput "e:\Airnovel\build_out.txt" -RedirectStandardError "e:\Airnovel\build_err.txt" -PassThru
$p.WaitForExit()
Write-Host "EXIT_CODE: $($p.ExitCode)"
