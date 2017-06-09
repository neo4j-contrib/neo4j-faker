package org.neo4j.faker.core;

public class NodeIdentifier implements Comparable<NodeIdentifier>{
	long id;
	String indexValue;
	String label;
	String indexProp;
	public NodeIdentifier(String label, String indexProp, String indexValue ) {
		this.indexValue = indexValue;
		this.label = label;
		this.indexProp = indexProp;
	}
	public String getIndexProp() {
		return indexProp;
	}
	public NodeIdentifier(long id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public long getId() {
		return id;
	}
	public String getIndexValue() {
		return indexValue;
	}
	@Override
	public int compareTo(NodeIdentifier o) {
		Long tl = new Long(id);
		long cl = new Long(o.getId());
		return tl.compareTo(cl);
	}
	
}
