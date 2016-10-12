package docAnalysis;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest {
	public static void main(String args[]) throws IOException {
		// String to be scanned to find the pattern.
		//System.out.println("Test");
		  String path = "/home/senjuti/Documents/Senjuti/ExtractedNamesHttp.txt";
		  String outputPath = "/home/senjuti/Documents/Senjuti/TypedNamesHttp.txt";
		  BufferedReader br = new BufferedReader(new FileReader(new File(path)));
		  BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputPath)));

		  //bw.write("TEST");
		  
	      String classExample = "SPNegoScheme";
	      String methodExample = "HttpEntity.getContent()";
	      String methodExample2 = "URIUtils.resolve()"; 
	      String methodExample3 = "handleResponse()";
	      String annotationExample = "@Override"; 
	      String packageExample = "java.io.IOException";

	      String patternClass = "(?:\\s|^)([A-Z]+[a-z0-9]+[A-Z][a-z0-9]+\\w*)(\\s|\\.\\s|\\.$|$|,\\s)|(?:\\s|^)([A-Z]+\\w*)(\\s|\\.\\s|\\.$|$|,\\s)";
	      String patternMethod = "(?:\\s|^)([A-Z]+[a-z0-9]+[A-Z][a-z0-9]+\\w*)\\.([a-z0-9]+[A-Z][a-z0-9]+)[(][)]|(?:\\s|^)([A-Z]+[a-z0-9]+[A-Z][a-z0-9]+\\w*)\\.([a-z0-9]+)[(][)]|([a-z0-9]+[A-Z]+[a-z]+\\w*)[(][)]|(?:\\s|^)([A-Z]+[a-z0-9A-Z]+\\w*\\.[a-z]+[(][)])";
	      String patternMethod2 = "(?:\\s|^)([A-Z]+[a-z0-9]+[A-Z][a-z0-9]+\\w*#[a-z0-9]+)[(][)]|(?:\\s|^)([A-Z]+[a-z0-9]+[A-Z][a-z0-9]+\\w*#[a-z]+[a-zA-Z0-9]+)[(][)]|(?:\\s|^)([A-Z]+[a-z0-9A-Z]+\\w*#[a-z]+)|(?:\\s|^)([A-Z]+[a-z0-9A-Z]+\\w*#[a-z]+)[(][)]|(?:\\s|^)([A-Z]+[a-z0-9A-Z]+\\w*#[a-z]+)";
	      String patternAnnotation = "(?:\\s|^)[@][a-zA-Z]+";
	      String patternPackage = "(?:\\s|^)[a-z]+\\.[a-z]+\\.[A-Za-z]+";

	      // Create a Pattern object
	      Pattern r1 = Pattern.compile(patternClass);
	      Pattern r2 = Pattern.compile(patternMethod);
	      Pattern r3 = Pattern.compile(patternAnnotation);
	      Pattern r4 = Pattern.compile(patternPackage);
	      Pattern r5 = Pattern.compile(patternMethod2);
	      
	      Matcher m2 = r2.matcher(methodExample2);
	      Matcher m5 = r5.matcher(methodExample2);
	      if ((m2.find() || m5.find())) {
	    	  System.out.println(methodExample2 + " Method");
	      }
	      else
	    	  System.out.println(methodExample2 + " Not Method");
	      
	      /*while (br.readLine() != null) {
	    	    String line = br.readLine();
	    	    Matcher m1 = r1.matcher(line);
	    	    Matcher m2 = r2.matcher(line);
	    	    Matcher m3 = r3.matcher(line);
	    	    Matcher m4 = r4.matcher(line);
	    	    Matcher m5 = r5.matcher(line);
	    	    
	    	    bw.write(line + ", ");
	    	    
	    	    while (m1.find()) {
	   	         //System.out.println("Found class value: " + m1.group());
	    	    	bw.write("CLASS/INTERFACE");
	   	        }
	    	    
	    	    while (m2.find() || m5.find()) {
		   	         //System.out.println("Found method value: " + m2.group());
	    	    	bw.write("METHOD");
		   	    }
	    	    
	    	    while (m3.find()) {
		   	         //System.out.println("Found annotation value: " + m3.group());
	    	    	bw.write("ANNOTATION");
		   	    }
	    	    
	    	    while (m4.find()) {
		   	         //System.out.println("Found package value: " + m4.group());
	    	    	bw.write("PACKAGE");
		   	    }
	    	    bw.write("\n");
	      }
	      br.close();
	      bw.close();*/
	      
	}
}
