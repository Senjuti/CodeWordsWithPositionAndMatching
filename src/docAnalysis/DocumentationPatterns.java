package docAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaConstructor;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;

public class DocumentationPatterns {

//	public static String path = "E:\\Akhil\\Senjuti\\Extract and Match\\httpcomponents-client-4.5.2\\httpclient";
//	public static String matchedNames = "./src/docAnalysis/Matched/HttpClientMathchedCode.txt";
//	public static String pathMethodAll = "./src/docAnalysis/Pipeline/Class and Method Names List/HttpClientmethodsALL.txt";

	public static String path = "/home/akhil/Senjuti/spring-framework";
	public static String matchedNames = "./src/docAnalysis/Matched/SpringMathchedCode.txt";
	public static String pathMethodAll = "./src/docAnalysis/Pipeline/Class and Method Names List/SpringmethodsALL.txt";

	public String qualifiedName = new String();
	public HashMap<String, Boolean> declaredElements = new HashMap<String, Boolean>();
	public HashMap<String, Boolean> concreteElements = new HashMap<String, Boolean>();
	public ArrayList<JavaClass> extendingElements = new ArrayList<JavaClass>();
	public ArrayList<JavaClass> concreteExtendingElements = new ArrayList<JavaClass>();
	public ArrayList<JavaClass> descendentElements = new ArrayList<JavaClass>();
	public ArrayList<JavaClass> concreteDescendentElements = new ArrayList<JavaClass>();
	public ArrayList<String> tokenElementsClass = new ArrayList<String>();
	public ArrayList<String> tokenElementsMethod = new ArrayList<String>();
	public ArrayList<Pattern> patterns = new ArrayList<Pattern>();

	public static JavaProjectBuilder builder = new JavaProjectBuilder();
	public static MyClass[] classArr;
	public static HashMap<String, Integer> javaClassNumber = new HashMap<String, Integer>();
	public static ArrayList<String> methodsAll = new ArrayList<String>();
	public static ArrayList<CodeTerm> codeTerms = new ArrayList<CodeTerm>();
	public static ArrayList<Pattern> filteredPatterns = new ArrayList<Pattern>();
	public static ArrayList<Pattern> docPatterns = new ArrayList<Pattern>();

	public static double filterthreshold = 0.34;
	public static double combinethreshold = 1 - 0.4;
	public static double linkthreshold = 0.51;

	public DocumentationPatterns() {
	}

	public static void globalInit() throws Exception {
		File pathToFile = new File(path);
		builder.addSourceTree(pathToFile);

		classesInit();

		BufferedReader br = new BufferedReader(new FileReader(pathMethodAll));
		String line = br.readLine();
		while (line != null) {
			methodsAll.add(line);
			line = br.readLine();
		}
		br.close();

		br = new BufferedReader(new FileReader(matchedNames));
		line = br.readLine();
		while (line != null) {
			CodeTerm c = new CodeTerm();
			String[] parts = line.split(", ");
			c.codeterm = parts[0];
			c.type = parts[1];
			c.row = Integer.parseInt(parts[2].replaceAll("\\s", ""));
			c.column = Integer.parseInt(parts[3].replaceAll("\\s", ""));
			c.section = parts[4];
			c.bestQualifiedName = parts[5];
			c.stereotype = parts[6];
			codeTerms.add(c);
			line = br.readLine();
		}
		br.close();
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
		LinkedList<Integer> finalOrder = new LinkedList<Integer>();
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

		// Calculate all Descendents
		for (Integer i : finalOrder) {
			for (Integer supers : classArr[i].directSupers) {
				classArr[supers].allDescendents.add(i);
			}
		}

		// BufferedReader br = new BufferedReader(new FileReader(pathClassAll));
		// String line = br.readLine();
		// while (line != null) {
		// classesAll.add(line);
		// line = br.readLine();
		// }
		// br.close();

	}

	public static ArrayList<String> cleanName(Collection<JavaMethod> methodNames) {
		ArrayList<String> newMethodNames = new ArrayList<String>();
		for (JavaMethod m : methodNames) {
			newMethodNames.add(m.getName());
		}
		return newMethodNames;
	}

	public static String returnQualifiedName(JavaClass c, JavaMethod m) {
		// TODO : Is it even possible to take into account the parameters?
		// I dont think so.
		return c.getClassNamePrefix().replaceAll("\\$", "") + "." + m.getName() + "()";
		// StringBuilder qualifiedName = new StringBuilder();
		// qualifiedName.append(c.getClassNamePrefix().replaceAll("\\$", "") +
		// "." + m.getName() + "(");
		// int tmp = 1;
		// for (JavaType param : m.getParameterTypes()) {
		// String paramName;
		// String[] splitWord = param.toString().split("\\.");
		// int l = splitWord.length;
		// paramName = splitWord[l - 1];
		// paramName = paramName.replaceAll("\\$", "\\.");
		// if (tmp++ == m.getParameterTypes().size()) {
		// qualifiedName.append(paramName);
		// } else {
		// qualifiedName.append(paramName + ", ");
		// }
		// }
		// qualifiedName.append(")");
		// return qualifiedName.toString().replaceAll("\\$", "\\.");
	}

	public static String returnQualifiedName(JavaClass c, JavaConstructor m) {
		// TODO : Is it even possible to take into account the parameters?
		// I dont think so.
		return c.getClassNamePrefix().replaceAll("\\$", "") + "." + m.getName() + "()";
		// StringBuilder qualifiedName = new StringBuilder();
		// qualifiedName.append(c.getClassNamePrefix().replaceAll("\\$", "") +
		// "." + m.getName() + "(");
		// int tmp = 1;
		// for (JavaType param : m.getParameterTypes()) {
		// String paramName;
		// String[] splitWord = param.toString().split("\\.");
		// int l = splitWord.length;
		// paramName = splitWord[l - 1];
		// paramName = paramName.replaceAll("\\$", "\\.");
		// if (tmp++ == m.getParameterTypes().size()) {
		// qualifiedName.append(paramName);
		// } else {
		// qualifiedName.append(paramName + ", ");
		// }
		// }
		// qualifiedName.append(")");
		// return qualifiedName.toString().replaceAll("\\$", "\\.");
	}

	public static String returnQualifiedName(JavaClass c, JavaField m) {
		return (c.getClassNamePrefix().replaceAll("\\$", "") + "." + m.getName());
	}

	public void declaredElements(JavaClass cls) {
		Collection<JavaMethod> methodNames = cls.getMethods();
		Collection<JavaConstructor> constructorNames = cls.getConstructors();
		Collection<JavaField> fieldNames = cls.getFields();

		for (JavaMethod m : methodNames) {
			String metName = returnQualifiedName(cls, m);
			declaredElements.put(metName, false);
			if (!m.isAbstract())
				concreteElements.put(metName, false);
		}

		for (JavaConstructor c : constructorNames) {
			String conName = returnQualifiedName(cls, c);
			declaredElements.put(conName, false);
			if (!c.isAbstract())
				concreteElements.put(conName, false);
		}

		for (JavaField f : fieldNames) {
			String conName = returnQualifiedName(cls, f);
			declaredElements.put(conName, false);
			if (!f.isAbstract())
				concreteElements.put(conName, false);
		}
	}

	public static ArrayList<String> tokenize(String qualifiedName) {
		ArrayList<String> tokens = new ArrayList<String>();
		for (String w : qualifiedName.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
			tokens.add(w);
		}
		// System.out.println(tokens);
		return tokens;
	}

	public static ArrayList<String> containsTokenClass(ArrayList<String> tokensqualifiedName) throws IOException {
		// TODO: Should be done with Trie
		ArrayList<String> tokenElements = new ArrayList<String>();
		for (MyClass myClass : classArr) {
			String classname = myClass.javaClass.getFullyQualifiedName();
			for (String c : tokensqualifiedName) {
				if (classname.contains(c)) {
					tokenElements.add(classname);
				}
			}
		}
		return tokenElements;
	}

	public static ArrayList<String> containsTokenMethod(ArrayList<String> tokensqualifiedName) throws IOException {
		// TODO: Should be done with Trie
		ArrayList<String> tokenElements = new ArrayList<String>();
		for (String classname : methodsAll) {
			for (String c : tokensqualifiedName) {
				if (classname.contains(c)) {
					tokenElements.add(classname);
				}
			}
		}
		return tokenElements;
	}

	public static void addToCoverageMap(HashMap<String, Section> coverageMap, CodeTerm codeTerm) {
		String baseSection = codeTerm.section.substring(0, codeTerm.section.indexOf(' '));
		baseSection = "0." + baseSection;
		String currentSection = baseSection;
		while (true) {
			if (coverageMap.containsKey(currentSection)) {
				Section section = coverageMap.get(currentSection);
				section.codeTermNames.add(codeTerm.bestQualifiedName);
				coverageMap.put(currentSection, section);
			} else {
				Section section = new Section();
				section.sectionNumber = currentSection;
				section.depth = StringUtils.countMatches(currentSection, ".");
				section.codeTermNames.add(codeTerm.bestQualifiedName);
				coverageMap.put(currentSection, section);
			}
			if (currentSection.lastIndexOf('.') == -1)
				break;
			currentSection = currentSection.substring(0, currentSection.lastIndexOf('.'));
		}
	}

	public void computeCodePatterns() throws IOException {

		JavaClass cls = builder.getClassByName(qualifiedName);

		// 1. The set of code elements declared by C.
		declaredElements(cls);
		//System.out.println("The set of code elements declared by " + qualifiedName);
		//System.out.println(declaredElements);

		// 2. The set of concrete code elements declared by C.
		//System.out.println("The set of concrete code elements declared by " + qualifiedName);
		//System.out.println(concreteElements);

		// 3. Elements extending C directly
		Integer classNumber = javaClassNumber.get(qualifiedName);
		extendingElements = new ArrayList<JavaClass>(classArr[classNumber].directDescSize);
		for (Integer i : classArr[classNumber].directDesc) {
			extendingElements.add(classArr[i].javaClass);
		}
		//System.out.println("The set of code elements extending " + qualifiedName + " directly");
		//System.out.println(extendingElements);

		// 4. Concrete Elements extending C directly
		for (JavaClass c : extendingElements) {
			if (!c.isAbstract() && !c.isInterface()) {
				concreteExtendingElements.add(c);
			}
		}
		//System.out.println("The set of concrete code elements extending " + qualifiedName + " directly");
		//System.out.println(concreteExtendingElements);

		// 5. Code elements whose ancestor is C
		descendentElements = new ArrayList<JavaClass>(classArr[classNumber].allDescendents.size());
		for (Integer i : classArr[classNumber].allDescendents) {
			extendingElements.add(classArr[i].javaClass);
		}
		//System.out.println("The set of code elements extending whose ancestor is " + qualifiedName);
		//System.out.println(descendentElements);

		// 6 .Concrete code elements whose ancestor is C
		for (JavaClass a : descendentElements) {
			if (!a.isAbstract() && !a.isInterface()) {
				concreteDescendentElements.add(a);
			}
		}
		//System.out.println("The set of concrete code elements extending whose ancestor is " + qualifiedName);
		//System.out.println(concreteDescendentElements);

		long time = System.currentTimeMillis();
		// 7. The sets of code elements classes starting, ending, or containing
		// a token
		// in C's name
		ArrayList<String> tokensqualifiedName = tokenize(qualifiedName);
		//tokenElementsClass = containsTokenClass(tokensqualifiedName);
		//System.out.println("The sets of code classes starting, ending, or containing a token in " + qualifiedName + "'s name");
		//System.out.println(tokenElementsClass);

		// 7.1 The sets of code elements methods starting, ending, or containing
		// a token
		// in C's name
		//tokenElementsMethod = containsTokenMethod(tokensqualifiedName);
		//System.out.println("The sets of code methods starting, ending, or containing a token in " + qualifiedName + "'s name");
		//System.out.println(tokenElementsMethod);

		//System.out.println("Time taken for TokenElements = " + (System.currentTimeMillis() - time) + " miliseconds");
		time = System.currentTimeMillis();

		// 8. The sets of code elements declared by C that start, end, or
		// contain the same token

		/*
		 * System.out.println("The sets of code elements declared by " +
		 * qualifiedName + " that start, end, or contain the same token");
		 * System.out.println(concreteDescendentElements);
		 */
	}

	public static HashMap<String, Boolean> convertClasstoMap(ArrayList<JavaClass> l) {
		HashMap<String, Boolean> newList = new HashMap<String, Boolean>();
		for (JavaClass c : l) {
			newList.put(c.getFullyQualifiedName(), false);
		}
		return newList;
	}

	// Returns proportion of code elements in a pattern that are mentioned in a
	// pattern
	// Returns section and location mentioning each code element in pattern
	public void computeCodePatternCoverage() throws IOException {

		Pattern p1 = new Pattern();
		p1.patternNumber = 1;
		p1.patternElements = declaredElements;
		patterns.add(p1);

		Pattern p2 = new Pattern();
		p2.patternNumber = 2;
		p2.patternElements = concreteElements;
		patterns.add(p2);

		Pattern p3 = new Pattern();
		p3.patternNumber = 3;
		p3.patternElements = convertClasstoMap(extendingElements);
		patterns.add(p3);

		Pattern p4 = new Pattern();
		p4.patternNumber = 4;
		p4.patternElements = convertClasstoMap(concreteExtendingElements);
		patterns.add(p4);

		Pattern p5 = new Pattern();
		p5.patternNumber = 5;
		p5.patternElements = convertClasstoMap(descendentElements);
		patterns.add(p5);

		Pattern p6 = new Pattern();
		p6.patternNumber = 6;
		p6.patternElements = convertClasstoMap(concreteDescendentElements);
		patterns.add(p6);

		Pattern p7 = new Pattern();
		p7.patternNumber = 7;
		p7.patternElements = new HashMap<String, Boolean>();
		for (String code : tokenElementsClass) {
			p7.patternElements.put(code, false);
		}
		patterns.add(p7);

		Pattern p8 = new Pattern();
		p8.patternNumber = 8;
		p8.patternElements = new HashMap<String, Boolean>();
		for (String code : tokenElementsMethod) {
			p8.patternElements.put(code, false);
		}
		patterns.add(p8);

		for (int i = 0; i < codeTerms.size(); i++) {
			CodeTerm c = codeTerms.get(i);
			// String q = c.bestQualifiedName.replaceAll("\\(",
			// "").replaceAll("\\)", "");
			String q = c.bestQualifiedName;
			for (Pattern p : patterns) {
				for (Map.Entry<String, Boolean> entry : p.patternElements.entrySet()) {
					if (entry.getKey().equals(q)) {
						p.numberFound++;
						addToCoverageMap(p.coverageNames, c);
						if (entry.getValue() == false) {
							p.uniqueNumberFound++;
							entry.setValue(true);
						}
					}
				}
			}
		}
		for (Pattern p : patterns) {
			p.percentage = (double) p.uniqueNumberFound / p.patternElements.size();
		}
		for (Pattern p : patterns) {
			//System.out.println("Percentage of elements mentioned in pattern " + i + " is " + (p.percentage * 100) + "%.");
		}
	}

	// eliminate any pattern whose coverage is below 50%
	public void filterPatterns() {
		filteredPatterns = new ArrayList<Pattern>();
		for (Pattern p : patterns) {
			if (p.percentage > filterthreshold) {
				p.filtered = true;
				filteredPatterns.add(p);
			}
		}
	}

	// two code patterns are redundant if one is a subset of the other
	// and the relative difference in the size of their extension is within a
	// certain percentage threshold.
	public static void combinePatterns() {
		Pattern[] filteredPatternsArray = filteredPatterns.toArray(new Pattern[0]);
		Arrays.sort(filteredPatternsArray);
		ArrayList<Pattern> processed = new ArrayList<Pattern>();

		// Iterate through all filtered
		for (int i = 0; i < filteredPatternsArray.length; i++) {
			System.out.println("Currently combining Pattern "+i+" of "+filteredPatternsArray.length);
			Pattern pattern = filteredPatternsArray[i];
			if (processed.contains(pattern)) {
				continue;
			}
			processed.add(pattern);
			ArrayList<Pattern> combined_patterns = new ArrayList<Pattern>();
			combined_patterns.add(pattern);

			// Iterate through all
			for (int j = i + 1; j < filteredPatternsArray.length; j++) {
				Pattern tempPattern = filteredPatternsArray[j];
				if ((tempPattern.patternElements.size() / pattern.patternElements.size()) < combinethreshold)
					break;
				boolean isSubset = true;
				for (Map.Entry<String, Boolean> entry : tempPattern.patternElements.entrySet()) {
					if (!pattern.patternElements.containsKey(entry.getKey())) {
						isSubset = false;
						break;
					}
				}
				if (isSubset == true) {
					combined_patterns.add(tempPattern);
					processed.add(tempPattern);
				}
			}
			// make docPattern Representative Pattern
			Pattern docPattern = representativePattern(combined_patterns);
			docPatterns.add(docPattern);
		}
	}

	public static Pattern representativePattern(ArrayList<Pattern> combined_patterns) {
		// TODO: Make faster by using binary search from Arrays.
		// If only 1 element, return it
		if (combined_patterns.size() == 1)
			return combined_patterns.get(0);

		// Else sort based on highest % coverage
		Pattern[] combined_array = combined_patterns.toArray(new Pattern[combined_patterns.size()]);
		Arrays.sort(combined_array, new Comparator<Pattern>() {
			public int compare(Pattern o1, Pattern o2) {
				return (int) (o2.percentage * 100 - o1.percentage * 100);
			}
		});

		// If only 1 has highest coverage, return that
		if (combined_array[0].percentage > combined_array[1].percentage)
			return combined_array[0];

		// Else make a list of all having highest percentages
		ArrayList<Pattern> bestRep = new ArrayList<Pattern>();
		for (int i = 0; i < combined_array.length; i++) {
			if (combined_array[i].percentage < combined_array[0].percentage)
				break;
			bestRep.add(combined_array[i]);
		}

		// Sort the ones with highest percentage based on size of the pattern
		combined_array = bestRep.toArray(new Pattern[bestRep.size()]);
		Arrays.sort(combined_array, new Comparator<Pattern>() {
			public int compare(Pattern o1, Pattern o2) {
				return (int) (o2.patternElements.size() - o1.patternElements.size());
			}
		});

		// if only one has highest number of matches, return that
		if (combined_array[0].patternElements.size() > combined_array[1].patternElements.size())
			return combined_array[0];

		// Else make a list of all having highest number of matches
		bestRep = new ArrayList<Pattern>();
		for (int i = 0; i < combined_array.length; i++) {
			if (combined_array[i].patternElements.size() < combined_array[0].patternElements.size())
				break;
			bestRep.add(combined_array[i]);
		}

		// Sort the ones with highest percentage based on patternNumber
		combined_array = bestRep.toArray(new Pattern[bestRep.size()]);
		Arrays.sort(combined_array, new Comparator<Pattern>() {
			public int compare(Pattern o1, Pattern o2) {
				return (int) (o1.patternNumber - o2.patternNumber);
			}
		});
		return combined_array[0];
	}

	public static void linkPattern() {
		for (Pattern p : docPatterns) {
			for (Section section : p.coverageNames.values()) {
				if (section.codeTermNames.size() / (p.uniqueNumberFound) > linkthreshold)
					p.sections.add(section);
			}
			Section[] sections = p.sections.toArray(new Section[p.sections.size()]);
			Arrays.sort(sections);
			p.sections = new ArrayList<Section>();
			for (int i = 0; i < sections.length; i++) {
				if (p.sections.size() == 0)
					p.sections.add(sections[i]);
				else if (p.sections.get(p.sections.size() - 1).sectionNumber.indexOf(sections[i].sectionNumber) == -1)
					p.sections.add(sections[i]);
			}
		}
	}

	public static void doAll(String fullyqualifiedName) throws Exception {
		DocumentationPatterns documentationPatterns = new DocumentationPatterns();
		documentationPatterns.qualifiedName = fullyqualifiedName;
		long time = System.currentTimeMillis();
		documentationPatterns.computeCodePatterns();
		//System.out.println("Time taken for computeCodePatterns = " + (System.currentTimeMillis() - time) / 1000.0 + " seconds");
		time = System.currentTimeMillis();

		documentationPatterns.computeCodePatternCoverage();
		//System.out.println("Time taken for computeCodePatternCoverage = " + (System.currentTimeMillis() - time) / 1000.0 + " seconds");
		time = System.currentTimeMillis();

		documentationPatterns.filterPatterns();
		//System.out.println("Time taken for filterPatterns = " + (System.currentTimeMillis() - time) / 1000.0 + " seconds");
		time = System.currentTimeMillis();

		combinePatterns();
		//System.out.println("Time taken for combinePatterns = " + (System.currentTimeMillis() - time) / 1000.0 + " seconds");
		time = System.currentTimeMillis();
	}

	public static void main(String[] args) throws Exception {

		DocumentationPatterns.globalInit();

		// For Running on all classes - Slow right now, and memory inefficient.
		// Will need to use files atleast to write out patterns of each
		// individual class instead of keeping them in memory

		Collection<JavaClass> SourceClasses = DocumentationPatterns.builder.getClasses();
		Iterator<JavaClass> allClassesIterator = SourceClasses.iterator();
		for (int i = 0; i < SourceClasses.size(); i++) {
			JavaClass javaClass = allClassesIterator.next();
			String curClass = javaClass.getFullyQualifiedName();
			if(i%100==0)
				System.out.println("Currently Processing Class " + curClass + "Class Number - " + i + " Total - " + SourceClasses.size());
			DocumentationPatterns.doAll(curClass);
		}
		
		DocumentationPatterns.filteredPatterns = new ArrayList<Pattern>(DocumentationPatterns.docPatterns);
		docPatterns = new ArrayList<Pattern>();
		DocumentationPatterns.combinePatterns();
		System.out.println("Combining patterns done");
		linkPattern();
		System.out.println("Final Number of Patterns - "+ docPatterns.size());
		for (Pattern p : docPatterns) {
			System.out.println("Pattern - Size -"+ p.patternElements.size() + ", Number of Occureences - " + p.numberFound + ", Number of Unique Occurences - " + p.uniqueNumberFound + ", Percentage Coverage - " + p.percentage + ", " + "Linked Sections - " + p.sections.size()+  ", "+ " Sections are - " + p.sections + ", Pattern Elements are - "+p.patternElements);
		}

	}
}

class Pattern implements Comparable<Pattern> {
	public int patternNumber;
	public int numberFound = 0;
	public int uniqueNumberFound = 0;
	public double percentage;
	public HashMap<String, Boolean> patternElements = new HashMap<String, Boolean>();
	public HashMap<String, Section> coverageNames = new HashMap<String, Section>();
	public ArrayList<Section> sections = new ArrayList<>();
	public boolean filtered = false;

	public String toString() {
		return (patternNumber + ", " + patternElements.size() + ", " + numberFound + ", " + uniqueNumberFound + ", " + percentage + ", " + filtered);
	}

	public int compareTo(Pattern p) {
		int compareQuantity = p.patternElements.size();
		return (compareQuantity - this.patternElements.size());
	}
}

class Section implements Comparable<Section> {
	// I append a 0. to the beginning of the section number to signify the base
	// section
	public String sectionNumber;
	public int depth;
	HashSet<String> codeTermNames = new HashSet<String>();

	public String toString() {
		// return sectionNumber+", "+codeTermNames.size()+codeTermNames+";";
		return "Section - " + sectionNumber + " Size - " + codeTermNames.size();
	}

	public int compareTo(Section s) {
		if (s.sectionNumber == sectionNumber)
			return 0;
		if (s.sectionNumber.contains(sectionNumber))
			return 1;
		else if (sectionNumber.contains(s.sectionNumber))
			return -1;
		return sectionNumber.compareTo(s.sectionNumber);
	}
}

class MyClass implements Comparable<MyClass> {
	public JavaClass javaClass;
	public int classNumber;
	public int directDescSize = 0;
	public ArrayList<Integer> directSupers = new ArrayList<Integer>();
	public ArrayList<Integer> directDesc = new ArrayList<Integer>();
	public TreeSet<Integer> allDescendents = new TreeSet<Integer>();
	public TreeSet<String> allDot = new TreeSet<String>();

	public int compareTo(MyClass b) {
		return javaClass.getFullyQualifiedName().compareTo(b.javaClass.getFullyQualifiedName());
	}
}