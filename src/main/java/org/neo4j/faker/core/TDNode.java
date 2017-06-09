package org.neo4j.faker.core;

import java.util.Map;

public class TDNode {
	String label;
	String indexProp;
	Map<String,Object> props;
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getIndexProp() {
		return indexProp;
	}
	public void setIndexProp(String indexProp) {
		this.indexProp = indexProp;
	}
	public Map<String, Object> getProps() {
		return props;
	}
	public void setProps(Map<String, Object> props) {
		this.props = props;
	}
}
