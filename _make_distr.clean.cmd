rd /S /Q distr\temp
if %errorlevel% NEQ 0 goto :setError

exit /B 0

:setError
exit /B 1