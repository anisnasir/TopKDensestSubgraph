package kcore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class EpastoTopK implements DensestSubgraph{
	int k; 
	double epsilon;
	EpastoFullyDyn [] epastoK;
	EpastoDensest [] densestK;
	NodeMap[] nodeMap;
	EdgeHandler utility;
	boolean LOGGING;
	
	EpastoTopK(int k, double epsilon) {
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
	
	boolean addEdge(StreamEdge edge) {
		String src = edge.getSource();
		String dst = edge.getDestination();
		
		HashSet<String> toAdd = new HashSet<String>();
		HashSet<String> toRemove = new HashSet<String>();
		
		System.out.println("+" + src + " " + dst);
		int i =0 ;
		while(i < k ) {
			if(!toRemove.contains(src) && !toRemove.contains(dst)) {
				utility.handleEdgeAddition(edge, nodeMap[i]);
				epastoK[i].MainFullyDynamic(edge, nodeMap[i], EpastoOp.ADD);
			}
			
			for(String str: toAdd) {
				if(!toRemove.contains(str)) { 
					HashSet<String> neighbors = new HashSet<String>(nodeMap[0].getNeighbors(str));
					if(neighbors != null) {
						for(String neighbor: neighbors) {
							if(!toRemove.contains(neighbor) && nodeMap[i].map.containsKey(neighbor)) {
								utility.handleEdgeAddition(new StreamEdge(str,neighbor), nodeMap[i]);
								epastoK[i].MainFullyDynamic(new StreamEdge(str,neighbor), nodeMap[i], EpastoOp.ADD);
							}
						}
					}
				}
			}
			
			for(String str: toRemove) {
				HashSet<String> neighbors = new HashSet<String>(nodeMap[0].getNeighbors(str));
				if(neighbors != null) {
					for(String neighbor: neighbors) {
						if(nodeMap[i].contains(new StreamEdge(str,neighbor))) {
							System.out.println("removing " + str + " " + neighbor);
							utility.handleEdgeDeletion(new StreamEdge(str,neighbor), nodeMap[i]);
							epastoK[i].MainFullyDynamic(new StreamEdge(str,neighbor), nodeMap[i], EpastoOp.REMOVE);
						}
					}
				}
			}
			

			EpastoDensest tempDensest = epastoK[i].getDensest();
			
			if(tempDensest!=null) {
				System.out.println(nodeMap[i].map);
				System.out.println(tempDensest.getDensest().keySet());
				toRemove.addAll(new HashSet<String>(tempDensest.densest.keySet()));
					if(!densestK[i].densest.isEmpty())
						toAdd.addAll(new HashSet<String>(densestK[i].densest.keySet()));
					densestK[i] = tempDensest;
				
			}else {
				densestK[i] = new EpastoDensest();
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
	boolean removeEdge(StreamEdge edge) {
		return true;
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
		for(int i = 0; i<densestK.length ; i++) {
			Output output = new Output();
			output.coreNum = 0;
			output.density = this.densestK[i].getDensity();
			output.size = this.densestK[i].densest.size();
			output.nodes = new ArrayList<String>(this.densestK[i].densest.keySet());
			outputArray.add(output);
		}
		return outputArray;
	}

}
