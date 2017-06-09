package org.neo4j.faker.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefNode {
	String label;
	String alias;
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public static final String INDEX = "tdg_id";
	int amount;
	Map<String,String> propDef;
	List<NodeIdentifier> nodes = new ArrayList<NodeIdentifier>();
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public Map<String, String> getPropDef() {
		return propDef;
	}
	public void setPropDef(Map<String, String> propDef) {
		this.propDef = propDef;
	}
	public List<NodeIdentifier> getNodes() {
		return nodes;
	}
	public void setNodes(List<NodeIdentifier> nodes) {
		this.nodes = nodes;
	}
}
