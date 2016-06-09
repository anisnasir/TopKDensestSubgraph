package input;
import java.io.Serializable;


public class StreamEdge implements Serializable, Comparable<StreamEdge>{
	private static final long serialVersionUID = -3733214465018614013L;
	private String src;
	private String dest;

	public StreamEdge(String src, String dest) {
		this.src = src;
		this.dest = dest;
	}

	public String getSource() {
		return this.src;
	}

	public String getDestination() {
		return this.dest;
	}
	
	
	public String toString() {
		return this.src+ " "+this.dest;
	}

	public int compareTo(StreamEdge o) {
		if(this.src == o.src && this.dest == o.dest )
			return 0;
		else if (this.src == o.dest && this.dest == o.src )
			return 0;
		else 
			return -1;
	}
}
