package docAnalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;

import org.apache.commons.collections4.trie.PatriciaTrie;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaConstructor;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * @stereotype BOUNDARY COMMANDER 
 */
public class ExtractAndMatch {
	public static String htmlpath;
	public static String outputcodepath;
	public static String outputexamplepath;
	public static String outputsummarypath;
	public static String classnamepath;
	public static String methodnamepath;
	public static String sectionPath;
	public static String truthpath;
	public static String sourceCodePath;
	public static String codetag;

	public static ArrayList<CodeTerm> codeTerms = new ArrayList<CodeTerm>();
	public static HashMap<String, ArrayList<String>> matchedNames = new HashMap<String, ArrayList<String>>();
	public static HashMap<String, String> truthMap = new HashMap<String, String>();
	public static int[] freqdistcode = new int[50];
	public static int[] freqdistexample = new int[50];
	public static PatriciaTrie<String> qualifiedTrie = new PatriciaTrie<String>();
	public static TreeMap<Integer, String> section = new TreeMap<Integer, String>();
	public static String filePath = new File("").getAbsolutePath();
	
	public static JavaProjectBuilder builder = new JavaProjectBuilder();

	/**
	 * @stereotype SET COLLABORATOR 
	 */
	public static void sectionValues() throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(sectionPath));
		String line = in.readLine();
		while (line != null) {	
			String[] lst = line.split(",");
			int beg = Integer.parseInt(lst[0]); 
			String sec = lst[2];
			section.put(beg, sec); 
			line = in.readLine();
		}
		in.close();
	}
	
	/**
	 * @stereotype SET COLLABORATOR 
	 */
	public static void insertTrie(String str){
		String revstr = new StringBuilder(str).reverse().toString();
		qualifiedTrie.put(revstr, str);
	}
	
	/**
	 * @stereotype FACTORY COLLABORATOR 
	 */
	public static ArrayList<String> searchTrie(String str){
		String revstr = new StringBuilder(str).reverse().toString();
		SortedMap<String,String> prefixMap = qualifiedTrie.prefixMap(revstr);
		ArrayList<String> retvalArrayList = new ArrayList<String> (prefixMap.values());
		if(retvalArrayList.size()>0)
			return retvalArrayList;
		else{
			String revstr2 = revstr.split("\\.")[0];
			prefixMap = qualifiedTrie.prefixMap(revstr2);
			return new ArrayList<String> (prefixMap.values());
		}
	}

	/**
	 * @stereotype COLLABORATOR 
	 */
	public static void main(String[] args) throws Exception {
		doAllSpring();
		//doAllHttpClient();
		//doAllAndroid();
		//doAllAndroidTutorial(); 
	}
	
	private static void doAllAndroidTutorial() throws Exception {
		htmlpath = "./src/docAnalysis/Pipeline/Android.html";
		codetag = "code";
		String codetag2 = "a href";
		outputcodepath = "./src/docAnalysis/Matched/AndroidMathchedCode.txt";
		System.out.println("Starting a new procedure");
		codeTerms = new ArrayList<CodeTerm>();
		matchedNames = new HashMap<String, ArrayList<String>>();
		sectionPath = "./src/docAnalysis/AndroidlineAndSection.txt";

		Source source = new Source(new File(htmlpath));
		source.fullSequentialParse();
		sectionValues();
		getallelementsAndroid(source, codetag, codetag2);
		//readTruth();
		//recallAndPrecisionNew();
		finalOutput(outputcodepath);
	}
	
	
	/**
	 * @stereotype COMMAND LOCAL_CONTROLLER 
	 */
	private static void doAllSpring() throws Exception {
		htmlpath = "./src/docAnalysis/Pipeline/Spring.html";
		classnamepath = "./src/docAnalysis/Pipeline/Class and Method Names List/SpringclassesALL.txt";
		methodnamepath = "./src/docAnalysis/Pipeline/Class and Method Names List/SpringmethodsALL.txt";
		outputcodepath = "./src/docAnalysis/Matched/SpringMathchedCode.txt";
		outputexamplepath = "./src/docAnalysis/Matched/SpringMathchedExample.txt";
		outputsummarypath = "./src/docAnalysis/Matched/SpringMathchedSummary.txt";
		truthpath = "./src/docAnalysis/Pipeline/Manually annotated CLTs/Spring.txt";
		sourceCodePath = "/home/senjuti/spring-framework";
		sectionPath = "./src/docAnalysis/SpringlineAndSection.txt";
		
		codetag = "code";
		sectionValues(); 
		doall();
		builder.addSourceTree(new File(sourceCodePath));
		//readTruth();
		//recallAndPrecisionNew();
	}

	/**
	 * @stereotype COMMAND LOCAL_CONTROLLER 
	 */
	private static void doAllHttpClient() throws Exception {
		htmlpath = filePath.concat("/src/docAnalysis/Pipeline/HttpClient.html");
		classnamepath = filePath.concat("/src/docAnalysis/Pipeline/Class and Method Names List/HttpclassesALL.txt");
		methodnamepath = filePath.concat("/src/docAnalysis/Pipeline/Class and Method Names List/HttpmethodsALL.txt");
		outputcodepath = filePath.concat("/src/docAnalysis/Matched/HttpClientMathchedCode.txt");
		outputexamplepath = filePath.concat("/src/docAnalysis/Matched/HttpClientMathchedExample.txt");
		outputsummarypath = filePath.concat("/src/docAnalysis/Matched/HttpClientMathchedSummary.txt");
		truthpath = filePath.concat("/src/docAnalysis/Pipeline/Manually annotated CLTs/HttpClient.txt");
		
		sourceCodePath = "/home/akhil/Senjuti/httpcomponents-client-4.5.2";
		sectionPath = filePath.concat("/src/docAnalysis/lineAndSection.txt");
		codetag = "code";
		sectionValues(); 
		doall();
		//builder.addSourceTree(new File(sourceCodePath));
		readTruth();
		recallAndPrecisionNew();
	}
	
	/**
	 * @stereotype COMMAND LOCAL_CONTROLLER 
	 */
	private static void doAllAndroid() throws Exception {
		htmlpath = "/home/senjuti/Project/Pipeline/Android.html";
		classnamepath = "/home/senjuti/Project/Pipeline/Class and Method Names List/Androidmethodnamesandclassnames.txt";
		methodnamepath = "/home/senjuti/Project/Pipeline/Class and Method Names List/Androidmethodnamesandclassnames.txt";
		sectionPath = filePath.concat("/src/docAnalysis/AndroidlineAndSection.txt");
		outputcodepath = "/home/senjuti/Documents/Senjuti/AndroidMathchedCode.txt";
		outputexamplepath = "/home/senjuti/Documents/Senjuti/AndroidMathchedExample.txt";
		outputsummarypath = "/home/senjuti/Documents/Senjuti/AndroidMathchedSummary.txt";
		codetag = "code";
		doall();
	}

	/**
	 * @stereotype COMMAND COLLABORATOR 
	 */
	public static void doall() throws Exception{
		System.out.println("Starting a new procedure");
		codeTerms = new ArrayList<CodeTerm>();
		matchedNames = new HashMap<String, ArrayList<String>>();
		freqdistcode = new int[50];

		Source source = new Source(new File(htmlpath));
		source.fullSequentialParse();
		sectionValues();
		getallelements(source, codetag);

		readQualifiedNames(classnamepath);
		readQualifiedNames(methodnamepath);

		System.out.println("Running matching");
		matchall();
		finalOutput(outputcodepath);
		codeTerms = new ArrayList<CodeTerm>();
		getallelementsexamples(source, "pre");
		
		SummaryOutput(outputsummarypath);
		
		String pathQualName = "/home/senjuti/Documents/CodeWordsWithPositionAndMatching/src/docAnalysis/SpringBestQualifiedName.txt"; 
		BufferedWriter bw = new BufferedWriter(new FileWriter(pathQualName));
		
		for (CodeTerm c : codeTerms) {
			//System.out.println("BESTQUALIFIEDNAME: "+ c.bestQualifiedName);
			bw.write(c.bestQualifiedName);
			bw.write("\n");
			c.stereotype = whatStereotype(c.bestQualifiedName);
			//System.out.println("STEREOTYPE: " + c.stereotype);
		}
		bw.close();
		
		//readTruth();
		//recallAndPrecisionNew();
		//finalOutput(outputcodepath);
		finalOutput(outputexamplepath);
	}
	
	/**
	 * @stereotype COLLABORATOR 
	 */
	public static String returnType(String name) {
		  String patternClass = "(?:\\s|^)([A-Z]+[a-z0-9]+[A-Z][a-z0-9]+\\w*)(\\s|\\.\\s|\\.$|$|,\\s)|(?:\\s|^)([A-Z]+\\w*)(\\s|\\.\\s|\\.$|$|,\\s)";
	      String patternMethod = "(?:\\s|^)([A-Z]+[a-z0-9]+[A-Z][a-z0-9]+\\w*)\\.([a-z0-9]+[A-Z][a-z0-9]+)[(][)]|(?:\\s|^)([A-Z]+[a-z0-9]+[A-Z][a-z0-9]+\\w*)\\.([a-z0-9]+)[(][)]|([a-z0-9]+[A-Z]+[a-z]+\\w*)[(][)]|(?:\\s|^)([A-Z]+[a-z0-9A-Z]+\\w*\\.[a-z]+[(][)])|(?:\\s|^)([a-z0-9A-Z]+[(][)])";
	      String patternMethod2 = "(?:\\s|^)([A-Z]+[a-z0-9]+[A-Z][a-z0-9]+\\w*#[a-z0-9]+)[(][)]|(?:\\s|^)([A-Z]+[a-z0-9]+[A-Z][a-z0-9]+\\w*#[a-z]+[a-zA-Z0-9]+)[(][)]|(?:\\s|^)([A-Z]+[a-z0-9A-Z]+\\w*#[a-z]+)|(?:\\s|^)([A-Z]+[a-z0-9A-Z]+\\w*#[a-z]+)[(][)]|(?:\\s|^)([A-Z]+[a-z0-9A-Z]+\\w*#[a-z]+)";
	      String patternAnnotation = "(?:\\s|^)[@][a-zA-Z]+";
	      String patternPackage = "(?:\\s|^)[a-z]+\\.[a-z]+\\.[A-Za-z]+";
	      
	      Pattern r1 = Pattern.compile(patternClass);
	      Pattern r2 = Pattern.compile(patternMethod);
	      Pattern r3 = Pattern.compile(patternAnnotation);
	      Pattern r4 = Pattern.compile(patternPackage);
	      Pattern r5 = Pattern.compile(patternMethod2);
	      
	      Matcher m1 = r1.matcher(name);
  	      Matcher m2 = r2.matcher(name);
  	      Matcher m3 = r3.matcher(name);
  	      Matcher m4 = r4.matcher(name);
  	      Matcher m5 = r5.matcher(name);
  	      
  	      if (m1.find())
  	    	  return "CLASS/INTERFACE";
  	      else if (m2.find() || m5.find())
  	    	  return "METHOD";
  	      else if (m3.find())
  	    	  return "ANNOTATION";
  	      else if (m4.find())
  	    	  return "PACKAGE";
  	      else
  	    	  return "UNIDENTIFIED-TYPE";
	}
	
	/**
	 * @stereotype SET COLLABORATOR 
	 */
	private static void readTruth() throws Exception{
			BufferedReader in = new BufferedReader(new FileReader(truthpath));
			String line = in.readLine();
			while (line != null) {
				String[] parts = line.split(";");
				line = cleanUpCodeTerm(parts[0]);
				truthMap.put(parts[0], parts[1]);
				line = in.readLine();
			}
			in.close();	
	}
	
	/**
	 * @throws IOException 
	 * @stereotype COLLABORATOR 
	 */
	private static void recallAndPrecisionNew() throws IOException {
		int falseNegatives = 0;
		int falsePositives = 0;
		int truePositives = 0; 
		for (Map.Entry<String, String> entry : truthMap.entrySet()) {
			//AuthCache, org.apache.http.client.AuthCache
			//cleanTruthCodeTerm = AuthCache
			//cleanTruthQualifiedName = org.apache.http.client.AuthCache
			String truthCodeTerm = entry.getKey();
			String cleanTruthCodeTerm = cleanUpCodeTerm(truthCodeTerm);
			String truthQualifiedName = entry.getValue();
			String cleanTruthQualifiedName = cleanUpCodeTerm(truthQualifiedName);
			//codeterm.codeterm = CloseableHttpResponse, httpclient.execute()
			//System.out.println("cleanTruthQualifiedName= " + cleanTruthQualifiedName);
			for (CodeTerm codeterm : codeTerms) {
				//cleanTruthQualifiedName = org.apache.http.impl.conn.PoolingHttpClientConnectionManager.PoolingHttpClientConnectionManager()
				if(codeterm.codeterm.equals(cleanTruthCodeTerm)) {
					codeterm.stereotype = whatStereotype(cleanTruthQualifiedName);
					//System.out.println((codeterm.codeterm + ", " + codeterm.type + ", " + codeterm.row + ", " + codeterm.column + ", " + codeterm.section + ", " + codeterm.bestQualifiedName + ", @" + codeterm.stereotype + "\n"));
		    		System.out.println(codeterm.codeterm + ", " + codeterm.bestQualifiedName);
					if (codeterm.bestQualifiedName.contains(truthQualifiedName) || codeterm.bestQualifiedName.contains(cleanTruthQualifiedName)) {
		    			truePositives++;
		    		}
			    	else {
			    		//System.out.println("False Positive - " + cleanTruthCodeTerm + ", "+ truthQualifiedName + ", "+ codeterm.bestQualifiedName);
			    		falsePositives++;
			    	}
    			}
    		}
		}
		double precision = truePositives/(truePositives+falsePositives + 0.0);
		double recall = truePositives/(falseNegatives+truePositives + 0.0);
		System.out.println("True Positives - " + truePositives);
		System.out.println("False Positives - " + falsePositives);
		System.out.println("False Negatives - " + falseNegatives);
	    System.out.println("Precision - " + precision);
		System.out.println("Recall - " + recall);
	}
	
	/**
	 * @stereotype COLLABORATOR 
	 */
	private static void finalOutput(String path) throws IOException {
		CodeTerm[] codeTermsArray = codeTerms.toArray(new CodeTerm[0]);
		Arrays.sort(codeTermsArray);
		System.out.println("Starting to write");
		BufferedWriter out = new BufferedWriter(new FileWriter(new File(path)));
		for (CodeTerm codeTerm : codeTermsArray) {
			out.write(codeTerm.toString());
		}
		out.close();
	}
	
	/**
	 * @stereotype COLLABORATOR 
	 */
	private static void SummaryOutput(String path) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(new File(path)));
		out.write("The frequency distribution of matching code is - ");
		out.newLine();
		for (int i = 0; i < freqdistcode.length; i++) {
			out.write(i + " " + freqdistcode[i]+"\n");
			out.newLine();
		}
		out.write("The frequency distribution of matching example is - ");
		out.newLine();
		for (int i = 0; i <freqdistexample.length; i++) {
			out.write(i + " " + freqdistexample[i]+"\n");
			out.newLine();
		}
		out.close();
	}

	/**
	 * @stereotype COLLABORATOR 
	 */
	public static void readQualifiedNames(String path) throws Exception {
		BufferedReader in = new BufferedReader(new FileReader(path));
		String line = in.readLine();
		while (line != null) {
			line = cleanUpCodeTerm(line);
			insertTrie(line);
			line = in.readLine();
		}
		in.close();
	}

	/**
	 * @stereotype SET COLLABORATOR 
	 */
	public static void matchall() {
		System.out.println("Total codeterms to match = " + codeTerms.size());
		for (int i = 0; i < codeTerms.size(); i++) {
			// Current Progress
			if (i % 100 == 0) {
				//System.out.println("currently processing " + i);
			}
			CodeTerm codeTerm = codeTerms.get(i);
			ArrayList<String> shortqualifiedname = new ArrayList<String>();

			// Check if this is already matched
			if (matchedNames.containsKey(codeTerm.codeterm)) {
				shortqualifiedname = matchedNames.get(codeTerm.codeterm);
			}
			// Try matching with all classnames/methodnames
			else {
				shortqualifiedname = searchTrie(codeTerm.codeterm);
			}
			//System.out.println(codeTerm.codeterm+" "+shortqualifiedname);
			//System.out.println(codeTerm.codeterm);
			if (!shortqualifiedname.isEmpty()) {
				for (String s : shortqualifiedname) {
					String[] qual = breakWordUp(s); 
					/*for (String q : qual) {
						System.out.print(q+" ");
					}
					System.out.println();*/
					int rank = compareAndRank(qual, codeTerm.surroundingWords);
					codeTerm.qualifiedNames.put(rank, s);
				}

				codeTerm.bestQualifiedName = codeTerm.qualifiedNames.get(codeTerm.qualifiedNames.lastKey());
				//System.out.println(codeTerm.bestQualifiedName);
				//codeTerm.qualifiedNames = new HashSet<String>(shortqualifiedname);
				matchedNames.put(codeTerm.codeterm, shortqualifiedname);
			}
			if (codeTerm.qualifiedNames.size() < 50)
				freqdistcode[codeTerm.qualifiedNames.size()]++;
		}
	}
	
	//Compare each item in qual[] to each in suroundingWords
	//If there is a match, add one to rank
	/**
	 * @stereotype COLLABORATOR 
	 */
	private static int compareAndRank(String[] qual, HashMap<String, Integer> surroundingWords) {
		int rank = 0; 
		int len = qual.length;
		for (int i = 0; i < len; i++) {
			String valQual = qual[i];
			//Give greater weight to second last element in array
			if(surroundingWords.containsKey(valQual)){
				rank+=surroundingWords.get(valQual);
			}
//			for (String valSurroundingWords : surroundingWords) {
//				if (valQual.equals(valSurroundingWords))
//					rank++;
//			}

		}
		return rank; 
	}
	
	/**
	 * @stereotype SET COLLABORATOR 
	 */
	private static void getallelementsexamples(Source source, String classname) {
		ArrayList<Element> elements = (ArrayList<Element>) source.getAllElements(classname);
		System.out.println("Total example blocks to match = " + elements.size());
		int counter = 0;
		for (Element element : elements) {
			counter++;
			if (counter % 1 == 0) {
				//System.out.println("currently processing " + counter);
			}
			Element parent = element.getParentElement(); 
			Segment parentContent = parent.getContent(); 
			String parentName = parentContent.getTextExtractor().toString();
			
			Element grandparent = parent.getParentElement(); 
			Segment grandparentContent = grandparent.getContent(); 
			String grandparentName = grandparentContent.getTextExtractor().toString();

			Segment content = element.getContent();
			String name = content.getTextExtractor().toString();
			String[] possiblenames = name.split(" ");
			
			for (String possiblename : possiblenames) {
				//possiblename = "metrics.getFailedConnectionCount();"
				possiblename = cleanUpCodeTerm(possiblename);
				if (ignorename(possiblename))
					continue;
				ArrayList<String> qualifiednames = searchTrie(possiblename);
				//metrics.getFailedConnectionCount(): [org.apache.http.impl.client.FutureRequestExecutionMetrics.getFailedConnectionCount()]
				//System.out.println(possiblename + ": " + qualifiednames);

				if (qualifiednames.size() > 0) {
					CodeTerm codeterm = new CodeTerm();
					//codeterm.codeterm = metrics.getFailedConnectionCount()
					codeterm.codeterm = possiblename;
					codeterm.type = returnType(possiblename);
					codeterm.row = element.getRowColumnVector().getRow();
					codeterm.column = element.getRowColumnVector().getColumn();
					codeterm.section = returnSection(codeterm.row); 
					String[] a = grandparentName.replaceAll("\\w*\\d\\w* *", "").replaceAll("[^a-zA-Z]", " ").split(" ");
					codeterm.surroundingWords = new HashMap<String,Integer>(a.length);
					for (String s : a) {
						if (s != null && !s.isEmpty() && !s.trim().isEmpty()) {
							if(codeterm.surroundingWords.containsKey(s)){
								codeterm.surroundingWords.replace(s, codeterm.surroundingWords.get(s)+1);
							}
							else
								codeterm.surroundingWords.put(s, 1);
						}
					}
					for (String s : qualifiednames) {
						String[] qual = breakWordUp(s); 
						int rank = compareAndRank(qual, codeterm.surroundingWords);
						codeterm.qualifiedNames.put(rank, s);
					}
					codeterm.bestQualifiedName = codeterm.qualifiedNames.get(codeterm.qualifiedNames.lastKey());
					codeTerms.add(codeterm);

					if (codeterm.qualifiedNames.size() < 50)
						freqdistexample[codeterm.qualifiedNames.size()]++;
				}
			}
		}
	}

	/**
	 * @stereotype SET COLLABORATOR 
	 */
	private static void getallelements(Source source, String classname) throws IOException {
		ArrayList<Element> elements = (ArrayList<Element>) source.getAllElements(classname);
		for (Element element : elements) {
			Element parent = element.getParentElement(); 
			Segment parentContent = parent.getContent(); 
			String parentName = parentContent.getTextExtractor().toString();
			
			Element grandparent = parent.getParentElement(); 
			Segment grandparentContent = grandparent.getContent(); 
			String grandparentName = grandparentContent.getTextExtractor().toString();
			
			CodeTerm codeterm = new CodeTerm();
			net.htmlparser.jericho.Attributes attributes = element.getAttributes();
			if (ignoreattribute(attributes))
				continue;
			Segment content = element.getContent();
			String name = content.getTextExtractor().toString();
			name = cleanUpCodeTerm(name);
			if (ignorename(name))
				continue;
			codeterm.codeterm = name;
			codeterm.type = returnType(name);
			codeterm.row = element.getRowColumnVector().getRow();
			codeterm.column = element.getRowColumnVector().getColumn();

			//Removing all digits and non-alphabetic characters from text
			String[] a = grandparentName.replaceAll("\\w*\\d\\w* *", "").replaceAll("[^a-zA-Z]", " ").split(" ");
			
			codeterm.surroundingWords = new HashMap<String, Integer>(a.length);
			for (String s : a) {
				if (s != null && !s.isEmpty() && !s.trim().isEmpty()) {
					if(codeterm.surroundingWords.containsKey(s)){
						codeterm.surroundingWords.replace(s, codeterm.surroundingWords.get(s)+1);
					}
					else
						codeterm.surroundingWords.put(s, 1);
				}
			}
			codeterm.section = returnSection(codeterm.row);
			//codeterm.bestQualifiedName = ;
			//codeterm.stereotype = whatStereotype(codeterm.bestQualifiedName); 
			codeTerms.add(codeterm);
		}
	}
	
	private static String cleanHtml(String td) {
		String res = ""; 
		String metName = ""; 
		if (td.contains("[<a href=\"https://developer.android.com/reference/")) {
			td = td.replace("[<a href=\"https://developer.android.com/reference/", ""); 
			https://developer.android.com/guide/topics/manifest/manifest-element.html
			if (td.indexOf(".") > 0) {
				//if it is a method name
				if (td.indexOf('#') >= 0) {
					metName = td.split("#")[1]; 
					metName = metName.split("\"")[0];
					metName = "." + metName; 
				}
				res = td.split("\\.")[0];
				res = res.replace("/", "."); 
			}
			res = res + metName; 
		}
		else if (td.contains("<a href=\"https://developer.android.com/reference/com/google/android/")) {
			res = "CLT hyperlinked to Google API reference pages"; 
		}
		else {
			res = "CLT not hyperlinked to reference pages"; 
		}
		return res; 
	}
	
	private static void getallelementsAndroid(Source source, String codetag1, String codetag2) throws IOException {
		ArrayList<Element> elements = (ArrayList<Element>) source.getAllElements(codetag1);

		for (Element element : elements) {
			net.htmlparser.jericho.Attributes attributes = element.getAttributes();
			if (ignoreattribute(attributes))
				continue;
			Segment content = element.getContent();
			CodeTerm codeterm = new CodeTerm();
			String fullyQualifiedName = "";
			String name = ""; 
			if (content.toString().contains("code")) {
				name = content.getTextExtractor().toString();
				fullyQualifiedName = cleanHtml(content.toString()); 
				//System.out.println(name + " : " + content);
				
			}
			else {
				name = content.getTextExtractor().toString();
				//if CLT is hyperlinked
				if (content.toString().contains("href")) {
					if (element.getAttributes() != null) {
						fullyQualifiedName = element.getAllElements("a href").toString();
						fullyQualifiedName = cleanHtml(fullyQualifiedName); 
					}
				}
				//if unhyperlinked CLT is a fully qualified name already
				else if(content.toString().startsWith("android.")) {
					fullyQualifiedName = name; 
				}
				//if CLT is not hyperlinked
				else {
					fullyQualifiedName = "Non-hyperlinked CLT"; 
				}
				//System.out.println(element.getContent().toString());
			}
			codeterm.codeterm = name;
			codeterm.type = returnType(name);
			codeterm.row = element.getRowColumnVector().getRow();
			codeterm.column = element.getRowColumnVector().getColumn();
			codeterm.bestQualifiedName = fullyQualifiedName; 
			//codeterm.stereotype = whatStereotype(codeterm.bestQualifiedName);
			
			codeterm.section = returnSection(codeterm.row); 
			codeTerms.add(codeterm);
		}
	}
	
	//qualName = org.apache.http.impl.conn.PoolingHttpClientConnectionManager.PoolingHttpClientConnectionManager()
	public static String whatStereotype(String qualName) throws IOException {
		//System.out.println(qualName);
		String stereotype = ""; 
		String classPath = getClassPath(qualName); 
		//if CLT is method name
		if (qualName.contains("()")) {
			String q = ""; 
			String[] parts = qualName.split("\\.");
			for (int i = 0; i < parts.length-1; i++) {
				if (i != parts.length-2)
					q = q + parts[i] + "."; 
				else
					q = q + parts[i];
			} 
			//classPath= /home/senjuti/Documents/httpcomponents-client-4.5.2/httpclient/src/main/java/org/apache/http/impl/conn/PoolingHttpClientConnectionManager.java
			//q= org.apache.http.impl.conn.PoolingHttpClientConnectionManager
			//qualName= org.apache.http.impl.conn.PoolingHttpClientConnectionManager.PoolingHttpClientConnectionManager()
			stereotype = getMethodFromPath(classPath, q, qualName); 
		}
		//if CLT is class name
		else {
			//System.out.println("classPath="+classPath);
			//System.out.println("qualName="+qualName);
			stereotype = getClassFromPath(classPath, qualName); 
		}
		System.out.println(classPath);
		String pathClassPaths = "/home/senjuti/Documents/CodeWordsWithPositionAndMatching/src/docAnalysis/SpringClassPath.txt"; 
		BufferedWriter bwClassPaths = new BufferedWriter(new FileWriter(pathClassPaths, true)); 
		bwClassPaths.write(classPath+"\n");
		bwClassPaths.close();
		//System.out.println("stereotype="+stereotype);
		return stereotype; 
	}
	
	//find path to class, given fully qualified class name
	public static String getClassPath(String qualName) throws IOException {
		//qualName = org.apache.http.impl.conn.PoolingHttpClientConnectionManager.PoolingHttpClientConnectionManager()
		//q = org.apache.http.impl.conn.PoolingHttpClientConnectionManager
		//q represents class name
		//classPath = /home/senjuti/Documents/httpcomponents-client-4.5.2/httpclient/src/main/java/org/apache/http/impl/conn/PoolingHttpClientConnectionManager.java
		String q = ""; 
		//if qualified name is a method
		if (qualName.contains("()")) {
			String[] parts = qualName.split("\\.");
			for (int i = 0; i < parts.length-1; i++) {
				if (i != parts.length-2)
					q = q + parts[i] + "."; 
				else
					q = q + parts[i];
			}
			q = q.replaceAll("\\.", "\\/");
		}
		else {
			q = qualName.replaceAll("\\.", "\\/"); 
		}
		//q = org/apache/http/impl/conn/PoolingHttpClientConnectionManager
		String classPath = "";
		String possibleOuterClassName = "";
		String classPathForInnerClasses = ""; 
		//String paths = "./src/docAnalysis/http-paths.txt"; 
		String paths = "./src/docAnalysis/spring-paths.txt"; 
		BufferedReader in = new BufferedReader(new FileReader(paths)); 
		int endIndex = q.lastIndexOf("/");
	    if (endIndex != -1)  
	    {
	        possibleOuterClassName = q.substring(0, endIndex); 
	    }
		String line = in.readLine();
		//q = q.replaceAll("/", "\\."); 
		while (line != null) {	
			if (line.contains(q)) {
				classPath = line; 
				break; 
			}
			if (line.contains(possibleOuterClassName)) {
				classPathForInnerClasses = line;
				break;
			}
			line = in.readLine();
		}
		//q = org.springframework.jca.work.jboss.JBossWorkManagerTaskExecutor
		//System.out.println("q="+q+" classPath="+classPath);
		//System.out.println("classPath=" + classPath);
		

		in.close();
		if (classPath.isEmpty()) {
			//System.out.println("classPathForInnerClasses="+classPathForInnerClasses);
			return classPathForInnerClasses;
		}
		else {
			//System.out.println("classPath="+classPath);
			return classPath;
		}
	}
	
	//qualName: org.apache.http.impl.auth.BasicScheme
	//path: /home/senjuti/Documents/httpcomponents-client-4.5.2/httpclient/src/main/java/org/apache/http/impl/auth/BasicSchemeFactory.java
	public static String getClassFromPath(String path, String qualName) throws IOException {
		//System.out.println("qualName: " + qualName);
		//System.out.println("path: " + path);
		File pathToFile = new File(path); 
		//JavaProjectBuilder builder = new JavaProjectBuilder();
		JavaSource src = builder.addSource(pathToFile);
		JavaClass cls = builder.getClassByName(qualName);
		//System.out.println("cls="+cls);
		
		String stereotype = ""; 
		List<DocletTag> returns = cls.getTagsByName("stereotype");
		//System.out.println("returns="+ returns); 
		if (returns != null) {
			for (DocletTag r : returns) {
				stereotype = r.getValue(); 
				//System.out.println("r.getValue()="+ r.getValue()); 
			}
		}

		return stereotype; 
	}
	//path=/home/senjuti/Documents/httpcomponents-client-4.5.2/httpclient/src/main/java/org/apache/http/impl/conn/PoolingHttpClientConnectionManager.java
	//qualName = org.apache.http.impl.conn.PoolingHttpClientConnectionManager
	//methodName = org.apache.http.impl.conn.PoolingHttpClientConnectionManager.PoolingHttpClientConnectionManager()
	public static String getMethodFromPath(String path, String qualName, String methodName) throws IOException {
		//System.out.println("path="+path);
		//System.out.println("qualName="+qualName);
		//System.out.println("methodName="+methodName);
		JavaMethod method = null;
		JavaConstructor con = null; 
		File pathToFile = new File(path); 
		JavaProjectBuilder builder = new JavaProjectBuilder();
		JavaSource src = builder.addSource(pathToFile);
		JavaClass cls = builder.getClassByName(qualName);
		
		//System.out.println("cls="+cls);
		Collection<JavaMethod> methodNames = cls.getMethods();
		Collection<JavaConstructor> constructorNames = cls.getConstructors(); 
		String stereotype = "";
		
		for (JavaMethod m : methodNames) {
			//m = org.apache.http.conn.routing.HttpRoute org.apache.http.impl.conn.BasicHttpClientConnectionManager.getRoute()
			//m = public void org.apache.http.impl.conn.BasicHttpClientConnectionManager.close()
			//System.out.println("m.toString()="+m.toString());
			String[] parts = m.toString().split(" "); 
			int l = parts.length; 
			//System.out.println("m[l-1] = " + m.toString().split(" ")[l-1]);
			if (m.toString().split(" ")[l-1].equals(methodName)) {
				method = m; 
			}
		}
		if (method == null) {
			for (JavaConstructor c : constructorNames) {
				String[] parts = c.toString().split(" "); 
				int l = parts.length; 
				String tmp = c.toString().split(" ")[l-1]; 
				String[] parts2 = tmp.split("\\."); 
				l = parts2.length; 
				String coName = qualName + "." + parts2[l-1]; 
				//System.out.println("coName = " + coName);
				if (coName.equals(methodName)) {
					con = c; 
				}
			}
			if (con != null) {
				List<DocletTag> returns = con.getTagsByName("stereotype");
				if (returns != null) {
					for (DocletTag r : returns) {
						stereotype = r.getValue(); 
					}
				}
			}
		}
		else {
			List<DocletTag> returns = method.getTagsByName("stereotype");
			if (returns != null) {
				for (DocletTag r : returns) {
					stereotype = r.getValue(); 
				}
			}
		}
		//System.out.println("stereotype="+stereotype);
		return stereotype; 
	}
	
	/**
	 * @stereotype COLLABORATOR 
	 */
	private static String returnSection(int row) {
		String sectionName = "";
		for (Map.Entry<Integer,String> entry : section.entrySet()) {
			  int key = entry.getKey();
			  int nextKey; 
			  if (section.higherEntry(key) != null) {
				  nextKey = section.higherKey(key);
			  }
			  else {
				  nextKey = Integer.MAX_VALUE; 
			  }
			  if (row > key && row < nextKey) {
				  sectionName = entry.getValue(); 
				  break;
			  }
		}
		return sectionName;
	}
	
	/**
	 * @stereotype COLLABORATOR 
	 */
	private static String[] breakWordUp(String name) {
		String[] splited = name.split("\\.");
		return splited;
	}

	/**
	 * @stereotype COLLABORATOR 
	 */
	private static boolean ignoreattribute(net.htmlparser.jericho.Attributes attributes) {
		String attribute = attributes.getValue("class");
		if (attribute == null)
			return false;
		else if (attribute.equals("literal"))
			return false;
		return true;
	}

	/**
	 * @stereotype COLLABORATOR 
	 */
	private static boolean ignorename(String name) {
		if (name.isEmpty())
			return true;
		if (name.length() <= 4)
			return true;
		if (name.contains("*"))
			return true;
		if (name.contains(" "))
			return true;
		if (name.contains("@"))
			return true;
		if (name.contains("\""))
			return true;
		if (name.contains(":"))
			return true;
		if (name.contains("-"))
			return true;
		if (name.contains("="))
			return true;
		if (name.endsWith(".xml"))
			return true;
		if (name.endsWith(".jpg"))
			return true;
		if (RESERVEDVALUES.contains(name))
			return true;
		return false;
	}

	/**
	 * @stereotype COLLABORATOR 
	 */
	private static String cleanUpCodeTerm(String name) {
		if (name.endsWith(".java"))
			name = name.substring(0, name.lastIndexOf(".java"));
		name = name.replaceAll("#", ".");		
		name = name.replaceAll(";", "");
		name = name.replaceAll("!", "");
		name = name.replaceAll(",", "");
		name = name.replaceAll("'", "");
		name = name.replaceAll(">", "");
		name = name.replaceAll("\\.\\.", "");
		name = name.replaceAll("\\.\\.", "");
		name = name.replaceAll("\\)\\.", "");
		name = name.replaceAll("-", "()");
		
		if (name.indexOf('(') > -1)
			name = name.substring(0, name.indexOf('(')) + "()";
		if (name.indexOf('$') > -1)
			name = name.substring(0, name.indexOf('$'));
		
		int cntDots = 0;
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (c == '.')
				cntDots++;
		}
		if (cntDots == 1 && name.indexOf('(') == -1)
			name = name + "()";
		
		if (name.indexOf('<') > -1 && name.indexOf('(') == -1) {
			if (name.indexOf('(') > -1)
				name = name.substring(0, name.indexOf('<')) + "()";
			else
				name = name.substring(0, name.indexOf('<'));
		}
		if (name.indexOf('/') > -1)
			name = name.substring(name.lastIndexOf('/') + 1);
		if (!name.isEmpty() && name.charAt(0) == '.')
			name = name.substring(1);
		
		return name;
	}

	static String[] reservedKeywords = { "abstract", "continue", "for", "new", "switch", "assert", "default", "goto",
			"package", "synchronized", "boolean", "do", "if", "private", "this", "break", "double", "implements",
			"protected", "throw", "byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return",
			"transient", "catch", "extends", "int", "short", "try", "char", "final", "interface", "static", "void",
			"class", "finally", "long", "strictfp", "volatile", "const", "float", "native", "super", "while" };
	static Set<String> RESERVEDVALUES = new HashSet<String>(Arrays.asList(reservedKeywords));
}

/**
 * @stereotype DATA_PROVIDER 
 */
class CodeTerm implements Comparable<CodeTerm> {
	public Object type;
	public String codeterm;
	public TreeMap<Integer, String> qualifiedNames = new TreeMap();
	HashMap<String, Integer> surroundingWords = new HashMap<String, Integer>();
    public String bestQualifiedName; 
    public String section;
    public String stereotype; 
	Integer row;
	int column;

	/**
	 * @stereotype PROPERTY COLLABORATOR 
	 */
	@Override
	public String toString() {
		StringBuilder qual = new StringBuilder();
		qual.append(",");
		
		Set set = qualifiedNames.entrySet();
		Iterator i = set.iterator();
		while(i.hasNext()) {
			 Map.Entry me = (Map.Entry)i.next();
			 qual = qual.append(", " + me.getValue());
		}		
		//return (codeterm + ", " + bestQualifiedName); 
		return (codeterm + ", " + type + ", " + row + ", " + column + ", " + section + ", " + bestQualifiedName + ", @" + stereotype + "\n");	
	}
	/**
	 * @stereotype PROPERTY 
	 */
	public int compareTo(CodeTerm k) {
		return row.compareTo(k.row);
	}
}