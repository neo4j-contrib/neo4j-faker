package org.neo4j.faker.util;

import org.neo4j.configuration.Config;
import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.config.Setting;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TDGUtils {
	public static final String SUN_JAVA_COMMAND = "sun.java.command";
	public static final String DBMS_DIRECTORIES_NEO4J_PLUGINS = "dbms.directories.plugins";
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
	public static String getPluginDir(Config neoconfig) {
		String nhd = "./plugins";
		if (neoconfig != null) {
			if (neoconfig.getDeclaredSettings().containsKey(DBMS_DIRECTORIES_NEO4J_PLUGINS)) {
				nhd = neoconfig.get(neoconfig.getSetting(DBMS_DIRECTORIES_NEO4J_PLUGINS)).toString();
			}
		} else {
			// defaulting to system.command
			String sysCommand = System.getProperty(SUN_JAVA_COMMAND);
			int startPos = sysCommand.indexOf("--home-dir");
			if ( startPos > -1) {
				int toPos = sysCommand.indexOf(" --", startPos + 5);
				// on windows the --home-dir may be the last in the sysCommand
				String fragment = "";
				if (toPos > -1 ) {
					fragment = sysCommand.substring(startPos, toPos);
				} else {
					fragment = sysCommand.substring(startPos);
				}
				String[] fragmented = fragment.split("=");
				if (fragmented.length == 2) {
					nhd = fragmented[1].trim() + File.separator + "plugins";
				}
			}
		}
		//System.out.println("Found Plugin Dir " + nhd);
		return nhd;
	}
}
