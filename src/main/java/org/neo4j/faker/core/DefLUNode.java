package org.neo4j.faker.core;

import java.util.*;

public class DefLUNode {
	String lookup;
	String cypher;
	int resultSize;
	Map<String,Map<String,String>> propDef = new HashMap<String,Map<String,String>>();
	Map<String,List<NodeIdentifier>> nodeMap = new HashMap<String,List<NodeIdentifier>>();	
	public int getResultSize() {
		return resultSize;
	}
	public void setResultSize(int resultSize) {
		this.resultSize = resultSize;
	}
	public String getLookup() {
		return lookup;
	}
	public void setLookup(String lookup) {
		this.lookup = lookup;
	}
	public String getCypher() {
		return cypher;
	}
	public void setCypher(String cypher) {
		this.cypher = cypher;
	}
	public Map<String,String> getPropDef(String column) {
		return propDef.get(column);
	}
	public void setPropDef(String column, Map<String, String> propDef) {
	    this.propDef.put(column, propDef);
	}
	public List<NodeIdentifier> getNodes(String column) {
		return nodeMap.get(column);
	}
	public void addNodeIdentifier(String column, NodeIdentifier ni) {
		List<NodeIdentifier> nilist;
		if (nodeMap.containsKey(column)) {
			nilist = nodeMap.get(column);
		} else {
			nilist = new ArrayList<NodeIdentifier>();
			nodeMap.put(column,nilist);
		}
		nilist.add(ni);
	}
	public List<NodeIdentifier> getNodeMap(String column) {
		return nodeMap.get(column);
	}
	public void addToPropDef(String column, String prop, String value) {
		System.out.println("addToPropDef col:" + column + ":prop:" + prop + ":" + value);
		Map<String,String> prpDef;
		if (!propDef.containsKey(column)) {
			prpDef = new HashMap<String,String>();
			propDef.put(column,prpDef);
		} else {
			prpDef = propDef.get(column);
		}
		prpDef.put(prop, value);
	}
	public Set<String> getColumns() {
		return propDef.keySet();
	}
	public void shuffle() {
		
		long seed = System.nanoTime();
		Random rnd = new Random(seed);
		for (String key : nodeMap.keySet() ) {
			List<NodeIdentifier> cur = nodeMap.get(key);
			Collections.shuffle(cur,rnd);
		}
	}
}
