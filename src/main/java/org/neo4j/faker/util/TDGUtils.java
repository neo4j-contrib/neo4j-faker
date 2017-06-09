package org.neo4j.faker.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TDGUtils {
	public static String getResourceFilePath(String TDGRoot, String fileName) {
		//String home = System.getProperty("neo4j.home");
		//if (home == null || home.equals("null")) {
		return TDGRoot 
		 + System.getProperty("file.separator") 
		 + "ddgres" 
		 + System.getProperty("file.separator") 
		 + fileName;
	}
	public static List<String> readFile(File fin) throws IOException {
		List<String> list = new ArrayList<String>();
		FileInputStream fis = new FileInputStream(fin);
	 
		//Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	 
		String line = null;
		while ((line = br.readLine()) != null) {
			list.add(line.trim());
		}
	 
		br.close();
		return list;
	}
	public static List<String> readResourceFile(String TDGRoot, String fileName) throws IOException {
		File f = new File(getResourceFilePath(TDGRoot, fileName));
		return readFile(f);
	}
	public static Properties loadProperties(String TDGRoot,String propFile) throws FileNotFoundException, IOException {
		// get the properties
		Properties props = new Properties();
		props.load(new FileReader(TDGUtils.getResourceFilePath(TDGRoot, propFile)));
		return props;
	}

}
