package org.neo4j.faker.core;

public class TDGConstants {
	public static final String PROP_COMMIT_SIZE = "tdg.commit.size";
	public static final String PROP_NODE_LIST = "tdg.node.list"; 
	public static final String PROP_NODE_PROPS_PREFIX = "tdg.node.props.";
	public static final String PROP_REL_LIST = "tdg.rel.list";
	public static final String PROP_REL_DEF_PREFIX = "tdg.rel.def.";
	public static final String PROP_REL_PROPS_PREFIX = "tdg.rel.props.";
	public static final String PROP_LOOKUP_PROPS_PREFIX = "tdg.lookup.props.";
	public static final String PROP_LOOKUP_CYPHER_PREFIX ="tdg.lookup.cypher.";
	public static final String PROP_LOOKUP_LIST = "tdg.lookup.nodes.list";
	public static final String CDN_ONE_TO_MANY = "1-n";
	public static final String CDN_MANY_TO_ONE = "n-1";
	public static final String CDN_MANY_TO_MANY = "n-n";
	public static final String CDN_ONE_TO_ONE = "1-1";
	public static final String PROP_REPEATER_LIST = "tdg.repeatnode.list";
	public static final String PROP_REPEATER_INPUT_PREFIX = "tdg.repeatnode.input.";
	public static final String PROP_REPEATER_CYPHER_PREFIX = "tdg.repeatnode.cypher.";
	public static final String PROP_REP_REL_LIST = "tdg.rel.repeat.list";
	public static final String PROP_REP_REL_DEF_PREFIX = "tdg.rel.repeat.def.";
	public static final String PROP_REP_REL_PROPS_PREFIX = "tdg.rel.repeat.props.";
	public static final String PROP_SWITCH_INDEX_ON_OFF = "tdg.tdgid.index"; // possible values on | off
	public static final String PROP_POST_STATEMENTS_PREFIX="tdg.post.processing.cypher.";
	public static final String PROP_PRE_STATEMENTS_PREFIX="tdg.pre.processing.cypher.";
	public static final String PROP_START_COMMENT="tdg.comment.start";
	public static final String PROP_END_COMMENT="tdg.comment.end";
	public static final String VERSION = "0.9.0";
}
