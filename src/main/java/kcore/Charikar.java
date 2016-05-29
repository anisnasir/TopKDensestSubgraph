package kcore;

import java.util.ArrayList;
import java.util.HashSet;

public class Charikar {
	Charikar() {
	}
	ArrayList<String> getDensest(DegreeMap degreeMap,NodeMap nodeMap) {
		int numEdges = nodeMap.numEdges;
		int numNodes = nodeMap.getNumNodes();
		double density = numEdges/(double)numNodes;

		ArrayList<String> densest = new ArrayList<String>(nodeMap.map.keySet());

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
				//System.out.println(element + " " + nodeMap.getDegree(element));
				HashSet<String> neighbors;

				if (nodeMap.getNeighbors(element) == null)
					neighbors = new HashSet<String>();
				else 
					neighbors = new HashSet<String>(nodeMap.getNeighbors(element));

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
				numNodes--;
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
		}
		System.out.println(density);
		return densest;

	}
}
