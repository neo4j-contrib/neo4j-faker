package org.neo4j.faker.core;

import java.util.Map;

public class DefRel {
	private String name;
	private String startIdentifier;
	private String endIdentifier;
	private String relType;
	private String cardinality;
	private int max = 100;
	Map<String,String> propDef;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStartIdentifier() {
		return startIdentifier;
	}
	public String getStartIdentifierPrefix() {
		if (startIdentifier.indexOf(".") > 0) {
			
			return startIdentifier.substring(0,startIdentifier.indexOf("."));
		} else {
			return startIdentifier;
		}
	}
	public String getStartColumn() {
		if (startIdentifier.indexOf(".") > 0) {
			return startIdentifier.substring(startIdentifier.indexOf(".") +1);
		} else {
			return "";
		}
	}
	public void setStartIdentifier(String startIdentifier) {
		this.startIdentifier = startIdentifier;
	}
	public String getEndIdentifier() {
		return endIdentifier;
	}
	public String getEndIdentifierPrefix() {
		if (endIdentifier.indexOf(".") > 0) {
			return endIdentifier.substring(0,endIdentifier.indexOf("."));
		} else {
			return endIdentifier;
		}
	}
	public String getEndColumn() {
		if (endIdentifier.indexOf(".") > 0) {
			return endIdentifier.substring(endIdentifier.indexOf(".") +1);
		} else {
			return "";
		}
	}
	public void setEndIdentifier(String endIdentifier) {
		this.endIdentifier = endIdentifier;
	}
	public String getRelType() {
		return relType;
	}
	public void setRelType(String relType) {
		this.relType = relType;
	}
	public String getCardinality() {
		return cardinality;
	}
	public void setCardinality(String cardinality) {
		this.cardinality = cardinality;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public Map<String, String> getPropDef() {
		return propDef;
	}
	public void setPropDef(Map<String, String> propDef) {
		this.propDef = propDef;
	}
	
}
