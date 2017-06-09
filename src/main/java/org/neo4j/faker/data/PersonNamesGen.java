package org.neo4j.faker.data;

import org.neo4j.faker.util.TDGUtils;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class PersonNamesGen {
	String firstNamesFile = "First_Names.csv";
	String lastNamesFile = "Last_Names.csv";
	List<String> firstNames;
	List<String> lastNames;
	Random randomF = new Random();
	Random randomL = new Random();
	//int step = 15;
	public PersonNamesGen(String TDGRoot) throws IOException {

		//System.out.println("Constructor NamesGen");
		firstNames = TDGUtils.readResourceFile(TDGRoot,firstNamesFile);
		lastNames = TDGUtils.readResourceFile(TDGRoot,lastNamesFile);
		//System.out.println(" loaded " + firstNames.size() + " firstNames and " + lastNames.size() + " lastnames" );
	}
	public String firstName() {
//		String fn = firstNames.get(randomF.nextInt(firstNames.size() -1));
//		fIndex++;
//		if (fIndex >= firstNames.size()) {
//			fIndex = 0;
//			//System.out.println(" firstname offset ");
//		}
		return  firstNames.get(randomF.nextInt(firstNames.size() -1));
	}
	public String lastName() {
//		String n = lastNames.get(lIndex);
//		lIndex++;
//		if (lIndex >= lastNames.size()) {
//			lIndex = 0;
//		}
		return lastNames.get(randomL.nextInt(lastNames.size() -1));
	}
	public String fullName() {
		return firstName() + " " + lastName();
	}

	
	
	
	
	private int getStart(int length) {
		String s = "" + System.currentTimeMillis();
		s = s.substring(s.length() - length);
		// System.out.println(" getStart " + s);
		return Integer.valueOf(s);
	}
}
