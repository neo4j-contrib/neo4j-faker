package org.neo4j.faker.proc;

import com.github.javafaker.Faker;
import org.neo4j.faker.data.ValueGen;
import org.neo4j.kernel.configuration.Config;
import org.neo4j.kernel.internal.GraphDatabaseAPI;
import org.neo4j.faker.data.PersonNamesGen;

import java.io.File;
import java.io.IOException;
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
    public static DDGFunctions getInstance(GraphDatabaseAPI db) throws IOException {

        if (inst == null) {
            synchronized (sdf) {
                if (inst == null) {
                    inst = new DDGFunctions(db);
                }
            }
        }
        return inst;
    }
    private DDGFunctions(GraphDatabaseAPI db) throws IOException {
        Config conf = null;
        try {
            conf = db.getDependencyResolver().resolveDependency(Config.class);

        } catch (Throwable ee) {
            ee.printStackTrace();

        }


        String home = conf.getRaw().get(CONFIG_HOME_DIR_PROP);
        System.out.println("Home from config " + home);
        if (home == null) {
            home = System.getProperty("neo4j.home");
            System.out.println("Home from system property neo4j.home " + System.getProperty("neo4j.home"));
        }
        if (home == null) {
            home = System.getenv("NEO4J_HOME");
            System.out.println("Home from system env NEO4J_HOME " + System.getenv("NEO4J_HOME"));
        }
        if (home == null) {
            home = ".";
            System.out.println("Home is not know via context, working with a '.'");
        }
        tdgRoot = home + File.separator + "plugins" + File.separator;

        faker = new Faker();
        // fakerDump(faker);
        names = new PersonNamesGen(tdgRoot);
        valuegen = new ValueGen(tdgRoot);
    }

    public PersonNamesGen getNames() { return names; }
    public Faker getFaker() {
        return faker;
    }
    public ValueGen getValuegen() {
        return valuegen;
    }
}
