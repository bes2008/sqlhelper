@echo off

@REM
@REM
set SQLHelperCLI_COMPONENT_HOME=%~dp0..
for /f "delims=" %%i in (%SQLHelperCLI_COMPONENT_HOME%/bin/pid) do (taskkill /pid %%i -f )