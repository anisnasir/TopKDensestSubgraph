package kcorelinear;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

import main.DensestSubgraph;
import output.Output;
import struct.DegreeMap;
import struct.NodeMap;
import utility.SetFunctions;


public class KCoreTraversal implements DensestSubgraph{
	HashMap<String,HashSet<String>> graph;
	public HashMap<String,Integer> kCore;
	public HashMap<String, Integer> mcd;
	public HashMap<String, Integer> pcd;
	int maxCore = 0;

	public KCoreTraversal(HashMap<String,HashSet<String>> graph) {
		kCore = new HashMap<String,Integer>();
		mcd  = new HashMap<String,Integer>();
		pcd = new HashMap<String,Integer>();
		this.graph = graph;
	}

	public void addNode(String src) {
		kCore.put(src, 0);
	}

	public void removeNode(String src) {
		kCore.remove(src);
	}
	public void addEdge(String src, String dst) {
		String r = src;
		if(getKCore(src) > getKCore(dst)) {
			r= dst;
		}

		//prepare RCDs;
		if(getKCore(src) == getKCore(dst)) {
			mcd.put(src, getmcd(src)+1);
			mcd.put(dst, getmcd(dst)+1);

			if(getmcd(src) > getKCore(dst)){
				pcd.put(dst, getpcd(dst)+1);
			}
			if(getmcd(dst) > getKCore(src)){
				pcd.put(src, getpcd(src)+1);
			}
			HashSet<String> temp = graph.get(src);
			HashSet<String> neighbors;
			if(temp == null) {
				neighbors = new HashSet<String>();
			}else {
				neighbors = temp;
			}

			for(String neighbor: neighbors) {
				if(getKCore(neighbor) == getKCore(src) && neighbor != dst) {
					if(getmcd(src)-1 ==  getKCore(neighbor)) {
						pcd.put(neighbor, getpcd(neighbor)+1);
					}
				}
			}

			temp = graph.get(dst);
			if(temp == null) {
				neighbors = new HashSet<String>();
			}else {
				neighbors = temp;
			}

			for(String neighbor: neighbors) {
				if(getKCore(neighbor) == getKCore(dst) && neighbor != src) {
					if(getmcd(dst)-1 ==  getKCore(neighbor)) {
						pcd.put(neighbor, getpcd(neighbor)+1);
					}
				}
			}


		}else {
			mcd.put(r, getmcd(r)+1);
			HashSet<String> temp = graph.get(r);
			HashSet<String> neighbors;
			if(temp == null) {
				neighbors = new HashSet<String>();
			}else {
				neighbors = temp;
			}

			int count = 0 ;
			for(String neighbor: neighbors) {
				if(getKCore(neighbor) == getKCore(r)) {
					if(getmcd(r) > getKCore(neighbor)) {
						pcd.put(neighbor, getpcd(neighbor)+1);
					}
				}
				if(getKCore(neighbor) > getKCore(r) || ((getKCore(neighbor) == getKCore(r)) && (getmcd(neighbor)>getKCore(r))))
					count++;
			}
			pcd.put(r,count);

		}
		//finished preparing rcds

		HashSet<String> temp = graph.get(r);
		HashSet<String> neighbors;
		if(temp == null) {
			neighbors = new HashSet<String>();
		}else {
			neighbors = temp;
		}


		Stack<String> stack = new Stack<String>();
		HashSet<String> visited = new HashSet<String>();
		HashSet<String> evicted = new HashSet<String>();
		HashMap<String,Integer> cd = new HashMap<String,Integer>();		

		int k  = getKCore(r);

		cd.put(r, getpcd(r));
		stack.push(r);
		visited.add(r);

		while(!stack.isEmpty()) {
			String v = stack.pop();
			if(getcd(cd,v) > k) {
				HashSet<String> neig = graph.get(v);
				if(neig != null)
					for(String neighbor: neig) {
						if(getKCore(neighbor) == k && getmcd(neighbor) > k && !visited.contains(neighbor)) {
							stack.push(neighbor);
							visited.add(neighbor);
							cd.put(neighbor,(getcd(cd,neighbor)+getpcd(neighbor)));
						}
					}	
			}else {
				if(!evicted.contains(v)) {
					propogateEviction(cd,evicted,k,v);
				}
			}
		}
		for(String w:visited) {
			if(!evicted.contains(w)) {

				kCore.put(w, getKCore(w)+1);
			}
		}
		//recomputercd
		for(String w:visited) {
			if(!evicted.contains(w)) {
				setmcd(w);
				neighbors = graph.get(w);
				if(neighbors!=null) {
					for(String x:neighbors) {
						setmcd(x);

					}
				}
			}
		}

		for(String w:visited) {
			if(!evicted.contains(w)) {
				setpcd(w);
				neighbors = graph.get(w);
				if(neighbors!=null) {
					for(String x:neighbors) {
						setpcd(x);
						HashSet<String> neig = graph.get(x);
						if(neig != null)
							for(String y:neig) {
								setpcd(y);
							}

					}
				}
			}
		}



	}

	void propogateEviction(HashMap<String,Integer> cd , HashSet<String> evicted, int k, String v) {
		evicted.add(v);
		HashSet<String> temp = graph.get(v);
		HashSet<String> neighbors;
		if(temp == null) {
			neighbors = new HashSet<String>();
		}else {
			neighbors = temp;
		}
		for(String neighbor: neighbors) {
			if(getKCore(neighbor) == k ) {
				cd.put(neighbor, getcd(cd,neighbor)-1);
				if(getcd(cd,neighbor) == k && !evicted.contains(neighbor)) {
					propogateEviction(cd,evicted,k,neighbor);
				}
			}
		}
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



	public void removeEdge(String src, String dst) {
		String r = src;
		if(getKCore(src) > getKCore(dst))
			r= dst;

		//prepare RCDs;
		if(getKCore(src) == getKCore(dst)) { 
			mcd.put(src, getmcd(src)-1);
			mcd.put(dst, getmcd(dst)-1);
			
			if(getmcd(src)+1 > getKCore(dst) && getmcd(src) == getKCore(dst))
				pcd.put(dst, getpcd(dst)-1);
			
			if(getmcd(dst)+1 > getKCore(src) && getmcd(dst) == getKCore(src))
				pcd.put(src, getpcd(src)-1);
			
			HashSet<String> neighbors= graph.get(src);;
			
			if(neighbors != null)
				for(String neighbor: neighbors) {
					if(getKCore(neighbor) == getKCore(src)) {
						if(getmcd(src)+1 > getKCore(neighbor) && getmcd(src) <=getKCore(neighbor)) {
							pcd.put(neighbor, getpcd(neighbor)-1);
						}
							
					}
				}
			
			neighbors= graph.get(dst);;
			
			if(neighbors != null)
				for(String neighbor: neighbors) {
					if(getKCore(neighbor) == getKCore(dst)) {
						if(getmcd(dst)+1 > getKCore(neighbor) && getmcd(dst) ==getKCore(neighbor)) {
							pcd.put(neighbor, getpcd(neighbor)-1);
						}
							
					}
				}
			
		}
		else if(getKCore(src) < getKCore(dst)) {
			mcd.put(src, getmcd(src)-1);
			
			HashSet<String> neighbors= graph.get(src);;
			
			if(neighbors != null)
				for(String neighbor: neighbors) {
					if(getKCore(neighbor) == getKCore(src)) {
						if(getmcd(src)+1 > getKCore(neighbor) && getmcd(src) <=getKCore(neighbor)) {
							pcd.put(neighbor, getpcd(neighbor)-1);
						}
							
					}
				}
			
		}
		else if(getKCore(dst) < getKCore(src)) {
			mcd.put(dst, getmcd(dst)-1);
			
			HashSet<String> neighbors= graph.get(dst);;
			
			if(neighbors != null)
				for(String neighbor: neighbors) {
					if(getKCore(neighbor) == getKCore(dst)) {
						if(getmcd(dst)+1 > getKCore(neighbor) && getmcd(dst) ==getKCore(neighbor)) {
							pcd.put(neighbor, getpcd(neighbor)-1);
						}
							
					}
				}
		}

		HashSet<String> neighbors;
		//finished preparing rcds

		HashMap<String,Integer> cd = new HashMap<String,Integer>();
		HashSet<String> dismissed = new HashSet<String>();
		HashSet<String> visited = new HashSet<String>();


		int k  = getKCore(r);
		cd.put(r, getpcd(r));

		if(getKCore(src) != getKCore(dst)) {
			visited.add(r);
			cd.put(r, getmcd(r));
			if (getcd(cd,r) < k) {
				propagateDismissal(cd, dismissed,visited, k,r);
			}
		}else {
			visited.add(src);
			cd.put(src, getmcd(src));
			if (getcd(cd,src) < k) {
				propagateDismissal(cd, dismissed,visited, k,src);
			}
			visited.add(dst);
			cd.put(dst, getmcd(dst));
			if (!dismissed.contains(dst) && getcd(cd,dst) < k) {
				propagateDismissal(cd, dismissed,visited, k,dst);
			}
		}


		//recomputercd
		for(String w:dismissed) {
			setmcd(w);
			neighbors = graph.get(w);
			if(neighbors!=null) {
				for(String x:neighbors) {
					setmcd(x);

				}
			}
		}

		for(String w:dismissed) {
			setpcd(w);
			neighbors = graph.get(w);
			if(neighbors!=null) {
				for(String x:neighbors) {
					setpcd(x);
					HashSet<String> neig = graph.get(x);
					if(neig != null)
						for(String y:neig) {
							setpcd(y);
						}

				}
			}
		}

	}

	void propagateDismissal(HashMap<String,Integer> cd, HashSet<String> dismissed, HashSet<String> visited,  int k ,String v) {
		dismissed.add(v);
		kCore.put(v, getKCore(v)-1);
		if(kCore.get(v) == 0) 
			kCore.remove(v);
		HashSet<String> neighbors = graph.get(v);
		if(neighbors!= null) {
			for(String neighbor: neighbors) {
				if(getKCore(neighbor) == k) {
					if(!visited.contains(neighbor)) {
						cd.put(neighbor,(getcd(cd,neighbor)+getmcd(neighbor)));
						visited.add(v);
					}
					cd.put(neighbor,(getcd(cd,neighbor)-1));

					if(getcd(cd, neighbor) < k && !dismissed.contains(neighbor)) 
						propagateDismissal(cd, dismissed,visited, k, neighbor);
				}
			}
		}

	}

	public void setmcd(String u) {
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
		if(count == 0)
			mcd.remove(u);
		else
			mcd.put(u, count);
	}

	public void setpcd(String u) {
		HashSet<String> temp = graph.get(u);
		HashSet<String> neighbors;
		if(temp == null) 
			neighbors = new HashSet<String>();
		else 
			neighbors = temp;

		int count=0;
		for(String neighbor:neighbors ) {
			if (getKCore(neighbor) > getKCore(u) || ((getKCore(neighbor) == getKCore(u)) && ( getmcd(neighbor) > getKCore(u)))) {
				count++;
			}
		}
		if(count == 0) 
			pcd.remove(u);
		else
			pcd.put(u, count);
	}
	public int getKCore(String src) {
		if(kCore.containsKey(src))
			return kCore.get(src);
		else 
			return 0;
	}
	int getcd(HashMap<String,Integer> cd, String str) {
		if(cd.containsKey(str))
			return cd.get(str);
		else 
			return 0;
	}
	int getmcd(String u) {
		if(mcd.containsKey(u))
			return mcd.get(u);
		else 
			return 0;
	}

	int getpcd(String u) {
		if(pcd.containsKey(u))
			return pcd.get(u);
		else 
			return 0;
	}
	public void color(String dst, HashSet<String> visited, HashSet<String> color) {
		KCoreResult H = findSubCore(dst);
		for(String str: H.graph.keySet()) {
			color.add(str);
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
