SET YOUR_DOMEROOT=C:\dome3
SET YOUR_DOMEROOT=C:\dome3
SET JAVA_HOME=C:\j2sdk1.4.2_13
rem SET JAVA_HOME=C:\jdk1.5.0_07
cd %YOUR_DOMEROOT%\scripts\db
%JAVA_HOME%\bin\java -DDOMEROOT=%YOUR_DOMEROOT% -Xmx250M -cp %YOUR_DOMEROOT%\lib\axis.jar;%YOUR_DOMEROOT%\lib\dom4j-full.jar;%YOUR_DOMEROOT%\lib\colt.jar;%YOUR_DOMEROOT%\lib\jcommon-1.0.5.jar;%YOUR_DOMEROOT%\lib\vecmath.jar;%YOUR_DOMEROOT%\lib\groovy-all-1.0-jsr-05.jar;%YOUR_DOMEROOT%\lib\dome3.jar; mit.cadlab.dome3.plugin.catalog.ui.ModelEditorFrame -debug:50

