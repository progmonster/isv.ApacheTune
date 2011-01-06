call _make_distr.prepare.cmd
if %errorlevel% NEQ 0 goto :end

call _make_distr.common.cmd linux
if %errorlevel% NEQ 0 goto :end

cd distr.unpacked
if %errorlevel% NEQ 0 goto :end

tar -cvf apachetune-1.0-lite.tar *
if %errorlevel% NEQ 0 goto :end

gzip apachetune-1.0-lite.tar
if %errorlevel% NEQ 0 goto :end

copy apachetune-1.0-lite.tar.gz ..\distr
if %errorlevel% NEQ 0 goto :end

cd ..
if %errorlevel% NEQ 0 goto :end

call _make_distr.clean.cmd

:end