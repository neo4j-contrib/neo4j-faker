package org.neo4j.faker.core;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.schema.ConstraintDefinition;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;

import java.io.IOException;
import java.util.*;

public class DBLoader {
	int commitSize = 1000;
	int processSize = 20000;
	GraphDatabaseService database;
	private List<TDNode> createNodeList = new ArrayList<TDNode>(); 
	private List<TDRelation> createRelationList = new ArrayList<TDRelation>();
	private SortedMap<NodeIdentifier,Map<String,Object>> nodePropMap = new TreeMap<NodeIdentifier,Map<String,Object>>();
	private TestDataLoader tdl;
	int cntr = 0;

	public DBLoader(TestDataLoader tdl, int commitSize) {
		this.database = tdl.getDatabase();
		this.commitSize = commitSize;
		// processSize = 10 * commitSize;
		this.tdl = tdl;
	}
	public void createNode(TDNode node) {
		if (createNodeList.size() < commitSize) {
			createNodeList.add(node);
		}
		if (createNodeList.size() == commitSize) {
			processCreate();
		}
	}
	public void createRelation(TDRelation tdr) {
		if (createRelationList.size() < commitSize) {
			createRelationList.add(tdr);
		}
		if (createRelationList.size() == commitSize)  {
			processCreateRelation();
		}
	}
	
	public void createUniqueIndex(String label, String property) {
		// indexes are created directly in one transaction
		try ( Transaction tx = database.beginTx()) {
			Schema schema = database.schema();
			// check first if index already exists
			boolean notExist = true;
			
			Iterator<ConstraintDefinition> iter = schema.getConstraints(DynamicLabel.label(label)).iterator();
			
			while (iter.hasNext()) {
				ConstraintDefinition cd = iter.next();
				Iterator<String> propiter = cd.getPropertyKeys().iterator();
				while (propiter.hasNext()) {
					String prop = propiter.next();
					if (prop.equals(property)) {
						// exists
						notExist = false;
						break;
					}
					if (!notExist) break;
				}
			}
			
			
			if (notExist) schema.constraintFor(DynamicLabel.label(label)).assertPropertyIsUnique(property).create();
			tx.success();
		}
	}
	public void createIndex(String label, String property) {
		// indexes are created directly in one transaction
		try ( Transaction tx = database.beginTx()) {
			Schema schema = database.schema();
			// check first if index already exists
			boolean notExist = true;
			
			Iterator<IndexDefinition> iter = schema.getIndexes(DynamicLabel.label(label)).iterator();
			
			while (iter.hasNext()) {
				IndexDefinition cd = iter.next();
				Iterator<String> propiter = cd.getPropertyKeys().iterator();
				while (propiter.hasNext()) {
					String prop = propiter.next();
					if (prop.equals(property)) {
						// exists
						notExist = false;
						break;
					}
					if (!notExist) break;
				}
			}
			
			
			if (notExist) schema.indexFor(DynamicLabel.label(label)).on(property).create();
			tx.success();
		}
	}
	public void setNodeProperties(NodeIdentifier node, Map<String,Object> props) {
		// 
		if (nodePropMap.size() < commitSize) {
			nodePropMap.put(node, props);
		}
		if (nodePropMap.size() == commitSize) {
			processNodeProps();
		}
	}
	public void flush() {
		processCreate();	
		processNodeProps();
		processCreateRelation();
	}
	private void processCreate() {
		try ( Transaction tx = database.beginTx()) {
			// walk though the list
			try {
				cntr++;
				if (cntr == 100) {
					cntr=0;
					tdl.out(" Process Create tm " + Runtime.getRuntime().totalMemory() + " fm " + Runtime.getRuntime().freeMemory() );
					//System.gc();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (TDNode tdn: createNodeList) {
				// create a node here in the db
				Node nn = database.createNode();
				nn.addLabel(DynamicLabel.label(tdn.getLabel()));
				for (String propName : tdn.getProps().keySet()) {
					Object val = tdn.getProps().get(propName);
					nn.setProperty(propName, val);
				}
			}
			// clear the list
			
			createNodeList.clear();
			tx.success();
		}
//		try{
//			if (createNodeList.size() > 0) {
//				ForkJoinPool fjp = getForkJoinPool();
//				CreateNodeTask task = new CreateNodeTask(createNodeList, database, commitSize);
//				tdl.out(" nodes created: " + fjp.invoke(task));
//				createNodeList.clear();
//			}
//		} catch (Exception ee) {
//			
//		}
	}
	private void processCreateRelation() {
		try ( Transaction tx = database.beginTx()) {
			// walk though the list
			for (TDRelation tdr: createRelationList) {
				// create a node here in the db
				Node startNode = getNode(tdr.getFrom());
				Node endNode = getNode(tdr.getTo());
				if (startNode != null && endNode != null) {
					Relationship rel = startNode.createRelationshipTo(endNode, DynamicRelationshipType.withName(tdr.getRelType()));
					for (String propName : tdr.getProps().keySet()) {
						Object val = tdr.getProps().get(propName);
						rel.setProperty(propName, val);
					}
				} else {
					try {
						tdl.out("WARNING not all nodes could be found! Relation not created!");
					} catch (IOException e) {

						e.printStackTrace();
					}
				}
			}
			// clear the list
			createRelationList.clear();
			tx.success();
		}
	}
	private void processNodeProps() {
		try ( Transaction tx = database.beginTx()) {
			for (NodeIdentifier nid : nodePropMap.keySet()) {
				Node n = getNode(nid);
				Map<String,Object> props = nodePropMap.get(nid);
				for (String propName : props.keySet()) {
					Object val = props.get(propName);
					n.setProperty(propName, val);
				}
			}
			// clear the list
			nodePropMap.clear();
			tx.success();
		}
				
	}
	private Node getNode(NodeIdentifier nid) {
		if (nid.getId() > 0) {
			return 	database.getNodeById(nid.getId());
		} else {
			if (nid.getLabel() != null && nid.getIndexProp() != null && nid.getIndexValue() != null) {
				ResourceIterator<Node> iter = database.findNodes(DynamicLabel.label(nid.getLabel()), nid.getIndexProp(), nid.getIndexValue());;
				return iter.next(); 
			} else {
				return null;
			}
		}
	}
	
	
	

}
