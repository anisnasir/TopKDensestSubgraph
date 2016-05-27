package kcore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;



/*
 * Node map store <node identifiers, Set<String> Node Neighbors> in a HashMap
 */
public class NodeMap {
	HashMap<String,ArrayList<String>>  map;
	public NodeMap() {
		map = new HashMap<String,ArrayList<String>> ();
	}
	
	public int addNode(String src, String dest) { 
		if (map.containsKey(src)) {
			ArrayList<String> neighbors = map.get(src);
			neighbors.add(dest);
			map.put(src, neighbors);
			return neighbors.size();
		}else {
			ArrayList<String> neighbors = new ArrayList<String> ();
			neighbors.add(dest);
			map.put(src, neighbors);
			return neighbors.size();
		}
	}
	
	int removeNode(String src, String dest) { 
		if(map.containsKey(src))
		{
			ArrayList<String> neighbors = map.get(src);
			neighbors.remove(dest);
			if(!neighbors.isEmpty()) {
				map.put(src, neighbors);
				return neighbors.size();
			}
			else {
				map.remove(src);
				return 0;
			}
		}else 
			return 0;
		
	}
	int getDegree(String nodeId) {
		if (map.containsKey(nodeId))
			return map.get(nodeId).size();
		else
			return 0;
	}
	int getNumNodes() {
		return map.size();
	}
	public void printMap() {
	    Iterator<Entry<String, ArrayList<String>>> it = map.entrySet().iterator();
	    while (it.hasNext()) {
	        Entry<String, ArrayList<String>> pair = it.next();
	        System.out.println(pair.getKey() + " = " + pair.getValue());
	    }
	}
	
	public ArrayList<String> getNeighbors(String node) {
		return map.get(node);
	}
	
	boolean contains(StreamEdge item) {
		String src = item.getSource();
		String dst = item.getDestination();
		
		if(map.containsKey(src)) {
			ArrayList<String> neighbors = map.get(src);
			if(neighbors != null)
				if(neighbors.contains(dst)) {
					if(map.containsKey(dst))
					{
						ArrayList<String> another = map.get(dst);
						if(another.contains(src))
							return true;
					}
				}
		}
		return false;
		
	}
	
}
