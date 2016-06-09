package output;

import java.util.ArrayList;

public class Output {
	public double density;
	public int coreNum;
	public int size;
	public double timeTaken;
	public ArrayList<String> nodes;
	
	
	public double getTimeTaken() {
		return timeTaken;
	}

	public void setTimeTaken(double timeTaken) {
		this.timeTaken = timeTaken;
	}

	public Output() {
		
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
	
	public void printOutput() {
		System.out.println("#main core\tDensity\tsize\tdensest\ttime taken");
		System.out.println(coreNum+"\t"+density+"\t"+size+"\t"+nodes+"\t"+timeTaken);
	}
	
	
}
