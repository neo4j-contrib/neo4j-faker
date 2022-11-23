package org.neo4j.faker.ump;

import org.neo4j.configuration.Config;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.faker.core.TDGConstants;
import org.neo4j.faker.util.TDGUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.faker.core.TestDataLoader;
import org.neo4j.graphdb.config.Setting;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

@Path( "/tdg" )
public class TestDataGen {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private DatabaseManagementService dbms;
	private Config neoConfig;
	private String dbname = null;
	private GraphDatabaseService getDatabase() {
		// for now use the default
		// assuming null will do that
		// later on we think about having a url parameter with dbname ?dbname=neo4j
		if (this.dbname == null) {
			System.out.println( "Databases list \n" + dbms.listDatabases() );
			//this.dbname = "";
		}
		return dbms.database(this.dbname);

	}
	// ExecutionEngine engine ;
	public TestDataGen( @Context DatabaseManagementService dbms, @Context Config config ) {
		this.dbms = dbms;

		this.neoConfig = config;
	}
	
	@GET
	@Produces( MediaType.TEXT_PLAIN )
	@Path( "/pfile/{pfile}" )
	public Response load( @PathParam( "pfile")  String pFile,  @QueryParam ("dbname") String dbname, @Context HttpServletRequest request) throws IOException
	{
		final String file = pFile;
		final String serverName = request.getServerName();
		if (dbname != null && !dbname.isEmpty()) {
			if (dbname.length() > 40) {
				System.out.println("Warning not accepting dbnames longer than 40 characters");
			} else {
				this.dbname = dbname;
			}
		} else {
			// read the default db from config
			Setting sett = null;
			if (this.neoConfig.getDeclaredSettings().containsKey("dbms.default_database")) {

				sett = this.neoConfig.getSetting("dbms.default_database");
			} else if (this.neoConfig.getDeclaredSettings().containsKey("initial.dbms.default_database")) {
				sett = this.neoConfig.getSetting("initial.dbms.default_database");
			}
			if (sett != null) {
				this.dbname = this.neoConfig.get(sett).toString();
			}
			System.out.println(" using default database " + this.dbname);
		}
        // it is loaded from a plugin so the plugin directory is the tdgroot
		String pluginsDir = TDGUtils.getPluginDir(this.neoConfig);
		// op("Home from config " + home);
//		if (home == null) {
//			home = System.getProperty("neo4j.home");
//			op("Home from system property neo4j.home " + System.getProperty("neo4j.home"));
//		}
//		if (home == null) {
//			home = System.getenv("NEO4J_HOME");
//			op("Home from system env NEO4J_HOME " + System.getenv("NEO4J_HOME"));
//		}
//		if (home == null) {
//			home = ".";
//			op("Home is not know via context, working with a '.'");
//		}
		final String tdgRoot = pluginsDir + System.getProperty("file.separator") ;
		StreamingOutput output = new StreamingOutput() {
			
			@Override
			public void write(OutputStream os) throws IOException,
					WebApplicationException {
				OutputStreamWriter writer = new OutputStreamWriter(os);
				writer.write(" DemoDataGen version "+ TDGConstants.VERSION + " \n" );
				writer.write(" STARTING on " + new Date() + "\n" );
				writer.write(" tdgRoot " + tdgRoot);
				writer.flush();
				if (validParameters(writer, file, serverName, tdgRoot)) {
					try {
						//
						// valid parameters now load the properties file
						//
						Properties props = TDGUtils.loadProperties(tdgRoot,file);
						//
						// Initiate now the TestDataLoader with the properties file and database
						//
						TestDataLoader ldr = new TestDataLoader(getDatabase(), props, writer,tdgRoot);
						ldr.loadData();
					} catch (Throwable t) {
						rout(writer,"error t:" + t.getMessage());
						t.printStackTrace();
					}
				} else {
					rout(writer,lineSep() + lineSep() + lineSep() + "Finished with errors!");
				}
				writer.flush();
			}
		};
		
		return Response.ok(output).build();
	}
	private void rout(Writer out, String s) throws IOException {
		out.write(s);
		out.write("\n");
		out.flush();
	}
	private void op(String s) {
		System.out.println("DemoDataGen ux:>" + s);
	}
	private String lineSep() {
		return System.getProperty("line.separator");
	}
	private boolean validParameters(Writer writer, String fileName, String serverName, String tdgRoot) throws IOException {
		// check if the filename is valid it may not contain \ / .. also the url escaped vaiant of this may not be there
		boolean valid = true;
		if (serverName.equals("127.0.0.1") || serverName.equalsIgnoreCase("localhost")) {
			// valid
		} else {
			// invalid
			valid = false;
			rout(writer,"Access denied" );
			return valid;
		}
		if (fileName.indexOf("..") > -1 
			|| fileName.indexOf("\\") > -1
			|| fileName.indexOf("/") > -1
			|| fileName.indexOf("%2F") > -1
			|| fileName.indexOf("%2E%2E") > -1) {
			valid = false;
			rout(writer,"Invalid file name!: " + fileName );
		} 
		// check the file
		File fFile = new File(TDGUtils.getResourceFilePath(tdgRoot, fileName));
		rout(writer," Property file to be loaded " + fFile.getAbsolutePath());
		if (!fFile.exists() || !fFile.isFile()) {
			rout(writer,"Property File does not exist or is not a file, it may be a directory!");
			valid =false;
		}
		return valid;
	}
}
