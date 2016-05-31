package kcore;

import java.util.HashSet;

public class KCore {
	
	KCore() {
	}
	HashSet<String> getCore(DegreeMap degreeMap,NodeMap nodeMap) {
		HashSet<String> core = new HashSet<String>();
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
				temp.remove(element);
				//System.out.println(element);
				int degree = nodeMap.getDegree(element);
				
				//System.out.println(element + " " + nodeMap.getDegree(element));
				if(degree >= coreNumber) {
					coreNumber = degree;
					//System.out.println(core);
					core = new HashSet<String>();
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
						nodeMap.removeEdge(element, neighbor);
						nodeMap.removeEdge(neighbor, element);
											
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
		//System.out.println(core);
		System.out.println("Densest size: " + core.size());
		return core;
		
	}
}
