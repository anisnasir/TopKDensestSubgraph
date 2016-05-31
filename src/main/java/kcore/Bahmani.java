package kcore;

import java.util.ArrayList;
import java.util.HashSet;

public class Bahmani {
	double epsilon;
	Bahmani(double epsilon) {
		this.epsilon = epsilon;
	}
	ArrayList<String> getDensest(DegreeMap degreeMap,NodeMap nodeMap) {
		int numNodes = nodeMap.getNumNodes();
		int numEdges = nodeMap.getNumEdges();

		double density = numEdges/(double)numNodes;
		ArrayList<String> densest = new ArrayList<String>();
		
		double threshold = 2*(1+epsilon)*density;

		int nextMin = 0 ;
		double prevNumNodes=numNodes;
		while(numNodes > 0){
			int i = nextMin ;
			HashSet<String> temp = new HashSet<String>();
			while( i <= threshold ) { 
				temp.addAll(new HashSet<String>(degreeMap.map.get(i)));
				i++;
			}
			//System.out.println(temp);
			
			numNodes-=temp.size();
			for(String element: temp) {
				densest.add(element);
				
				//System.out.println(element + " " + nodeMap.getDegree(element));
				HashSet<String> neighbors;

				if (nodeMap.getNeighbors(element) == null)
					neighbors = new HashSet<String>();
				else 
					neighbors = new HashSet<String>(nodeMap.getNeighbors(element));

				if(neighbors.size() > 0 ) {
					degreeMap.removeNode(neighbors.size(), element);
					for(String neighbor:neighbors) {
						//System.out.println(element+ " " + neighbor);
						nodeMap.removeEdge(element, neighbor);
						nodeMap.removeEdge(neighbor, element);
						numEdges--;

						int nodeDegree = nodeMap.getDegree(neighbor);
						degreeMap.decremnetDegree(nodeDegree+1, neighbor);
						
						if(nodeDegree< nextMin) {
							nextMin = nodeDegree;
						}
					}
				}else 
					degreeMap.removeNode(0, element);
			}
			
			if(numNodes == 0) { 
				System.out.println("Density: " + density);
				System.out.println("Densest size: " + densest.size());
				return densest;
			}
			double newDensity = numEdges/(double)numNodes;
			if(newDensity > density) {
				density = newDensity;
				densest = new ArrayList<String>();
			}
			if(numNodes == prevNumNodes) {
				densest.addAll(nodeMap.map.keySet());
				System.out.println("Density: " + density);
				System.out.println("Densest size: " + densest.size());
				return densest;
			}
			else
				prevNumNodes = numNodes;
			threshold = 2*(1+epsilon)*newDensity;
		}
		System.out.println("Density: " + density);
		System.out.println("Densest size: " + densest.size());
		return densest;

		}
	}
