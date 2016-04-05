#!/bin/bash   
KITCHEN_HOME='/opt/pentaho/data-integration/kitchen.sh'

$KITCHEN_HOME /file load_fatal.kjb 10
$KITCHEN_HOME /file load_fatal.kjb 11
$KITCHEN_HOME /file load_fatal.kjb 12
$KITCHEN_HOME /file load_fatal.kjb 13

echo 'Done!!!'
