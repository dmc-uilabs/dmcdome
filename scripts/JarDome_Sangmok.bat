cd ..\out
SET JAVA_HOME=C:\j2sdk1.4.2_16
%java_home%\bin\jar cvf ..\lib\dome3.jar com edu mit net org uk
rem copy ..\lib\dome3.jar C:\tomcat5-domewc\webapps\samplewc\WEB-INF\lib
rem copy ..\lib\dome3.jar C:\tomcat5-domewc\webapps\domewc\WEB-INF\lib
rem copy ..\lib\dome3.jar C:\tomcat5-domewc\webapps\vensimdemo\WEB-INF\lib
copy ..\lib\dome3.jar C:\tomcat5-domewc\webapps\extendsimdemo\WEB-INF\lib