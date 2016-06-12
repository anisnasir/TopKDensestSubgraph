package struct;
import input.StreamEdge;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;



/*
 * Node map store <node identifiers, Set<String> Node Neighbors> in a HashMap
 */
public class NodeMap {
	public HashMap<String,HashSet<String>>  map;
	public int numEdges;
	public NodeMap() {
		map = new HashMap<String,HashSet<String>> ();
		numEdges= 0;
	}
	
	void addNode(String str) {
		map.put(str, new HashSet<String>());
	}
	public void addEdge(String src, String dest) { 
		numEdges++;
		if (map.containsKey(src)) {
			HashSet<String> neighbors = map.get(src);
			neighbors.add(dest);
			map.put(src, neighbors);
		}else {
			HashSet<String> neighbors = new HashSet<String> ();
			neighbors.add(dest);
			map.put(src, neighbors);
		}
		
	}
	public void removeEdge(String src, String dest) { 
		if(map.containsKey(src))
		{
			HashSet<String> neighbors = map.get(src);
			if(neighbors.contains(dest)) {
				numEdges--;
			}
			neighbors.remove(dest);
		
			if(!neighbors.isEmpty()) {
				map.put(src, neighbors);
			}
			else {
				map.remove(src);
			}
		}
	}
	public int getDegree(String nodeId) {
		if (map.containsKey(nodeId))
			return map.get(nodeId).size();
		else
			return 0;
	}
	public int getNumNodes() {
		return map.size();
	}
	public int getNumEdges () { 
		return this.numEdges/2;
	}
	public void printMap() {
	    Iterator<Entry<String, HashSet<String>>> it = map.entrySet().iterator();
	    while (it.hasNext()) {
	        Entry<String, HashSet<String>> pair = it.next();
	        System.out.println(pair.getKey() + " = " + pair.getValue());
	    }
	}
	
	public HashSet<String> getNeighbors(String node) {
		return map.get(node);
	}
	
	public boolean contains(StreamEdge item) {
		String src = item.getSource();
		String dst = item.getDestination();
		
		if(map.containsKey(src)) {
			HashSet<String> neighbors = map.get(src);
			if(neighbors != null)
				if(neighbors.contains(dst)) {
					if(map.containsKey(dst))
					{
						HashSet<String> another = map.get(dst);
						if(another.contains(src))
							return true;
					}
				}
		}
		return false;
		
	}
	
	public NodeMap getCopy() {
		NodeMap returnMap = new NodeMap();
		returnMap.numEdges = this.numEdges;
		for(String key: this.map.keySet()) {
			returnMap.map.put(key, new HashSet<String>(this.map.get(key)));
		}
		return returnMap;
	}
	
	public double getDensity() {
		if (this.getNumNodes() == 0)
			return 0;
		else 
			return (this.getNumEdges()/(double)this.getNumNodes());
	}
	
}
