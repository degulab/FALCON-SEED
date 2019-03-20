#!/bin/sh
MY_DIRNAME=$(dirname "$0")
cd "$MY_DIRNAME"
java -Dsun.jnu.encoding=UTF-8 -Dfile.encoding=UTF-8 -jar moquette-broker-0.1-jar-with-dependencies.jar
