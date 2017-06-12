package org.neo4j.faker.core;

import org.neo4j.graphdb.RelationshipType;

/**
 * Created by keesv on 12/06/2017.
 */
public class DynRel  {
    public static RelationshipType get(final String type) {
        return new RelationshipType() {
            @Override
            public String name() {
                return type;
            }
        };
    }
}
