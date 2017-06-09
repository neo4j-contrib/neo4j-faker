package org.neo4j.faker.core;

public class TDGVersion {

	public static void main(String[] args) {
		op(" version " + TDGConstants.VERSION);
	}
	private static void op(String s) {
		System.out.println("TestDataGen:" + s);
	}

}
