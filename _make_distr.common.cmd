if (%1) equ (windows) set mavenProfile=windows
if (%1) equ (linux) set mavenProfile=linux

call mvn clean package assembly:assembly -P %mavenProfile%
if %errorlevel% NEQ 0 goto :setError

copy /Y target\apache-tune-1.0-lite-alpha-SNAPSHOT-bin.dir\lib distr.unpacked\lib
if %errorlevel% NEQ 0 goto :setError

copy /Y src\distr_files\*.* distr.unpacked
if %errorlevel% NEQ 0 goto :setError

exit /B 0

:setError
exit /B 1
