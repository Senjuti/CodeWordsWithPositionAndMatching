//produces <Section Name:Fully qualified CLT name:Class stereotype>
package docAnalysis;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.collections4.CollectionUtils;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotatedElement;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.JavaType;

public class StereotypeAndClassAnalysis {
	public static HashMap<String, String> CLTWithStereotypes = new HashMap<String, String>();
	public static List<String> qualifiedNames = new ArrayList<String>(); 
	public static String returnQualifiedName(JavaClass c, JavaMethod m) {
		StringBuilder qualifiedName = new StringBuilder();
		qualifiedName.append(c.getClassNamePrefix().replaceAll("\\$", "")+"."+m.getName()+"("); 
		int tmp = 1;
		for (JavaType param : m.getParameterTypes()) {
			String paramName; 
			String[] splitWord = param.toString().split("\\.");
			int l = splitWord.length;
			paramName = splitWord[l-1]; 
			paramName = paramName.replaceAll("\\$", "\\."); 
			if (tmp++ == m.getParameterTypes().size()) {
				qualifiedName.append(paramName); 
			}
			else {
				qualifiedName.append(paramName+", "); 
			}
		}
		qualifiedName.append(")"); 
		return qualifiedName.toString(); 
	}
	
	public static void main(String[] args) throws IOException {
		JavaProjectBuilder builder = new JavaProjectBuilder();
		String filepath = "/home/senjuti/Documents/CodeWordsWithPositionAndMatching/src/docAnalysis/httpclient-paths.txt";
		String outputfilepath = "/home/senjuti/Documents/CodeWordsWithPositionAndMatching/src/docAnalysis/class-stereotype-httpclient.txt";
		String outputfilepath2 = "/home/senjuti/Documents/CodeWordsWithPositionAndMatching/src/docAnalysis/method-stereotype-httpclient.txt";
		String inputfile = "/home/senjuti/Documents/CodeWordsWithPositionAndMatching/src/docAnalysis/Pipeline/Manually annotated CLTs/HttpClient.txt"; 
		String outputfilepath3 = "/home/senjuti/Documents/CodeWordsWithPositionAndMatching/src/docAnalysis/qualname-stereotype-httpclient.txt";

		Scanner s = new Scanner(new File(filepath));
		ArrayList<String> list = new ArrayList<String>();
		BufferedWriter out = new BufferedWriter(new FileWriter(new File(outputfilepath)));
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputfilepath2)));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(new File(outputfilepath3)));
		BufferedReader in = new BufferedReader(new FileReader(new File(inputfile)));

		String line = in.readLine();
		while (line != null) {	
			String[] lst = line.split(":");
			String qualName = lst[1]; 
			qualifiedNames.add(qualName);
			//System.out.println(qualName);
			line = in.readLine();
		}
		
		while (s.hasNext()){
		    list.add(s.next());
		}
		s.close();
		
		for (String l : list) {
			JavaSource src = builder.addSource(new FileReader(l));
			Collection<JavaClass> cls = src.getClasses();

			for (JavaClass c : cls) {
				List<DocletTag> returns = c.getTagsByName("stereotype");
				if (!(returns.isEmpty())) {
					out.write(c.getCanonicalName() + ", "); 
					for (DocletTag r : returns) {
						out.write("@" + r.getName() + " " + r.getValue());
						CLTWithStereotypes.put(c.getCanonicalName().toString(), r.getValue()); 
					}
					out.write("\n");
				}
				
				Collection<JavaMethod> methodNames = c.getMethods();
				for (JavaMethod m : methodNames) {
					List<DocletTag> rtn = m.getTagsByName("stereotype");
					if (!(rtn.isEmpty())) {
						bw.write(returnQualifiedName(c, m));
						for (DocletTag r : rtn) {
							bw.write(", @" + r.getName() + " " + r.getValue());
							CLTWithStereotypes.put(returnQualifiedName(c, m), r.getValue()); 
						}
						bw.write("\n");
					}
				}
			}
		}
		out.close(); 
		bw.close();
		int match = 0; 
		
		for (String q : qualifiedNames) {
			for (Map.Entry<String, String> entry : CLTWithStereotypes.entrySet()) {
				String c = entry.getKey(); 
				if (q.equals(c)) {
					match++; 
					//System.out.println(q + ", " + c);
					bw2.write(q + ":@stereotype " + entry.getValue() + "\n");
				}
			}
		}
		System.out.println(match);
		bw2.close();
		
	}
}
