rem SET JAVA_HOME=C:\java\j2sdk1.4.2_16
SET JAVA_HOME=C:\Java\jdk1.6.0_26
SET YOUR_DOMEROOT=D:\Work\DOME\Src
cd %YOUR_DOMEROOT%\scripts\db
%JAVA_HOME%\bin\java -DDOMEROOT=%YOUR_DOMEROOT% -Xmx250M -Dorg.omg.CORBA.ORBClass=com.iona.corba.art.artimpl.ORBImpl  -Dorg.omg.CORBA.ORBSingletonClass=com.iona.corba.art.artimpl.ORBSingleton -cp %YOUR_DOMEROOT%\out;%YOUR_DOMEROOT%\lib\dome3.jar;%YOUR_DOMEROOT%\lib\axis.jar;%YOUR_DOMEROOT%\lib\colt.jar;%YOUR_DOMEROOT%\lib\dom4j-full.jar;%YOUR_DOMEROOT%\lib\domehelp.jar;%YOUR_DOMEROOT%\lib\hsqldb.jar;%YOUR_DOMEROOT%\lib\jakarta-oro-2.0.6.jar;%YOUR_DOMEROOT%\lib\Jama-1.0.1.jar;%YOUR_DOMEROOT%\lib\jcommon-0.8.9.jar;%YOUR_DOMEROOT%\lib\jfreechart-0.9.14.jar;%YOUR_DOMEROOT%\lib\jh.jar;%YOUR_DOMEROOT%\lib\jython.jar;%YOUR_DOMEROOT%\lib\openideas13.jar;%YOUR_DOMEROOT%\lib\orbix2000.jar;%YOUR_DOMEROOT%\lib\ostermillerutils_1_02_24.jar;%YOUR_DOMEROOT%\lib\vecmath.jar;%YOUR_DOMEROOT%\lib\groovy-all-1.0-jsr-05.jar;%YOUR_DOMEROOT%\lib\Vensim.jar;%YOUR_DOMEROOT%\lib\DceGui.jar;%YOUR_DOMEROOT%\lib\secondstring-2003.jar mit.cadlab.dome3.DomeClientApplication -debug:50

