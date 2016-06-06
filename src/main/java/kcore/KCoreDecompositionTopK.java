package kcore;


import java.util.ArrayList;
import java.util.HashSet;

public class KCoreDecompositionTopK implements DensestSubgraph{
	int k ;
	public KCoreDecomposition densest;
	
	public void setDensest(KCoreDecomposition densest) {
		this.densest = densest;
	}

	KCoreDecompositionTopK(int k, NodeMap nodeMap) {
		this.k = k;
		densest = new KCoreDecomposition(nodeMap.map);
	}

	
	@Override
	public ArrayList<Output> getDensest(DegreeMap degreeMap, NodeMap nodeMap) {
		ArrayList<Output> list = new ArrayList<Output>();
		ArrayList<StreamEdge> removedEdges = new ArrayList<StreamEdge>();
		ArrayList<Output> out = null;
		
		for(int i =0 ; i< k; i++) {
			out = densest.getDensest(degreeMap, nodeMap);
			list.add(out.get(0));
			removeBulk(nodeMap, out.get(0), removedEdges);
			if(nodeMap.getNumNodes() == 0) {
				addRemovedEdges(removedEdges, nodeMap);
				return list;
			}
		}
		addRemovedEdges(removedEdges, nodeMap);
		return list;
	}
	
	void addRemovedEdges(ArrayList<StreamEdge> removedEdges, NodeMap nodeMap) {
		for(StreamEdge edge: removedEdges) {
			nodeMap.addEdge(edge.getSource(), edge.getDestination());
			nodeMap.addEdge(edge.getDestination(), edge.getSource());
			densest.addEdge(edge.getSource(), edge.getDestination());
		}
	}
 	void removeBulk(NodeMap nodeMap, Output out, ArrayList<StreamEdge> removedEdges) {
 		ArrayList<String> nodes = out.getNodes();
		for(String node:nodes) {
			HashSet<String > temp = nodeMap.getNeighbors(node);
			ArrayList<String> neighbors;
			if( temp != null)
				neighbors = new ArrayList<String>(nodeMap.getNeighbors(node));
			else 
				neighbors = new ArrayList<String>();
			for(String neighbor: neighbors) {
				removedEdges.add(new StreamEdge(node,neighbor));
				nodeMap.removeEdge(node, neighbor);
				nodeMap.removeEdge(neighbor, node);
				densest.removeEdge(node, neighbor);
			}
		}
	}
	
	
}
