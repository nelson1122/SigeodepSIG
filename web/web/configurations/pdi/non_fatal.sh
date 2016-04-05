#!/bin/bash   
KITCHEN_HOME='/opt/pentaho/data-integration/kitchen.sh'

$KITCHEN_HOME /file load_non_fatal.kjb 50
$KITCHEN_HOME /file load_non_fatal.kjb 51
$KITCHEN_HOME /file load_non_fatal.kjb 52
$KITCHEN_HOME /file load_non_fatal.kjb 54
$KITCHEN_HOME /file load_non_fatal.kjb 53

echo 'Done!!!'
