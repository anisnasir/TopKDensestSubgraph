package interfaces;

import java.util.HashSet;

public interface IncrementalKCore {
	public void addEdge(String src, String dst);
	public void removeEdge(String src, String dst);
	public int mainCore();
	public int getKCore(String src);
	
}
