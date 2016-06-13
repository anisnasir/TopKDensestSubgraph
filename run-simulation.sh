#!/bin/bash
JAR="target/kcore-0.0.1-SNAPSHOT.jar"
simulatorType=10
inDir="/home/anis/Datasets/"
inFile="com-amazon.ungraph"
window=100000
epsilon=0.05
algo="greedy"
k=10
command="java -Xms1024m -Xmx8192m -jar ${JAR} ${simulatorType} ${inDir}${inFile}.txt ${window} ${epsilon} output_${algo}_${inFile}_${window}_${epsilon}_${k}.out ${k} output_logs/${algo}/k${k}/"


$command

