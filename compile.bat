@echo off
chcp 65001 >nul
title AirNovel 编译中
echo ============================================
echo   AirNovel - 正在编译
echo ============================================
echo.

set GRADLE=%USERPROFILE%\.gradle\wrapper\dists\gradle-8.13-bin\ap7pdhvhnjtc6mxtzz89gkh0c\gradle-8.13\bin\gradle.bat
set JAVA_HOME=D:\Program Files\Zulu\zulu-17

if not exist "%GRADLE%" (
    echo [错误] 找不到 Gradle: %GRADLE%
    pause
    exit /b 1
)

echo Gradle: %GRADLE%
echo Java:   %JAVA_HOME%
echo 项目:   %CD%
echo.
echo 开始编译（首次编译需要下载依赖，请耐心等待）...
echo.

"%GRADLE%" assembleDebug
if %ERRORLEVEL% EQU 0 (
    echo.
    echo ============================================
    echo   BUILD SUCCESSFUL - 编译成功！
    echo ============================================
    if exist "app\build\outputs\apk\debug\" (
        echo.
        echo 生成的 APK 文件:
        dir /b "app\build\outputs\apk\debug\*.apk"
    )
) else (
    echo.
    echo ============================================
    echo   BUILD FAILED - 编译失败 (错误码: %ERRORLEVEL%)
    echo ============================================
)
echo.
pause
