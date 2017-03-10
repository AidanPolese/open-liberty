@echo off
set PATH=C:\Cygwin\bin;%PATH%

REM Copy arguments
set arguments=%*

REM Copy Batch File Path Location
set batchPath=%~dp0%

REM Convert the batch file extension from .bat to .sh
set batchFile=%~n0.sh

REM Lets be nice to arguments that have the typical 'Documents and Settings' sub-directory in them.
REM Find/Replace spaces with an escaped space so that Cygwin can deal with it. 
set arguments=%arguments:Documents and Settings=Documents' 'and' 'Settings%

REM Same for the location of the batch file.  Escape if spaces are found.
REM We can blindly escape all spaces because we know this is a PATH.  Unlike
REM the arguments above, we have no way of demarcating the arguments.
set batchPath=%batchPath: =' '%

REM Now re-build bash argument which is run from Cygwin
set completeCommand=%batchPath%%batchFile% %arguments%

REM Print out Cygwin bash command we are about to issue.
echo %completeCommand%

REM Issue bash command which kicks off build.
bash %completeCommand%
