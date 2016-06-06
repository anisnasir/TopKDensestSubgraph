package kcore;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class OutputWriter {
	String fileName;
	BufferedWriter ow;

	OutputWriter(String fileName) {
		this.fileName = fileName;
		try {
			ow = new BufferedWriter(new FileWriter(fileName));
			ow.write("#main core\tDensity\tsize\tdensest\ttime taken\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void writeOutput(Output output) {
		try {
			ow.write(output.getCoreNum()+"\t"+output.getDensity()+"\t"+output.getSize()+"\t"+output.getNodes()+"\t"+output.getTimeTaken()+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	void close() {
		try {
			ow.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
