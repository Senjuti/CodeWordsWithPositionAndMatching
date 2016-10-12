package docAnalysis;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.collections4.CollectionUtils;

import com.thoughtworks.qdox.*;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaConstructor;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.JavaType;

public class QDoxTest {
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
	
	public static String returnQualifiedName(JavaClass c, JavaConstructor m) {
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
	
	public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException {
		JavaProjectBuilder builder = new JavaProjectBuilder();
		String filepath = "/home/senjuti/Documents/CodeWordsWithPositionAndMatching/src/docAnalysis/http-paths.txt";
		Scanner s = new Scanner(new File(filepath));
		ArrayList<String> list = new ArrayList<String>();
		PrintWriter writer = new PrintWriter("/home/senjuti/Project/Pipeline/Class and Method Names List/HttpmethodSignaturesALL.txt", "UTF-8");
		
		while (s.hasNext()){
		    list.add(s.next());
		}
		s.close();
		
		for (String l : list) {
			JavaSource src = builder.addSource(new FileReader(l));
			JavaPackage pkg = src.getPackage(); 
			Collection<JavaClass> cls = src.getClasses();
			
			for (JavaClass c : cls) {
				JavaClass superclass = (JavaClass) c.getSuperClass(); 
				Collection<JavaClass> imps = c.getInterfaces();
				Collection<JavaClass> de = c.getDerivedClasses();

				if (de != null) {
					for (JavaClass d : de) {
						Collection<JavaMethod> methodNamesDe = d.getMethods(true);
						for(JavaMethod m : methodNamesDe) {
							writer.println(returnQualifiedName(d, m)); 
				        }
					}
				}
				 
				if (imps != null) {
					for (JavaClass imp : imps) {
						Collection<JavaMethod> methodNamesInterface = imp.getMethods();
						Collection<JavaConstructor> constructorNamesInterface = imp.getConstructors();
						Collection<JavaField> fieldNamesInterface = imp.getFields();
						
						for(JavaMethod m : methodNamesInterface) {
							writer.println(returnQualifiedName(c, m)); 
				        }

						for(JavaConstructor co : constructorNamesInterface) {
							writer.println(returnQualifiedName(c, co)); 
				        }

						for(JavaField f : fieldNamesInterface) {
							writer.println(pkg.getName()+"."+c.getName()+"."+f.getName()); 
				        }
					}
				}
				
				Collection<JavaMethod> methodNames = c.getMethods();
				Collection<JavaConstructor> constructorNames = c.getConstructors();
				Collection<JavaField> fieldNames = c.getFields();
				
				if (superclass != null) {
					Collection<JavaMethod> methodNamesSuperclass = superclass.getMethods();
					Collection<JavaConstructor> constructorNamesSuperclass = superclass.getConstructors();
					Collection<JavaField> fieldNamesSuperclass = superclass.getFields();
					
					for(JavaMethod m : methodNamesSuperclass) {
						writer.println(returnQualifiedName(c, m)); 
						
			        }
					for(JavaConstructor co : constructorNamesSuperclass) {
						writer.println(returnQualifiedName(c, co)); 
			        }
					for(JavaField f : fieldNamesSuperclass) {
						writer.println(pkg.getName()+"."+c.getName()+"."+f.getName()); 
			        }
				}
				int defaultConstructorMentioned = 0; 
				for(JavaMethod m : methodNames) {
					writer.println(returnQualifiedName(c, m)); 
		        }
				for(JavaConstructor co : constructorNames) {
					if (co.getName().equals(c.getName())) {
						defaultConstructorMentioned++;
					}
					writer.println(returnQualifiedName(c, co)); 
		        }
				//if default constructor is never explicitly mentioned
				if (defaultConstructorMentioned == 0) {
					writer.println(pkg.getName()+"."+c.getName()+"."+c.getName()+"()"); 
				}
				for(JavaField f : fieldNames) {
					writer.println(pkg.getName()+"."+c.getName()+"."+f.getName()); 
		        }
				
				if (c.getName().contains("HttpPost")) {
					System.out.println(c.getName());
					System.out.println("DERIVED CLASSES");
					for (JavaClass d : de) {
						System.out.println(d);
					}
					System.out.println("METHODS IN DERIVED CLASSES");
					for (JavaClass d : de) {
						Collection<JavaMethod> methodNamesDe = d.getMethods(true);
						for(JavaMethod m : methodNamesDe) {
							String resolvedName = d.getClassNamePrefix().replaceAll("\\$", ""); 
							System.out.println(resolvedName+"."+m.getName()+"()"); 
				        }
					}
					System.out.println("SUPERCLASS");
					System.out.println(superclass);
					System.out.println("METHODS IN SUPERCLASS");
					Collection<JavaMethod> methodNamesSuperclass = superclass.getMethods();
					for(JavaMethod m : methodNamesSuperclass) {
						System.out.println(pkg.getName()+"."+c.getName()+"."+m.getName()+"()"); 
			        }
					System.out.println("INTERFACES IMPLEMENTED");
					System.out.println(imps);
					System.out.println("METHODS IN INTERFACES");
					for (JavaClass imp : imps) {
					Collection<JavaMethod> methodNamesInterface = imp.getMethods();
						for(JavaMethod m : methodNamesInterface) {
							System.out.println(pkg.getName()+"."+c.getName()+"."+m.getName()+"()"); 
				        }
						System.out.println("METHODS");
						System.out.println(methodNames);
					}
				}
			}
		}
		writer.close();
		
	}
}
