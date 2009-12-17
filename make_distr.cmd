rd /S /Q distr
md distr

rd /S /Q distr
md distr\temp

rd /S /Q distr.unpacked
md distr.unpacked
md distr.unpacked\lib

call mvn clean package assembly:assembly

copy /Y target\apache-tune-1.0-lite-alpha-SNAPSHOT-bin.dir\lib distr.unpacked\lib
copy /Y src\distr_files\*.* distr.unpacked

call launch4jc src\launcher\apachetune_launch4j.xml
call launch4jc src\launcher\uninstaller_launch4j.xml

call compile src/installer/apachetune_izpack.xml -h %IZPACK_HOME% -b src/installer -o distr/temp/apachetune_installer.jar
call launch4jc src\installer\installer_launch4j.xml

rd /S /Q distr\temp