package org.neo4j.faker.core;

import org.neo4j.faker.util.TDGUtils;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RepeatNodesProcessor {
	
	TestDataLoader tdl;

	
	public RepeatNodesProcessor(TestDataLoader tdl) {
		this.tdl = tdl;
	}
	public void process() throws Exception {
		//
		// Reading the repeat nodes which results in a list of RepeatNode definitions
		//
		List<DefRepeatNode> repnodes = getRepeatNodeDefs();
		//
		// We have to determine the repeater relations
		//
		List<DefRel> reprelations = getRepRelationDefinitions();
		//
		//
		// Now for every repeat Node definition
		//
		// we have to execute the queries. per input value and return the list of NodeIdentifiers to process later
		//
		for (DefRepeatNode repn: repnodes) {
			out(" === start processing repeater node " + repn.getRepeatName());
			//
			// Get the cypher definitions
			//
			Map<String,String> cypherDefinitions = repn.getCypherStatements();
			String[] inputA = repn.getInput();
			for (String input: inputA) {
				out(" ====== input value :" + input);
				// a repeat node can have multiple queries
				// for every query definition an intermeidate lookup node definitions
				// is created. The lookup name will then be "repeaterName#queryName
				// the key of the map with node identifiers 
				// = #<queryName>.<column Name>
				List<DefLUNode> repLUNodes = new ArrayList<DefLUNode>();
				
				for (String qName: cypherDefinitions.keySet()) {
					// we have to parse the input value in the query
					DefLUNode dlu = new DefLUNode();
					// the name of this lookup node is repeatername#qName
					dlu.setLookup(repn.getRepeatName() + "#" + qName);
					String cypher = cypherDefinitions.get(qName).replaceAll("##INPUT##",input);
					// dlu.setCypher(cypher)
					out(" ====== executing query name " + dlu.getLookup() + " :" + cypher);
					Result res = DBLoader.dbExecute(tdl.getDatabase(),cypher);
					out(" ====== The return columns:  " + res.columns());
					int size = 0;
					while (res.hasNext()) {
					// for (Map<String,Object> row : res) {
						size++;
						Map<String,Object> row  = res.next();
						for ( Entry<String, Object> col : row.entrySet() )
					    {
							Node n = (Node) col.getValue();
					        dlu.addNodeIdentifier(col.getKey(), new NodeIdentifier(n.getId()));
					    }
					}
					dlu.setResultSize(size);
					dlu.shuffle();
					repLUNodes.add(dlu);
					out(" ====== " + size + " records found");
					
				}
				//
				// now the queries are executed. and the resulting id's are stored.
				//
				//
				// Now we can create relations
				//
				//
				// We have now all the nodes, now we have tp processs the relations
				//
				for (DefRel rd : reprelations) {
					// get the start nodes or node id
					// get the end nodes or node id
					out(" processing relation "  + rd.getName());
					out("  START node identifier " + rd.getStartIdentifier());
					out("  END node identifier " + rd.getEndIdentifier());
					Object startDef = null;
					Object endDef = null;
					
					//
					// A repeat node relation definition must have a repeat node as start or end identifier 
					// If not available the relation definition will be skipped
					//
					// check the current set of repeater lookup nodes
					//

					for (DefLUNode dnode : repLUNodes) {
						if (dnode.getLookup().equals(rd.getStartIdentifierPrefix())) startDef = dnode;
						if (dnode.getLookup().equals(rd.getEndIdentifierPrefix())) endDef = dnode;
						if (startDef != null && endDef != null) break;
					}
					if (startDef == null && endDef == null) {
					    out("  WARNING in the relation definition of a repeat node there must have at least one repeat node identifier as start or end node identifier!");
					    continue;
					}
					if (startDef == null || endDef == null) {
						if (tdl.isUseIndex()) {
							for (DefNode dnode : tdl.getNdef()) {
								if (dnode.getAlias().equals(rd.getStartIdentifier())) startDef = dnode;
								if (dnode.getAlias().equals(rd.getEndIdentifier())) endDef = dnode;
								if (startDef != null && endDef != null) break;
							}
						}
					}
					// check the lookup nodes
					if (startDef == null || endDef == null) {
						for (DefLUNode dnode : tdl.getLudef()) {
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
					// check if there any nodes
					//
					//
					// Now we have a start and end node now we can process it
					//
					if (rd.getCardinality().equals(TDGConstants.CDN_ONE_TO_MANY)) {
						tdl.processOneToMany(rd,tdl.getNodeIdentifiers(startDef,rd.getStartColumn()),tdl.getNodeIdentifiers(endDef,rd.getEndColumn()));
					} else if (rd.getCardinality().equals(TDGConstants.CDN_MANY_TO_ONE)) {
						tdl.processManyToOne(rd,tdl.getNodeIdentifiers(startDef,rd.getStartColumn()),tdl.getNodeIdentifiers(endDef,rd.getEndColumn()));
					} else if (rd.getCardinality().equals(TDGConstants.CDN_MANY_TO_MANY)) {
						tdl.processManyToMany(rd,tdl.getNodeIdentifiers(startDef,rd.getStartColumn()),tdl.getNodeIdentifiers(endDef,rd.getEndColumn()));
					} else if (rd.getCardinality().equals(TDGConstants.CDN_ONE_TO_ONE)) {
						tdl.processOneToOne(rd,tdl.getNodeIdentifiers(startDef,rd.getStartColumn()),tdl.getNodeIdentifiers(endDef,rd.getEndColumn())); 
					} else {
						out("  WARNING unsupported cardinality "+ rd.getCardinality() +" this relation will not be created!");
					}
				}
				//
	            // now we have to remove the results of the queries because we do not need them anymore
				// 
				repn.clearIdentifiers();
			}
		}
	}
	
	private List<DefRel> getRepRelationDefinitions() throws Exception {
		List<DefRel> list = new ArrayList<DefRel>();
		String val = tdl.getProps().getProperty(TDGConstants.PROP_REP_REL_LIST);
		if (val != null && !val.isEmpty()) {
			String[] els = val.split(",");
			for (String elm: els) {
				DefRel dr = new DefRel();
				dr.setName(elm);
				// read the relation definition
				String reldefprop = tdl.getProps().getProperty(TDGConstants.PROP_REP_REL_DEF_PREFIX + elm);
				if (reldefprop == null ) {
					out(" Warning relation definition " + elm + " is not valid check your property file");
					throw new Exception("Invalid Definition");
				}
				String[] reldef = tdl.getProps().getProperty(TDGConstants.PROP_REP_REL_DEF_PREFIX + elm).split(",");
				dr.setStartIdentifier(reldef[0]);
				dr.setRelType(reldef[1]);
				dr.setEndIdentifier(reldef[2]);
				dr.setCardinality(reldef[3]);
				// read the properties
				String propCheck = TDGConstants.PROP_REP_REL_PROPS_PREFIX + elm + ".";
				Map<String,String> pmap = new HashMap<String,String>();
				for (Object pkey : tdl.getProps().keySet() ){
					String skey = (String) pkey;
					if (skey.startsWith(propCheck)) {
						// aha we found a property
						String prop = skey.substring(propCheck.length());
						out(" found property " + prop + " for repeat relation " + elm);
						pmap.put(prop, (String) tdl.getProps().get(skey));
					}
				}
				dr.setPropDef(pmap);
				list.add(dr);
			}
		}
		return list;
	}	
	
	private List<DefRepeatNode> getRepeatNodeDefs() throws IOException{
		List<DefRepeatNode> list = new ArrayList<DefRepeatNode>();
		String val = tdl.getProps().getProperty(TDGConstants.PROP_REPEATER_LIST,"");
		if (!val.isEmpty()) {
			out("getRepeatNodeDefs() repeater list " + val);
			String[] els = val.split(",");
			//out("els " + els.length);
			for (String ef: els) {
				//
				// get the repeater input
				//
				String repInput = tdl.getProps().getProperty(TDGConstants.PROP_REPEATER_INPUT_PREFIX + ef);
				if (repInput == null || repInput.trim().isEmpty()) {
					out("WARNING: repeater definition " + ef + " does not have an input specified, this is required!");
					continue;
				}
				int startPos = repInput.indexOf(":");
				if (startPos == -1) {
					out("WARNING: repeater definition " + ef + " does not have a valid input specified: " + repInput );
					continue;
				}
				String[] inputValues = getInputValues(repInput.substring(0,startPos).trim(), repInput.substring(startPos + 1).trim());
				//
				if (inputValues == null || inputValues.length == 0) {
					continue;
				}
				DefRepeatNode def = new DefRepeatNode();
				
				def.setRepeatName(ef);
				def.setInput(inputValues);
				// now load the cypher statements for this repeater definition
				// reading 
				// the cypher definitions have a name
				// the property file hase the construct:
				// tdg.repeatnode.cypher.<repeatnode identifier>.<name>
				
				String cypherCheck = TDGConstants.PROP_REPEATER_CYPHER_PREFIX+ def.getRepeatName() + ".";
				Map<String,String> cypherDefinitions = new HashMap<String,String>();
				String cypher = null;
				String cname = null;
				boolean invalidCypher = false;
				for (Object pkey : tdl.getProps().keySet() ){
					String skey = (String) pkey;
					if (skey.startsWith(cypherCheck)) {
						// aha we found a cypher statement
						cname = skey.substring(cypherCheck.length());
						cypher = tdl.getProps().getProperty(skey);
						// check if the ##INPUT## is there in the cypher statement!
						if (cypher.indexOf("##INPUT##") == -1) {
							out("REMARK: repeater definition " + ef + " has a cypher query without the mandatory ##INPUT## marker! query name =  " +  cname + " check the property file!");
//							invalidCypher = true;
//							break;
						}
						cypherDefinitions.put(cname,cypher);
					}
				}
				if (invalidCypher) continue;
				def.setCypherStatements(cypherDefinitions);
				list.add(def);
			}
		}
		return list;
	}
	private String[] getInputValues(String type, String def) throws IOException {
		out("type>>" + type + "<<");
		
		if (type.equals("list")) {
			return def.split(",");
		} else if (type.equals("file")) {
			File f = new File(TDGUtils.getResourceFilePath(tdl.getTDGRoot(),def));
			if (!f.exists() || f.isDirectory()) {
				out("WARNING file for repeater input : " + f.toString() + "  does not exist or is not a file!");
				return null;
			}
			List<String> values = TDGUtils.readFile(f);
			if (values == null || values.size() == 0) {
				out("WARNING file for repeater input : " + f.toString() + "  does not have any values!");
				return null;
			}
			return values.toArray(new String[0]);
			
		} else if (type.equals("range")) {
			// two paramter are there in the def
			String[] rv = def.split(",");
			if (rv.length == 2) {
				int from = Integer.valueOf(rv[0]);
				int to = Integer.valueOf(rv[1]);
				int size = to - from;
				String[] ret = new String[size];
				int pos = 0;
				for (int i = from; i < to; i++) {
					ret[pos] = "" + i;
					pos++;
				}
				return ret;
			}
		} else if (type.equals("query")) {
				// two paramter are there in the def
				// def contains the query

			    //	Result res = tdl.getDatabase().execute(def);
			    Result res = DBLoader.dbExecute(tdl.getDatabase(), def);
				out(" ====== The return columns:  " + res.columns());
				ArrayList<String> vals = new ArrayList<String>();
				while (res.hasNext()) {
					Map<String,Object> row = res.next();
					for ( Entry<String, Object> col : row.entrySet() )
				    {
						vals.add( "" +  col.getValue());
				    }
				}
				if (vals.size() > 0) {
					return vals.toArray(new String[0]);
				} else {
					out("WARNING repeater input query : " + def + " does not have any results!");
					return null;
				}
		} else {
			out("WARNING repeater input type : " + type + " is not supported!");
			return null;
		}
		return null;
	}
	
	
	private void out(String line) throws IOException {
		tdl.out(line);
	}

	
}
