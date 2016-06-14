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

import main.DensestSubgraph;
import output.Output;
import struct.DegreeMap;
import struct.NodeMap;
import utility.EdgeHandler;



public class EpastoFullyDyn implements DensestSubgraph{
	HashMap<String, Integer> sk ;
	double epsilon;
	double epsilon_tilda;
	EpastoDensest densest;
	double beta;
	NodeMap nodeMap_tilda;
	DegreeMap degreeMap_tilda;
	boolean LOGGING;
	int R_tilda;
	int s;
	double R_star;
	
	public EpastoFullyDyn(double epsilon) {
		sk = new HashMap<String, Integer>();
		densest = null;
		this.epsilon = epsilon;
		epsilon_tilda = 2*epsilon + Math.pow(epsilon, 2);
		Properties prop = new Properties();
		try {

			InputStream input = new FileInputStream("config.properties");
			prop.load(input);
			input.close();
		} catch (Exception ex) {
			
		}
		LOGGING = Boolean.parseBoolean(prop.getProperty("LOGGING"));
		
	}
	
	public void MainFullyDynamic(StreamEdge edge, NodeMap nodeMap,DegreeMap degreeMap, EpastoOp op) {
		if(densest ==null) {
			s=1;
			R_tilda=0;
			densest = findDensest(nodeMap,degreeMap, 0, epsilon_tilda);
			sk = densest.getSk();
		
			nodeMap_tilda = nodeMap.getCopy();
			degreeMap_tilda = degreeMap.getCopy();
			
			double m0 = nodeMap.getNumEdges();
			double a = 6*Math.pow((1+epsilon),2);
			double b = logb(nodeMap.getNumNodes(),(1+epsilon));
			R_star = m0*epsilon/(a*b)-1;
			
		} else {
			boolean rebuild= false;
			
			if(op == EpastoOp.ADD) {
				if (R_tilda < R_star) {
					EdgeHandler helper = new EdgeHandler();
					helper.handleEdgeAddition(edge, nodeMap_tilda, degreeMap_tilda);
				}
				rebuild = addEdge(edge, nodeMap, densest.beta, epsilon_tilda);
			}else {

				rebuild = removeEdge(edge, nodeMap, densest, epsilon_tilda);
				R_tilda++;
			}
			if(rebuild) {
				if(R_tilda < R_star ) { 
					EpastoDensest h = this.find(nodeMap_tilda, degreeMap_tilda, densest.beta, epsilon_tilda);	
					if(h.getDensity() >= densest.getDensity()) {
						densest = h;
						sk = h.getSk();
					}
				} else {
					s= s+1;
					EpastoDensest h = findDensest(nodeMap, degreeMap, 0, epsilon_tilda);
					sk= h.getSk();
					nodeMap_tilda = nodeMap.getCopy();
					degreeMap_tilda = degreeMap.getCopy();
	
					if(h.getDensity() >= densest.getDensity()) {
						densest = h;
								
					}
					double m0 = nodeMap.getNumEdges();
					double a = 6*Math.pow((1+epsilon),2);
					double b = logb(nodeMap.getNumNodes(),(1+epsilon));
					R_star = m0*epsilon/(a*b)-1;
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
		
		double threshold = 2*(1+epsilon)*beta;
		double bound= (int)Math.ceil(logb(nodeMap.getNumNodes(), 1+epsilon));
		
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
			if(t_tilda >= bound)
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
	
	EpastoDensest findDensest(NodeMap nodeMap,DegreeMap degreeMap, double density, double epsilon) {
		EpastoDensest returnResult = null;
		double beta = Math.max(1/(4*(1+epsilon)), (1+epsilon)*density);
		
		while(true) {
			EpastoDensest temp = find(nodeMap.getCopy(),degreeMap.getCopy(),beta, this.epsilon);
			
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
	
	EpastoDensest find(NodeMap nodeMap,DegreeMap degreeMap, double beta, double epsilon) {
		//output parameters
		EpastoDensest returnResult = new EpastoDensest();
		HashMap<String, Integer> dSk = new HashMap<String, Integer>();
		NodeMap densest_subgraph = nodeMap.getCopy();
		double density = (nodeMap.getNumNodes() == 0 ) ? 0  : (nodeMap.getNumEdges()/(double)nodeMap.getNumNodes());
		double max_density = (nodeMap.getNumNodes() == 0 ) ? 0  : (nodeMap.getNumEdges()/(double)nodeMap.getNumNodes());
		
		
		//algorithm parameters
		double bound= (int)Math.ceil(logb(nodeMap.getNumNodes(), 1+epsilon));
		
		//algorithm variables
		int t = 0 ;
		
		while(nodeMap.getNumNodes() > 0 && t < bound) {
			for(String str: nodeMap.map.keySet()) {
				dSk.put(str, t);
			}
			removeNode(nodeMap,degreeMap, beta, epsilon);
			
			density = (nodeMap.getNumNodes() == 0 ) ? 0  : (nodeMap.getNumEdges()/(double)nodeMap.getNumNodes());
			
			if(density > max_density) {
				max_density = density;
				densest_subgraph = nodeMap.getCopy();
			}
			++t;
		}
		returnResult.setDensity(max_density);
		returnResult.setDensest(densest_subgraph);
		returnResult.setBeta(beta);
		returnResult.setSk(dSk);
		return returnResult;
	}
	
	
	void removeNode(NodeMap nodeMap, DegreeMap degreeMap, double beta, double epsilon) {
		double threshold = 2*(1+epsilon)*beta;
		HashSet<String> nodesRemove = new HashSet<String>();
		for(int i =0; i< degreeMap.capacity;i++) {
			if(i>threshold)
				break;
			else {
				nodesRemove.addAll(new HashSet<String>(degreeMap.getNodes(i)));
			}
		}
		EdgeHandler utility = new EdgeHandler();
		for(String str: nodesRemove) {
			HashSet<String> temp = nodeMap.getNeighbors(str);
			HashSet<String> neighbors;
			if(temp == null )
				neighbors = new HashSet<String>();
			else 
				neighbors = new HashSet<String>(temp);
			
			for(String neighbor: neighbors) {
				utility.handleEdgeDeletion(new StreamEdge(str,neighbor), nodeMap,degreeMap);
			}
		}
	}
	
	double logb(int num,  double base) {
		return (Math.log(num)/Math.log(base));
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
		output.size = this.densest.densest.getNumNodes();
		output.nodes = new ArrayList<String>(this.densest.densest.map.keySet());
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
