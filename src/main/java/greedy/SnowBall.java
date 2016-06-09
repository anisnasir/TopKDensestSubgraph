package greedy;
import input.StreamEdge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import struct.NodeMap;
import kcore.KCoreDecomposition;


public class SnowBall implements Serializable, Comparable<SnowBall>{
	private static final long serialVersionUID = 1872315406990468794L;
	double maximalDensity;
	double density;
	int numEdges;
	int numNodes;
	String id;
	
	public int getNumNodes() {
		return this.numNodes;
	}
	public int getNumEdges() {
		return this.numEdges;
	}
	HashMap<String,HashSet<String>> graph;
	KCoreDecomposition kCore;
	public SnowBall() {
		this.id = UUID.randomUUID().toString();
		this.density = 0;
		this.graph = new HashMap<String,HashSet<String>>();
		this.kCore = new KCoreDecomposition(graph);
	}
	
	boolean containsNode (String src ) {
		return this.graph.containsKey(src);
	}
	void removeNode(String src) {
		HashSet<String> neighbors = graph.get(src);
		if(neighbors != null ) {
			ArrayList<String> removeNodes = new ArrayList<String>();
			for(String neighbor:neighbors) {
				removeNodes.add(neighbor);
			}
			for( String neighbor:removeNodes) {
				graph.get(neighbor).remove(src);
				graph.get(src).remove(neighbor);
				numEdges--;
				kCore.removeEdge(src, neighbor);
			}
		}
		graph.remove(src);
		kCore.removeNode(src);
		numNodes--;
	}
	void setMaximalDensity(double externalDensity, NodeMap nodeMap) {
		this.maximalDensity = Math.max(density,externalDensity);
	}
	
	Set<String> getNodes() {
		return graph.keySet();
	}
	public double getDensity() {
		if(numNodes == 0)
			return 0;
		
		this.density = numEdges/(double)numNodes;
		//density = approximator.getApproximation((HashMap<String,HashSet<String>>)graph.clone(), numEdges, numNodes);
		return this.density;
	}
	
	int getIntersection (HashSet<String> nodes) {
		if(nodes == null)
			return 0;
		
		//return Sets.intersection(this.graph.keySet(),nodes).size();
		return intersection(this.graph.keySet(),nodes);
	}
	
	public int intersection (Set<String> set1, Set<String> set2) {
		Set<String> a;
		Set<String> b;
		int counter = 0;
		if (set1.size() <= set2.size()) {
			a = set1;
			b = set2; 
		} else {
			a = set2;
			b = set1;
		}
		for (String e : a) {
			if (b.contains(e)) {
				counter++;
			} 
		}
		return counter;
	}
	public HashSet<String> intersectionSet (Set<String> set1, Set<String> set2) {
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
	
	public void addNode(String src, NodeMap nodeMap) {
		HashSet<String> tempNeighbors = nodeMap.getNeighbors(src);
		if(tempNeighbors == null)
			return;
		
		HashSet<String> neighbors = new HashSet<String>(intersectionSet(tempNeighbors,graph.keySet()));
		
		graph.put(src, neighbors);
		kCore.addNode(src);
		for(String neighbor:neighbors) {
			graph.get(neighbor).add(src);
			kCore.addEdge(src, neighbor);
		}
		numEdges+=neighbors.size();
		numNodes++;
		return;
	}
	public void ensureFirstInVariant(NodeMap nodeMap, HashSet<String> temp, BagOfSnowballs bag) {
		
		while(!verifyFirstInVariant(nodeMap, temp,bag)) {
			/* 1. remove all the node with degrees lower than the density
			 * 2. remove all the nodes with degree less than the maximal density
			 * 3. remove all the nodes with core number lower than the maximum core
			 */
			getDensity();
		}
	}
	
	boolean verifyFirstInVariant(NodeMap nodeMap, HashSet<String> temp, BagOfSnowballs bag) {
		/*apply Charikar in bulk
		 * removing all the nodes with degree 
		 * lower than the density
		 */
		boolean flag = true;
		double newDensity = getDensity();
		HashSet<String> removeNodes = new HashSet<String>();
	
		for(String str:graph.keySet()) {
			int globalDegree = nodeMap.getDegree(str);
			HashSet<String> neighbors = graph.get(str);
			int localDegree = neighbors.size();
			if(globalDegree < maximalDensity) {
				removeNodes.add(str);
				flag =  false;
			}else if (localDegree < newDensity || kCore.getKCore(str) < this.getMainCore()) {
				removeNodes.add(str);
				temp.add(str);
				flag = false;
			}
		}
		if(!flag)
			for(String str:removeNodes) {
				removeNode(str);
				if(!temp.contains(str))
					bag.removeNodekCore(str);
			}
		return flag;
	}
	boolean contains(String src) {
		return graph.containsKey(src);
	}
	
	int getMainCore() {
		return kCore.mainCore();
	}
	
	void merge(SnowBall newSnowBall, NodeMap nodeMap) {
		Set<String> nodes = newSnowBall.getNodes();
		for(String node:nodes) {
			this.addNode(node, nodeMap);
		}
		
	}
	
	public void addEdge(StreamEdge edge) {
		String src = edge.getSource();
		String dst = edge.getDestination();
		if(graph.containsKey(src) )
			if(!graph.get(src).contains(dst)) {
				if(graph.containsKey(dst))
					if(!graph.get(dst).contains(src)) {
						graph.get(src).add(dst);
						graph.get(dst).add(src);
						numEdges++;
						kCore.addEdge(src, dst);
					}
			}
	}
	
	void print() {
		
		for (String name: graph.keySet()){

            String key =name;
            HashSet<String> value = graph.get(key);  
            System.out.print(key + " ["); 
            for(String str:value) {
            	System.out.print("<"+str+","+kCore.getKCore(str)+">");
            }
            System.out.println("]");
            
		} 
		
		
	}
	
	boolean containsEdge(StreamEdge edge) {
		String src = edge.getSource();
		String dst = edge.getDestination();
		if(graph.containsKey(src) && graph.containsKey(dst))
			if(graph.get(src).contains(dst) && graph.get(dst).contains(src))
				return true;
		
		return false;
	}
	
	public void removeEdge(StreamEdge edge,NodeMap nodeMap) {
		String src = edge.getSource();
		String dst = edge.getDestination();
		
		graph.get(src).remove(dst);
		graph.get(dst).remove(src);
		kCore.removeEdge(src, dst);
		
		numEdges--;
	}
	boolean isEmpty() {
		return (this.getNumEdges()== 0 && this.getNumNodes() == 0);
	}

	@Override
	public int compareTo(SnowBall o) {
		if (o.getDensity() == this.getDensity())
			return 0;
		else if (this.getDensity() > o.getDensity())
			return 1;
		else 
			return -1;
	}
	
	public boolean equals(SnowBall o) {
		return this.id.equals(o.id);
	}
	
	public int  getCoreNumber(String src) {
		return this.kCore.getKCore(src);
	}
}
