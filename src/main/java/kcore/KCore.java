package kcore;

import java.util.ArrayList;
import java.util.HashSet;

public class KCore {
	
	KCore() {
	}
	ArrayList<String> getCore(DegreeMap degreeMap,NodeMap nodeMap) {
		ArrayList<String> core = new ArrayList<String>();
		int coreNumber =0;
		
		int i = 0 ;
		while(i < degreeMap.capacity){
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
				int degree = nodeMap.getDegree(element);
				temp.remove(element);
				//System.out.println(element + " " + nodeMap.getDegree(element));
				if(degree >= coreNumber) {
					coreNumber = degree;
					core = new ArrayList<String>();
				}
				core.add(element);
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
											
						int nodeDegree = nodeMap.getDegree(neighbor);
						degreeMap.decremnetDegree(nodeDegree+1, neighbor);
						if(nodeDegree < i) {
							i=nodeDegree;
						}	
					}
				}	
			}	
		}
		System.out.println("main core " + coreNumber);
		return core;
		
	}
}
