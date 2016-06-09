package epasto;
import input.StreamEdge;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import main.DensestSubgraph;
import output.Output;
import struct.DegreeMap;
import struct.NodeMap;



public class EpastoFullyDyn implements DensestSubgraph{
	HashMap<String, Integer> sk ;
	double epsilon;
	double epsilon_tilda;
	EpastoDensest densest;
	double beta;
	//NodeMap G_tilda;
	boolean LOGGING;
	int R_tilda;
	int s;
	Integer bound;
	double R_star;
	
	public EpastoFullyDyn(double epsilon) {
		sk = new HashMap<String, Integer>();
		densest = null;
		this.epsilon = epsilon;
		//G_tilda = new NodeMap();
		epsilon_tilda = 2*epsilon + Math.pow(epsilon, 2);
		bound = 0;
		Properties prop = new Properties();
		try {

			InputStream input = new FileInputStream("config.properties");
			prop.load(input);
			input.close();
		} catch (Exception ex) {
			
		}
		LOGGING = Boolean.parseBoolean(prop.getProperty("LOGGING"));
		
	}
	
	public void MainFullyDynamic(StreamEdge edge, NodeMap nodeMap, EpastoOp op) {
		if(densest ==null) {
			s=1;
			R_tilda=0;
			densest = findDensest(nodeMap.map, nodeMap.getNumEdges(), nodeMap.getNumNodes(), 0, epsilon_tilda);
			sk = densest.getSk();
		
			//G_tilda.map = deepCopy(nodeMap.map);
			//G_tilda.numEdges = nodeMap.numEdges;
			double m0 = nodeMap.getNumEdges();
			double a = 6*Math.pow((1+epsilon),2);
			double b = logb(nodeMap.getNumNodes(),(1+epsilon));
			R_star = m0*epsilon/(a*b);
			
		} else {
			boolean rebuild= false;
			if(op == EpastoOp.ADD) {
				if (R_tilda < R_star) {
					//G_tilda.addEdge(edge.getSource(), edge.getDestination());
					//G_tilda.addEdge(edge.getDestination(), edge.getSource());
					//G_tilda.incrementEdges();
				}
				rebuild = addEdge(edge, nodeMap, densest.beta, epsilon_tilda);
			}else {
				rebuild = removeEdge(edge, nodeMap, densest, epsilon_tilda);
				R_tilda++;
			}
			if(rebuild) {
				if(R_tilda < R_star ) { 
					//Densest h = this.find(G_tilda.map, G_tilda.numEdges, G_tilda.getNumNodes(), densest.beta, epsilon_tilda);	
					EpastoDensest h = this.find(nodeMap.map, nodeMap.getNumEdges(), nodeMap.getNumNodes(), densest.beta, epsilon_tilda);	
					sk = h.getSk();
					if(h.getDensity() >= densest.getDensity()) {
						densest = h;
						//sk = h.getSk();
						
					}
				} else {
					s= s+1;
					
					EpastoDensest h = findDensest(nodeMap.map, nodeMap.getNumEdges(), nodeMap.getNumNodes(), densest.density, epsilon_tilda);
					sk= h.getSk();	
					//G_tilda.map = deepCopy(nodeMap.map);
					//G_tilda.numEdges = nodeMap.numEdges;
					if(h.getDensity() >= densest.getDensity()) {
						densest = h;
						
					}
					double m0 = nodeMap.getNumEdges();
					double a = 6*Math.pow((1+epsilon),2);
					double b = logb(nodeMap.getNumNodes(),(1+epsilon));
					R_star = m0*epsilon/(a*b);
					R_tilda = 0;
				}
			}
		} 
		
	}
	
	boolean removeEdge(StreamEdge edge, NodeMap nodeMap, EpastoDensest densest, double epsilon_tilda) {
		double new_density = densest.removeEdge(edge);
		double b = Math.pow((1+epsilon_tilda),2);
		
		String src = edge.getSource();
		String dst = edge.getDestination();
		
		if(nodeMap.getDegree(src) == 0)
			sk.remove(src);
		if(nodeMap.getDegree(dst) == 0)
			sk.remove(dst);
		
		if(new_density < (densest.beta/b)) {
			return true;
		}
		return false;
	}
	
	EpastoDensest getDensest () {
		return densest;
	}
	
	
	boolean addEdge(StreamEdge edge, NodeMap nodeMap, double beta, double epsilon) {
		String src = edge.getSource();
		String dst = edge.getDestination();
		
		Deque<String> stack = new ArrayDeque<String>();
		stack.push(src);
		stack.push(dst);
		
		while(!stack.isEmpty()) {
			String node = stack.pop();
			
			if(!sk.containsKey(node))
				sk.put(node, 0);
			
			int st = sk.get(node);
		
			HashSet<String> neighbors = nodeMap.getNeighbors(node);
			int inDegree = 0;
			for(String neighbor:neighbors) {
				int neighborst;
				if(sk.containsKey(neighbor)) 
					neighborst = sk.get(neighbor);
				else 
					neighborst = 0;
				
				if(neighborst >= st) 
					inDegree++;
			}
			double threshold = 2*(1+epsilon)*beta;
			if(inDegree < threshold) {
				continue;
			}
		
			int t_tilda = st;
			while( inDegree >= threshold && t_tilda < bound) {
				t_tilda++;
				inDegree = 0;
				for(String neighbor:neighbors) {
					int neighborst;
					if(sk.containsKey(neighbor)) 
						neighborst = sk.get(neighbor);
					else 
						neighborst = 0;
					if(neighborst >= t_tilda) 
						inDegree++;
				}
			}
			if(t_tilda == bound)
				return true;
			
			sk.put(node, t_tilda);
			
			for(String neighbor:neighbors) 
				stack.push(neighbor);
		}
		return false;
		
	}
	HashMap<String,HashSet<String>> deepCopy(HashMap<String,HashSet<String>> graph) {
		HashMap<String,HashSet<String>> returnGraph = new HashMap<String,HashSet<String>>();
		for(String str:graph.keySet()) {
			HashSet<String> neighbors = new HashSet<String>(graph.get(str));
			returnGraph.put(str, neighbors);
		}
		return returnGraph;
		
	}
	
	EpastoDensest findDensest(HashMap<String,HashSet<String>> graph, int numEdges, int numNodes, double density, double epsilon) {
		EpastoDensest returnResult = null;
		double beta = Math.max(1/(4*(1+epsilon)), (1+epsilon)*density);
		while(true) {
			EpastoDensest temp = find(graph,numEdges,numNodes,beta, this.epsilon);
			if(returnResult == null )
				returnResult = temp;
			
			if(temp.getDensity() >= beta) {
				returnResult = temp;
				beta = (1+epsilon)*temp.getDensity();
			}else {
				returnResult.setBeta(beta);
				returnResult.setSk(temp.sk);
				return returnResult;
			}
		}
	}
	
	EpastoDensest find(HashMap<String,HashSet<String>> graph, int numEdges, int numNodes, double beta, double epsilon) {
		//output parameters
		EpastoDensest returnResult = new EpastoDensest();
		HashMap<String, Integer> dSk = new HashMap<String, Integer>();
		HashMap<String,HashSet<String>> densest_subgraph = deepCopy(graph);
		double density = numEdges/(double)numNodes;
		double max_density = density;
		int edgeCount = numEdges;
		
		//algorithm parameters
		bound= (int)Math.ceil(logb(numNodes, 1+epsilon));
		graph = deepCopy(graph); 
		
		//algorithm variables
		int new_edges = numEdges;
		int t = 0 ;
		
		while(!graph.isEmpty() && t < bound) {
			for(String str: graph.keySet()) {
				dSk.put(str, t);
			}
			new_edges = removeNode(graph, new_edges, beta, epsilon);
			
			
			density = (graph.size()==0) ? 0  : (new_edges/(double)graph.size());
			
			if(density > max_density) {
				max_density = density;
				densest_subgraph =deepCopy(graph);
				edgeCount = new_edges;
			}
			++t;
		}
		/*if(!graph.isEmpty()) {
			for(String str: graph.keySet()) {
				dSk.put(str, t);
			}
		}*/
		returnResult.setDensity(max_density);
		returnResult.setDensest(densest_subgraph,edgeCount);
		returnResult.setBeta(beta);
		returnResult.setSk(dSk);
		return returnResult;
	}
	
	
	int removeNode(HashMap<String,HashSet<String>> graph,int numEdges, double beta, double epsilon) {
		double threshold = 2*(1+epsilon)*beta;
		HashSet<String> nodesRemove = new HashSet<String>();
		for(String str: graph.keySet()) {
			if(graph.get(str).size() <= threshold) {
				nodesRemove.add(str);
			}
		}
		for(String str: nodesRemove) {
			HashSet<String> neighbors = new HashSet<String>(graph.get(str)); 
			for(String neighbor: neighbors) {
				graph.get(neighbor).remove(str);
				numEdges--;
			}
			graph.remove(str);
		}
		return numEdges;
	}
	
	double logb(int num,  double base) {
		return (Math.log(num)/Math.log(base));
	}
	public int intersection (Set<String> set1, Set<String> set2) {
		Set<String> a;
		Set<String> b;
		int counter = 0;
		if (set1.size() <= set2.size()) {
			a = set1;
			b = set2; 
		} else {
			a = set2;
			b = set1;
		}
		for (String e : a) {
			if (b.contains(e)) {
				counter++;
			} 
		}
		return counter;
	}
	public HashSet<String> intersectionSet (Set<String> set1, Set<String> set2) {
		Set<String> a;
		Set<String> b;
		HashSet<String> returnSet = new HashSet<String>();
		if (set1.size() <= set2.size()) {
			a = set1;
			b = set2; 
		} else {
			a = set2;
			b = set1;
		}
		for (String e : a) {
			if (b.contains(e)) {
				returnSet.add(e);
			} 
		}
		return returnSet;
	}

	@Override
	public ArrayList<Output> getDensest(DegreeMap degreeMap, NodeMap nodeMap) {
		// TODO Auto-generated method stub
		
		if(nodeMap.map.isEmpty()) {
			return getDummy();
		}
		ArrayList<Output> outputArray = new ArrayList<Output>();
		Output output = new Output();
		output.coreNum = 0;
		output.density = this.densest.getDensity();
		output.size = this.densest.densest.size();
		output.nodes = new ArrayList<String>(this.densest.densest.keySet());
		outputArray.add(output);
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
