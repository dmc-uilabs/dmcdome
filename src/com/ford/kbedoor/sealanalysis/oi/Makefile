SDRC_INSTL=/apps/c3p/10.1/sun
OI_LIB=$(SDRC_INSTL)/oirun/lib/java/openideas13.jar
ORBIX_LIB= $(SDRC_INSTL)/orbix/orbix_art/2.0/lib/runtime/art.jar:$(SDRC_INSTL)/orbix/orbix_art/2.0/lib/runtime/poa.jar:$(SDRC_INSTL)/orbix/orbix_art/2.0/lib/runtime/naming.jar

CLASSPATH=$(OI_LIB):$(ORBIX_LIB):.

testapp:
	javac -classpath $(CLASSPATH) -d ..  *.java
###        cd ..; jar -cvf oi.jar oi

