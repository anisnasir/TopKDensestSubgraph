package kcore;

import input.StreamEdge;
import interfaces.DensestSubgraph;

import java.util.ArrayList;
import java.util.HashSet;

import output.Output;
import struct.DegreeMap;
import struct.NodeMap;
import utility.EdgeHandler;

public class KCore implements DensestSubgraph{
	
	public KCore() {
	}
	public ArrayList<Output> getDensest(DegreeMap degreeMap,NodeMap nodeMap) {
		ArrayList<String> core = new ArrayList<String>();
		int coreNumber =0;
		double density = 0;
		
		//System.out.println(degreeMap.capacity);
		int i = 0 ;
		while(i < degreeMap.capacity){
			HashSet<String> temp = degreeMap.map.get(i);
			//System.out.println(temp);
			if(temp.size() == 0) {
				i++;
			}
			else { 
				String element= "";
				for(String str:temp) {
					element = str;
					break;
				}
				temp.remove(element);
				//System.out.println(element);
				int degree = nodeMap.getDegree(element);
				
				//System.out.println(element + " " + nodeMap.getDegree(element));
				if(degree > coreNumber) {
					coreNumber = degree;
					//System.out.println(core);
					core = new ArrayList<String>();
					density = nodeMap.getNumEdges()/(double)nodeMap.getNumNodes();
					//System.out.println(i + " " + nodeMap.getNumEdges() + " " + nodeMap.getNumNodes() + " "   + nodeMap.map.keySet());
				} 
				core.add(element);
				HashSet<String> neighbors;
				
				if (nodeMap.getNeighbors(element) == null)
					neighbors = new HashSet<String>();
				else 
					neighbors = new HashSet<String>(nodeMap.getNeighbors(element));
				
				EdgeHandler helper = new EdgeHandler();
				if(neighbors.size() > 0 ) {
					for(String neighbor:neighbors) {
						//System.out.println(element+ " " + neighbor);
						helper.handleEdgeDeletion(new StreamEdge(element,neighbor), nodeMap,degreeMap);
						
						int nodeDegree = nodeMap.getDegree(neighbor);
						if(nodeDegree < i) {
							i=nodeDegree;
						}	
					}
				}
				
				
			}	
		}
		ArrayList<Output> outputArray = new ArrayList<Output>();
		Output output = new Output();
		output.coreNum = coreNumber;
		output.density = density;
		output.size = core.size();
		output.nodes = core;
		outputArray.add(output);
		return outputArray;
		
	}
}
