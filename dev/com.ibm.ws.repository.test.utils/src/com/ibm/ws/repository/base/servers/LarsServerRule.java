package com.ibm.ws.repository.base.servers;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.websphere.simplicity.config.ApplicationBnd;
import com.ibm.websphere.simplicity.config.BasicRegistry;
import com.ibm.websphere.simplicity.config.BasicRegistry.Group;
import com.ibm.websphere.simplicity.config.BasicRegistry.Group.Member;
import com.ibm.websphere.simplicity.config.BasicRegistry.User;
import com.ibm.websphere.simplicity.config.MongoDBElement;
import com.ibm.websphere.simplicity.config.MongoElement;
import com.ibm.websphere.simplicity.config.SecurityRole;
import com.ibm.websphere.simplicity.config.ServerConfiguration;
import com.ibm.websphere.simplicity.config.SpecialSubject;
import com.ibm.websphere.simplicity.config.SpecialSubject.Type;
import com.ibm.websphere.simplicity.config.WebApplication;
import com.ibm.ws.repository.connections.RestRepositoryConnection;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import componenttest.topology.impl.LibertyFileManager;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.impl.LibertyServerFactory;

/**
 * Starts and stops a LARS server around a test.
 * <p>
 * Handles installing the server, updating the configuration for the test environment,
 * creating a new database for the test and dropping the database once the test is completed.
 * <p>
 * Generally, this should be used as a ClassRule on the FATSuite class to ensure that the
 * LARS server set up once, runs for the whole FAT bucket and is only cleaned up at the end.
 * <p>
 * LARS requires a mongo database. The mongo database details must be provided in
 * mongodb.properties
 *
 */
public class LarsServerRule implements TestRule {

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd-HHmmss");
    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private LibertyServer larsServer;

    private MongoClient mongoClient;
    private String dbName;

    private final List<ServerAddress> serverAddresses = new ArrayList<ServerAddress>();
    private String username;
    private String password;

    private RestRepositoryConnection connection;

    @Override
    public Statement apply(final Statement statement, Description description) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                // dbname must be less than 64chars and contains no '.' (from the mongodb spec)
                String hostname = InetAddress.getLocalHost().getHostName();
                int dotIndex = hostname.indexOf('.');
                if (dotIndex > 0) {
                    hostname = hostname.substring(0, dotIndex);
                }
                dbName = "lars-" + DATE_FORMAT.format(new Date()) + "-" + hostname;
                readProperties();

                try {
                    // Set up LARS server
                    setUpDatabase();
                    startLarsServer();
                    // Run the tests
                    statement.evaluate();
                } finally {
                    // Do cleanup
                    if (larsServer != null) {
                        larsServer.stopServer();
                    }
                    dropDatabase();
                }
            }
        };
    }

    private void readProperties() throws Exception {

        Properties mongoProperties = new Properties();

        mongoProperties.load(this.getClass().getResourceAsStream("/mongodb.properties"));

        String hostsString = mongoProperties.getProperty("hosts");
        String portsString = mongoProperties.getProperty("ports");

        if (hostsString != null && portsString != null) {
            String[] hostStrings = hostsString.split(",");
            String[] portStrings = portsString.split(",");

            if (hostStrings.length != portStrings.length) {
                throw new Exception("Supplied hosts and ports for mongodb are different lengths");
            }

            for (int i = 0; i < hostStrings.length; i++) {
                Integer port = new Integer(portStrings[i]);
                serverAddresses.add(new ServerAddress(hostStrings[i], port));
            }
        }

        String username = mongoProperties.getProperty("username");
        if (username != null && !username.isEmpty()) {
            this.username = username;
        }

        String password = mongoProperties.getProperty("password");
        if (password != null && !password.isEmpty()) {
            this.password = password;
        }

    }

    private void startLarsServer() throws Exception {

        larsServer = LibertyServerFactory.getLibertyServer("larsServer");

        // Move the test server xml into sample.xml
        RemoteFile sampleServerFile = LibertyFileManager.createRemoteFile(larsServer.getMachine(), larsServer.getServerRoot() + "/sample.xml");
        LibertyFileManager.moveLibertyFile(larsServer.getServerConfigurationFile(), sampleServerFile);

        // And upload the FAT server XML that will include sample.xml
        LibertyFileManager.copyFileIntoLiberty(larsServer.getMachine(), larsServer.getServerRoot(), "server.xml", "productSampleServer.xml");

        // Upload the FAT bootstrap.properties
        LibertyFileManager.copyFileIntoLiberty(larsServer.getMachine(), larsServer.getServerRoot(), "bootstrap.properties", "productSample_noBootstrap.properties");

        ServerConfiguration config = larsServer.getServerConfiguration(sampleServerFile);

        // Override the database configuration
        MongoElement mongo = config.getMongos().get(0);

        List<String> hostNames = mongo.getHostNamesElements();
        List<Integer> ports = mongo.getPortsElements();

        hostNames.clear();
        ports.clear();
        for (ServerAddress server : serverAddresses) {
            hostNames.add(server.getHost());
            ports.add(server.getPort());
        }

        mongo.setUser(username);
        mongo.setPassword(password);

        MongoDBElement mongodb = config.getMongoDBs().get(0);
        mongodb.setDatabaseName(dbName);

        // Set up the user registry with admin user and group
        config.getBasicRegistries().clear();
        BasicRegistry reg = new BasicRegistry();
        reg.setId("defaultRegistry");
        reg.setRealm("default");
        config.getBasicRegistries().add(reg);

        User adminUser = new User();
        adminUser.setName("admin");
        adminUser.setPassword("testPassword");
        reg.getUsers().add(adminUser);

        Group adminGroup = new Group();
        adminGroup.setName("Administrators");
        reg.getGroups().add(adminGroup);

        Member adminMember = new Member();
        adminMember.setName("admin");
        adminGroup.getMembers().add(adminMember);

        // Configure the application with the admin and user security roles
        WebApplication app = config.getWebApplications().getById("com.ibm.ws.lars.rest");
        ApplicationBnd appBnd = app.getApplicationBnd();
        appBnd.getSecurityRoles().clear();

        SecurityRole userRole = new SecurityRole();
        userRole.setName("User");
        appBnd.getSecurityRoles().add(userRole);

        SpecialSubject everyone = new SpecialSubject();
        everyone.set(Type.EVERYONE);
        userRole.getSpecialSubjects().add(everyone);

        SecurityRole adminRole = new SecurityRole();
        adminRole.setName("Administrator");
        appBnd.getSecurityRoles().add(adminRole);

        SecurityRole.Group adminRoleGroup = new SecurityRole.Group();
        adminRoleGroup.setName("Administrators");
        adminRole.getGroups().add(adminRoleGroup);

        // remove the httpendpoint stanza, it's provided in fatTestPorts.xml
        config.getHttpEndpoints().clear();

        // Enable some logging
        config.getLogging().setTraceSpecification("com.ibm.ws.lars.*=all");

        larsServer.updateServerConfiguration(config, sampleServerFile);

        larsServer.startServer();
    }

    private void setUpDatabase() {
        mongoClient = new MongoClient(serverAddresses);

        DB adminDb = mongoClient.getDB("admin");
        adminDb.authenticateCommand(username, password.toCharArray()).throwOnError();

        DB db = mongoClient.getDB(dbName);

        // Create a user with the same username and password on the new database
        // Needed because Liberty always authenticates against the DB it uses
        BasicDBList roles = new BasicDBList();
        roles.add("readWrite");
        BasicDBObject createUser = new BasicDBObject("createUser", username)
                        .append("pwd", password)
                        .append("roles", roles);
        db.command(createUser).throwOnError();
    }

    private void dropDatabase() {
        if (mongoClient != null) {
            if (mongoClient.getDatabaseNames().contains(dbName)) {
                mongoClient.dropDatabase(dbName);
            }
        }
    }

    public RestRepositoryConnection getRestConnection() {
        if (connection == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("http://");
            sb.append(larsServer.getHostname());
            sb.append(":").append(larsServer.getHttpDefaultPort());
            sb.append("/ma/v1");
            connection = new RestRepositoryConnection("admin", "testPassword", "1", sb.toString());
        }
        return connection;
    }

}