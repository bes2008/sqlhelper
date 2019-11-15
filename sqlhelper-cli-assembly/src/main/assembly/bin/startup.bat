@echo off

@REM
@REM

rem set JAVA_HOME=""

title SQLHelper-CLI

if "x%JAVA_HOME%" == "x" (
  set  JAVA=java
  echo JAVA_HOME is not set. Unexpected results may occur.
  echo Set JAVA_HOME to the directory of your local JDK to avoid this message.
) else (
  if not exist "%JAVA_HOME%" (
    echo JAVA_HOME "%JAVA_HOME%" path doesn't exist
    goto END
  ) else (
    echo Using JAVA: "%JAVA_HOME%\bin\java"
    set "JAVA=%JAVA_HOME%\bin\java"
  )
)

set SQLHelperCLI_COMPONENT_HOME=%~dp0..
set SQLHelperCLI_COMPONENT_LOG_DIR=%SQLHelperCLI_COMPONENT_HOME%/logs
set CLI_LOG_FILE=%SQLHelperCLI_COMPONENT_LOG_DIR%/sqlhelper-cli.out
set GC_LOG_FILE_NAME=sqlhelper-cli-gc.log
if exist CLI_LOG_FILE goto setJVMOptions

@REM make sure cli log file exist
:mkLogsDir
if not exist %SQLHelperCLI_COMPONENT_LOG_DIR% (
  set CURRENT_DIR=%CD%
  @cd %SQLHelperCLI_COMPONENT_HOME%
  @mkdir logs
  cd %CURRENT_DIR%
  set CURRENT_DIR=
)

:mkCliLogFile
if not exist %CLI_LOG_FILE% (
  @REM echo create file "%CLI_LOG_FILE%"
  type nul>%CLI_LOG_FILE%
)

:setJVMOptions
@REM clear JAVA_OPTS environment and set it
set JAVA_OPTS= -Xmx2048m -Xms1024m -XX:MetaspaceSize=64M -XX:MaxMetaspaceSize=512M
set JAVA_OPTS=%JAVA_OPTS% -DSQLHelper-CLI.location="%SQLHelperCLI_COMPONENT_HOME%"
set JAVA_OPTS=%JAVA_OPTS% -Xloggc:"%SQLHelperCLI_COMPONENT_LOG_DIR%/%GC_LOG_FILE_NAME%" -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps
set JAVA_OPTS=%JAVA_OPTS% -XX:+PrintCommandLineFlags
set JAVA_OPTS=%JAVA_OPTS% -XX:+UseParNewGC -XX:+UseConcMarkSweepGC
set JAVA_OPTS=%JAVA_OPTS% -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath="%SQLHelperCLI_COMPONENT_LOG_DIR%"
set JAVA_OPTS=%JAVA_OPTS% -XX:ErrorFile="%SQLHelperCLI_COMPONENT_LOG_DIR%/jvm-error-%%p.log"

@REM clear DEBUG_OPTS environment and set it
set DEBUG_OPTS=
@REM set DEBUG_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5008

@REM echo %JAVA_OPTS%
@REM echo %DEBUG_OPTS%

@REM set jdbc drivers directory
set LOADER_PATH=
set LOADER_PATH=%SQLHelperCLI_COMPONENT_HOME%/lib/drivers

"%JAVA%" -version
@REM echo "%JAVA%" -server %JAVA_OPTS% %DEBUG_OPTS% -jar "%SQLHelperCLI_COMPONENT_HOME%\lib\sqlhelper-cli.jar" >>%CLI_LOG_FILE% 2>&1
"%JAVA%" -server %JAVA_OPTS% %DEBUG_OPTS% -jar "%SQLHelperCLI_COMPONENT_HOME%\lib\sqlhelper-cli.jar"
