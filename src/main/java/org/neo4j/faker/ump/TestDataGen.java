package org.neo4j.faker.ump;

import org.neo4j.faker.core.TDGConstants;
import org.neo4j.faker.util.TDGUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.configuration.Config;
import org.neo4j.faker.core.TestDataLoader;

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
	private static final String CONFIG_HOME_DIR_PROP = "unsupported.dbms.directories.neo4j_home";
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	GraphDatabaseService database;
	private Config neoConfig;
	private GraphDatabaseService getDatabase() {
		return database;
	}
	// ExecutionEngine engine ;
	public TestDataGen( @Context GraphDatabaseService database, @Context Config config ) {
		this.database = database;
		this.neoConfig = config;
		
	}
	
	@GET
	@Produces( MediaType.TEXT_PLAIN )
	@Path( "/pfile/{pfile}" )
	public Response load( @PathParam( "pfile" ) String pFile, @Context HttpServletRequest request) throws IOException
	{
		final String file = pFile;
		final String serverName = request.getServerName();
        // it is loaded from a plugin so the plugin directory is the tdgroot
		String home = neoConfig.getRaw().get(CONFIG_HOME_DIR_PROP);
		// op("Home from config " + home);
		if (home == null) {
			home = System.getProperty("neo4j.home");
			op("Home from system property neo4j.home " + System.getProperty("neo4j.home"));
		}
		if (home == null) {
			home = System.getenv("NEO4J_HOME");
			op("Home from system env NEO4J_HOME " + System.getenv("NEO4J_HOME"));
		}
		if (home == null) {
			home = ".";
			op("Home is not know via context, working with a '.'");
		}
		final String tdgRoot = home + System.getProperty("file.separator") + "plugins" + System.getProperty("file.separator") ;
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
