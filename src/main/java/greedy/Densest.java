package greedy;
import java.util.HashMap;
import java.util.HashSet;


public class Densest {
	double density;
	HashMap<String,HashSet<String>> densest;
	public double getDensity() {
		return density;
	}
	public void setDensity(double density) {
		this.density = density;
	}
	public HashMap<String,HashSet<String>> getDensest() {
		return densest;
	}
	public void setDensest(HashMap<String,HashSet<String>> densest_subgraph) {
		this.densest = densest_subgraph;
	}
	
	void print() {
		System.out.println("Density:" + density);
		for(String str:densest.keySet()) {
			System.out.println(str+" " + densest.get(str));
		}
	}
	
}
