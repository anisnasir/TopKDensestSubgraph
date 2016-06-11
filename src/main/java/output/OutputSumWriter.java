package output;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class OutputSumWriter {
	String fileName;
	BufferedWriter ow;
	Output output;

	public int flushInterval = 10000;
	ArrayList<Double> list;
	int flushCounter;

	public OutputSumWriter(String fileName) {
		list = new ArrayList<Double>();
		this.fileName = fileName;
		flushCounter = 0;
		try {
			ow = new BufferedWriter(new FileWriter(fileName));
			ow.write("#sum_densities\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addOutput(double value) {
		if(++flushCounter %flushInterval == 0) {
			flush();
		}else 
			list.add(value);
	}
	public void flush() {
		try {
			for(double d: list)
				ow.write(d+"\n");
			list = new ArrayList<Double>();
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
