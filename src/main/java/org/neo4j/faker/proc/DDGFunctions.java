package org.neo4j.faker.proc;

import com.github.javafaker.Faker;
import org.neo4j.configuration.Config;
import org.neo4j.faker.data.ValueGen;
import org.neo4j.faker.util.TDGUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.faker.data.PersonNamesGen;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * Created by keesv on 08/05/2017.
 */
public class DDGFunctions {
    private static final String CONFIG_HOME_DIR_PROP = "unsupported.dbms.directories.neo4j_home";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private Faker faker;
    private PersonNamesGen names;
    private ValueGen valuegen;
    private static DDGFunctions inst;
    private String tdgRoot;

    public SimpleDateFormat getSdf(){
        return sdf;
    }
    public static DDGFunctions getInstance(GraphDatabaseService db) throws Exception {

        if (inst == null) {
            synchronized (sdf) {
                if (inst == null) {
                    inst = new DDGFunctions(db);
                }
            }
        }
        return inst;
    }
    private DDGFunctions(GraphDatabaseService db) throws Exception {

        String pluginsDir = TDGUtils.getPluginDir(null);
        tdgRoot = pluginsDir + File.separator;
        try {
            faker = new Faker();
            // fakerDump(faker);
            names = new PersonNamesGen(tdgRoot);
            valuegen = new ValueGen(tdgRoot);
        } catch (Exception ee) {
            ee.printStackTrace();
            throw ee;
        }
    }

    public PersonNamesGen getNames() { return names; }
    public Faker getFaker() {
        return faker;
    }
    public ValueGen getValuegen() {
        return valuegen;
    }

}
