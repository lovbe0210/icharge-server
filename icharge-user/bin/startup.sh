#!/bin/bash

current_path=`pwd`
case "`uname`" in
    Linux)
                bin_abs_path=$(readlink -f $(dirname $0))
                ;;
        *)
                bin_abs_path=`cd $(dirname $0); pwd`
                ;;
esac
base=${bin_abs_path}/..

export LANG=en_US.UTF-8
export BASE=$base

if [ -f $base/bin/pid ] ; then
        echo "found pid , Please run stop.sh first ,then startup.sh" 2>&2
    exit 1
fi

if [ ! -d $base/logs/icharge ] ; then
        mkdir -p $base/logs/icharge
fi

## set java path
if [ -z "$JAVA" ] ; then
  JAVA=$(which java)
fi


str=`file -L $JAVA | grep 64-bit`
if [ -n "$str" ]; then
        JAVA_OPTS="-server -Xms256m -Xmx512m -XX:MetaspaceSize=48m -XX:MaxMetaspaceSize=128m -Xss256k -XX:+HeapDumpOnOutOfMemoryError"
else
        JAVA_OPTS="-server -Xms256m -Xmx512m -XX:MaxMetaspaceSize=128m "
fi

JAVA_OPTS=" $JAVA_OPTS -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -Dfile.encoding=UTF-8"
APP_OPTS="-DappName=icharge-user -Dlogdir=../logs -Dloader.path=../lib/,../conf/"

cd $bin_abs_path

nohup $JAVA $JAVA_OPTS $APP_OPTS -jar $(ls $base/lib/icharge-user-*.jar) > /dev/null 2>&1 &
echo $! > $base/bin/pid

echo "icharge-user is running"

logfile=$base/logs/log.log
if [ "log"x == "$1"x ]; then
  if [ ! -f $logfile ]; then
    for i in {1..10}
    do
      if [ -f $logfile ]; then
        break;
      fi
    sleep .5
    done
  fi
  tail -f $logfile
fi

