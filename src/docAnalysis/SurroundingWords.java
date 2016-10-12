package docAnalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;

public class SurroundingWords {
	public static void main(String[] args) throws IOException {
		String path = "/home/senjuti/Documents/grandparent.txt"; 
		BufferedWriter out = new BufferedWriter(new FileWriter("/home/senjuti/Documents/Senjuti/grandparent.txt"));
		out.write("Test");
		out.newLine();
		out.close();
	}

}

 
 
