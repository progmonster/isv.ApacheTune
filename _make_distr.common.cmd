set mavenProfile=
if (%1) equ (windows) set mavenProfile=windows
if (%1) equ (linux) set mavenProfile=linux

set mvn_skip_tests=
if (%2) equ (skip_tests) set mvn_skip_tests=-Dmaven.test.skip.exec=true

call mvn clean package %mvn_skip_tests% assembly:assembly -P %mavenProfile%
if %errorlevel% NEQ 0 goto :setError

xcopy /Y /E /H target\assembly-bin.dir distr.unpacked
if %errorlevel% NEQ 0 goto :setError

exit /B 0

:setError
exit /B 1
