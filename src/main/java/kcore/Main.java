package kcore;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;


public class Main {
	private static void ErrorMessage() {
		System.err.println("Choose the type of simulator using:");
		System.err
				.println("0 K Core Decomposition");
		System.err
				.println("1 Charikar" ) ; 
		System.err
				.println("2 Bahmani <epsilon>" ) ;
		System.exit(1);
	}
	private static void displayAlgorithm( int simulatorType) {
		if(simulatorType == 0 ) { 
			System.out.println("Executing K core decomposition");
		}else if (simulatorType == 1) {
			System.out.println("Executing charikar greedy densest subgraph ");
		}
	}
	public static void main(String[] args) throws IOException {
		double epsilon = 0 ;
		if(args.length <1 ) {
			System.out.println("simulator type missing");
		}
		int simulatorType = Integer.parseInt(args[0]);
		
		if(simulatorType == 0 || simulatorType == 1) {
			if(args.length < 2) {
				ErrorMessage();
			}
		}else if (simulatorType == 2) {
			if(args.length < 3) {
				ErrorMessage();
			}
			epsilon = Double.parseDouble(args[2]);
		}
		
		
		String inFileName= args[1];
		
		String sep = "\t";
		BufferedReader in = null;
		
		long startTime = System.currentTimeMillis();
		try {
            InputStream rawin = new FileInputStream(inFileName);
            if (inFileName.endsWith(".gz"))
                rawin = new GZIPInputStream(rawin);
            in = new BufferedReader(new InputStreamReader(rawin));
        } catch (FileNotFoundException e) {
            System.err.println("File not found");
            e.printStackTrace();
            System.exit(1);
        }

        //initialize the input reader
        StreamEdgeReader reader = new StreamEdgeReader(in,sep);
		StreamEdge item = reader.nextItem();
		
		//Declare outprint interval variables
		int PRINT_INTERVAL=1000000;
		long simulationStartTime = System.currentTimeMillis();
		
		//Data Structures specific to the Algorithm
		NodeMap nodeMap = new NodeMap();
		DegreeMap degreeMap = new DegreeMap();
		EdgeHandler utility = new EdgeHandler();
		
		//Start reading the input
		System.out.println("Reading the input");
		int edgeCounter = 0;
		while (item != null) {
			if (++edgeCounter % PRINT_INTERVAL == 0) {
				System.out.println("Read " + edgeCounter/PRINT_INTERVAL
						+ "M edges.\tSimulation time: "
						+ (System.currentTimeMillis() - simulationStartTime)
						/ 1000 + " seconds");
				
			}
			
			//System.out.println(item);
			utility.handleEdgeAddition(item,nodeMap,degreeMap);
			
			item = reader.nextItem();
			if(item !=null)
				while(nodeMap.contains(item)) {
					item = reader.nextItem();
					if(item == null)
						break;
				}
		}
	in.close();
	
	long endTime   = System.currentTimeMillis();
	System.out.println("Reading input time : " + ((endTime-startTime)/(double)1000) + " secs ");
	startTime = System.currentTimeMillis();
	//System.out.println(nodeMap.map);
	//System.out.println(degreeMap.map);
	if(simulatorType == 0) { 
		displayAlgorithm(simulatorType);
		KCore kCore = new KCore();
		endTime   = System.currentTimeMillis();
		System.out.println("Time to calculate main core : " + ((endTime-startTime)/(double)1000) + " secs ");
		
		System.out.println(kCore.getCore(degreeMap,nodeMap));
	}else if (simulatorType == 1) {
		displayAlgorithm(simulatorType);
		Charikar densest = new Charikar();
		endTime   = System.currentTimeMillis();
		System.out.println("Time to calculate main core : " + ((endTime-startTime)/(double)1000) + " secs ");
		
		System.out.println(densest.getDensest(degreeMap,nodeMap));
	} else if (simulatorType == 2) {
		displayAlgorithm(simulatorType);
		Bahmani densest = new Bahmani(epsilon);
		endTime   = System.currentTimeMillis();
		System.out.println("Time to calculate main core : " + ((endTime-startTime)/(double)1000) + " secs ");
		
		System.out.println(densest.getDensest(degreeMap,nodeMap));
	}
		
	}
	
	
	

}
