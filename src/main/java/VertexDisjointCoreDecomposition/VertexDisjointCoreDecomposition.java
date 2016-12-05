package VertexDisjointCoreDecomposition;

import input.StreamEdge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import struct.DegreeMap;
import struct.NodeMap;
import utility.EdgeHandler;

public class VertexDisjointCoreDecomposition{

	public VertexDisjointCoreDecomposition() {
	}
	public HashMap<String,Integer> calculateKCore(DegreeMap degreeMap,NodeMap nodeMap) {
		HashMap<String,Integer> core = new HashMap<String,Integer>();
		int coreNumber =0;
		double density = 0;

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
				if(degree > coreNumber) {
					coreNumber = degree;
					//System.out.println(core);
					density = nodeMap.getNumEdges()/(double)nodeMap.getNumNodes();
					//System.out.println(i + " " + nodeMap.getNumEdges() + " " + nodeMap.getNumNodes() + " "   + nodeMap.map.keySet());
				} 
				core.put(element,coreNumber);
				HashSet<String> neighbors;

				if (nodeMap.getNeighbors(element) == null)
					neighbors = new HashSet<String>();
				else 
					neighbors = new HashSet<String>(nodeMap.getNeighbors(element));

				if(neighbors.size() > 0 ) {
					for(String neighbor:neighbors) {
						//System.out.println(element+ " " + neighbor);
						int neighborDegree = nodeMap.getDegree(neighbor);
						degreeMap.decremnetDegree(neighborDegree, neighbor);

						//removes from each others neighbor table
						nodeMap.removeEdge(element, neighbor);
						nodeMap.removeEdge(neighbor, element);

						int nodeDegree = nodeMap.getDegree(neighbor);

						if(nodeDegree < i) {
							i=nodeDegree;
						}	
					}
				}


			}	
		}

		return core;	
	}
	public HashMap<String,Integer> calculateNodeDisjointKCore(DegreeMap degreeMap,NodeMap nodeMap) {
		HashMap<String,Integer> core = calculateKCore(degreeMap.getCopy(), nodeMap.getCopy());
		//System.out.println(core);
		int count = 0 ;
		while(removeBoundaryEdges(degreeMap,nodeMap, core )) {
			System.out.println(++count);
			core = calculateKCore(degreeMap.getCopy(), nodeMap.getCopy());
		}
		return core;
	}
	
	boolean removeBoundaryEdges(DegreeMap degreeMap,NodeMap nodeMap, HashMap<String,Integer> core ) {
		ArrayList<String> nodes = new ArrayList<String>(nodeMap.map.keySet());
		EdgeHandler utility = new EdgeHandler();
		
		boolean flag = false;
		for(String node: nodes) {
			HashSet<String> temp = nodeMap.getNeighbors(node);
			if(temp != null) {
				HashSet<String> neighbors = new HashSet<String>(temp);
				if(neighbors != null) {
					for(String neighbor: neighbors) {
						if(core.get(node) != core.get(neighbor)) {
							utility.handleEdgeDeletion(new StreamEdge(node,neighbor), nodeMap, degreeMap);
							flag = true;
						}
					}
				} 
			}
		}
		return flag;
		
	}
	
}
