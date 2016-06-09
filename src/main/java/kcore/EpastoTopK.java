package kcore;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;


public class EpastoTopK {
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
		String src  = edge.getSource();
		String dst = edge.getDestination();
		
		System.out.println("+"+ src+ "  " + dst);
		
		HashSet<String> added = new HashSet<String>();
		HashSet<String> removed = new HashSet<String>();
		
		if(nodeMap[0].contains(edge))
			return false;
 		
		int i =0;
 		
		while (i < k) {
			if(!removed.contains(src) && !removed.contains(dst)) {
				utility.handleEdgeAddition(edge, nodeMap[i]);
				epastoK[i].MainFullyDynamic(edge, nodeMap[i], EpastoOp.ADD);
				added.add(src);
				added.add(dst);
			}else if (!removed.contains(src)) {
				nodeMap[i].map.put(src, new HashSet<String>());
				added.add(src);
			}else if (!removed.contains(dst)) {
				nodeMap[i].map.put(dst, new HashSet<String>());
				added.add(dst);
			}else {
				return true;
			}
			
			if(i != 0 ) {
				for(String str: added) {
					if(!removed.contains(str)) {
						HashSet<String> neighbors = nodeMap[i].getNeighbors(str);
						if(neighbors!= null)
							for(String neighbor:neighbors) {
								utility.handleEdgeAddition(new StreamEdge(str,neighbor), nodeMap[i]);
								epastoK[i].MainFullyDynamic(new StreamEdge(str,neighbor), nodeMap[i], EpastoOp.ADD);
							}
					}
				}
			}
			
			for(String str:removed) {
				HashSet<String> neighbors = nodeMap[i].getNeighbors(str);
				if(neighbors!=null) {
					neighbors = new HashSet<String>(neighbors);
					for(String neighbor:neighbors) { 
						utility.handleEdgeDeletion(new StreamEdge(str,neighbor), nodeMap[i]);
						epastoK[i].MainFullyDynamic(new StreamEdge(str,neighbor), nodeMap[i], EpastoOp.REMOVE);
					}
				}
			}
		
			EpastoDensest tempDensest = epastoK[i].getDensest();
			if(tempDensest == null)
				return true;
			if(densestK[i].densest.size() != 0) {
				HashSet<String> common = this.intersectionSet(tempDensest.densest.keySet(), densestK[i].densest.keySet());
				removed.addAll(tempDensest.densest.keySet());
				
				added.addAll(densestK[i].densest.keySet());
				added.removeAll(common); 
			}else {
				densestK[i] = tempDensest;
				removed.addAll(tempDensest.densest.keySet());
			}
			
			densestK[i] = tempDensest;
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
		HashSet<String> added = new HashSet<String>();
		HashSet<String> removed = new HashSet<String>();
		System.out.println("-" + edge.getSource() + " " + edge.getDestination());
 		
		if(!nodeMap[0].contains(edge))
			return false;
		
 		int i =0;
		while (i < k) {
			utility.handleEdgeDeletion(edge, nodeMap[i]);
			epastoK[i].MainFullyDynamic(edge, nodeMap[i], EpastoOp.REMOVE);
			removed.add(edge.getSource());
			removed.add(edge.getDestination());
			
			for(String str: added) {
				if(!removed.contains(str)) {
					HashSet<String> neighbors = nodeMap[i].getNeighbors(str);
					if(neighbors != null) 
						for(String neighbor:neighbors) {
							utility.handleEdgeAddition(new StreamEdge(str,neighbor), nodeMap[i]);
							epastoK[i].MainFullyDynamic(new StreamEdge(str,neighbor), nodeMap[i], EpastoOp.ADD);
						}
				}
			}
			if(i != 0) {
				for(String str:removed) {
					HashSet<String> neighbors = nodeMap[i].getNeighbors(str);
					if(neighbors!=null) {
						neighbors = new HashSet<String>(neighbors);
						for(String neighbor:neighbors) { 
							utility.handleEdgeDeletion(new StreamEdge(str,neighbor), nodeMap[i]);
							epastoK[i].MainFullyDynamic(new StreamEdge(str,neighbor), nodeMap[i], EpastoOp.REMOVE);
						}
					}
				}
			}
			
			EpastoDensest tempDensest = epastoK[i].getDensest();
			
			if(densestK[i].densest.size() != 0) {
				HashSet<String> common = this.intersectionSet(tempDensest.densest.keySet(), densestK[i].densest.keySet());
				removed.addAll(tempDensest.densest.keySet());
				
				added.addAll(densestK[i].densest.keySet());
				added.removeAll(common); 
			}else {
				densestK[i] = tempDensest;
				removed.addAll(tempDensest.densest.keySet());
			}
			
			i++;
		}
		return true;
	}
	
	EpastoDensest getDensest() { 
		return this.densestK[0];
	}
	EpastoDensest[] getTopK(){
		return densestK;
	}

}
