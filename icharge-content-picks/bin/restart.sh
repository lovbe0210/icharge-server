#!/bin/bash

args=$@
binpath=$(cd `dirname $0`; pwd)
/bin/bash $binpath/stop.sh $args
/bin/bash $binpath/startup.sh $args
