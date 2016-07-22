package greedy;
import input.StreamEdge;
import interfaces.DensestSubgraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import kcorelinear.KCoreTraversal;
import output.Output;
import struct.DegreeMap;
import struct.NodeMap;
import utility.SetFunctions;


public class BagOfSnowballs implements DensestSubgraph{
	public Bag<SnowBall> bag;
	HashMap<String,HashSet<String>> bagGraph;
	public KCoreTraversal kCore;
	double maximalDensity = 0;
	int count = 0; 
	int k ;
	
	public BagOfSnowballs(int k ) {
		bagGraph = new HashMap<String,HashSet<String>> ();
		kCore = new KCoreTraversal(bagGraph);
		bag = new Bag<SnowBall>();
		this.k = k;
	}
	public void addNodeKCore(String node, NodeMap nodeMap) {
		//kCore.addNode(node);
		HashSet<String> neighbors = nodeMap.getNeighbors(node);
		SetFunctions helper = new SetFunctions();
		HashSet<String> localNeighbors = helper.intersectionSet(neighbors, bagGraph.keySet());
		for(String neighbor: localNeighbors) {
			addEdgeKCore(new StreamEdge(node,neighbor));
		}
	}
	
	public void addNodeKCore(String src,String dst, NodeMap nodeMap) {
		//kCore.addNode(node);
		HashSet<String> neighbors = nodeMap.getNeighbors(src);
		SetFunctions helper = new SetFunctions();
		HashSet<String> localNeighbors = helper.intersectionSet(neighbors, bagGraph.keySet());
		for(String neighbor: localNeighbors) {
			if(!neighbor.equals(dst))
				addEdgeKCore(new StreamEdge(src,neighbor));
		}
	}
	
	void removeNodekCore(String node) {
		HashSet<String > temp = bagGraph.get(node);
		if(temp!= null) {
			ArrayList<String> neighbors = new ArrayList<String>(temp);
			if(neighbors != null)
				for(String neighbor: neighbors ) { 
					bagGraph.get(neighbor).remove(node);
					bagGraph.get(node).remove(neighbor);
					kCore.removeEdge(node, neighbor);
				}
		}
		bagGraph.remove(node);
		//kCore.removeNode(node);
	}
	void addEdgeKCore(StreamEdge edge) {
		String src = edge.getSource();
		String dst = edge.getDestination();
		
		if(bagGraph.containsKey(src)) {
			bagGraph.get(src).add(dst);
		}else {
			HashSet<String> neighbors = new HashSet<String>();
			//kCore.addNode(src);
			neighbors.add(dst);
			bagGraph.put(src, neighbors);
		}
		if(bagGraph.containsKey(dst)) {
			bagGraph.get(dst).add(src);
		}else {
			HashSet<String> neighbors = new HashSet<String>();
			//kCore.addNode(dst);
			neighbors.add(src);
			bagGraph.put(dst, neighbors);
		}
		kCore.addEdge(src, dst);
	}

	public void removeEdgeKCore(StreamEdge edge) {
		String src = edge.getSource();
		String dst = edge.getDestination();
		if( bagGraph.containsKey(src)) { 
			if(bagGraph.containsKey(dst)) {
				if(bagGraph.get(src).contains(dst)) {
					if(bagGraph.get(dst).contains(src)) {
						bagGraph.get(src).remove(dst);
						bagGraph.get(dst).remove(src);
						kCore.removeEdge(src, dst);
					}
				}
				
			}
		}
	}
	public void addEdge(StreamEdge edge, NodeMap nodeMap,DegreeMap degreeMap) {
		String src = edge.getSource();
		String dst = edge.getDestination();
		
		int srcDegree = nodeMap.getDegree(src);
		int dstDegree = nodeMap.getDegree(dst);
		
		//System.out.println("maximal Density " + maximalDensity ) ; 
		SnowBall temp = null;
		if (srcDegree < maximalDensity && dstDegree < maximalDensity) {
			return;
		} 
		
		double initialDensity= this.getMaximalDensity(nodeMap);

		if (srcDegree >= maximalDensity && dstDegree < maximalDensity ) {
			temp = addNode(src,nodeMap);
			if (!bagGraph.containsKey(src)){
				addNodeKCore(src,nodeMap);
			}
			if(this.verifyMainCore(src, nodeMap, temp)) {
				this.fixMainCore(src, nodeMap,temp);
			}
		} else if (srcDegree < maximalDensity && (double)dstDegree >= maximalDensity ) {
			temp = addNode(dst,nodeMap);
			if( !bagGraph.containsKey(dst)) {
				addNodeKCore(dst,nodeMap);
			}
			if(this.verifyMainCore(dst, nodeMap, temp)) {
				this.fixMainCore(dst, nodeMap, temp);
			}
		} else {
			if(!bagGraph.containsKey(src) && !bagGraph.containsKey(dst)) {
				addNodeKCore(src,dst, nodeMap);
				addNodeKCore(dst,src, nodeMap);
				addEdgeKCore(edge);
			}else if (!bagGraph.containsKey(src)){
				addNodeKCore(src,nodeMap);
			} else if (!bagGraph.containsKey(dst)){
				addNodeKCore(dst,nodeMap);
			}else {
				addEdgeKCore(edge);
			}
			SnowBall srcSnowBall = addNode(src,nodeMap);
			SnowBall dstSnowBall = addNode(dst, nodeMap);
	
			if(dstSnowBall.contains(src)) {
				if(!dstSnowBall.containsEdge(edge)) {
					dstSnowBall.addEdge(edge);
					ensureInvariant(dstSnowBall,nodeMap);
				}
			}
			else {
				if(this.canMerge(src, dst, srcSnowBall,dstSnowBall, nodeMap)) {
					this.fixMainCore(src, nodeMap, srcSnowBall);
					//ensureInvariant(srcSnowBall,nodeMap);
				}
			}
			
		}
		
		if(this.getMaximalDensity(nodeMap) < initialDensity) {
			HashSet<String> addNodes = degreeMap.getNodesBetween(initialDensity, this.getMaximalDensity(nodeMap));
			if(addNodes == null){
				return;
			}

			for(String str: addNodes) {
				SnowBall snowBall = addNode(str,nodeMap);
				addNodeKCore(str,nodeMap);
				if(this.verifyMainCore(str, nodeMap,snowBall)) {
					this.fixMainCore(str, nodeMap, snowBall);
				}
			}
		}
		
		if(this.getMaximalDensity(nodeMap) != initialDensity) {
			synchronizeSnowBalls(nodeMap);
		}
		
		cleanup(nodeMap);
		//System.out.println("+bag graph" + bagGraph+ " " + maximalDensity);
		//System.out.println("kcore" + kCore.kCore);
		//System.out.println(" bag mcd " + kCore.mcd);
		//System.out.println( " bag pcd " + kCore.pcd);
		//this.print();
	}

	/*public SnowBall addNode(String src, NodeMap nodeMap) {
		int maxIntersection = 0;
		SnowBall max = null;
		ArrayList<SnowBall> allMax = new ArrayList<SnowBall>();
		HashSet<String> neighbors = nodeMap.getNeighbors(src);
		for(SnowBall s: bag) {
			if(s.contains(src)) {
				return s;
			}
			SetFunctions helper = new SetFunctions();
			int internalDegree = helper.intersection(s.getNodes(), neighbors);
			if(internalDegree != 0 && internalDegree >= s.getDensity() 
					&& internalDegree >= maxIntersection && internalDegree >= s.getMainCore()) {
				if(internalDegree == maxIntersection) {
					allMax.add(s);
					max = s;
				}else {
					allMax = new ArrayList<SnowBall>();
					allMax.add(s);
					max = s;
					maxIntersection = internalDegree;
				}
			}
		}

		if(max == null) {
			SnowBall newBall = new SnowBall();
			newBall.addNode(src, nodeMap);
			newBall.setMaximalDensity(maximalDensity, nodeMap);
			bag.add(newBall);
			//updateStats(newBall,src,nodeMap);
			return newBall;
		}else {
			max.addNode(src, nodeMap);
			if(allMax.size() > 1) {
				for(SnowBall s: allMax) {
					if(!s.contains(src)) {
						max.merge(s, nodeMap);
						bag.remove(s);
					}
				}
			}
			
			ensureInvariant(max,nodeMap);
			//updateStats(max,src,nodeMap);
			return max;
		}
	}
*/
	public SnowBall addNode(String src, NodeMap nodeMap) {
		int maxIntersection = 0;
		SnowBall max = null;
		HashSet<String> neighbors = nodeMap.getNeighbors(src);
		for(SnowBall s: bag) {
			if(s.contains(src)) {

				return s;
			}
			SetFunctions helper = new SetFunctions();
			int internalDegree = helper.intersection(s.getNodes(), neighbors);
			if(internalDegree != 0 && internalDegree >= s.getDensity() 
					&& internalDegree >= maxIntersection && internalDegree >= s.getMainCore()) {
				max = s;
				maxIntersection = internalDegree;
			}
		}

		if(max == null) {
			SnowBall newBall = new SnowBall();
			newBall.addNode(src, nodeMap);
			newBall.setMaximalDensity(maximalDensity, nodeMap);
			bag.add(newBall);
			//updateStats(newBall,src,nodeMap);

			return newBall;
		}else {
			max.addNode(src, nodeMap);
			ensureInvariant(max,nodeMap);
			//updateStats(max,src,nodeMap);

			return max;
		}
	}
	boolean canMerge(String src, String dst, SnowBall s1, SnowBall s2, NodeMap nodeMap) {
		HashSet<String> visited = new HashSet<String>();
		HashSet<String> neighbors = new HashSet<String>();

		kCore.color(src,kCore.getKCore(src), visited, neighbors);
		return neighbors.contains(dst);
		
		/*if(this.verifyMainCore(src, nodeMap, s1)) {
			this.fixMainCore(src, nodeMap,s1);
		}
		if(!s1.contains(dst)) {
			if(this.verifyMainCore(dst, nodeMap, s2)) {
				this.fixMainCore(dst, nodeMap,s2);
			}
		}
		return true;*/
	} 

	void cleanup(NodeMap nodeMap) {
		ArrayList<SnowBall> snowBalls = new ArrayList<SnowBall>();
		
		for(SnowBall s: bag) {
			if(s.getNumEdges()== 0 && s.getNumNodes() == 0)
				snowBalls.add(s);
		}

		for(SnowBall s: snowBalls)
		{
			bag.remove(s);
			//stats.removeSnowBall(s);
		}
	}
	/*double getMaximalDensity1(NodeMap nodeMap) {
		double max = 0.0;
		for(SnowBall s: bag) {
			double tempDensity = s.getDensity();
			if(tempDensity > max) 
				max = tempDensity;
		}
		maximalDensity  = max;
		return max;
	}*/
	double getMaximalDensity(NodeMap nodeMap) {
		if(bag.size() == 0) {
			maximalDensity = 0 ;
			return 0;
		}
		int k1;
		if(bag.size() > k) 
			k1  =1;
		else 
			k1 = k;
		int count = 0;
		PriorityQueue<Double> queue = new PriorityQueue<Double>(k1);
		for(SnowBall s: bag) {
			double tempDensity = s.getDensity();
			if(count < k1) {
				queue.offer(tempDensity);
				count++;
			}else {
				Double min = queue.peek();
				if(tempDensity > min) {
					queue.poll();
					queue.offer(tempDensity);
				}
			}

		}
		if(count < k1 )
			return 0;
		maximalDensity = queue.peek();
		return maximalDensity;
	}
	/*double getMaximalDensity(NodeMap nodeMap) {
		return getMaximalDensityGreater(nodeMap);
	}*/
	
	/*void merge(SnowBall S1, SnowBall S2, NodeMap nodeMap) {
		if(S1.id == S2.id)  {
			return;
		} else {
			S1.merge(S2, nodeMap);
			bag.remove(S2);
		}
	}*/
	public void print() {
		int i =0;
		for(SnowBall s: bag) {
			System.out.println("Density: " +s.getDensity());
			System.out.println("SnowBall "+(i+1));
			s.print();
			//System.out.println(s.getDensity());
			i++;
		}
	}
	public void removeEdge(StreamEdge edge, NodeMap nodeMap, DegreeMap degreeMap) {
		String src = edge.getSource();
		String dst = edge.getDestination();

		int srcDegree = nodeMap.getDegree(src);
		int dstDegree = nodeMap.getDegree(dst);

		int prevSrcDegree = srcDegree+1;
		int prevDstDegree = dstDegree+1;
		
		if(prevSrcDegree < maximalDensity && prevDstDegree < maximalDensity)
			return;

		if(bagGraph.containsKey(src)) {
			if(bagGraph.containsKey(dst)) {
				if(bagGraph.get(src).contains(dst)) 
					if(bagGraph.get(dst).contains(src))
					{	
						removeEdgeKCore(edge);
					}
			}
		}
		
	
		double currentMaximalDensity = this.getMaximalDensity(nodeMap);
		boolean flag = false;

		if(prevSrcDegree >= maximalDensity ) {
			if(srcDegree < maximalDensity || srcDegree == 0 ) {
				for(SnowBall s: bag) {
					if(s.containsNode(src)) {
						s.removeNode(src);
						ensureInvariant(s,nodeMap);
						flag = true;
						break;
					}	
				}
				//removeNodekCore(src);
			}
		}
		if( prevDstDegree >= maximalDensity) {
			if(dstDegree < maximalDensity || dstDegree == 0) {
				for(SnowBall s: bag) {

					if(s.containsNode(dst)) {
						s.removeNode(dst);
						ensureInvariant(s,nodeMap);
			
						flag = true;
						break;
					}	
				}
				//removeNodekCore(dst);
			}
		} 

		if(prevSrcDegree < maximalDensity || prevDstDegree < maximalDensity) {
			return;
		}
		SnowBall srcSnowBall= null;
		SnowBall dstSnowBall = null;
		if(!flag) {
			SnowBall temp = null;
			for(SnowBall s: bag) {
				if(s.containsEdge(edge)){
					s.removeEdge(edge,nodeMap);
					temp =s;
				}else if (s.contains(src)) {
					srcSnowBall = s;
				}else if (s.contains(dst)) {
					dstSnowBall = s;
				}

			}
			if(temp!=null) {
				HashSet<String> nodes = new HashSet<String>(temp.getNodes());
				for(String node: nodes) {
					if(temp.contains(node)) {
						if(this.verifyMainCore(node, nodeMap, temp)) {
							this.fixMainCore(node, nodeMap, temp);
					}
				}
				ensureInvariant(temp,nodeMap);
				/*if(this.verifyMainCore(src, nodeMap, temp)) {
					this.fixMainCore(src, nodeMap, temp);
				}else if(this.verifyMainCore(dst, nodeMap, temp)) {
						this.fixMainCore(dst, nodeMap, temp);
				}else {
					ensureInvariant(temp,nodeMap);
				}
				*/
				}
				if(temp.contains(src) && temp.contains(dst) && temp.getMainCore() == 1) {
					ArrayList<String> visited = new ArrayList<String>();
					//checking for disconnected snowBalls
					if(!isConnected(temp,src,dst,visited)) {
						SnowBall newSnowBall = new SnowBall();
						for(String s:visited) {
							temp.removeNode(s);
							//stats.removeNode(temp, s);
							if(nodeMap.getDegree(s) >= maximalDensity) {
								newSnowBall.addNode(s, nodeMap);
								//this.updateStats(newSnowBall, s, nodeMap);
							}
						}
						ensureInvariant(newSnowBall,nodeMap);
						ensureInvariant(temp,nodeMap);
						if(!newSnowBall.isEmpty())
							bag.add(newSnowBall);
					}
				}
			}/* else {
				if(srcSnowBall != null ) {
					if(this.verifyMainCore(src, nodeMap, srcSnowBall)) {
						this.fixMainCore(src, nodeMap, srcSnowBall);
					}
					if(!srcSnowBall.contains(dst)) {
						if(dstSnowBall != null) {
							if(this.verifyMainCore(dst, nodeMap, dstSnowBall)) {
								this.fixMainCore(dst, nodeMap, dstSnowBall);
							}
						}
					} 
				}
				else if(dstSnowBall != null) {
					if(this.verifyMainCore(dst, nodeMap, dstSnowBall)) {
						this.fixMainCore(dst, nodeMap, dstSnowBall);
					}
				}
			}*/
		}

		//System.out.println("-bag graph" + bagGraph+ " " + maximalDensity);
		//this.print();

		double updatedMaximalDensity = maximalDensity;
		if(currentMaximalDensity != updatedMaximalDensity) {
			HashSet<String> addNodes = degreeMap.getNodesBetween(currentMaximalDensity, updatedMaximalDensity);
			if(addNodes == null){
				return;
			}

			for(String str: addNodes) {
				SnowBall snowBall = addNode(str,nodeMap);
				addNodeKCore(str,nodeMap);
				if(this.verifyMainCore(str, nodeMap,snowBall)) {
					this.fixMainCore(str, nodeMap, snowBall);
				}
			}
		}


		if(this.getMaximalDensity(nodeMap) != updatedMaximalDensity) {
			synchronizeSnowBalls(nodeMap);
		}
		cleanup(nodeMap);

	}

	void ensureInvariant(SnowBall s, NodeMap nodeMap) {
		HashSet<String> nodes = new HashSet<String>();
		s.ensureFirstInVariant(nodeMap,nodes,this);
		for(String node: nodes) {
			addNode(node,nodeMap);
		}

	}
	void synchronizeSnowBalls(NodeMap nodeMap) {
		for(SnowBall s: bag) {
			if(s.getDensity() < maximalDensity ) {
				s.setMaximalDensity(maximalDensity, nodeMap);
			}
		}
		ArrayList<String> addNodes= new ArrayList<String>();
		for(SnowBall s :bag) {
			HashSet<String> nodes = new HashSet<String>();
			s.ensureFirstInVariant(nodeMap,nodes,this);
			addNodes.addAll(nodes);
		}
		
		for(String node: addNodes) {
			addNode(node,nodeMap);
		}
	}
	boolean isConnected(SnowBall snowBall, String src, String dst, ArrayList<String> visited) {
		//System.out.println(src+ " " + visited);
		visited.add(src);
		HashSet<String> neighbors = snowBall.graph.get(src);
		if(neighbors == null)
			return false;
		if(neighbors.contains(dst))
			return true;
		for(String s:neighbors) {
			if(!visited.contains(s))
				if(isConnected(snowBall,s,dst,visited))
					return true;
		}
		return false;

	}

	boolean verifyMainCore(String src, NodeMap nodeMap, SnowBall srcSnowBall) { 
		return (kCore.getKCore(src) > srcSnowBall.kCore.getKCore(src));
	}
	void fixMainCore(String src, NodeMap nodeMap, SnowBall srcSnowBall) {
		HashSet<String> visited = new HashSet<String>();
		HashSet<String> neighbors = new HashSet<String>();
		
 		kCore.color(src,kCore.getKCore(src), visited, neighbors);
 		
 		
 		//remove all the other snowballs
 		HashSet<SnowBall> merge = new HashSet<SnowBall>();
 		for(SnowBall s:bag) {
			SetFunctions helper = new SetFunctions();
			HashSet<String> common = helper.intersectionSet(new HashSet<String>(s.getNodes()), neighbors) ;
			if(common.size() > 0 && !srcSnowBall.equals(s)) {
				merge.add(s);
			}
		}
 		
 		for(SnowBall s:merge) {
			//srcSnowBall.merge(s, nodeMap);
			bag.remove(s);
		}
		
 		srcSnowBall.graph = new HashMap<String,HashSet<String>>();
 		srcSnowBall.kCore = new KCoreTraversal(srcSnowBall.graph);
 		srcSnowBall.kCore.mcd = new HashMap<String,Integer>();
 		srcSnowBall.kCore.pcd = new HashMap<String,Integer>();
 		
 		int edgeCount = 0 ;
 		for(String node: neighbors) {
 			HashSet<String> neigs = nodeMap.getNeighbors(node);
 			SetFunctions helper = new SetFunctions();
			HashSet<String> common = helper.intersectionSet(neigs, neighbors) ;
			edgeCount+= common.size();
			srcSnowBall.graph.put(node, common);
			srcSnowBall.kCore.kCore.put(node, kCore.getKCore(node));	
 		}
 		
 		srcSnowBall.numEdges = edgeCount/2;
 		srcSnowBall.numNodes = neighbors.size();
 		srcSnowBall.getDensity();
 		
 		
 		for(String node: neighbors) {
 			srcSnowBall.kCore.setmcd(node);
 		}
 		for(String node: neighbors) {
 			srcSnowBall.kCore.setpcd(node);
 		}
 		
 		this.ensureInvariant(srcSnowBall, nodeMap);

	}

	@Override
	public ArrayList<Output> getDensest(DegreeMap degreeMap, NodeMap nodeMap) {
		ArrayList<Output> outputArray = new ArrayList<Output>();

		PriorityQueue<SnowBall> queue = new PriorityQueue<SnowBall>(k,Collections.reverseOrder());
		for(SnowBall temp: bag) {
			queue.offer(temp);
		}

		while(!queue.isEmpty()) {
			SnowBall s = queue.poll();
			Output returnOutput = new Output();
			returnOutput.setCoreNum(s.getMainCore());
			returnOutput.setDensity(s.getDensity());
			returnOutput.setNodes(new ArrayList<String>(s.getNodes()));
			returnOutput.setSize(s.getNumNodes());
			outputArray.add(returnOutput);
		}
		return outputArray;
	}
}
