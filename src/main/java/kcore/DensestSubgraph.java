package kcore;

import java.util.ArrayList;


public interface DensestSubgraph {
	public ArrayList<Output> getDensest(DegreeMap degreeMap, NodeMap nodeMap);
}
