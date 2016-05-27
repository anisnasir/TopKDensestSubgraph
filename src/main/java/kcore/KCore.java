package kcore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

public class KCore {
	
	KCore() {
	}
	ArrayList<String> getCore(DegreeMap degreeMap,NodeMap nodeMap, int numEdges, int numNodes) {
		TreeMap<Integer, ArrayList<String>> core = new TreeMap<Integer,ArrayList<String>>();
		int coreNumber =0;
		
		int i = 0 ;
		while(i < degreeMap.capacity){
			ArrayList<String> temp = degreeMap.map.get(i);
			
			if(temp.size() == 0) {
				i++;
			}
			else { 
				String element = temp.remove(0);
				int degree = nodeMap.getDegree(element);
				//System.out.println(element + " " + nodeMap.getDegree(element));
				if(degree > coreNumber)
					coreNumber = degree;
				put(core, element, coreNumber);
				HashSet<String> neighbors;
				
				if (nodeMap.getNeighbors(element) == null)
					neighbors = new HashSet<String>();
				else 
					neighbors = new HashSet<String>(nodeMap.getNeighbors(element));
				
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
		System.out.println("main core " + core.lastKey());
		return core.get(core.lastKey());
		
	}
	
	void put(TreeMap<Integer, ArrayList<String>> map, String element, Integer coreNumber) {
		if(map.containsKey(coreNumber)) {
			map.get(coreNumber).add(element);
		}else {
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(element);
			map.put(coreNumber,temp);
		}
	}
}
