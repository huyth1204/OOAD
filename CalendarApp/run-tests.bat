@echo off
REM Script to run tests for Calendar App with file persistence

echo ========================================
echo Calendar App - Test Runner
echo ========================================
echo.

REM Check if Maven is installed
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Maven is not installed or not in PATH
    echo.
    echo Please install Maven:
    echo 1. Download from: https://maven.apache.org/download.cgi
    echo 2. Extract to a directory (e.g., C:\Program Files\Apache\maven)
    echo 3. Add bin directory to PATH environment variable
    echo 4. Restart terminal and run this script again
    echo.
    pause
    exit /b 1
)

echo [INFO] Maven found: 
call mvn -version
echo.

REM Check if Java is installed
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java is not installed or not in PATH
    echo.
    echo Please install Java 11 or higher
    pause
    exit /b 1
)

echo [INFO] Java found:
java -version
echo.

REM Run tests
echo [INFO] Running tests...
echo.
call mvn clean test

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo [SUCCESS] All tests passed!
    echo ========================================
    echo.
    echo To view coverage report:
    echo   1. Run: mvn jacoco:report
    echo   2. Open: target\site\jacoco\index.html
) else (
    echo.
    echo ========================================
    echo [FAILURE] Some tests failed
    echo ========================================
    echo.
    echo Check the output above for details
)

echo.
pause
