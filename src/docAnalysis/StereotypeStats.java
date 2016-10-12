package docAnalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StereotypeStats {
	public static String findPopularElement(ArrayList<String> tmpStereoName) {
		Map<Integer, String> wordCount = new HashMap<Integer, String>();
		TreeMap<Integer, String> treeMap = new TreeMap<Integer, String>();
		
		for(String word : tmpStereoName) {
		  Integer count = null;          
		  wordCount.put((count==null) ? 1 : count+1, word);
		}
		treeMap.putAll(wordCount);
		
		for (Map.Entry<Integer, String> entry : treeMap.entrySet()) {
		     System.out.println("Key: " + entry.getKey() + ". Value: " + entry.getValue());
		}
		System.out.println("---");
		return treeMap.get(treeMap.firstKey());
	}
	
	public static void main(String[] args) throws IOException {
		ArrayList<stereoHolder> sH = new ArrayList<stereoHolder>(); 
		ArrayList<stereoHolder> sH2 = new ArrayList<stereoHolder>(); 
		ArrayList<String> tmpStereoName = new ArrayList<String>(); 
		ArrayList<String> tmpCLT = new ArrayList<String>(); 
		ArrayList<String> allStereoName = new ArrayList<String>(); 
		int numSections = 7; 
		String[] popularElement = new String[numSections]; 
		String[] popularCLT = new String[numSections]; 
		String[] stereotypeNames = {"ENTITY", "MINIMAL_ENTITY", "DATA_PROVIDER", "COMMANDER", "BOUNDARY", "FACTORY", "CONTROLLER", "PURE_CONTROLLER", "LARGE_CLASS", "LAZY_CLASS", "DEGENERATE", "DATA_CLASS", "POOL"};
		String path = "/home/senjuti/Documents/CodeWordsWithPositionAndMatching/src/docAnalysis/Matched/HttpClientMathchedCode.txt";
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = br.readLine();
		while (line != null) {
			String[] parts = line.split(",");
			String section = parts[4]; 
			String CLT = parts[5].replaceAll("\\s",""); 
			String stereo = parts[6].replaceAll("@","");
			String[] partSec = section.split("\\.");
			int secNumber = Integer.parseInt(partSec[0].replaceAll("\\s",""));
			stereoHolder tmp = new stereoHolder(); 
			tmp.sectionNum = secNumber; 
			tmp.CLT = CLT; 
			if (stereo.split(" ").length > 2) {
				for (String s : stereo.split(" ")) {
					stereoHolder t = new stereoHolder(); 
					t.sectionNum = secNumber; 
					t.CLT = CLT; 
					t.stereoName = s.replaceAll("\\s",""); 
					if (!t.stereoName.isEmpty()) {
						sH.add(t);
					}
				}
			}
			else {
				tmp.stereoName = stereo.replaceAll("\\s",""); 
				sH.add(tmp); 
			}
			line = br.readLine();
		}
		Collections.sort(sH);
		int i;
		for (i = 0; i < sH.size(); i++) { 
			stereoHolder tmp = sH.get(i);
			if (!(tmp.stereoName.equals("null") || tmp.stereoName.equals("") || tmp.stereoName.equals(null))) {
				sH2.add(tmp);
			}
		}
		i = 1;
		for (stereoHolder tmp : sH2) {
			allStereoName.add(tmp.stereoName);
			if (i == tmp.sectionNum) {
				tmpStereoName.add(tmp.stereoName);
				tmpCLT.add(tmp.CLT);
			}
			else {
				popularElement[i-1] = findPopularElement(tmpStereoName); 
				tmpStereoName.clear();
				i++; 
				tmpStereoName.add(tmp.stereoName);
				tmpCLT.add(tmp.CLT);
			}
			System.out.println("sectionNum="+tmp.sectionNum+", CLT="+tmp.CLT);
		}
		/*for (String tmp : allStereoName) {
			System.out.println(tmp);
		}*/
		System.out.println("Most commonly occurring stereotype: " + findPopularElement(allStereoName));
		for (i = 1; i <= numSections; i++) {
			System.out.println("Most commonly occurring stereotype in Section " + i + " is " + popularElement[i-1]);
			System.out.println("Most commonly occurring CLT in Section " + i + " is " + popularCLT[i-1]);
		}
	}
}

class stereoHolder implements Comparable<stereoHolder> {
	public int sectionNum;
	public String stereoName; 
	public String CLT; 
	public int compareTo(stereoHolder k) {
		int compareQuantity = k.sectionNum; 
		return (this.sectionNum - compareQuantity); 
	}
}
