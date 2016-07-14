#/bin/bash
JAR="target/kcore-0.0.1-SNAPSHOT.jar"
simulatorType=10
inDir="/home/anis/Datasets/"
inFile="com-lj-1.5.ungraph"
window=1000001
epsilon=0.1
algo="greedy"
k=10
command="java -d64 -Xms2g -Xmx16g -jar ${JAR} ${simulatorType} ${inDir}${inFile}.txt ${window} ${epsilon} output_${algo}_${inFile}_${window}_${epsilon}_${k}.out ${k} output_logs/${algo}/k${k}/"


$command
