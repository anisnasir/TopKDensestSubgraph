package epasto;

import input.StreamEdge;
import interfaces.DensestSubgraph;

import java.util.ArrayList;
import java.util.HashSet;

import output.Output;
import struct.DegreeMap;
import struct.NodeMap;
import utility.EdgeHandler;
import utility.SetFunctions;


public class EpastoTopK implements DensestSubgraph{
	int k; 
	double epsilon;
	EpastoFullyDyn [] epastoK;
	EpastoDensest [] densestK;
	NodeMap[] nodeMap;
	DegreeMap[] degreeMap;
	EdgeHandler utility;
	boolean LOGGING;

	public EpastoTopK(int k, double epsilon) {
		this.k = k ;
		this.epsilon = epsilon;
		epastoK = new EpastoFullyDyn[k];
		for(int i=0; i < k ; i ++) 
			epastoK[i] = new EpastoFullyDyn(epsilon);

		densestK = new EpastoDensest[k];
		for(int i=0; i < k ; i ++) 
			densestK[i] = new EpastoDensest();

		nodeMap = new NodeMap[k];
		degreeMap = new DegreeMap[k];
		for(int i =0 ;i < k;i++) {
			nodeMap[i] = new NodeMap();
			degreeMap[i] = new DegreeMap();
		}
		utility = new EdgeHandler();

	}

	public void addEdge(StreamEdge edge) {
		String src = edge.getSource();
		String dst = edge.getDestination();

		if(nodeMap[0].contains(edge))
			return ;

		HashSet<String> prevDensest = new HashSet<String>();
		HashSet<String> allDensest = new HashSet<String>();

		EdgeHandler utility = new EdgeHandler();
		int i =0 ;
		while( i<k) {
			
			if(!allDensest.contains(src) && !allDensest.contains(dst)) {
				utility.handleEdgeAddition(edge, nodeMap[i],degreeMap[i]);
				epastoK[i].MainFullyDynamic(edge, nodeMap[i],degreeMap[i] ,EpastoOp.ADD);

			}
			
			if(i != 0 ) {
				for(String str: prevDensest) {
					if(!allDensest.contains(str)) {
						HashSet<String> neighbors = nodeMap[0].getNeighbors(str);
						if(neighbors!= null)
							for(String neighbor:neighbors) {
								if(!allDensest.contains(neighbor) && nodeMap[i].map.containsKey(neighbor)) {
									utility.handleEdgeAddition(new StreamEdge(str,neighbor), nodeMap[i], degreeMap[i]);
									epastoK[i].MainFullyDynamic(new StreamEdge(str,neighbor), nodeMap[i],degreeMap[i], EpastoOp.ADD);
								}
							}
					}
				}
			}
			
			for(String str:allDensest) {
				HashSet<String> neighbors = nodeMap[i].getNeighbors(str);
				if(neighbors!=null) {
					neighbors = new HashSet<String>(neighbors);
					for(String neighbor:neighbors) { 
						if(nodeMap[i].map.containsKey(neighbor)) {
							if(nodeMap[i].map.get(neighbor).contains(str)) {
								utility.handleEdgeDeletion(new StreamEdge(str,neighbor), nodeMap[i],degreeMap[i]);
								epastoK[i].MainFullyDynamic(new StreamEdge(str,neighbor), nodeMap[i],degreeMap[i], EpastoOp.REMOVE);
							}
						}
					}
				}
			}
			EpastoDensest tempDensest =epastoK[i].getDensest();

			if(tempDensest!= null) {
				if(tempDensest.densest.equals(densestK[i].densest)) {
					
				}else if(densestK[i].densest.getNumNodes() != 0) {
					SetFunctions helper = new SetFunctions();
					HashSet<String> common = helper.intersectionSet(tempDensest.densest.map.keySet(), densestK[i].densest.map.keySet());
					prevDensest.addAll(densestK[i].densest.map.keySet());
					prevDensest.removeAll(common); 
				}
				allDensest.addAll(new ArrayList<String>(tempDensest.getDensest().map.keySet()));
				densestK[i] = tempDensest;
			}else {
				densestK[i] = new EpastoDensest();
			}
			i++;
		}

	}

	public boolean removeEdge(StreamEdge edge) {
		HashSet<String> prevDensest = new HashSet<String>();
		HashSet<String> allDensest = new HashSet<String>();
		EdgeHandler utility = new EdgeHandler();
		
		if(!nodeMap[0].contains(edge))
			return false;
		
		int i =0 ;
		while( i<k) {
			if(nodeMap[i].contains(edge)) {
				utility.handleEdgeDeletion(edge, nodeMap[i],degreeMap[i]);
				epastoK[i].MainFullyDynamic(edge, nodeMap[i],degreeMap[i], EpastoOp.REMOVE);
			}
			
			if( i != 0 ) {
				for(String str: prevDensest) {
					if(!allDensest.contains(str)) {
						HashSet<String> neighbors = nodeMap[0].getNeighbors(str);
						if(neighbors != null) 
							for(String neighbor:neighbors) {
								if(!allDensest.contains(neighbor) && nodeMap[i].map.containsKey(neighbor)) {
									utility.handleEdgeAddition(new StreamEdge(str,neighbor), nodeMap[i],degreeMap[i]);
									epastoK[i].MainFullyDynamic(new StreamEdge(str,neighbor), nodeMap[i],degreeMap[i], EpastoOp.ADD);
								}
							}
					}
				}
			}
			if(i != 0) {
				for(String str:allDensest) {
					HashSet<String> neighbors = nodeMap[0].getNeighbors(str);
					if(neighbors!=null) {
						neighbors = new HashSet<String>(neighbors);
						for(String neighbor:neighbors) {
							if(nodeMap[i].map.containsKey(neighbor)) {
								if(nodeMap[i].map.get(neighbor).contains(str)) {
									utility.handleEdgeDeletion(new StreamEdge(str,neighbor), nodeMap[i],degreeMap[i]);
									epastoK[i].MainFullyDynamic(new StreamEdge(str,neighbor), nodeMap[i],degreeMap[i], EpastoOp.REMOVE);
								}
							}
						}
					}
				}
			}

			EpastoDensest tempDensest = epastoK[i].getDensest();
			if(tempDensest!= null) {
				if(tempDensest.densest.equals(densestK[i].densest)) {
					
				}
				else if(densestK[i].densest.getNumNodes() != 0) {
					SetFunctions helper = new SetFunctions();
					HashSet<String> common = helper.intersectionSet(tempDensest.densest.map.keySet(), densestK[i].densest.map.keySet());
					allDensest.addAll(tempDensest.densest.map.keySet());

					prevDensest.addAll(densestK[i].densest.map.keySet());
					prevDensest.removeAll(common); 
					densestK[i] = tempDensest;
				}else {
					densestK[i] = tempDensest;
					allDensest.addAll(tempDensest.densest.map.keySet());
				}
			}else {
				densestK[i] = new EpastoDensest();
			}

			i++;
		}
		return true;
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

		for(int i = 0 ; i < k ;i ++ ) {
			Output output = new Output();
			output.coreNum = 0;
			output.density = this.densestK[i].getDensity();
			output.size = this.densestK[i].densest.getNumNodes();
			output.nodes = new ArrayList<String>(this.densestK[i].densest.map.keySet());
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
