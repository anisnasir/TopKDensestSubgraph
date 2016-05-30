package kcore;


import java.io.BufferedReader;
import java.io.IOException;

/**
 * Reads a stream of StreamItems from a file.
 */
public class StreamEdgeReader {
	private BufferedReader in;
	private String sep ;

	public StreamEdgeReader(BufferedReader input, String sep) {
		this.in = input;
		this.sep = sep;
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

			String src = tokens[0].trim();
			String dest = tokens[1].trim();
			if(src.compareTo(dest) < 0)
				return new StreamEdge(src, dest);
			else
				return new StreamEdge(dest, src);
			
		} catch (IOException e) {
			System.err.println("Unable to read from file");
			throw e;
		}

		
	}	
}
