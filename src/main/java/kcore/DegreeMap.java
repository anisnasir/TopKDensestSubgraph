package kcore;
import java.util.ArrayList;
import java.util.HashSet;



/*
 * Degree map store <degree, Set<String> nodes> in a HashMap
 */
public class DegreeMap {
	ArrayList<HashSet<String>>  map;
	int capacity;
	DegreeMap(int i) {
		map = new ArrayList<HashSet<String>> ();
		capacity = i;
	}
	DegreeMap() {
		map = new ArrayList<HashSet<String>> ();
		capacity = 2;
		for(int i =0;i< capacity;i++) {
			map.add(new HashSet<String>());
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
			map.add(new HashSet<String>());
	}
	HashSet<String> getNodes(int degree) {
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
	void decremnetDegreeExpire(int degree, String nodeId) {
		removeNode(degree,nodeId);
		if(degree-1 > 0)
			addNode(degree-1,nodeId);
	}
	
	HashSet<String> getNodesBetween(double upperBound, double lowerBound) {
		if(Math.floor(upperBound) < lowerBound) {
			return null;
		}else {
			return map.get((int)Math.round(lowerBound));
		}
	}
	
	DegreeMap getCopy() {
		DegreeMap returnMap = new DegreeMap(this.capacity);
		for(int i =0; i< this.capacity ;i++) {
			returnMap.map.add(new HashSet<String>(this.map.get(i)));
		}
		return returnMap;
	}
	

	
	
}
