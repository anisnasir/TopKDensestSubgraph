package kcore;

import java.util.ArrayList;

public class Output {
	double density;
	int coreNum;
	int size;
	ArrayList<String> nodes;
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
	
	void printOutput() {
		//System.out.println("main core " + coreNum);
		System.out.println("Density: " + density);
		//System.out.println("Densest size: " + size);
		//System.out.println(nodes);
	}
}
