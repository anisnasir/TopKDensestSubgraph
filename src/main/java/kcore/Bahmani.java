package kcore;

import java.util.ArrayList;
import java.util.HashSet;

public class Bahmani {
	double epsilon;
	Bahmani(double epsilon) {
		this.epsilon = epsilon;
	}
	ArrayList<String> getDensest(DegreeMap degreeMap,NodeMap nodeMap) {
		int numEdges = nodeMap.numEdges;
		int numNodes = nodeMap.getNumNodes();
			
		double density = numEdges/(double)numNodes;
		double threshold = 2*(1+epsilon)*density;
		
		int counter  = 0 ;
		int PRINT_INTERVAL = 1000000;
		long simulationStartTime = System.currentTimeMillis();

		ArrayList<String> densest = new ArrayList<String>(nodeMap.map.keySet());
		
		int i = 0 ;
		while(i < degreeMap.capacity){
			while(i < threshold && i<degreeMap.capacity) {
				HashSet<String> temp = degreeMap.map.get(i);
			
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
					
					if (++counter % PRINT_INTERVAL == 0) {
						System.out.println("Read " + counter/PRINT_INTERVAL
								+ "M edges.\tSimulation time: "
								+ (System.currentTimeMillis() - simulationStartTime)
								/ 1000 + " seconds");
						
					}
					
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
							if(nodeDegree < i) {
								i=nodeDegree;
							}	
						}
					}
					numNodes--;
				}
			}
			if(numNodes == 0 ) {
				System.out.println(density);
				return densest;
			}
			double newDensity = numEdges/(double)numNodes;
			if(density < newDensity) {
				density = newDensity;
				densest = new ArrayList<String>(nodeMap.map.keySet());
			}
			threshold = 2*(1+epsilon)*newDensity;
		}
		System.out.println(density);
		return densest;
		
	}

}
