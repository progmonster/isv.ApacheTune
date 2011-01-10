rd /S /Q distr
md distr
md distr\temp
if %errorlevel% NEQ 0 goto end

rd /S /Q distr.unpacked
md distr.unpacked
md distr.unpacked\lib
if %errorlevel% NEQ 0 goto end

call mvn clean package assembly:assembly -Dmaven.test.skip=true
if %errorlevel% NEQ 0 goto end

copy /Y target\apache-tune-1.0-lite-alpha-SNAPSHOT-bin.dir\lib distr.unpacked\lib
if %errorlevel% NEQ 0 goto end

copy /Y src\distr_files\*.* distr.unpacked
if %errorlevel% NEQ 0 goto end

call launch4jc src\launcher\apachetune_launch4j.xml
if %errorlevel% NEQ 0 goto end

call launch4jc src\launcher\uninstaller_launch4j.xml
if %errorlevel% NEQ 0 goto end

rd /S /Q distr\temp

:end