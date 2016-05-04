#!/bin/bash

cd src/

javac -classpath ../lib/json-simple-1.1.1.jar *.java

jar cvfm ../BilibiliLiveDanmaku.jar MANIFEST.MF  *.class ../lib/*.jar

cd ..

java -jar BilibiliLiveDanmaku.jar
