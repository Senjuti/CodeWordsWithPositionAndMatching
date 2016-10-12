package docAnalysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaConstructor;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

public class QDoxTest2 {

	public static MyClass[] classArr;
	public static HashMap<String, Integer> javaClassNumber = new HashMap<String, Integer>();
	public static LinkedList<Integer> finalOrder = new LinkedList<Integer>();

	public static JavaProjectBuilder builder = new JavaProjectBuilder();

	public static String returnQualifiedName(JavaClass c, JavaMethod m) {
		return c.getFullyQualifiedName().replaceAll("\\$", "") + "." + m.getName() + "()";
	}

	public static String returnQualifiedName(JavaClass c, JavaConstructor m) {
		return c.getFullyQualifiedName().replaceAll("\\$", "") + "." + m.getName() + "()";
	}

	public static void classesInit() {
		// Get all Classes
		Collection<JavaClass> sourceClasses = builder.getClasses();
		classArr = new MyClass[sourceClasses.size()];
		Iterator<JavaClass> myiter = sourceClasses.iterator();
		for (int i = 0; i < sourceClasses.size(); i++) {
			MyClass myClass = new MyClass();
			myClass.javaClass = myiter.next();
			classArr[i] = myClass;
		}
		// Sort classes Alphabetically - Dunno why I did this :-/
		Arrays.sort(classArr);
		javaClassNumber = new HashMap<String, Integer>(classArr.length);
		for (int i = 0; i < classArr.length; i++) {
			JavaClass javaclass = classArr[i].javaClass;
			javaClassNumber.put(javaclass.getFullyQualifiedName(), i);
		}

		// Get Direct Supers and All Implemented Intefaces for all Classes
		for (int i = 0; i < classArr.length; i++) {
			classArr[i].classNumber = i;
			JavaClass javaclass = classArr[i].javaClass;
			JavaClass superClass = javaclass.getSuperJavaClass();
			if (superClass != null) {
				Integer noInteger = javaClassNumber.get(javaclass.getSuperJavaClass().getFullyQualifiedName());
				if (noInteger != null) {
					classArr[i].directSupers.add(noInteger);
					classArr[noInteger].directDesc.add(i);
					classArr[noInteger].directDescSize++;
				}
			}
			for (JavaClass interfaces : javaclass.getImplementedInterfaces()) {
				Integer noInteger = javaClassNumber.get(interfaces.getFullyQualifiedName());
				if (noInteger != null) {
					classArr[i].directSupers.add(noInteger);
					classArr[noInteger].directDesc.add(i);
					classArr[noInteger].directDescSize++;
				}
			}
		}

		// Run Topological Sort (Kahn's Algorithm) to figure out
		// in which order to calculate all descendents
		finalOrder = new LinkedList<Integer>();
		LinkedList<Integer> noIncoming = new LinkedList<Integer>();
		for (int i = 0; i < classArr.length; i++) {
			if (classArr[i].directDescSize == 0)
				noIncoming.add(i);
		}
		while (noIncoming.size() != 0) {
			MyClass myClass = classArr[noIncoming.remove()];
			finalOrder.addLast(myClass.classNumber);
			for (Integer superClass : myClass.directSupers) {
				classArr[superClass].directDescSize--;
				if (classArr[superClass].directDescSize == 0)
					noIncoming.add(superClass);
			}
		}

		// Reverse The order for Calculating alldot
		Collections.reverse(finalOrder);
	}

	public static void calculateAllDot() {
		// Process all classes in reverse Topological Order
		for (Integer i : finalOrder) {

			MyClass curClass = classArr[i];
			for (Integer supers : curClass.directSupers) {
				curClass.allDot.addAll(classArr[supers].allDot);
			}

			Collection<JavaMethod> methodNames = curClass.javaClass.getMethods();
			Collection<JavaField> fieldNames = curClass.javaClass.getFields();
			for (JavaMethod m : methodNames) {
				curClass.allDot.add(m.getName() + "()");
			}
			for (JavaField f : fieldNames) {
				curClass.allDot.add(f.getName());
			}
		}
	}
	
	public static void makeSections(String htmlpath, String outputSectionsPath) throws IOException{
		Source source = new Source(new File(htmlpath));
		source.fullSequentialParse();
		ArrayList<Element> elements = (ArrayList<Element>) source.getAllElements("h3");
		elements.addAll(source.getAllElements("h2"));
		Element[] elemArr = elements.toArray(new Element[elements.size()]);
		Arrays.sort(elemArr, new Comparator<Element>() {
			public int compare(Element o1, Element o2) {
				return (int) (o1.getRowColumnVector().getRow() - o2.getRowColumnVector().getRow());
			}
		});

		BufferedWriter out = new BufferedWriter(new FileWriter(outputSectionsPath));
		for(int i =0 ; i< elements.size(); i++){
			Element element = elemArr[i];
			int row = element.getRowColumnVector().getRow();
			String name = element.getContent().getTextExtractor().toString();
			out.write(row+","+row+","+name);
			out.newLine();
		}
		out.close();
	}

	public static void main(String args[]) throws IOException {
		String sourceCodePath = "/home/spring-framework";
		String outputMethodsPath = "./src/docAnalysis/Pipeline/Class and Method Names List/SpringmethodsALL.txt";
		String outputClassesPath = "./src/docAnalysis/Pipeline/Class and Method Names List/SpringclassesALL.txt";
		String htmlpath = "./src/docAnalysis/Pipeline/Spring.html";
		String outputSectionsPath = "./src/docAnalysis/SpringlineAndSection.txt";
		
		/*String sourceCodePath = "/home/akhil/Senjuti/httpcomponents-client-4.5.2";
		String outputMethodsPath = "./src/docAnalysis/Pipeline/Class and Method Names List/HttpmethodsALL.txt";
		String outputClassesPath = "./src/docAnalysis/Pipeline/Class and Method Names List/HttpclassesALL.txt";
		String htmlpath = "./src/docAnalysis/Pipeline/SpringFramework.html";
		String outputSectionsPath = "./src/docAnalysis/SpringlineAndSection.txt";*/
		
		//makeSections(htmlpath, outputSectionsPath);

		File pathToFile = new File(sourceCodePath);
		System.out.println("Started Reading Source Code");
		builder.addSourceTree(pathToFile);
		System.out.println("Finsihed Reading Source Code");
		classesInit();
		System.out.println("Finsihed Calculating Topological Order");
		calculateAllDot();
		System.out.println("Finsihed Calculating All Fields and Methods");

		PrintWriter writer = new PrintWriter(outputMethodsPath, "UTF-8");
		for (int i = 0; i < classArr.length; i++) {
			if (i % 1000 == 0)
				System.out.println("Currently Processing " + i + " out of " + classArr.length);
			MyClass curclass = classArr[i];
			String fullyQualifiedName = curclass.javaClass.getFullyQualifiedName().replaceAll("\\$", ".");
			if(fullyQualifiedName.equals("Null"))
				continue;
			writer.println(fullyQualifiedName + "()");
			for (String m : curclass.allDot) {
				writer.println(fullyQualifiedName + "." + m);
			}
		}
		writer.close();

		writer = new PrintWriter(outputClassesPath, "UTF-8");
		for (int i = 0; i < classArr.length; i++) {
			if (i % 1000 == 0)
				System.out.println("Currently Processing " + i + " out of " + classArr.length);
			MyClass curclass = classArr[i];
			String fullyQualifiedName = curclass.javaClass.getFullyQualifiedName().replaceAll("\\$", ".");
			if(fullyQualifiedName.equals("Null"))
				continue;
			writer.println(fullyQualifiedName);
		}
		writer.close();
	}
}
