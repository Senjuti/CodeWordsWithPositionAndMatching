package docAnalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;

public class testStereotype {
	
	public static void main(String[] args) throws IOException {
		String pathQualName = "/home/senjuti/Documents/CodeWordsWithPositionAndMatching/src/docAnalysis/SpringBestQualifiedName.txt"; 
		BufferedReader br = new BufferedReader(new FileReader(pathQualName)); 
		String pathClassPaths = "/home/senjuti/Documents/CodeWordsWithPositionAndMatching/src/docAnalysis/SpringClassPath.txt"; 
		BufferedReader br2 = new BufferedReader(new FileReader(pathClassPaths)); 
		
		String line = br.readLine();
		String line2 = br2.readLine(); 
		JavaProjectBuilder builder = new JavaProjectBuilder();
		
		String stereoPath = "/home/senjuti/Documents/CodeWordsWithPositionAndMatching/src/docAnalysis/SpringStereotype.txt"; 
		BufferedWriter bw = new BufferedWriter(new FileWriter(stereoPath)); 
		
		while (line != null) {
			JavaSource src = builder.addSource(new FileReader(line2));
			JavaClass cls = builder.getClassByName(line);
			
			String stereotype = ""; 
			List<DocletTag> returns = cls.getTagsByName("stereotype");
			System.out.println("returns="+ returns); 
			if (returns != null) {
				for (DocletTag r : returns) {
					stereotype = r.getValue(); 
					System.out.println("r.getValue()="+ r.getValue()); 
				}
			}
			System.out.println("stereotype="+stereotype);
			bw.write(line2 + ", " + line + ", " + stereotype);
			bw.newLine();
			
			line = br.readLine(); 
			line2 = br2.readLine();
		}		
		
		bw.close();
	}
}
