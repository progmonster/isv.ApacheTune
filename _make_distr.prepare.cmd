rd /S /Q distr
md distr
md distr\temp
if %errorlevel% NEQ 0 goto :setError

rd /S /Q distr.unpacked
md distr.unpacked
md distr.unpacked\lib
if %errorlevel% NEQ 0 goto :setError

exit /B 0

:setError
exit /B 1