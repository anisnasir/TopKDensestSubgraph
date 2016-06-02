package kcore;

import java.util.ArrayList;
import java.util.HashSet;

public class KCore {
	
	KCore() {
	}
	Output getCore(DegreeMap degreeMap,NodeMap nodeMap) {
		ArrayList<String> core = new ArrayList<String>();
		int coreNumber =0;
		
		int numEdges = 0;
		int numNodes = 0;
		
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
				if(degree >= coreNumber) {
					coreNumber = degree;
					//System.out.println(core);
					core = new ArrayList<String>();
					numNodes = 0;
					numEdges = 0;
				}
				core.add(element);
				numNodes++;
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
						numEdges++;
						
						int nodeDegree = nodeMap.getDegree(neighbor);
						degreeMap.decremnetDegree(nodeDegree+1, neighbor);
						if(nodeDegree < i) {
							i=nodeDegree;
						}	
					}
				}
				
				
			}	
		}
		Output output = new Output();
		output.coreNum = coreNumber;
		output.density = (numEdges/(double)numNodes);
		output.size = core.size();
		output.nodes = core;
		return output;
		
	}
}
