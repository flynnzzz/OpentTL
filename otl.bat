@echo off
setlocal

set "MODULE_PATH=.\lib\windows"
set "BINARIES=.\lib\windows\bin"
set "ADD_MODULES=javafx.controls"
set "JAR_FILE=.\target\opentierlist.jar"
set "LOG_FILE=.\otl.log"

if not exist "%MODULE_PATH%\" (
    echo [ERROR] --- lib folder does not exist ---
)

if not exist "%JAR_FILE%" (
    echo [ERROR] --- Jar file not found at: %JAR_FILE% ---
)

start "" /B java "-Djava.library.path=%BINARIES%" --module-path "%MODULE_PATH%" --add-modules %ADD_MODULES% -jar "%JAR_FILE%" >> "%LOG_FILE%" 2>&1

endlocal