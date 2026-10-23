@echo off
setlocal
if "%~1"=="" (
  echo Usage: build_windows.bat "C:\path\to\NuvioMobile"
  exit /b 2
)
pushd "%~1" || exit /b 2
call gradlew.bat :androidApp:assembleFullDebug
if errorlevel 1 (popd & exit /b 1)
popd
endlocal
