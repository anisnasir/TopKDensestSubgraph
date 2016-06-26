package interfaces;

import java.util.ArrayList;

import output.Output;
import struct.DegreeMap;
import struct.NodeMap;


public interface DensestSubgraph {
	public ArrayList<Output> getDensest(DegreeMap degreeMap, NodeMap nodeMap);
}
