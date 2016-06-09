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
		
		if(nodeMap[0].contains(edge))
			return false;
 		
		return true;
		
	}
	
	boolean removeEdge(StreamEdge edge) {
		
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
		if(nodeMap.map.isEmpty()) {
			for(int i =0 ;i< k;i++)
				outputArray.add(getDummy().get(0));
			return outputArray;
		}
		
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
