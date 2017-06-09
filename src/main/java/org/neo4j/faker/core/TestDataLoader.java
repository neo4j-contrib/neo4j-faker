package org.neo4j.faker.core;

import org.neo4j.faker.data.PropertyParser;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;
import java.util.Map.Entry;


public class TestDataLoader {
	
	GraphDatabaseService database;
	Properties props;
	List<Comment> output;
	OutputStreamWriter out;
	Writer writer;
	PropertyParser propertyParser;
	DBLoader dbl;
	boolean useIndex = true; // default on
	public boolean isUseIndex() {
		return useIndex;
	}
	// counters
	long nodeCount = 0;
	long propertyCount = 0;
	long relCount = 0;
	List<DefNode> ndef;
	List<DefLUNode> ludef; 
	String TDGRoot = null;
	
	public String getTDGRoot() {
		return TDGRoot;
	}
	public List<DefNode> getNdef() {
		return ndef;
	}
	public List<DefLUNode> getLudef() {
		return ludef;
	}
	public GraphDatabaseService getDatabase() {
		return database;
	}
	public Properties getProps() {
		return props;
	}
	public OutputStreamWriter getOut() {
		return out;
	}
	public Writer getWriter() {
		return writer;
	}
	public PropertyParser getPropertyParser() {
		return propertyParser;
	}
	public DBLoader getDbl() {
		return dbl;
	}
	public long getNodeCount() {
		return nodeCount;
	}
	public long getPropertyCount() {
		return propertyCount;
	}
	public long getRelCount() {
		return relCount;
	}
	public TestDataLoader(GraphDatabaseService database, Properties genProperties,Writer writer, String TDGRoot) {
		this.database = database;
		this.props = genProperties;
		this.writer = writer;
		this.TDGRoot = TDGRoot;
	}
	public TestDataLoader(GraphDatabaseService database, Properties genProperties,List<Comment> outl, String TDGRoot) {
		this.database = database;
		this.props = genProperties;
		this.output = outl;
		this.TDGRoot = TDGRoot;
	}
	public TestDataLoader(GraphDatabaseService database, Properties genProperties, String TDGRoot) {
		this.database = database;
		this.props = genProperties;
		this.TDGRoot = TDGRoot;
	}
	public void loadData() throws Exception {
		long tStart = System.currentTimeMillis();
		int commitSize = Integer.valueOf(props.getProperty(TDGConstants.PROP_COMMIT_SIZE, "1000"));
		String startComment = "* " + new Date() + " start :" + props.getProperty(TDGConstants.PROP_START_COMMENT, " ");
		out("*******************************************************************************************");
		out(startComment);
		out("*******************************************************************************************");
		
		out(" Commit size " + commitSize);
		executePreProcessingCypherStateMents();
		
		
		out(" Reading node list ");
		//
		// Checking the use of the index
		//
		String switchIndex = props.getProperty(TDGConstants.PROP_SWITCH_INDEX_ON_OFF, "on"); // the default is that the index is used
		useIndex = !switchIndex.equalsIgnoreCase("off");
		out(" tgd_id INDEX : " + useIndex);
		//
		// Reading node definitions
		//
		ndef = getNodeDefs();
		//
		// reading lookup nodes
		//
		out(" Reading lookup nodes");
		ludef = getLookUpNodeDef();
		//
		// Reading relationd definitions
		//
		out(" Reading relation definitions ");
		List<DefRel> reldef = getRelationDefinitions();
		//
		// Get the lookup nodes
		//
		for (DefLUNode lud : ludef) {
			
			out(" executing " + lud.getLookup() + "  cypher query " + lud.getCypher());
			Result res = database.execute(lud.getCypher());
			// out(" qu sery result " + res.dumpToString());
			out(" The return columns:  " + res.columns());
			int size = 0; 
			while (res.hasNext()){
				// for (Map<String,Object> row : res) 
				Map<String,Object> row = res.next();
				size++;
				for ( Entry<String, Object> col : row.entrySet() )
			    {
					Node n = (Node) col.getValue();
			        lud.addNodeIdentifier(col.getKey(), new NodeIdentifier(n.getId()));
			    }
			}
			lud.setResultSize(size);
			out(" processing lookup " + lud.getLookup() + " found " + size + " results " );
			lud.shuffle(); // shuffles all nodes in the list for every key
		}
        //
		// init property parser and DBLoader
		//
		propertyParser = new PropertyParser( writer, TDGRoot);
		dbl = new DBLoader(this,commitSize);
		//
		// When there are lookup nodes and there are property definitions
		// then the properties must be set.
		//
		if (ludef != null && ludef.size() > 0) {
			for (DefLUNode dln: ludef) {
				// now per column we have to set the propertys
				for (String column: dln.getColumns()) {
					// out(" lookup node propdef " + dln.getPropDef());
					if (dln.getResultSize() > 0 && dln.getPropDef(column) != null && dln.getPropDef(column).size() > 0) {
						List<NodeIdentifier> nodeMap = dln.getNodeMap(column);
						for (NodeIdentifier node: nodeMap) {
							Map<String,Object> props = new HashMap<String,Object>();
							propertyParser.parse(props,dln.getPropDef(column));
							// now add to the dbloader
							dbl.setNodeProperties(node,props);
							propertyCount = propertyCount + props.size();
						}
					}
				}
			}
		}
		dbl.flush(); // save now the node properties
		//
		// create node section
		//
		TDNode tdn = null;
		UUID uuid = null;
		for (DefNode df: ndef) {
			out(" found node -alias " + df.getAlias() + " -label:" + df.getLabel() + " -indexProperty: " + df.INDEX+ " -amount:" + df.getAmount());
			// create index tbd
			if (useIndex) dbl.createIndex(df.getLabel(), df.INDEX);
			// create nodes
			for (int i = 0; i < df.getAmount(); i++) {
				tdn = new TDNode();
				tdn.setLabel(df.getLabel());
				tdn.setIndexProp(df.INDEX);
				Map<String,Object> props = new HashMap<String,Object>();
				// for testing normally we readout now a property to have all the 
				// properties and their types
				uuid = UUID.randomUUID();
				NodeIdentifier ni = new NodeIdentifier(df.getLabel(), tdn.getIndexProp(),uuid.toString());
				// setting the node
				df.getNodes().add(ni); 
				props.put(df.INDEX, uuid.toString());
				propertyParser.parse(props,df.getPropDef());
				tdn.setProps(props);
				if (tdn.getProps() != null) propertyCount = propertyCount + tdn.getProps().size();
				dbl.createNode(tdn);
				nodeCount++;
			}
			dbl.flush();
		}		
		//
		// We have now all the nodes, now we have tp processs the relations
		//
		for (DefRel rd : reldef) {
			// get the start nodes or node id
			// get the end nodes or node id
			out(" processing relation "  + rd.getName());
			out("  START node identifier " + rd.getStartIdentifier());
			out("  END node identifier " + rd.getEndIdentifier());
			Object startDef = null;
			Object endDef = null;
			// check the list of node definitions
            if (useIndex) {
				for (DefNode dnode : ndef) {
					if (dnode.getAlias().equals(rd.getStartIdentifier())) startDef = dnode;
					if (dnode.getAlias().equals(rd.getEndIdentifier())) endDef = dnode;
					if (startDef != null && endDef != null) break;
				}
            }
			// check the lookup nodes
			if (startDef == null || endDef == null) {
				for (DefLUNode dnode : ludef) {
					if (dnode.getLookup().equals(rd.getStartIdentifierPrefix())) startDef = dnode;
					if (dnode.getLookup().equals(rd.getEndIdentifierPrefix())) endDef = dnode;
					if (startDef != null && endDef != null) break;
				}
			}
			if (startDef == null || endDef == null) {
			    out("  WARNING no start or end identifier found for this relation this relation will not be created!");
			    continue;
			}
			//
			// Now we have a start and end node now we can process it
			//
			if (rd.getCardinality().equals(TDGConstants.CDN_ONE_TO_MANY)) {
				processOneToMany(rd,getNodeIdentifiers(startDef,rd.getStartColumn()),getNodeIdentifiers(endDef,rd.getEndColumn()));
			} else if (rd.getCardinality().equals(TDGConstants.CDN_MANY_TO_ONE)) {
				processManyToOne(rd,getNodeIdentifiers(startDef,rd.getStartColumn()),getNodeIdentifiers(endDef,rd.getEndColumn()));
			} else if (rd.getCardinality().equals(TDGConstants.CDN_MANY_TO_MANY)) {
				processManyToMany(rd,getNodeIdentifiers(startDef,rd.getStartColumn()),getNodeIdentifiers(endDef,rd.getEndColumn()));
			} else if (rd.getCardinality().equals(TDGConstants.CDN_ONE_TO_ONE)) {
				processOneToOne(rd,getNodeIdentifiers(startDef,rd.getStartColumn()),getNodeIdentifiers(endDef,rd.getEndColumn())); 
			} else {
				out("  WARNING unsupported cardinality "+ rd.getCardinality() +" this relation will not be created!");
			}
		}
		// flushing remaining transactions.
		dbl.flush();
		//
		// Now we have to process the repeat nodes
		//
		RepeatNodesProcessor prcRepeat  = new RepeatNodesProcessor( this);
		prcRepeat.process();
		
		// flushing remaining transactions.
		dbl.flush();
		//
		// Handle post processing cypher statements 
		//
		executePostProcessingCypherStateMents();
		
		long tEnd = System.currentTimeMillis();
		out(" nodeCount " + nodeCount);
		out(" relation count " + relCount);
		out(" property count " + propertyCount);
		out(" finished total time was (ms): " + (tEnd - tStart));
		out(" load speed node and relations per second: " + ( ( (nodeCount + relCount) / (tEnd - tStart) ) * 1000d ) );
		String endComment = "* " + new Date() + " END :" + props.getProperty(TDGConstants.PROP_END_COMMENT, " ");
		out("*******************************************************************************************");
		out(endComment);
		out("*******************************************************************************************");

	}
	private void executePostProcessingCypherStateMents() throws IOException {
		// max is 10000
		for (int i=1 ; i<10000;i++) {
			String cypher = props.getProperty(TDGConstants.PROP_POST_STATEMENTS_PREFIX + i,"");
			if (cypher.equals("")) break;
			out(" ============== post cypher query " + i + "===================");
			Result er = database.execute(cypher);
			out(er.resultAsString());
		}
	}
	private void executePreProcessingCypherStateMents() throws IOException {
		// max is 10000
		for (int i=1 ; i<10000;i++) {
			String cypher = props.getProperty(TDGConstants.PROP_PRE_STATEMENTS_PREFIX + i,"");
			if (cypher.equals("")) break;
			out(" ============== pre cypher query " + i + "===================");
			Result er = database.execute(cypher);
			out(er.resultAsString());
		}
	}
	public void processOneToMany(DefRel dr, List<NodeIdentifier>  startNID, List<NodeIdentifier>  endNID) throws Exception {
		if (startNID == null || endNID == null) return;
		if (startNID.size() == 0 || endNID.size() == 0 ) return;
		
		// The "end node" may have max one relation from the start node 
		// we loop here through the end nodes and every end node may get a relation to the start node
		// for determining the start node we use a randomizer. 
		// for future maybe we use a 90% of the end nodes will get a relation
		out("  processing " + dr.getStartIdentifier() + " one to many " + endNID.size() + " - "+ dr.getEndIdentifier());
		Random random = new Random();
		for (NodeIdentifier endnid : endNID) {
			// pick a start node
			NodeIdentifier startnid = startNID.get(random.nextInt(startNID.size()));
			TDRelation tdr = prepareRelation(dr,startnid,endnid);
			if (tdr.getProps() != null) propertyCount = propertyCount + tdr.getProps().size();
			relCount++;
			dbl.createRelation(tdr);
		}
	}
	private TDRelation prepareRelation(DefRel dr, NodeIdentifier startnid, NodeIdentifier endnid) throws Exception {
		TDRelation tdr = new TDRelation();
		tdr.setFrom(startnid);
		tdr.setTo(endnid);
		tdr.setRelType(dr.getRelType());
		// parse properties
		Map<String,Object> props = new HashMap<String,Object>();
		propertyParser.parse(props,dr.getPropDef());
		tdr.setProps(props);
		return tdr;
	}
	
	
	public void processManyToOne(DefRel dr, List<NodeIdentifier>  startNID, List<NodeIdentifier>  endNID) throws Exception {
		if (startNID == null || endNID == null) return;
		if (startNID.size() == 0 || endNID.size() == 0 ) return;
		// The "start node" may have max one relation to the end node
		// we loop here through the start nodes and pick a random end node to connect to
		out("  processing " + dr.getStartIdentifier() + " many to one " + startNID.size() + " - "+ dr.getEndIdentifier());

		Random random = new Random(System.currentTimeMillis());
		for (NodeIdentifier startnid : startNID) {
			NodeIdentifier endnid = endNID.get(random.nextInt(endNID.size()));
			TDRelation tdr = prepareRelation(dr, startnid, endnid);
			if (tdr.getProps() != null) propertyCount = propertyCount + tdr.getProps().size();
			relCount++;
			dbl.createRelation(tdr);
		}
	}
	public void processManyToMany(DefRel dr, List<NodeIdentifier>  startNID, List<NodeIdentifier>  endNID) throws IOException {
		if (startNID == null || endNID == null) return;
		if (startNID.size() == 0 || endNID.size() == 0 ) return;
		// The "end node" may have many relations from the start node and the "start node" may have many relations to the end node.
		// if these objects are the same we must work with a clone
		// now for every startnode there will be an 
		out(" many to many not implemented yet, try to use a double relation definition 1 to many the start node and endnodes must be switched in the definitions");
		
	}
	public void processOneToOne(DefRel dr, List<NodeIdentifier>  startNID, List<NodeIdentifier>  endNID) throws Exception {
		if (startNID == null || endNID == null) return;
		if (startNID.size() == 0 || endNID.size() == 0 ) return;
		// The "end node" may have one relation from the start node and the "start node" may have one relation to the end node
		// check the smallest set
		out("  processing " + dr.getStartIdentifier() + " one to one " + startNID.size() + " - "+ dr.getEndIdentifier());
		if (endNID.equals(startNID)) {
			out(" a self reference !");
			endNID = new ArrayList<NodeIdentifier>();
			for (NodeIdentifier nid : startNID) {
				endNID.add(nid);
			}
			// shuffle only by a self reference
			Collections.shuffle(endNID);
			
		}
		// shuffle one of the lists
		if (startNID.size() < endNID.size()) {
			// looping through startNID list
			int ia = 0;
			for (NodeIdentifier startnid: startNID) {
				NodeIdentifier endnid = endNID.get(ia);
				ia++;
				if (endnid.equals(startnid)) continue; // skip this one
				TDRelation tdr = prepareRelation(dr, startnid, endnid);
				if (tdr.getProps() != null) propertyCount = propertyCount + tdr.getProps().size();
				relCount++;
				dbl.createRelation(tdr);
				
			}
		} else {
			int ib = 0;
			
			for (NodeIdentifier endnid: endNID) {
				NodeIdentifier startnid = startNID.get(ib);
				ib++;
				if (endnid.equals(startnid)) continue; // skip this one
				TDRelation tdr = prepareRelation(dr, startnid, endnid);
				if (tdr.getProps() != null) propertyCount = propertyCount + tdr.getProps().size();
				relCount++;
				dbl.createRelation(tdr);
			}
		}
	}
	public List<NodeIdentifier> getNodeIdentifiers(Object def, String column) throws IOException {
		if (def instanceof DefNode) return ((DefNode) def).nodes;
		if (def instanceof DefLUNode) return ((DefLUNode) def).getNodes(column);
		return null;
	}
	public void out(String line) throws IOException {
		if (writer != null) {
			writer.write(line);
			writer.write("\n");
			writer.flush();
		} else if (output != null){ 
			output.add( new Comment(line));
			System.out.println(line);
		} else {
			System.out.println(line);
		}
	}
	private List<DefLUNode> getLookUpNodeDef() throws IOException {
		List<DefLUNode> list = new ArrayList<DefLUNode>();
		String val = props.getProperty(TDGConstants.PROP_LOOKUP_LIST,"");
		if (!val.isEmpty()) {
			out("getLookUpNodeDef() lookup list " + val);
			String[] els = val.split(",");
			//out("els " + els.length);
			for (String ef: els) {
				// the column is before the propery
				String[] cp = ef.split(",");
				DefLUNode def = new DefLUNode();
				def.setLookup(ef);
				// reading 
				def.setCypher(props.getProperty(TDGConstants.PROP_LOOKUP_CYPHER_PREFIX + ef));
				// reading properties definitions
				String propCheck = TDGConstants.PROP_LOOKUP_PROPS_PREFIX+ def.getLookup() + ".";
				
				for (Object pkey : props.keySet() ){
					String skey = (String) pkey;
					if (skey.startsWith(propCheck)) {
						// aha we found a property
						String prop = skey.substring(propCheck.length());
						// the propery must have a . in it the notations is column.property
						String[] splitted = prop.split("\\.");
						out(" found property " + splitted[1] + " for lookup " + def.getLookup() + "." + splitted[0]);
						def.addToPropDef(splitted[0], splitted[1], (String) props.get(skey));
						//pmap.put(prop, (String) props.get(skey));
					}
				}
				//def.setPropDef(pmap);
				list.add(def);
			}
		}
		return list;
	}
	
	private List<DefNode> getNodeDefs() throws IOException{
		List<DefNode> list = new ArrayList<DefNode>();
		String val = props.getProperty(TDGConstants.PROP_NODE_LIST);
		if (val != null && !val.isEmpty()) {
			// parse the value
			String[] els = val.split(",");
			for (String elm: els) {
				String[] vals = elm.split(":");
				DefNode df = new DefNode();
				out(" processing node definition " + elm);
				out("  vals.length " + vals.length);
				
				if (vals.length == 2) {
					//Label:amount (Label and alias will be the same
					df.setAlias(vals[0]);
					df.setLabel(vals[0]);
					df.setAmount(Integer.valueOf(vals[1]));
				} else if (vals.length == 3) {
					// alias:Label:amount
					out(" -- 0 " + vals[0] + " -- 1 " + vals[1] + " -- 2 " + vals[2]);
					df.setAlias(vals[0]);
					df.setLabel(vals[1]);
					df.setAmount(Integer.valueOf(vals[2]));
				} else {
					out(" Invalid node definition it must follow the format label:amount or alias:label:amount!-->" + elm);
					continue;
				}
				// read the properties
				// the label property will be read first, the alias property afterwards
				// if there is a same prop name the definition will be overwritten
				String propCheck = TDGConstants.PROP_NODE_PROPS_PREFIX + df.getLabel() + ".";
				Map<String,String> pmap = new HashMap<String,String>();
				for (Object pkey : props.keySet() ){
					String skey = (String) pkey;
					if (skey.startsWith(propCheck)) {
						// aha we found a property
						String prop = skey.substring(propCheck.length());
						out(" found property " + prop + " for label " + df.getLabel());
						pmap.put(prop, (String) props.get(skey));
					}
				}
				// now the alias only if alias and label are different
				if (!df.getAlias().equals(df.getLabel())) {
					propCheck = TDGConstants.PROP_NODE_PROPS_PREFIX + df.getAlias() + ".";
					for (Object pkey : props.keySet() ){
						String skey = (String) pkey;
						if (skey.startsWith(propCheck)) {
							// aha we found a property
							String prop = skey.substring(propCheck.length());
							out(" found property " + prop + " for alias " + df.getAlias());
							pmap.put(prop, (String) props.get(skey));
						}
					}
				}
				df.setPropDef(pmap);
				list.add(df);
			}
		}
		
		
		
		return list;
	}
	private List<DefRel> getRelationDefinitions() throws Exception {
		List<DefRel> list = new ArrayList<DefRel>();
		String val = props.getProperty(TDGConstants.PROP_REL_LIST);
		if (val != null && !val.isEmpty()) {
			String[] els = val.split(",");
			for (String elm: els) {
				DefRel dr = new DefRel();
				dr.setName(elm);
				// read the relation definition
				String reldefprop = props.getProperty(TDGConstants.PROP_REL_DEF_PREFIX + elm);
				if (reldefprop == null ) {
					out(" Warning relation definition " + elm + " is not valid check your property file");
					throw new Exception("Invalid Definition");
				}
				String[] reldef = props.getProperty(TDGConstants.PROP_REL_DEF_PREFIX + elm).split(",");
				dr.setStartIdentifier(reldef[0]);
				dr.setRelType(reldef[1]);
				dr.setEndIdentifier(reldef[2]);
				dr.setCardinality(reldef[3]);
				// read the properties
				String propCheck = TDGConstants.PROP_REL_PROPS_PREFIX + elm + ".";
				//System.out.println("PROP CHECK " + propCheck);
				Map<String,String> pmap = new HashMap<String,String>();
				for (Object pkey : props.keySet() ){
					String skey = (String) pkey;
					if (skey.startsWith(propCheck)) {
						// aha we found a property
						String prop = skey.substring(propCheck.length());
						out(" found property " + prop + " for relation " + elm);
						pmap.put(prop, (String) props.get(skey));
					}
				}
				dr.setPropDef(pmap);
				list.add(dr);
			}
		}
		return list;
	}
}
