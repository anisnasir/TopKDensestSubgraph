package kcorelinear;

import java.util.HashMap;
import java.util.HashSet;

public class KCoreResult {
	HashMap<String,Integer> cd;
	HashMap<String,HashSet<String>> graph;
	KCoreResult() { 
		cd = new HashMap<String,Integer>();
		graph = new HashMap<String,HashSet<String>>();
	}
}
