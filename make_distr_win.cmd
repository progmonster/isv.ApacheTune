call _make_distr.prepare.cmd
if %errorlevel% NEQ 0 goto :end

call _make_distr.common.cmd windows %1
if %errorlevel% NEQ 0 goto :end

call launch4jc distr\temp\launcher.win\apachetune_launch4j.xml
if %errorlevel% NEQ 0 goto :end

call launch4jc distr\temp\launcher.win\uninstaller_launch4j.xml
if %errorlevel% NEQ 0 goto :end

call compile distr/temp/installer.win/apachetune_izpack.xml -h %IZPACK_HOME% -b distr/temp/installer.win -o distr/temp/apachetune_installer.jar
if %errorlevel% NEQ 0 goto :end

call launch4jc distr\temp\installer.win\installer_launch4j.xml
if %errorlevel% NEQ 0 goto :end

call _make_distr.clean.cmd

:end