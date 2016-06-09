package epasto;

import input.StreamEdge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import main.DensestSubgraph;
import output.Output;
import struct.DegreeMap;
import struct.NodeMap;
import utility.EdgeHandler;


public class EpastoTopK implements DensestSubgraph{
	int k; 
	double epsilon;
	EpastoFullyDyn [] epastoK;
	EpastoDensest [] densestK;
	NodeMap[] nodeMap;
	EdgeHandler utility;
	boolean LOGGING;
	
	public EpastoTopK(int k, double epsilon) {
		this.k = k ;
		this.epsilon = epsilon;
		epastoK = new EpastoFullyDyn[k];
		for(int i=0; i < k ; i ++) 
			epastoK[i] = new EpastoFullyDyn(epsilon);
		
		densestK = new EpastoDensest[k];
		for(int i=0; i < k ; i ++) 
			densestK[i] = new EpastoDensest();
		
		nodeMap = new NodeMap[k];
		for(int i =0 ;i < k;i++) {
			nodeMap[i] = new NodeMap();
		}
		utility = new EdgeHandler();
		
	}
	
	public void addEdge(StreamEdge edge) {
		String src = edge.getSource();
		String dst = edge.getDestination();
		
		if(nodeMap[0].contains(edge))
			return ;
		
		HashSet<String> oldDensest = new HashSet<String>();
		HashSet<String> allDensest = new HashSet<String>();
		
		EdgeHandler utility = new EdgeHandler();
		
		int i =0 ;
		while( i<k) {
			if(!allDensest.contains(src) && !allDensest.contains(dst)) {
				utility.handleEdgeAddition(edge, nodeMap[i]);
				epastoK[i].MainFullyDynamic(edge, nodeMap[i], EpastoOp.ADD);
			} else if (!allDensest.contains(src)) {
				nodeMap[i].map.put(src, new HashSet<String>());
			}else if (!allDensest.contains(dst)) {
				nodeMap[i].map.put(dst, new HashSet<String>());
			}
			
			if(i != 0 ) {
				for(String str: oldDensest) {
					if(!allDensest.contains(str)) {
						HashSet<String> neighbors = nodeMap[0].getNeighbors(str);
						if(neighbors!= null)
							for(String neighbor:neighbors) {
								if(!allDensest.contains(neighbor)) {
									utility.handleEdgeAddition(new StreamEdge(str,neighbor), nodeMap[i]);
									epastoK[i].MainFullyDynamic(new StreamEdge(str,neighbor), nodeMap[i], EpastoOp.ADD);
								}
							}
					}
				}
			}
			
			for(String str:allDensest) {
				HashSet<String> neighbors = nodeMap[i].getNeighbors(str);
				if(neighbors!=null) {
					neighbors = new HashSet<String>(neighbors);
					for(String neighbor:neighbors) { 
						utility.handleEdgeDeletion(new StreamEdge(str,neighbor), nodeMap[i]);
						epastoK[i].MainFullyDynamic(new StreamEdge(str,neighbor), nodeMap[i], EpastoOp.REMOVE);
					}
				}
			}
			
			EpastoDensest tempDensest =epastoK[i].getDensest();
			
			if(tempDensest == null)
				return;
			if(densestK[i].densest.size() != 0) {
				HashSet<String> common = this.intersectionSet(tempDensest.densest.keySet(), densestK[i].densest.keySet());
				
				oldDensest.addAll(densestK[i].densest.keySet());
				oldDensest.removeAll(common); 
			}
			allDensest.addAll(new ArrayList<String>(tempDensest.getDensest().keySet()));
			densestK[i] = tempDensest;
			i++;
		}
 		
	}
	
	public boolean removeEdge(StreamEdge edge) {
		HashSet<String> oldDensest = new HashSet<String>();
		HashSet<String> allDensest = new HashSet<String>();
		
		EdgeHandler utility = new EdgeHandler();
		
		int i =0 ;
		while( i<k) {
			utility.handleEdgeDeletion(edge, nodeMap[i]);
			epastoK[i].MainFullyDynamic(edge, nodeMap[i], EpastoOp.REMOVE);
			i++;
		
			
		for(String str: oldDensest) {
			if(!allDensest.contains(str)) {
				HashSet<String> neighbors = nodeMap[0].getNeighbors(str);
				if(neighbors != null) 
					for(String neighbor:neighbors) {
						if(!allDensest.contains(neighbor)) {
							utility.handleEdgeAddition(new StreamEdge(str,neighbor), nodeMap[i]);
							epastoK[i].MainFullyDynamic(new StreamEdge(str,neighbor), nodeMap[i], EpastoOp.ADD);
						}
					}
			}
		}
			if(i != 0) {
				for(String str:allDensest) {
					HashSet<String> neighbors = nodeMap[i].getNeighbors(str);
					if(neighbors!=null) {
						neighbors = new HashSet<String>(neighbors);
						for(String neighbor:neighbors) {
							if(nodeMap[i].map.containsKey(neighbor)) {
							utility.handleEdgeDeletion(new StreamEdge(str,neighbor), nodeMap[i]);
							epastoK[i].MainFullyDynamic(new StreamEdge(str,neighbor), nodeMap[i], EpastoOp.REMOVE);
							}
						}
					}
				}
			}
			
			EpastoDensest tempDensest = epastoK[i].getDensest();
			
			if(densestK[i].densest.size() != 0) {
				HashSet<String> common = this.intersectionSet(tempDensest.densest.keySet(), densestK[i].densest.keySet());
				allDensest.addAll(tempDensest.densest.keySet());
				
				oldDensest.addAll(densestK[i].densest.keySet());
				oldDensest.removeAll(common); 
			}else {
				densestK[i] = tempDensest;
				allDensest.addAll(tempDensest.densest.keySet());
			}
			
			i++;
		}
		return true;
	}
	public HashSet<String> intersectionSet (Set<String> set1, Set<String> set2) {
		if(set1 == null)
			return new HashSet<String>();
		else if (set2 == null)
			return new HashSet<String>();
		
		Set<String> a;
		Set<String> b;
		HashSet<String> returnSet = new HashSet<String>();
		if (set1.size() <= set2.size()) {
			a = set1;
			b = set2; 
		} else {
			a = set2;
			b = set1;
		}
		for (String e : a) {
			if (b.contains(e)) {
				returnSet.add(e);
			} 
		}
		return returnSet;
	}
	
	EpastoDensest getDensest() { 
		return this.densestK[0];
	}
	EpastoDensest[] getTopK(){
		return densestK;
	}

	@Override
	public ArrayList<Output> getDensest(DegreeMap degreeMap, NodeMap nodeMap) {
		// TODO Auto-generated method stub
		
		ArrayList<Output> outputArray = new ArrayList<Output>();
		
		for(int i = 0 ; i < k ;i ++ ) {
			Output output = new Output();
			output.coreNum = 0;
			output.density = this.densestK[i].getDensity();
			output.size = this.densestK[i].densest.size();
			output.nodes = new ArrayList<String>(this.densestK[i].densest.keySet());
			outputArray.add(output);
		}
		return outputArray;
	}
	
	 ArrayList<Output> getDummy() { 
		ArrayList<Output> arr = new ArrayList<Output>();
		Output returnOut = new Output();
		returnOut.setCoreNum(0);
		returnOut.setDensity(0.0);
		returnOut.setSize(0);
		returnOut.setTimeTaken(0);
		returnOut.setNodes(new ArrayList<String>());
		arr.add(returnOut);
		return arr;
	}

}
