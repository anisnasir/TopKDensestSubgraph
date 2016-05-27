#!/bin/bash
JAR="target/kcore-0.0.1-SNAPSHOT.jar"
inFile="/home/anis/Datasets/com-dblp.ungraph.txt"
command="java -jar ${JAR} ${inFile} "

$command

