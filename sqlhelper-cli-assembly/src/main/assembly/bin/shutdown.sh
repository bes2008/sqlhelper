#!/bin/sh
#
#
SQLHelperCLI_COMPONENT_HOME=$(cd `dirname $0`/..; pwd)
pid=$(cat $SQLHelperCLI_COMPONENT_HOME/bin/pid)
kill -s TERM $pid