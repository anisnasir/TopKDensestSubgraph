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
	public HashSet<SnowBall> bag;
	HashMap<String,HashSet<String>> bagGraph;
	KCoreTraversal kCore;
	double maximalDensity = 0;
	int count = 0; 
	int k ;

	public BagOfSnowballs(int k ) {
		bagGraph = new HashMap<String,HashSet<String>> ();
		kCore = new KCoreTraversal(bagGraph);
		bag = new HashSet<SnowBall>();
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
	
	void removeNodekCore(String node) {
		HashSet<String > temp = bagGraph.get(node);
		if(temp!= null) {
			ArrayList<String> neighbors = new ArrayList<String>(temp);
			if(neighbors != null)
				for(String neighbor: neighbors ) { 
					bagGraph.get(neighbor).remove(node);
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
				bagGraph.get(src).remove(dst);
				bagGraph.get(dst).remove(src);
				//kCore.removeEdge(src, dst);
			}
		}
	}
	public void addEdge(StreamEdge edge, NodeMap nodeMap,DegreeMap degreeMap) {
		String src = edge.getSource();
		String dst = edge.getDestination();

		int srcDegree = nodeMap.getDegree(src);
		int dstDegree = nodeMap.getDegree(dst);
		
		SnowBall temp = null;
		if (srcDegree < maximalDensity && dstDegree < maximalDensity) {
			return;
		} 
		
		double initialDensity= this.getMaximalDensity(nodeMap);

		if (srcDegree >= maximalDensity && dstDegree < maximalDensity ) {
			temp = addNode(src,nodeMap);
			if(this.verifyMainCore(src, nodeMap, temp)) {
				this.fixMainCore(src, nodeMap,temp);
			}
		} else if (srcDegree < maximalDensity && (double)dstDegree >= maximalDensity ) {
			temp = addNode(dst,nodeMap);
			if(this.verifyMainCore(dst, nodeMap, temp)) {
				this.fixMainCore(dst, nodeMap, temp);
			}
		} else {
			SnowBall srcSnowBall = addNode(src,nodeMap);
			if(this.verifyMainCore(src, nodeMap,srcSnowBall)) {
				this.fixMainCore(src, nodeMap, srcSnowBall);
			}
			SnowBall dstSnowBall = addNode(dst, nodeMap);

			if(this.verifyMainCore(dst, nodeMap,dstSnowBall)) {
				this.fixMainCore(dst, nodeMap, dstSnowBall);
			}

			addEdgeKCore(edge);

			if(srcSnowBall.equals(dstSnowBall)) {
				srcSnowBall.addEdge(edge);
				ensureInvariant(srcSnowBall,nodeMap);	
			}/*
			else {
				if(this.canMerge(src, dst, srcSnowBall,dstSnowBall, nodeMap)) {
					this.fixMainCore(src, nodeMap, srcSnowBall);
				}
			}*/
		}
		
		if(this.getMaximalDensity(nodeMap) < initialDensity) {
			HashSet<String> addNodes = degreeMap.getNodesBetween(initialDensity, this.getMaximalDensity(nodeMap));
			if(addNodes == null){
				return;
			}

			for(String str: addNodes) {
				addNode(str,nodeMap);
			}
		}
		
		if(this.getMaximalDensity(nodeMap) != initialDensity) {
			synchronizeSnowBalls(nodeMap);
		}
		
		cleanup(nodeMap);
		//System.out.println("+bag graph" + bagGraph+ " " + maximalDensity);
		//System.out.println(kCore.kCore);
		//this.print();
	}

	public SnowBall addNode(String src, NodeMap nodeMap) {
		if(!bagGraph.containsKey(src))
			addNodeKCore(src,nodeMap);
		
		int mainCore = kCore.mainCore();
		
		int maxIntersection = 0;
		SnowBall max = null;
		HashSet<String> neighbors = nodeMap.getNeighbors(src);
		for(SnowBall s : bag) {
			if(s.contains(src)) {
				return s;
			}
			SetFunctions helper = new SetFunctions();
			int internalDegree = helper.intersection(s.getNodes(), neighbors);
			int sCore = s.getMainCore();
			if(internalDegree != 0 && internalDegree >= s.getDensity() 
					&& internalDegree > maxIntersection && internalDegree >= sCore) {
				max = s;
				maxIntersection = internalDegree;
				if(sCore == mainCore)
					break;
			}
		}

		if(max == null) {
			SnowBall newBall = new SnowBall();
			newBall.addNode(src, nodeMap);
			newBall.setMaximalDensity(this.getMaximalDensity(nodeMap), nodeMap);
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

	/*boolean canMerge(String src, String dst, SnowBall s1, SnowBall s2, NodeMap nodeMap) {
		if(s1.id == s2.id) 
			return false;
		else {
			return (s1.kCore.getKCore(src) == s2.kCore.getKCore(src));
			/*HashSet<String> visited = new HashSet<String>();
			HashSet<String> neighbors = new HashSet<String>();

			kCore.color(src, visited, neighbors);
			return neighbors.contains(dst);
		}
	} */

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
	double getMaximalDensity1(NodeMap nodeMap) {
		double max = 0.0;
		for(SnowBall s : bag) {
			double tempDensity = s.getDensity();
			if(tempDensity > max) 
				max = tempDensity;
		}
		maximalDensity  = max;
		return max;
	}
	double getMaximalDensityGreater(NodeMap nodeMap) {
		if(bag.size() == 0) {
			maximalDensity = 0 ;
			return 0;
		}
		int count = 0;
		PriorityQueue<Double> queue = new PriorityQueue<Double>(k);
		for(SnowBall s: bag) {
			double tempDensity = s.getDensity();
			if(count < k) {
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
		maximalDensity = queue.peek();
		return maximalDensity;
	}
	double getMaximalDensity(NodeMap nodeMap) {
		if( k == 1)
			return getMaximalDensity1(nodeMap);
		else 
			return getMaximalDensityGreater(nodeMap);
	}
	
	void merge(SnowBall S1, SnowBall S2, NodeMap nodeMap) {
		if(S1.id == S2.id)  {
			return;
		} else {
			S1.merge(S2, nodeMap);
			bag.remove(S2);
		}
	}
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

		//System.out.println(src +  " " + srcDegree + " " + dst + " " + dstDegree);
		if(prevSrcDegree < maximalDensity && prevDstDegree < maximalDensity)
			return;

		double currentMaximalDensity = this.getMaximalDensity(nodeMap);
		boolean flag = false;

		if(prevSrcDegree >= maximalDensity ) {
			if(srcDegree < maximalDensity || srcDegree == 0) {
				for(SnowBall s: bag) {
					if(s.containsNode(src)) {
						s.removeNode(src);
						ensureInvariant(s,nodeMap);
						flag = true;
						break;
					}	
				}
				this.removeNodekCore(src);
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
				this.removeNodekCore(dst);
			}
		} 

		if(prevSrcDegree < maximalDensity || prevDstDegree < maximalDensity) {
			return;
		}

		if(!flag) {
			this.removeEdgeKCore(edge);
			SnowBall temp = null;
			for(SnowBall s: bag) {
				if(s.containsEdge(edge)){
					s.removeEdge(edge,nodeMap);
					temp =s;
				}

			}

			if(temp!=null) {
				ensureInvariant(temp,nodeMap);
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
			}
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
				addNode(str,nodeMap);
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
			//System.out.println("adding node "+ node);
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
		if(!bagGraph.containsKey(src))
			return true;
		return (kCore.getKCore(src) > srcSnowBall.kCore.getKCore(src));
	}
	void fixMainCore(String src, NodeMap nodeMap, SnowBall srcSnowBall) {
		HashSet<String> visited = new HashSet<String>();
		HashSet<String> neighbors = new HashSet<String>();
		HashSet<SnowBall> remove = new HashSet<SnowBall>();

		kCore.color(src, visited, neighbors);
		for(SnowBall s:bag) {
			SetFunctions helper = new SetFunctions();
			if(helper.intersection(s.getNodes(), neighbors) > 0  && !srcSnowBall.equals(s)) {
				srcSnowBall.merge(s, nodeMap);
				remove.add(s);
			}
		}
		for(SnowBall s:remove) {
			bag.remove(s);
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
