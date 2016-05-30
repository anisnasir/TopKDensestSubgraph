package kcore;

import java.util.ArrayList;
import java.util.HashSet;

public class Charikar {
	
	Charikar() {
	}
	ArrayList<String> getDensest(DegreeMap degreeMap,NodeMap nodeMap) {
		int numNodes = nodeMap.getNumNodes();
		int numEdges = nodeMap.numEdges;
		
		System.out.println("num edges" + numEdges + " num nodes: " + numNodes);
		double density = numEdges/(double)numNodes;
		ArrayList<String> densest = new ArrayList<String>();
		
		int counter  = 0 ;
		int PRINT_INTERVAL = 1000000;
		long simulationStartTime = System.currentTimeMillis();
		
		
		int i = 0 ;
		while(i < degreeMap.capacity){
			System.out.println(density);
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
				
				densest.add(element);
				//System.out.println(element + " " + nodeMap.getDegree(element));
				
				if (++counter % PRINT_INTERVAL == 0) {
					System.out.println("Read " + counter/PRINT_INTERVAL
							+ "M edges.\tSimulation time: "
							+ (System.currentTimeMillis() - simulationStartTime)
							/ 1000 + " seconds");
					
				}
				
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
				if(numNodes == 0) {
					System.out.println("Density: " + density);

					System.out.println("Densest size: " + densest.size());
					return densest;
				}
				
				double newDensity = numEdges/(double)numNodes;
				if(newDensity >density) {
					density = newDensity;
					densest = new ArrayList<String>();
				}
				
			}	
		}
		System.out.println("Density: " + density);
		System.out.println("Densest size: " + densest.size());
		return densest;
		
	}
}
