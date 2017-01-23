SET YOUR_DOMEROOT=C:\dome3
SET YOUR_EXECUTABLE_FOLDER=C:\DOME\DOME-DAKOTA\DOME-DAKOTA\bin\
SET YOUR_EXECUTABLE_CLASSNAME=DomeModelRunnerForDakota

cd %YOUR_EXECUTABLE_FOLDER%
java -DDOMEROOT=%YOUR_DOMEROOT% -cp .;%YOUR_DOMEROOT%\lib\dome3.jar;%YOUR_DOMEROOT%\lib\axis.jar;%YOUR_DOMEROOT%\lib\colt.jar;%YOUR_DOMEROOT%\lib\dom4j-full.jar;%YOUR_DOMEROOT%\lib\domehelp.jar;%YOUR_DOMEROOT%\lib\hsqldb.jar;%YOUR_DOMEROOT%\lib\jakarta-oro-2.0.6.jar;%YOUR_DOMEROOT%\lib\Jama-1.0.1.jar;%YOUR_DOMEROOT%\lib\jcommon-0.8.9.jar;%YOUR_DOMEROOT%\lib\jfreechart-0.9.14.jar;%YOUR_DOMEROOT%\lib\jh.jar;%YOUR_DOMEROOT%\lib\jython.jar;%YOUR_DOMEROOT%\lib\openideas13.jar;%YOUR_DOMEROOT%\lib\orbix2000.jar;%YOUR_DOMEROOT%\lib\ostermillerutils_1_02_24.jar;%YOUR_DOMEROOT%\lib\vecmath.jar;%YOUR_DOMEROOT%\lib\groovy-all-1.0-jsr-05.jar;%YOUR_DOMEROOT%\lib\Vensim.jar;%YOUR_DOMEROOT%\lib\mysql.jar %YOUR_EXECUTABLE_CLASSNAME%