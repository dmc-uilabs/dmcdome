#ksh
#~/java/j2se/env.sh
export DOME_ROOT=~/Desktop/Research/OPN/DOME
cd $DOME_ROOT/scripts/db
pwd
CLASSPATH=$DOME_ROOT/lib/dome3.jar:$DOME_ROOT/lib/axis.jar:$DOME_ROOT/lib/colt.jar:$DOME_ROOT/lib/dom4j-full.jar:$DOME_ROOT/lib/domehelp.jar:$DOME_ROOT/lib/hsqldb.jar:$DOME_ROOT/lib/jakarta-oro-2.0.6.jar:$DOME_ROOT/lib/Jama-1.0.1.jar:$DOME_ROOT/lib/jcommon-0.8.9.jar:$DOME_ROOT/lib/jfreechart-0.9.14.jar:$DOME_ROOT/lib/jh.jar:$DOME_ROOT/lib/jython.jar:$DOME_ROOT/lib/openideas13.jar:$DOME_ROOT/lib/orbix2000.jar:$DOME_ROOT/lib/ostermillerutils_1_02_24.jar:$DOME_ROOT/lib/vecmath.jar:$DOME_ROOT/lib/groovy-all-1.0-jsr-05.jar:$DOME_ROOT/lib/Vensim.jar:$DOME_ROOT/lib/DceGui.jar:$DOME_ROOT/lib/secondstring-2003.jar
export CLASSPATH
java -Xmx512M -Djavax.swing.plaf.metal.MetalLookAndFeel -DDOMEROOT=$DOME_ROOT mit.cadlab.dome3.network.server.DomeServer
