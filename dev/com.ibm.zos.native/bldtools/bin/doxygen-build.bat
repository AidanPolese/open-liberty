@echo off
set PATH=C:\Cygwin\bin;%PATH%

IF NOT EXIST %~dp2\bin\doxygen.exe GOTO USECYGWIN
%~dp2doxygen %~dp1bldtools\config\Doxyfile
EXIT

:USECYGWIN
bash %~dp0doxygen-build.sh %*