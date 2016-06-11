package output;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class OutputWriter {
	String fileName;
	BufferedWriter ow;
	ArrayList<Output> list;

	public int flushInterval = 10000;
	int flushCounter ;

	public OutputWriter(String fileName) {
		list = new ArrayList<Output>();
		flushCounter = 0;
		this.fileName = fileName;
		try {
			ow = new BufferedWriter(new FileWriter(fileName));
			ow.write("#main core\tDensity\tsize\tnodes\ttime taken\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addOutput(Output output) {
		if(++flushCounter %flushInterval == 0) {
			flush();
		}else 
			list.add(output);
	}
	
	public void flush() { 
		for(Output out: list) {
			writeOutput(out);
		}
		list = new ArrayList<Output>();
	}
	public void writeOutput(Output output) {
		try {
			ow.write((output.getCoreNum())+"\t"+(output.getDensity())+"\t"+(output.getSize())+"\t"+output.getNodes()+"\t"+(output.getTimeTaken())+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void close() {
		try {
			flush();
			ow.flush();
			ow.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
