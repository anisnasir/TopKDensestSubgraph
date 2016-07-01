package kcore;


import input.StreamEdge;
import interfaces.DensestSubgraph;

import java.util.ArrayList;
import java.util.HashSet;

import output.Output;
import struct.DegreeMap;
import struct.NodeMap;
import utility.EdgeHandler;

public class KCoreTopK implements DensestSubgraph{
	int k ;
	public KCoreTopK(int k ) {
		this.k = k;
	}
	@Override
	public ArrayList<Output> getDensest(DegreeMap degreeMap, NodeMap nodeMap) {
		ArrayList<Output> list = new ArrayList<Output>();
		ArrayList<Output> out = null;
		NodeMap nodeMapLocal = nodeMap.getCopy();
		DegreeMap degreeMapLocal = degreeMap.getCopy();
		DensestSubgraph densest = new KCore();
		
		for(int i =0 ; i< k; i++) {
			out = densest.getDensest(degreeMapLocal.getCopy(),nodeMapLocal.getCopy());
			list.add(out.get(0));
			if(i+1 < k )
				removeBulk(degreeMapLocal, nodeMapLocal, out.get(0));
			if(nodeMapLocal.getNumNodes() == 0)
				return list;
		}
		return list;
	}
	
	void removeBulk(DegreeMap degreeMap, NodeMap nodeMap, Output out) {
		ArrayList<String> nodes = new ArrayList<String>(out.getNodes());
		EdgeHandler helper = new EdgeHandler();
		for(String node:nodes) {
			HashSet<String > temp = nodeMap.getNeighbors(node);
			ArrayList<String> neighbors;
			if( temp != null)
				neighbors = new ArrayList<String>(nodeMap.getNeighbors(node));
			else 
				neighbors = new ArrayList<String>();
			for(String neighbor: neighbors) {
				helper.handleEdgeDeletion(new StreamEdge(node,neighbor), nodeMap,degreeMap);
			}
		}
	}
	
	
}
