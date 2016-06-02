package kcore;

public class EdgeHandler {
	EdgeHandler() {
		
	}
	public void handleEdgeAddition(StreamEdge item, NodeMap nodeMap, DegreeMap degreeMap ) {
		//System.out.println("+ " + item.toString());
		String src = item.getSource();
		String dest = item.getDestination();
		
		int predegree1 = nodeMap.getDegree(src);
		int predegree2 = nodeMap.getDegree(dest);
		
		//update node map
		nodeMap.addEdge(src, dest);
		nodeMap.addEdge(dest, src);

		//update degree map
		degreeMap.incrementDegree(predegree1, src);
		degreeMap.incrementDegree(predegree2, dest);
	}
	
	public void handleEdgeDeletion(StreamEdge oldestEdge, NodeMap nodeMap, DegreeMap degreeMap ) {
		//System.out.println("- " + oldestEdge.toString());
		String oldSrc = oldestEdge.getSource();
		String oldDest = oldestEdge.getDestination();
		
		int oldSrcDegree = nodeMap.getDegree(oldSrc);
		int oldDestDegree = nodeMap.getDegree(oldDest);
		
		//update degree map
		degreeMap.decremnetDegreeExpire(oldSrcDegree, oldSrc);
		degreeMap.decremnetDegreeExpire(oldDestDegree, oldDest);
					
		//removes from each others neighbor table
		nodeMap.removeEdge(oldSrc, oldDest);
		nodeMap.removeEdge(oldDest, oldSrc);
	}
}
