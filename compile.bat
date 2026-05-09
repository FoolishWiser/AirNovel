@echo off
chcp 65001 >nul
title AirNovel 编译
set LOGFILE=e:\Airnovel\build_log.txt

:: 设置 JAVA_HOME
set JAVA_HOME=D:\Program Files\Zulu\zulu-17

echo ============================================ > %LOGFILE%
echo   AirNovel - 正在编译 >> %LOGFILE%
echo ============================================ >> %LOGFILE%
echo. >> %LOGFILE%

set GRADLE=e:\gradle-9.5.0\bin\gradle.bat

if not exist "%GRADLE%" (
    echo 错误: 找不到 %GRADLE%
    echo Error: Gradle not found >> %LOGFILE%
    pause
    exit /b 1
)

echo Gradle: %GRADLE%
echo Java:   %JAVA_HOME%
echo Log:    %LOGFILE%
echo.

echo Gradle: %GRADLE% >> %LOGFILE%
echo Java:   %JAVA_HOME% >> %LOGFILE%
echo. >> %LOGFILE%

echo Compiling...
"%GRADLE%" assembleDebug --no-daemon >> %LOGFILE% 2>&1

echo. >> %LOGFILE%
echo Exit code: %ERRORLEVEL% >> %LOGFILE%

if %ERRORLEVEL% EQU 0 (
    color 2f
    echo.
    echo ============================================
    echo   BUILD SUCCESSFUL - 编译成功！
    echo ============================================
    dir /b "app\build\outputs\apk\debug\*.apk" 2>nul
) else (
    color 4f
    echo.
    echo ============================================
    echo   BUILD FAILED - 编译失败 (错误码: %ERRORLEVEL%)
    echo ============================================
    echo 错误详情请查看: %LOGFILE%
    echo.
)
echo.
pause
