package output;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class OutputWriter {
	String fileName;
	BufferedWriter ow;

	public OutputWriter(String fileName) {
		this.fileName = fileName;
		try {
			ow = new BufferedWriter(new FileWriter(fileName));
			ow.write("#main core\tDensity\tsize\tnodes\ttime taken\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeOutput(Output output) {
		try {
			ow.write(output.getCoreNum()+"\t"+output.getDensity()+"\t"+output.getSize()+"\t"+output.getNodes()+"\t"+output.getTimeTaken()+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void close() {
		try {
			ow.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
