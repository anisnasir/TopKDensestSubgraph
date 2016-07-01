package kcorequad;


import input.StreamEdge;
import interfaces.DensestSubgraph;

import java.util.ArrayList;
import java.util.HashSet;

import output.Output;
import struct.DegreeMap;
import struct.NodeMap;
import utility.EdgeHandler;

public class KCoreQuadTopK implements DensestSubgraph{
	int k ;
	public KCoreQuad densest;
	
	public void setDensest(KCoreQuad densest) {
		this.densest = densest;
	}

	public KCoreQuadTopK(int k, NodeMap nodeMap) {
		this.k = k;
		densest = new KCoreQuad(nodeMap.map);
	}

	
	@Override
	public ArrayList<Output> getDensest(DegreeMap degreeMap, NodeMap nodeMap) {
		ArrayList<Output> list = new ArrayList<Output>();
		ArrayList<StreamEdge> removedEdges = new ArrayList<StreamEdge>();
		ArrayList<Output> out = null;
		
		for(int i =0 ; i< k; i++) {
			out = densest.getDensest(null, nodeMap);
			list.add(out.get(0));
			if(i+1 < k )
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
		//System.out.println(removedEdges);
		EdgeHandler helper = new EdgeHandler();
		for(StreamEdge edge: removedEdges) {
			helper.handleEdgeAddition(edge, nodeMap);
			densest.addEdge(edge.getSource(), edge.getDestination());
		}
	}
 	void removeBulk(NodeMap nodeMap, Output out, ArrayList<StreamEdge> removedEdges) {
 		ArrayList<String> nodes = out.getNodes();
 		for(String node:nodes) {
			HashSet<String > temp = densest.graph.get(node);
			if( temp != null) { 
				HashSet<String> neighbors =  new HashSet<String>(temp);
				EdgeHandler helper = new EdgeHandler();
				for(String neighbor: neighbors) {
					StreamEdge edge = new StreamEdge(node,neighbor);
					helper.handleEdgeDeletion(edge, nodeMap);
					removedEdges.add(edge);
					densest.removeEdge(edge.getSource(), edge.getDestination());
				}
			}
			
		}
		
	}
	
	
}
