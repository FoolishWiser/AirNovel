@echo off
chcp 65001 >nul
set GRADLE=%USERPROFILE%\.gradle\wrapper\dists\gradle-8.13-bin\ap7pdhvhnjtc6mxtzz89gkh0c\gradle-8.13\bin\gradle.bat
"%GRADLE%" assembleDebug --no-daemon > e:\Airnovel\build_out.txt 2>&1
echo EXIT_CODE=%ERRORLEVEL%
