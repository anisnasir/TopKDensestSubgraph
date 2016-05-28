#!/bin/bash
JAR="target/kcore-0.0.1-SNAPSHOT.jar"
inFile="/home/anis/Datasets/soc-LiveJournal1.txt"
command="java -Xms1024m -Xmx8192m -jar ${JAR} 0 ${inFile} 0.005 "

$command

