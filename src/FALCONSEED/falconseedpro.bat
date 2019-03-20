@echo off

REM
REM  FALCON-SEED Launcher for Development 1.10
REM

if "%OS%"=="Windows_NT" @setlocal
if "%OS%"=="WINNT" @setlocal

REM ----------------------
REM  Options
REM ----------------------

REM 'JAVAVM_OPTIONS' is JavaVM option for java command.
set JAVAVM_OPTIONS=

REM 'LANGUAGE' is locale for FALCON-SEED. 'ja' or 'en' can be specified for this option. 
set LANGUAGE=

REM ----------------------
REM  Command
REM ----------------------
set HOME_DIR=%~dp0
if not "%LANGUAGE%"=="" (
  set OPT_APP_LANG=-language %LANGUAGE%
)
set JAVACMD=javaw
@echo off
%JAVACMD% %JAVAVM_OPTIONS% -jar "%HOME_DIR%FALCONSEEDPro.jar" %OPT_APP_LANG%
@echo off


rem end
:end

endlocal
if "%OS%"=="Windows_NT" @endlocal
if "%OS%"=="WINNT" @endlocal

