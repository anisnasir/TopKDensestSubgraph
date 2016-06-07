package kcore;

import java.util.ArrayList;

public class Output {
	double density;
	int coreNum;
	int size;
	double timeTaken;
	ArrayList<String> nodes;
	
	public double getTimeTaken() {
		return timeTaken;
	}

	public void setTimeTaken(double timeTaken) {
		this.timeTaken = timeTaken;
	}

	Output() {
		
	}
	
	

	public double getDensity() {
		return density;
	}
	public void setDensity(double density) {
		this.density = density;
	}
	public int getCoreNum() {
		return coreNum;
	}
	public void setCoreNum(int coreNum) {
		this.coreNum = coreNum;
	}
	public ArrayList<String> getNodes() {
		return nodes;
	}
	public void setNodes(ArrayList<String> nodes) {
		this.nodes = nodes;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getSize() {
		return this.size;
	}
	
	void printOutput() {
		System.out.println("#main core\tDensity\tsize\tdensest\ttime taken");
		System.out.println(coreNum+"\t"+density+"\t"+size+"\t"+nodes+"\t"+timeTaken);
	}
	
	
}
