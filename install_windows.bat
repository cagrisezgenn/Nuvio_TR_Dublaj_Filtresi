@echo off
setlocal
if "%~1"=="" (
  echo Usage: install_windows.bat "C:\path\to\NuvioMobile"
  exit /b 2
)
py -3 "%~dp0scripts\install_filter.py" "%~1"
if errorlevel 1 exit /b 1
endlocal
