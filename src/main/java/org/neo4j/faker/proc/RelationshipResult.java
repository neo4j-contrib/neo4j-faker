package org.neo4j.faker.proc;

import org.neo4j.graphdb.Relationship;

import java.util.List;

/**
 * Created by keesv on 12/06/2017.
 */
public class RelationshipResult {
    public final List<Relationship> relationships;

    public RelationshipResult(List<Relationship> rels) {
        this.relationships = rels;
    }

}
