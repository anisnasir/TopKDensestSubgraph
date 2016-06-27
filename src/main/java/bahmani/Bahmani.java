package bahmani;

import input.StreamEdge;
import interfaces.DensestSubgraph;

import java.util.ArrayList;
import java.util.HashSet;

import output.Output;
import struct.DegreeMap;
import struct.NodeMap;
import utility.EdgeHandler;

public class Bahmani implements DensestSubgraph {
	double epsilon;
	public Bahmani(double epsilon) {
		this.epsilon = epsilon;
	}
	public ArrayList<Output> getDensest(DegreeMap degreeMap,NodeMap nodeMap) {
		double density = nodeMap.getNumEdges()/(double)nodeMap.getNumNodes();
		ArrayList<String> densest = new ArrayList<String>();
		
		double threshold = 2*(1+epsilon)*density;
		
		int nextMin = 0 ;
		int prevNumNodes = nodeMap.getNumNodes();
		while(nodeMap.getNumNodes() > 0){
			int i = nextMin ;
			HashSet<String> temp = new HashSet<String>();
			while( i <= threshold && i < degreeMap.capacity ) { 
				temp.addAll(new HashSet<String>(degreeMap.map.get(i)));
				i++;
			}

			for(String element: temp) {
				densest.add(element);
				
				//System.out.println(element + " " + nodeMap.getDegree(element));
				HashSet<String> neighbors;

				if (nodeMap.getNeighbors(element) == null)
					neighbors = new HashSet<String>();
				else 
					neighbors = new HashSet<String>(nodeMap.getNeighbors(element));

				if(neighbors.size() > 0 ) {
					EdgeHandler helper = new EdgeHandler();
					if(neighbors.size() > 0 ) {
						for(String neighbor:neighbors) {
							//System.out.println(element+ " " + neighbor);
							helper.handleEdgeDeletion(new StreamEdge(element,neighbor), nodeMap,degreeMap);

							int nodeDegree = nodeMap.getDegree(neighbor);
							if(nodeDegree < nextMin) {
								nextMin=nodeDegree;
							}	
						}
					}
				}
			}
			
			if(nodeMap.getNumNodes() == 0) { 
				ArrayList<Output> outputArray = new ArrayList<Output>();
				Output output = new Output();
				output.density = density;
				output.size = densest.size();
				output.nodes = densest;
				outputArray.add(output);
				return outputArray;
			}
			double newDensity = nodeMap.getNumEdges()/(double)nodeMap.getNumNodes();
			if(newDensity >= density) {
				density = newDensity;
				densest = new ArrayList<String>();
			}
			if(nodeMap.getNumNodes() == prevNumNodes) {
				densest.addAll(nodeMap.map.keySet());
				ArrayList<Output> outputArray = new ArrayList<Output>();
				Output output = new Output();
				output.density = density;
				output.size = densest.size();
				output.nodes = densest;
				outputArray.add(output);
				return outputArray;
			}
			else
				prevNumNodes = nodeMap.getNumNodes();
			threshold = 2*(1+epsilon)*newDensity;
		}
		ArrayList<Output> outputArray = new ArrayList<Output>();
		Output output = new Output();
		output.density = density;
		output.size = densest.size();
		output.nodes = densest;
		outputArray.add(output);
		return outputArray;

		}
	}
