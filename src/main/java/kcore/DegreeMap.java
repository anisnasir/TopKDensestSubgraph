package kcore;
import java.util.ArrayList;



/*
 * Degree map store <degree, Set<String> nodes> in a HashMap
 */
public class DegreeMap {
	ArrayList<ArrayList<String>>  map;
	int capacity;
	DegreeMap() {
		map = new ArrayList<ArrayList<String>> ();
		capacity = 2;
		for(int i =0;i< capacity;i++) {
			map.add(new ArrayList<String>());
		}
	}
	
	void addNode(int degree, String nodeId) {
		while(degree >= capacity) 
			increaseCapacity();
		
		map.get(degree).add(nodeId);
	}
	
	void removeNode(int degree, String nodeId) {
		map.get(degree).remove(nodeId);
	}
	
	void increaseCapacity() {
		capacity=2*capacity;
		for(int i = capacity/2;i<capacity;i++) 
			map.add(new ArrayList<String>());
	}
	ArrayList<String> getNodes(int degree) {
		return this.map.get(degree);
	}
	
	void incrementDegree(int degree, String nodeId) {
		removeNode(degree,nodeId);
		addNode(degree+1,nodeId);
	}
	
	void decremnetDegree(int degree, String nodeId) {
		removeNode(degree,nodeId);
		addNode(degree-1,nodeId);
	}
	
	ArrayList<String> getNodesBetween(double upperBound, double lowerBound) {
		if(Math.floor(upperBound) < lowerBound) {
			return null;
		}else {
			return map.get((int)Math.round(lowerBound));
		}
	}
	

	
	
}
