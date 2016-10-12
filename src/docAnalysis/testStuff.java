package docAnalysis;

public class testStuff {
	public static void main(String[] args) {
		//We want URIUtils#resolve()
		String name = "URIUtils.resolve"; 
		int cntDots = 0;
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (c == '.')
				cntDots++;
		}
		
		System.out.println(cntDots);
		if (cntDots == 1 && name.indexOf('(') == -1)
			name = name + "()";
	
		System.out.println(name);
		
		String d = "org.apache.http.impl.client.CloseableHttpClient.doExecute(HttpHost, HttpRequest, HttpContext)";
		String c = "org.apache.http.impl.client.CloseableHttpClient.doExecute()";
		c = c.replaceAll("\\(", "").replaceAll("\\)", "");
		System.out.println(c);
		if (d.contains(c))
			System.out.println("True");
		else
			System.out.println("False");
		int pattern[] = new int[8];
		for (int i : pattern)
			System.out.println(i);
		pattern[0]++;
		for (int i : pattern)
			System.out.println(i);
	}
}
