$env:JAVA_HOME = 'D:\Program Files\Zulu\zulu-21'
$gradle = "$env:USERPROFILE\.gradle\wrapper\dists\gradle-8.13-bin\ap7pdhvhnjtc6mxtzz89gkh0c\gradle-8.13\bin\gradle.bat"
$process = Start-Process -FilePath $gradle `
    -ArgumentList 'assembleDebug', '--no-daemon' `
    -WorkingDirectory 'e:\Airnovel' `
    -NoNewWindow `
    -RedirectStandardOutput 'e:\Airnovel\build_log.txt' `
    -RedirectStandardError 'e:\Airnovel\build_err.txt' `
    -PassThru
$process.WaitForExit()
Write-Host "Exit code: $($process.ExitCode)"
