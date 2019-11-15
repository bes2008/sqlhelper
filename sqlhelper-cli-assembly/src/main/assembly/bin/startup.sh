#!/bin/sh
#
#

#JAVA_HOME=""

# Setup the JVM
if [ "x$JAVA" = "x" ]; then
    if [ "x$JAVA_HOME" != "x" ]; then
        JAVA="$JAVA_HOME/bin/java"
    else
        JAVA="java"
    fi
fi

SQLHelperCLI_COMPONENT_HOME=$(cd `dirname $0`/..; pwd)
SQLHelperCLI_COMPONENT_LOG_DIR=$SQLHelperCLI_COMPONENT_HOME/logs
GC_LOG_FILE_NAME=sqlhelper-cli-gc.log
CLI_LOG_FILE=$SQLHelperCLI_COMPONENT_LOG_DIR/sqlhelper-cli.out

# make sure cli log file exist
if [ -e $CLI_LOG_FILE ]; then
    if [ ! -w $CLI_LOG_FILE ]; then
        echo No permission to access file $CLI_LOG_FILE
        exist 1
    fi
else
    mkdir -p $SQLHelperCLI_COMPONENT_LOG_DIR
    touch $CLI_LOG_FILE
    chmod 664 $CLI_LOG_FILE
fi


# clear JAVA_OPTS environment and set it
JAVA_OPTS="-Xmx2048m -Xms1024m -XX:MetaspaceSize=64M -XX:MaxMetaspaceSize=512M"
JAVA_OPTS="$JAVA_OPTS -DSQLHelper-CLI.location=$SQLHelperCLI_COMPONENT_HOME"
JAVA_OPTS="$JAVA_OPTS -Xloggc:$SQLHelperCLI_COMPONENT_LOG_DIR/$GC_LOG_FILE_NAME -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps"
JAVA_OPTS="$JAVA_OPTS -XX:+PrintCommandLineFlags"
JAVA_OPTS="$JAVA_OPTS -XX:+UseParNewGC -XX:+UseConcMarkSweepGC"
JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$SQLHelperCLI_COMPONENT_LOG_DIR"
JAVA_OPTS="$JAVA_OPTS -XX:ErrorFile=$SQLHelperCLI_COMPONENT_LOG_DIR/jvm-error-%p.log"

# clear DEBUG_OPTS environment and set it
# DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5008"

# set jdbc drivers directory
export LOADER_PATH=$SQLHelperCLI_COMPONENT_HOME/lib/drivers

#echo $JAVA_OPTS
#echo $DEBUG_OPTS
$JAVA -version
#echo $JAVA -server $JAVA_OPTS $DEBUG_OPTS -jar $SQLHelperCLI_COMPONENT_HOME/lib/sqlhelper-cli.jar >>$CLI_LOG_FILE 2$CLI_LOG_FILE &
$JAVA -server $JAVA_OPTS $DEBUG_OPTS -jar $SQLHelperCLI_COMPONENT_HOME/lib/sqlhelper-cli.jar >>$CLI_LOG_FILE 2>>$CLI_LOG_FILE &