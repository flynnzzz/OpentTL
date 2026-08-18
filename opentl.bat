@echo off
setlocal

set "MODULE_PATH=.\lib\windows\"
set "BINARIES=.\lib\windows\dll\"
set "ADD_MODULES=javafx.controls"
set "JAR_FILE=.\target\opentierlist.jar"
set "LOG_FILE=.\log\otl.log"

if not exist "%MODULE_PATH%\" (
    echo [ERROR] --- lib folder does not exist ---
    exit /b 1
)

if not exist "%JAR_FILE%" (
    echo [ERROR] --- Jar file not found at: %JAR_FILE% ---
    exit /b 1
)

start "" /B java ^
	--enable-native-access javafx.graphics ^
	"-Djava.library.path="%BINARIES% ^
	--module-path %MODULE_PATH% ^
	--add-modules %ADD_MODULES% ^
	-jar %JAR_FILE% ^
	>> %LOG_FILE% 2>&1

endlocal

