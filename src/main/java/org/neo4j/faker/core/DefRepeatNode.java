package org.neo4j.faker.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefRepeatNode {
	private String repeatName;
	private String[] input;
	// A repeater can have multiple queries every query has a name (is part of the property name).
	private Map<String,String> cypherStatements = new HashMap<String,String>();
	// private Map<String,List<NodeIdentifier>> identifiers = new HashMap<String,List<NodeIdentifier>>();
	private Map<String,List<DefLUNode>> queryMap = new HashMap<String,List<DefLUNode>>();	

	public String getRepeatName() {
		return repeatName;
	}
	public void setRepeatName(String repeatName) {
		this.repeatName = repeatName;
	}
	public String[] getInput() {
		return input;
	}
	public void setInput(String[] input) {
		this.input = input;
	}
	public Map<String, String> getCypherStatements() {
		return cypherStatements;
	}
	public void setCypherStatements(Map<String, String> cypherStatements) {
		this.cypherStatements = cypherStatements;
	}
	public void clearIdentifiers() {
		queryMap = new HashMap<String,List<DefLUNode>>();	
	}

	
	

}
