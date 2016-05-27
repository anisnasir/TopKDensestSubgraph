package kcore;


import java.io.BufferedReader;
import java.io.IOException;

/**
 * Reads a stream of StreamItems from a file.
 */
public class StreamEdgeReader {
	private BufferedReader in;
	private String sep ;
	long edgeCount;

	public StreamEdgeReader(BufferedReader input, String sep) {
		this.in = input;
		this.sep = sep;
		this.edgeCount = 0;
	}

	public StreamEdge nextItem() throws IOException {
		String line = null;
		try {
			line = in.readLine();
			if (line == null || line.length() == 0)
				return null;
			
			if(line.startsWith("#"))
				return null;

			String[] tokens = line.split(sep);
			if (tokens.length < 2)
				return null;

			edgeCount++;
			String src = tokens[0];
			String dest = tokens[1];
			if(src.compareTo(dest) < 0)
				return new StreamEdge(src, dest);
			else
				return new StreamEdge(dest, src);
			
		} catch (IOException e) {
			System.err.println("Unable to read from file");
			throw e;
		}

		
	}
	
	public long getEdgeCount() {
		return this.edgeCount;
	}
	
	
}
