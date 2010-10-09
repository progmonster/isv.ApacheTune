call mvn install:install-file -Dfile=swt-3.6-win32-win32-x86.jar -DgroupId=swt -DartifactId=swt-win32 -Dversion=3.6 -Dpackaging=jar -DgeneratePom=true

call mvn install:install-file -Dfile=mydoggy-res-1.4.2.jar -DgroupId=net.sourceforge.mydoggy -DartifactId=mydoggy-res -Dversion=1.4.2 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=l2fprod-common-all-7.3-20070317.jar -DgroupId=com.l2fprod -DartifactId=common -Dversion=7.3 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=jsyntaxpane-0.9.4.jar -DgroupId=jsyntaxpane -DartifactId=jsyntaxpane -Dversion=0.9.4 -Dpackaging=jar -DgeneratePom=true

call mvn install:install-file -Dfile=DJNativeSwing-SWT-0-9-9-20100914.jar -DgroupId=djproject.sourceforge.net -DartifactId=DJNativeSwing-SWT -Dversion=0-9-9-20100914 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=DJNativeSwing-SWT-0-9-9-20100914.src.zip -DgroupId=djproject.sourceforge.net -DartifactId=DJNativeSwing-SWT -Dversion=0-9-9-20100914 -Dpackaging=jar -Dclassifier=sources

call mvn install:install-file -Dfile=DJNativeSwing-0-9-9-20100914.jar -DgroupId=djproject.sourceforge.net -DartifactId=DJNativeSwing -Dversion=0-9-9-20100914 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=DJNativeSwing-0-9-9-20100914.src.zip -DgroupId=djproject.sourceforge.net -DartifactId=DJNativeSwing -Dversion=0-9-9-20100914 -Dpackaging=jar -Dclassifier=sources






