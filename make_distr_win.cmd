call _make_distr.prepare.cmd
if %errorlevel% NEQ 0 goto :end

call _make_distr.common.cmd windows
if %errorlevel% NEQ 0 goto :end

call launch4jc src\launcher.win\apachetune_launch4j.xml
if %errorlevel% NEQ 0 goto :end

call launch4jc src\launcher.win\uninstaller_launch4j.xml
if %errorlevel% NEQ 0 goto :end

call compile src/installer.win/apachetune_izpack.xml -h %IZPACK_HOME% -b src/installer.win -o distr/temp/apachetune_installer.jar
if %errorlevel% NEQ 0 goto :end

call launch4jc src\installer.win\installer_launch4j.xml
if %errorlevel% NEQ 0 goto :end

call _make_distr.clean.cmd

:end