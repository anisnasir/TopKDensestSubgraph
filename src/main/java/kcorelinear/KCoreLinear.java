package kcorelinear;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import main.DensestSubgraph;
import output.Output;
import struct.DegreeMap;
import struct.NodeMap;
import utility.SetFunctions;


public class KCoreLinear implements DensestSubgraph{
	HashMap<String,HashSet<String>> graph;
	public HashMap<String,Integer> kCore;
	public HashMap<String, Integer> mcd;
	public HashMap<String, Integer> pcd;
	int maxCore = 0;
	
	public KCoreLinear(HashMap<String,HashSet<String>> graph) {
		kCore = new HashMap<String,Integer>();
		mcd  = new HashMap<String,Integer>();
		this.graph = graph;
	}
	
	public void addNode(String src) {
		kCore.put(src, 0);
	}
	
	public void removeNode(String src) {
		kCore.remove(src);
	}
	
	public int getKCore(String src) {
		if(kCore.containsKey(src))
			return this.kCore.get(src);
		else 
			return 0;
	}
	
	public void addEdge(String src, String dst) {
		String r = src;
		if(getKCore(src) > getKCore(dst))
			r= dst;
		
		KCoreResult H = findPureCore(r);
		int k = getKCore(r);
		HashMap<Integer,HashSet<String>> sortedcd = new HashMap<Integer,HashSet<String>>();
		int maxDegree = 0;
		for(String str:H.cd.keySet()) {
			int degree = H.cd.get(str);
			insert(degree, str, sortedcd);
			if(degree>maxDegree)
				maxDegree = degree;
		}
		
		int i =0 ;
		while (i<=maxDegree) {
			if(sortedcd.containsKey(i)) {
				HashSet<String> temp = sortedcd.get(i);
				if(temp.size() == 0) 
					i++;
				else {
					String element = getFirst(temp);
					if( i<= k) {
						HashSet<String> neighbors = H.graph.get(element);
						for(String neighbor:neighbors) {
							if(wrapperCd(H.cd,neighbor) > wrapperCd(H.cd,element)) {
								int prevCd = H.cd.get(neighbor);
								H.cd.put(neighbor, prevCd-1);
								sortedcd.get(prevCd).remove(neighbor);
								insert(prevCd-1,neighbor,sortedcd);
								if(prevCd-1 < i) {
									i= prevCd-1;
								}
							}
						}
						
					} else {
						kCore.put(element, k+1);
					}
				}
			}else {
				i++;
			}
		}
	}
	
	int wrapperCd(HashMap<String,Integer> cd, String str) {
		if(cd.containsKey(str))
			return cd.get(str);
		else 
			return 0;
	}
	String getFirst(HashSet<String> temp) {
		String element= "";
		for(String str:temp) {
			element = str;
			break;
		}
		temp.remove(element);
		return element;
		
	}
	void insert(int degree, String node, HashMap<Integer,HashSet<String>> sortedcd) {
		if(sortedcd.containsKey(degree))
			sortedcd.get(degree).add(node);
		else {
			HashSet<String> temp =  new HashSet<String>();
			temp.add(node);
			sortedcd.put(degree,temp);
		}
			
	}
	public KCoreResult findSubCore(String u) {
		KCoreResult result = new KCoreResult();
		LinkedList<String> queue = new LinkedList<String>();
		HashMap<String,HashSet<String>> localGraph = new HashMap<String,HashSet<String>>();
		HashSet<String> visited = new HashSet<String>();
		HashMap<String,Integer> cd = new HashMap<String,Integer>();
		int k = getKCore(u);
		queue.add(u);
		visited.add(u);
		
		SetFunctions helper = new SetFunctions();
		while(!queue.isEmpty()) {
			String v = queue.getFirst();
			queue.removeFirst();
			HashSet<String> temp = graph.get(v);
			HashSet<String> neighbors;
			if(temp == null)
				neighbors = new HashSet<String>();
			else
				neighbors = new HashSet<String>(temp);
			
			localGraph.put(v, helper.intersectionSet(neighbors,localGraph.keySet()));
			
			for(String neighbor:neighbors) {
				if(getKCore(neighbor) >= k ) {
					if(cd.containsKey(v)) {
						int prevVal = cd.get(v);
						cd.put(v, prevVal+1);
					}else {
						cd.put(v, 1);
					}
					if(getKCore(neighbor) == k && !visited.contains(neighbor)) {
						queue.add(neighbor);
						localGraph.get(v).add(neighbor);
						visited.add(neighbor);
					}
					
				}
			}
 			
		}
		result.graph= localGraph;
		result.cd=cd;
		return result;
		
		
	}
	
	public KCoreResult findPureCore(String u) {
		KCoreResult result = new KCoreResult();
		LinkedList<String> queue = new LinkedList<String>();
		HashMap<String,HashSet<String>> localGraph = new HashMap<String,HashSet<String>>();
		HashSet<String> visited = new HashSet<String>();
		HashMap<String,Integer> cd = new HashMap<String,Integer>();
		int k = getKCore(u);
		queue.add(u);
		visited.add(u);
		
		SetFunctions helper = new SetFunctions();
		while(!queue.isEmpty()) {
			String v = queue.getFirst();
			queue.removeFirst();
			HashSet<String> temp = graph.get(v);
			HashSet<String> neighbors;
			if(temp == null)
				neighbors = new HashSet<String>();
			else
				neighbors = new HashSet<String>(temp);
			
			localGraph.put(v, helper.intersectionSet(neighbors,localGraph.keySet()));
			
			for(String neighbor:neighbors) {
				if(getKCore(neighbor) > k || ((getKCore(neighbor) == k) && (MCDDegree(neighbor) > k) )) {
					if(cd.containsKey(v)) {
						int prevVal = cd.get(v);
						cd.put(v, prevVal+1);
					}else {
						cd.put(v, 1);
					}
					if(getKCore(neighbor) == k && !visited.contains(neighbor)) {
						queue.add(neighbor);
						localGraph.get(v).add(neighbor);
						visited.add(neighbor);
					}
					
				}
			}
 			
		}
		result.graph= localGraph;
		result.cd=cd;
		return result;
		
		
	}
	
	public void removeEdge(String src, String dst) {
		String r = src;
		if(getKCore(src) > getKCore(dst)) {
			r = dst;
		}
		KCoreResult H, H1, H2;
		if(getKCore(src) != getKCore(dst)) {
			H1 = findSubCore(r);
			H=H1;
		} else {
			H1 = findSubCore(src);
			H2 = findSubCore(dst);
		
			//H = H1 u H2
			H=H1;
			for(String str: H2.graph.keySet()) {
				H.graph.put(str, H2.graph.get(str));
			}
			
			//cd = c1 u c2
			for(String str: H2.graph.keySet()) {
				if(H2.cd.containsKey(str))
					H.cd.put(str, H2.cd.get(str));
			}	
		}
		
		
		int k = getKCore(r);
		HashMap<Integer,HashSet<String>> sortedcd = new HashMap<Integer,HashSet<String>>();
		int maxDegree = 0;
		for(String str:H.graph.keySet()) {
			int degree=0;
			if(H.cd.containsKey(str))
				degree = H.cd.get(str);
			insert(degree, str, sortedcd);
			if(degree>maxDegree)
				maxDegree = degree;
		}
		
		int i =0 ;
		while (i<=maxDegree) {
			if(sortedcd.containsKey(i)) {
				HashSet<String> temp = sortedcd.get(i);
				if(temp.size() == 0) 
					i++;
				else {
					String element = getFirst(temp);
					if( i< k) {
						int prevCore = kCore.get(element);
						if(prevCore > 1)
							kCore.put(element, prevCore-1);
						else 
							kCore.remove(element);
						
						HashSet tempNeighbors = H.graph.get(element);
						HashSet<String> neighbors;
						if(tempNeighbors == null) 
							neighbors = new HashSet<String>();
						neighbors = H.graph.get(element);
						for(String neighbor:neighbors) {
							if(H.cd.get(neighbor) > H.cd.get(element)) {
								int prevCd = H.cd.get(neighbor);
								H.cd.put(neighbor, prevCd-1);
								sortedcd.get(prevCd).remove(neighbor);
								insert(prevCd-1,neighbor,sortedcd);
								if(prevCd-1 < i) {
									i= prevCd-1;
								}
							}
						}
						
					} else {
						break;
					}
				}
			}else {
				i++;
			}
		}
		
	}
	
	
	public int MCDDegree(String u) {
		HashSet<String> temp = graph.get(u);
		HashSet<String> neighbors;
		if(temp == null) 
			neighbors = new HashSet<String>();
		else 
			neighbors = temp;
		
		int count=0;
		for(String neighbor:neighbors ) {
			if (getKCore(neighbor) >= getKCore(u)) {
				count++;
			}
		}
		return count;
	}
	public void color(String dst, HashSet<String> visited, HashSet<String> color) {
		KCoreResult H = findSubCore(dst);
		for(String str: H.graph.keySet()) {
			color.add(str);
		}
	}

	@Override
	public ArrayList<Output> getDensest(DegreeMap degreeMap, NodeMap nodeMap) {
		// TODO Auto-generated method stub
		ArrayList<Output> outputArray = new ArrayList<Output>();
		Output returnOutput = new Output();
		ArrayList<String> maxCore = new ArrayList<String>();
		int maxCoreNum = 0;
		for(String str: kCore.keySet()) {
			int core = kCore.get(str);
			if(core > maxCoreNum)  {
				maxCoreNum = core;
				maxCore = new ArrayList<String>();
			}
			if(core == maxCoreNum) {
				maxCore.add(str);
			}
		}
		
		returnOutput.setCoreNum(maxCoreNum);
		returnOutput.setDensity(maxCoreNum/(double)2);
		returnOutput.setNodes(maxCore);
		returnOutput.setSize(maxCore.size());
		outputArray.add(returnOutput);
		return outputArray;
	}
	public int mainCore() {
		// TODO Auto-generated method stub
		ArrayList<String> maxCore = new ArrayList<String>();
		int maxCoreNum = 0;
		for(String str: kCore.keySet()) {
			int core = kCore.get(str);
			if(core > maxCoreNum)  {
				maxCoreNum = core;
				maxCore = new ArrayList<String>();
			}
			if(core == maxCoreNum) {
				maxCore.add(str);
			}
		}
		
		return maxCoreNum;
	}
	
}
