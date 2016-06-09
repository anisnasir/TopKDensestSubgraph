package kcore;
import java.util.HashMap;
import java.util.HashSet;


public class EpastoDensest implements Comparable<EpastoDensest>{
	double density;
	int numEdges; 
	HashMap<String,HashSet<String>> densest;
	HashMap<String, Integer> sk ;
	double beta;
	
	EpastoDensest() {
		densest = new HashMap<String,HashSet<String>>();
		sk = new HashMap<String,Integer>();
		beta = 0.0;
		density = 0;
		numEdges = 0;
	}
	
	void setNumEdges(int numEdges) {
		this.numEdges = numEdges;
	}
	
	void setSk(HashMap<String, Integer> sk) {
		this.sk = sk;
	}
	
	HashMap<String, Integer> getSk() {
		return this.sk;
	}
	double removeEdge (StreamEdge edge) {
		String src = edge.getSource();
		String dst = edge.getDestination();
		
		if( densest.containsKey(src)) {
			if(densest.containsKey(dst)) {
				if(densest.get(src).contains(dst)) {
					if(densest.get(dst).contains(src) ) {
						densest.get(src).remove(dst);
						densest.get(dst).remove(src);
						numEdges--;
						if(densest.size() == 0)
							density = 0;
						else
							density = numEdges/(double)densest.size();
					}
				}
			}
		}
		return density;
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
	public HashMap<String,HashSet<String>> getDensest() {
		return densest;
	}
	public void setDensest(HashMap<String,HashSet<String>> densest_subgraph, int numEdges) {
		this.densest = densest_subgraph;
		this.numEdges = numEdges;
	}
	void print() {
		System.out.println("Density: " + density);
		for(String str:densest.keySet()) {
			System.out.println(str+" " + densest.get(str));
		}
	}
	public int compareTo(EpastoDensest o) {
		return Double.compare(this.density, o.density);
	}
	
	
	
}
