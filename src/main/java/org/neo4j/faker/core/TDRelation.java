package org.neo4j.faker.core;

import java.util.Map;

public class TDRelation {
	NodeIdentifier from;
	NodeIdentifier to;
	String relType;
	public String getRelType() {
		return relType;
	}
	public void setRelType(String relType) {
		this.relType = relType;
	}
	Map<String,Object> props;
	public NodeIdentifier getFrom() {
		return from;
	}
	public void setFrom(NodeIdentifier from) {
		this.from = from;
	}
	public NodeIdentifier getTo() {
		return to;
	}
	public void setTo(NodeIdentifier to) {
		this.to = to;
	}
	public Map<String, Object> getProps() {
		return props;
	}
	public void setProps(Map<String, Object> props) {
		this.props = props;
	}
}
