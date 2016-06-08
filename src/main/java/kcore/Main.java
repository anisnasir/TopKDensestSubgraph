package kcore;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class Main {
	private static void ErrorMessage() {
		System.err.println("Choose the type of simulator using:");
		System.err.println("KCore: 0 inputFile <sliding_window> [epsilon] outFile [k] outDir ");
		System.err.println("Charikar: 1 inputFile <sliding_window> [epsilon] outFile [k] outDir " ) ; 
		System.err.println("Bahmani: 2 inputFile <sliding_window> <epsilon> outFile [k] outDir " ) ;
		System.err.println("Dynamic K core: 3 inputFile <sliding_window> [epsilon] outFile [k] outDir " ) ;
		System.err.println("Top-k Bahmani: 4 inputFile <sliding_window> epsilon outFile k outDir " ) ;
		System.err.println("Top-k KCore: 5 inputFile <sliding_window> [epsilon] outFile k outDir " ) ;
		System.err.println("Top-k Charikar: 6 inputFile <sliding_window> [epsilon] outFile k outDir " ) ;
		System.err.println("Top-k KCoreDecomposition: 7 inputFile <sliding_window> [epsilon] outFile k outDir " ) ;
		System.err.println("Epasto Fully Dynamic: 8 inputFile <sliding_window> epsilon outFile [k] outDir " ) ;
		System.err.println("Epasto Top-k: 9 inputFile <sliding_window> epsilon outFile k outDir " ) ;
		System.exit(1);
	}
	public static void main(String[] args) throws IOException {
		
		if(args.length < 7 ) {
			ErrorMessage();
		}
		
		//retrieve input variables
		int simulatorType = Integer.parseInt(args[0]);
		String inFileName= args[1];
		int sliding_window = Integer.parseInt(args[2]);
		double epsilon = 0.0;
		String outFileName = args[4];
		String outDir = args[6];
		int k = 1;
		boolean degreeMapFlag = true;
		
		if(simulatorType == 3 || simulatorType == 7 || simulatorType == 8 || simulatorType == 9 ) {
			degreeMapFlag = false;
		}
		if(simulatorType == 2 || simulatorType == 8) {
			epsilon = Double.parseDouble(args[3]);
		}
		else if(simulatorType == 4  || simulatorType == 9) {
			epsilon = Double.parseDouble(args[3]);
			k = Integer.parseInt(args[5]);
		} else if (simulatorType == 5 || simulatorType == 6 || simulatorType == 7) {
			k = Integer.parseInt(args[5]);
		}
		
		
		//input reader
		String sep = "\t";
		BufferedReader in = null;
		
		try {
            InputStream rawin = new FileInputStream(inFileName);
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
		
		//outputWriter
		ArrayList<OutputWriter> ow = new ArrayList<OutputWriter>();
		for(int i =0 ;i < k;i++) {
			ow.add(new OutputWriter(outDir+"/"+outFileName+"_"+i+".out"));
		}
	
		ArrayList<Output> output = null;
		
		//Data Structures specific to the Algorithm
		NodeMap nodeMap = new NodeMap();
		DegreeMap degreeMap = new DegreeMap();
		EdgeHandler utility = new EdgeHandler();
		FixedSizeSlidingWindow sw = new FixedSizeSlidingWindow(sliding_window);
		
		DensestSubgraph densest = null;
		
		if(simulatorType == 0) { 
			densest = new KCore();
		}else if (simulatorType == 1) {
			densest = new Charikar();
		} else if (simulatorType == 2) {
			densest = new Bahmani(epsilon);
		} else if (simulatorType == 3) {
			densest = new KCoreDecomposition(nodeMap.map);
		} else if (simulatorType == 4) {
			densest = new BahmaniTopK(epsilon, k);
		} else if (simulatorType == 5) {
			densest = new KCoreTopK(k);	
		} else if (simulatorType == 6) {
			densest = new CharikarTopK(k);
		} else if (simulatorType == 7) {
			densest = new KCoreDecompositionTopK(k, nodeMap);
		} else if (simulatorType == 8) {
			densest = new EpastoFullyDyn(epsilon);
		} else if (simulatorType == 9) {
			densest = new EpastoTopK(k,epsilon);
		}
		
		
		
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
	
			if(degreeMapFlag)
				utility.handleEdgeAddition(item,nodeMap,degreeMap);
			else 
				utility.handleEdgeAddition(item, nodeMap);

			
			StreamEdge oldestEdge = sw.add(item);

			if(simulatorType != 8 && simulatorType != 9) {
				if(oldestEdge != null) {
					if(degreeMapFlag)
						utility.handleEdgeDeletion(oldestEdge, nodeMap, degreeMap);
					else
						utility.handleEdgeDeletion(oldestEdge, nodeMap); 
				}
			}
			
			long startTime = System.currentTimeMillis();
			if(simulatorType == 0) { 
				output = densest.getDensest(degreeMap.getCopy(),nodeMap.getCopy());
			}else if (simulatorType == 1) {
				output = densest.getDensest(degreeMap.getCopy(),nodeMap.getCopy());
			} else if (simulatorType == 2) {
				output = densest.getDensest(degreeMap.getCopy(),nodeMap.getCopy());
			} else if (simulatorType == 3) {
				KCoreDecomposition kCore = (KCoreDecomposition) densest;
				
				kCore.addEdge(item.getSource(), item.getDestination());
				if(oldestEdge != null) 
					kCore.removeEdge(oldestEdge.getSource(), oldestEdge.getDestination());
				
				output = densest.getDensest(degreeMap.getCopy(),nodeMap.getCopy());
			} else if ( simulatorType == 4) {
				output = densest.getDensest(degreeMap.getCopy(),nodeMap.getCopy());
			} else if ( simulatorType == 5) {
				output = densest.getDensest(degreeMap.getCopy(),nodeMap.getCopy());
			} else if ( simulatorType == 6) {
				output = densest.getDensest(degreeMap.getCopy(),nodeMap.getCopy());
			} else if (simulatorType == 7) {
				KCoreDecompositionTopK kCoreTopK = (KCoreDecompositionTopK) densest;
				KCoreDecomposition kCore = kCoreTopK.densest;
				
				kCore.addEdge(item.getSource(), item.getDestination());
				if(oldestEdge != null) 
					kCore.removeEdge(oldestEdge.getSource(), oldestEdge.getDestination());
				
				output = densest.getDensest(degreeMap,nodeMap);
			} else if (simulatorType == 8) {
				EpastoFullyDyn epasto = (EpastoFullyDyn)densest;
				epasto.MainFullyDynamic(item, nodeMap, EpastoOp.ADD);
				
				if(oldestEdge != null) {
					utility.handleEdgeDeletion(oldestEdge, nodeMap);
					epasto.MainFullyDynamic(oldestEdge, nodeMap, EpastoOp.REMOVE);
				}
				output=densest.getDensest(degreeMap, nodeMap);
 			} else if ( simulatorType == 9) {
 				EpastoTopK epasto = (EpastoTopK)densest;
				epasto.addEdge(item);
				
				if(oldestEdge != null) {
					epasto.removeEdge(oldestEdge);
				}
				output=densest.getDensest(degreeMap, nodeMap);
 			}
			
			for(int i =0; i< k;i++) {
				if( i<output.size()) {
				output.get(i).setTimeTaken((System.currentTimeMillis()-startTime)/1000.0);
				output.get(i).printOutput(); 
				ow.get(i).writeOutput(output.get(i));
				}else {
					output = getDummy();
					ow.get(i).writeOutput(output.get(0));
				}
				
			}
			item = reader.nextItem();
			if(item !=null)
				while(nodeMap.contains(item)) {
					item = reader.nextItem();
					if(item == null)
						break;
				}
		}
	
	System.out.println("Finished Processing! Read " + edgeCounter/PRINT_INTERVAL
			+ "M edges.\tSimulation time: "
			+ (System.currentTimeMillis() - simulationStartTime)
			/ 1000 + " seconds");
	in.close();
	for(int i = 0; i< ow.size();i++)
		ow.get(i).close();
		
	}
	
	static ArrayList<Output> getDummy() { 
		ArrayList<Output> arr = new ArrayList<Output>();
		Output returnOut = new Output();
		returnOut.setCoreNum(0);
		returnOut.setDensity(0.0);
		returnOut.setSize(0);
		returnOut.setTimeTaken(0);
		returnOut.setNodes(new ArrayList<String>());
		arr.add(returnOut);
		return arr;
	}

}
