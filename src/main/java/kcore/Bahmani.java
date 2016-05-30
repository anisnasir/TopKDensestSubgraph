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

		while(numNodes > 0){
			int i = 0 ;
			while(i<threshold) { 
				HashSet<String> temp = new HashSet<String>(degreeMap.map.get(i));
				while(!temp.isEmpty()) {
					String element= "";
					for(String str:temp) {
						element = str;
						break;
					}
					temp.remove(element);
					degreeMap.removeNode(i, element);
	
					densest.add(element);
					//System.out.println(element + " " + nodeMap.getDegree(element));
	
	
					HashSet<String> neighbors;
	
					if (nodeMap.getNeighbors(element) == null)
						neighbors = new HashSet<String>();
					else 
						neighbors = new HashSet<String>(nodeMap.getNeighbors(element));
	
					if(neighbors.size() > 0 ) {
						for(String neighbor:neighbors) {
							//System.out.println(element+ " " + neighbor);
							nodeMap.removeNode(element, neighbor);
							nodeMap.removeNode(neighbor, element);
							numEdges--;
	
							int nodeDegree = nodeMap.getDegree(neighbor);
							degreeMap.decremnetDegree(nodeDegree+1, neighbor);
						}
					}
					numNodes--;
					if(numNodes == 0) { 
						System.out.println("Density: " + density);
						System.out.println("Densest size: " + densest.size());
						return densest;
					}
				}
				i++;
			}
			double newDensity = numEdges/(double)numNodes;
			if(newDensity > density) {
				density = newDensity;
				densest = new ArrayList<String>();
			}
			threshold = 2*(1+epsilon)*newDensity;
		}
		System.out.println("Density: " + density);
		System.out.println("Densest size: " + densest.size());
		return densest;

		}
	}
