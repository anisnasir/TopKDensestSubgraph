package greedy;
import input.StreamEdge;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import struct.NodeMap;
import utility.SetFunctions;
import kcore.KCoreDecomposition;

public class SnowBall implements Serializable, Comparable<SnowBall>{
	private static final long serialVersionUID = 1872315406990468794L;
	double maximalDensity;
	double density;
	int numEdges;
	int numNodes;
	String id;
	HashMap<String,HashSet<String>> graph;
	KCoreDecomposition kCore;
	
	public SnowBall() {
		this.id = UUID.randomUUID().toString();
		this.density = 0;
		this.graph = new HashMap<String,HashSet<String>>();
		this.kCore = new KCoreDecomposition(graph);
	}
	public int getNumNodes() {
		return this.numNodes;
	}
	public int getNumEdges() {
		return this.numEdges;
	}
	boolean containsNode (String src ) {
		return this.graph.containsKey(src);
	}
	void removeNode(String src) {
		HashSet<String> neighbors = new HashSet<String>(graph.get(src));
		if(neighbors != null ) {
			for(String neighbor:neighbors) {
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
	public void addNode(String src, NodeMap nodeMap) {
		kCore.addNode(src);
		HashSet<String> tempNeighbors = nodeMap.getNeighbors(src);
		if(tempNeighbors == null)
			return;

		SetFunctions helper = new SetFunctions();
		HashSet<String> neighbors = new HashSet<String>(helper.intersectionSet(tempNeighbors,graph.keySet()));
		graph.put(src,neighbors);

		for(String neighbor:neighbors) {
			graph.get(neighbor).add(src);
			kCore.addEdge(src, neighbor);
		}
		numEdges+=neighbors.size();
		numNodes++;
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

/*	boolean verifyFirstInVariant(NodeMap nodeMap, HashSet<String> temp, BagOfSnowballs bag) {
		DegreeMap degreeMap = new DegreeMap();
		for(String str:graph.keySet()) {
			degreeMap.addNode(graph.get(str).size(), str);
		}
		System.out.println("start" + degreeMap.map);
		
		int mainCore = this.getMainCore();

		int i =0 ;
		while( i< degreeMap.capacity && (i< mainCore || i< this.maximalDensity)) {
			HashSet<String> nodes = degreeMap.map.get(i);

			if(nodes.size() == 0) {
				i++;
			}
			else { 
				String element= "";
				for(String str:nodes) {
					element = str;
					break;
				}
				nodes.remove(element);

				int globalDegree = nodeMap.getDegree(element);
				if(globalDegree < maximalDensity) {
					HashSet<String> tempNbr = graph.get(element);
					HashSet<String> neighbors ;
					if( tempNbr == null) {
						neighbors = new HashSet<String>();
					}else {
						neighbors = new HashSet<String>(tempNbr);
					}
					for( String neighbor:neighbors) {
						graph.get(neighbor).remove(element);
						graph.get(element).remove(neighbor);

						numEdges--;
						kCore.removeEdge(element, neighbor);
						int neighborDegree = graph.get(neighbor).size();
						degreeMap.decremnetDegree(neighborDegree+1, neighbor);

						if(neighborDegree < i)
							i = neighborDegree;
					}
					graph.remove(element);
					kCore.removeNode(element);
					numNodes--;
					bag.removeNodekCore(element);

				} else {
					HashSet<String> tempNbr = graph.get(element);
					HashSet<String> neighbors ;
					if( tempNbr == null) {
						neighbors = new HashSet<String>();
					}else {
						neighbors = new HashSet<String>(tempNbr);
					}
					for( String neighbor:neighbors) {
						graph.get(neighbor).remove(element);
						graph.get(element).remove(neighbor);

						numEdges--;
						kCore.removeEdge(element, neighbor);
						degreeMap.decremnetDegree(graph.get(neighbor).size()+1, neighbor);

						if(graph.get(neighbor).size() < i)
							i = graph.get(neighbor).size();
					}
					graph.remove(element);
					kCore.removeNode(element);
					numNodes--;
					bag.removeNodekCore(element);
					bag.removeNodekCore(element);
					temp.add(element);
				}
			}
		}
		System.out.println("end" + degreeMap.map);
		return true;
	}*/
	
	boolean verifyFirstInVariant(NodeMap nodeMap, HashSet<String> temp, BagOfSnowballs bag) {

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