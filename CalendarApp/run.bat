@echo off
cd /d %~dp0
echo Xoa bin cu...
rmdir /s /q bin 2>nul
mkdir bin
mkdir bin\calendar
mkdir bin\calendar\persistence
echo Compiling...
javac -encoding UTF-8 -d bin src\calendar\*.java src\calendar\persistence\*.java
if %errorlevel% neq 0 (
    echo COMPILE THAT BAI! Xem loi o tren.
    pause
    exit /b 1
)
echo Compile thanh cong! Dang chay...
java -cp bin calendar.Main
pause
