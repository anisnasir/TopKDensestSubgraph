package epasto;

import input.StreamEdge;

import java.util.HashMap;

import struct.NodeMap;
import utility.EdgeHandler;


public class EpastoDensest implements Comparable<EpastoDensest>{
	double density;
	NodeMap densest;
	HashMap<String, Integer> sk ;
	int numEdges;
	double beta;
	
	public EpastoDensest() {
		densest = new NodeMap();
		sk = new HashMap<String,Integer>();
		beta = 0.0;
		density = 0;
		numEdges = 0 ;
	}
	
	void setSk(HashMap<String, Integer> sk) {
		this.sk = sk;
	}
	
	void setNumEdges( int numEdges) {
		this.numEdges = numEdges;
	}
	int getNumEdges() {
		return this.numEdges;
	}
	
	double removeEdge(StreamEdge edge) {
		EdgeHandler helper = new EdgeHandler();
		helper.handleEdgeDeletion(edge, densest);
		density = densest.getDensity();
		return densest.getDensity();
	}
	
	HashMap<String, Integer> getSk() {
		return this.sk;
	}
	public void setBeta(double a) {
		beta = a;
	}
	public double getDensity() {
		return density;
	}
	public void setDensity(double density) {
		this.density = density;
	}
	public NodeMap getDensest() {
		return densest;
	}
	public void setDensest(NodeMap densest_subgraph) {
		this.densest = densest_subgraph;
	}
	public int compareTo(EpastoDensest o) {
		return Double.compare(this.density, o.density);
	}
	
	
	
}
