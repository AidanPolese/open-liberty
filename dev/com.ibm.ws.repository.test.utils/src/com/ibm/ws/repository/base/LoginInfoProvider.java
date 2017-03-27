/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.ibm.ws.repository.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import com.ibm.ws.repository.connections.RestRepositoryConnection;
import com.ibm.ws.repository.transport.client.DataModelSerializer;
import com.ibm.ws.repository.transport.client.JSONAssetConverter;
import com.ibm.ws.repository.transport.client.RestClient;
import com.ibm.ws.repository.transport.exceptions.BadVersionException;

/**
 * Creates a loginInfo object for the credentials listed in /com.ibm.ws.repository.test.utils/src/testLogin.properties.
 * <p>
 * If testLogin.properties contains more than one set of server details, this class will try each of them in a random order until it finds one that is contactable.
 */
public class LoginInfoProvider {

    private String userId = null;
    private String password = null;
    private String softlayerUserId = null;
    private String softlayerPassword = null;

    private List<ServerInfo> servers = null;

    private static final Logger logger = Logger.getLogger(LoginInfoProvider.class.getName());

    private static class ServerInfo {
        String url;
        String apiKey;
    }

    private RestRepositoryConnection cachedLoginInfo;

    private static final String TESTRUN_USER_ID = "repoBuilds@ibm.com";
    private static final String TESTRUN_PASSWORD = "T35tiN6!";

    private static final String MARKETPLACE_PREFIX = "RepoTest_";

    /**
     * Match property keys which hold server urls and capture the name in group 1.
     * <p>
     * E.g. "foo.url" matches and captures "foo"
     */
    private static final Pattern URL_PROPERTY_KEY = Pattern.compile("(\\w+)\\.url");

    /**
     * Create a new LoginInfoProvider, loading the repository details and credentials from the given property file.
     * 
     * @param propertiesFile the file containing the repository details
     * @throws FileNotFoundException if the file does not exist
     * @throws IOException if a problem is encountered reading the properties file
     */
    public LoginInfoProvider(File propertiesFile) throws FileNotFoundException, IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(propertiesFile));
        loadProperties(props);
    }

    public LoginInfoProvider(Properties props) {
        loadProperties(props);
    }

    /**
     * Get a LoginInfo object for a valid server. This is only suitable for tests using a
     * single LoginInfoResource in the LoginInfo (which covers all of the tests which
     * are not multi repository specific)
     * <p>
     * This method will take the following steps to find a valid server:
     * <ul>
     * <li>If a LoginInfo object has previously been created, it will check if it is still valid and, if so, return it.</li>
     * <li>If not, it will attempt to contact each server in turn</li>
     * <li>If apiKeys were provided in the properties file, it will attempt to access the marketplace associated with the apiKey</li>
     * <li>If apiKeys were not provided in the properties file, it will attempt to create a test marketplace and apiKey and use that</li>
     * </ul>
     * 
     * @return a valid LoginInfo object
     * @throws NoRepoAvailableException if no repository can be reached and so a valid LoginInfo object cannot be created
     */
    public RestRepositoryConnection getLoginInfo() throws NoRepoAvailableException {

        if (cachedLoginInfo != null) {
            try {
                assertLoginInfoValid(cachedLoginInfo);
                logger.log(Level.INFO, "Reusing cached loginInfo - " + cachedLoginInfo.getRepositoryUrl() + " - " + cachedLoginInfo.getApiKey());
                return cachedLoginInfo;
            } catch (IOException e) {
                // The cached login info is not valid.
                // In this case continue on to create a new login info
            }
        }

        cachedLoginInfo = createLoginInfoResource();
        logger.log(Level.INFO, "Created new loginInfo - " + cachedLoginInfo.getRepositoryUrl() + " - " + cachedLoginInfo.getApiKey());
        return cachedLoginInfo;
    }

    /**
     * Used only by multi repository tests
     * 
     * @return
     * @throws NoRepoAvailableException
     */
    public RestRepositoryConnection createNewRepoFromProperties() throws NoRepoAvailableException {
        return createLoginInfoResource();
    }

    /**
     * Load server details from the given properties object and clear the cached loginInfo.
     * <p>
     * This method is only meant for testing.
     * 
     * @param props the properties object
     */
    private void loadProperties(Properties props) {
        logger.log(Level.INFO, "loadProperties entry");
        // Load basic properties, set to null if blank
        userId = normalizeProperty(props.getProperty("userId"));
        password = normalizeProperty(props.getProperty("password"));
        softlayerUserId = normalizeProperty(props.getProperty("softlayerUserId"));
        softlayerPassword = normalizeProperty(props.getProperty("softlayerPassword"));

        servers = new ArrayList<ServerInfo>();
        boolean apiKeysPresent = false;
        // Pull out pairs of properties which identify a repository (URL/apiKey pair)
        // foo.url = "http://example.com"
        // foo.apiKey = "123456"
        for (String key : props.stringPropertyNames()) {
            Matcher m = URL_PROPERTY_KEY.matcher(key);
            if (m.matches()) {
                String name = m.group(1);
                ServerInfo server = new ServerInfo();
                server.url = props.getProperty(name + ".url");
                server.apiKey = props.getProperty(name + ".apiKey");
                if (server.apiKey != null) {
                    apiKeysPresent = true;
                }
                servers.add(server);
            }
        }

        if (servers.isEmpty()) {
            logger.log(Level.INFO, "loadProperties exit");
            throw new IllegalArgumentException("No server details were provided");
        }

        // Check that if any apiKeys are provided, then all apiKeys have been provided
        if (apiKeysPresent) {
            for (ServerInfo server : servers) {
                if (server.apiKey == null) {
                    logger.log(Level.INFO, "loadProperties exit");
                    throw new IllegalArgumentException("LoginInfo contains apiKeys for some servers, but not for " + server.url);
                }
            }
        }

        // Check whether we're using a user-defined API key or generating API keys
        if (apiKeysPresent) {
            if (userId == null || password == null) {
                logger.log(Level.INFO, "loadProperties exit");
                throw new IllegalArgumentException("LoginInfo userId and password must be provided if a fixed apiKey is used");
            }
        } else {
            if (userId != null || password != null) {
                logger.log(Level.INFO, "loadProperties exit");
                throw new IllegalArgumentException("LoginInfo userId, password and apiKey must all be empty to create a testrun apiKey");
            }
            // Set the userId and password to the test credentials
            userId = TESTRUN_USER_ID;
            password = TESTRUN_PASSWORD;
        }

        // Randomly choose the order we will contact servers
        Collections.shuffle(servers);
        logger.log(Level.INFO, "loadProperties exit");
    }

    /**
     * Normalises property values by converting blank or whitespace values to null
     * 
     * @param value the value to normalise
     * @return null if value is blank or only contains whitespace, otherwise value
     */
    private String normalizeProperty(String value) {
        if (value != null) {
            value = value.trim();
            if (value.isEmpty()) {
                value = null;
            }
        }
        return value;
    }

    /**
     * Checks that the repository referred to by loginInfo is valid and reachable.
     * <p>
     * This method will throw an IOException containing the problem details if the server is not reachable.
     * 
     * @param loginInfo the repository details and credentials
     * @throws IOException if the repository cannot be contacted.
     */
    private void assertLoginInfoValid(RestRepositoryConnection loginInfoResource) throws IOException {
        logger.log(Level.INFO, "assertLoginInfoValid entry");
        HttpURLConnection connection = createConnectionFromLoginInfo("/assets", loginInfoResource);
        connection.setRequestMethod("HEAD");
        connection.connect();
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK
            && connection.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT) {
            throw new IOException("Bad response code: " + connection.getResponseCode());
        }
        logger.log(Level.INFO, "assertLoginInfoValid exit");
    }

    /**
     * Find a working server and create a new LoginInfo object for it.
     * 
     * @return a LoginInfo object for a working server
     * @throws NoRepoAvailableException if we could not reach any server
     */
    private RestRepositoryConnection createLoginInfoResource() throws NoRepoAvailableException {
        logger.log(Level.INFO, "createLoginInfoResource entry");
        NoRepoAvailableException noRepoException = new NoRepoAvailableException();

        // Failover between servers
        for (ServerInfo server : servers) {
            try {
                // If the user has provided an API key, use it with the given server
                if (server.apiKey != null) {
                    RestRepositoryConnection loginInfoResource = new RestRepositoryConnection(userId, password, server.apiKey, server.url, softlayerUserId, softlayerPassword);
                    assertLoginInfoValid(loginInfoResource); // Exception thrown if server is down
                    logger.log(Level.INFO, "createLoginInfoResource exit");
                    return loginInfoResource;
                }
                // Otherwise, we need to try to create an API key
                else {
                    String testApiKey = createApiKey(server.url);
                    RestRepositoryConnection loginInfoResource = new RestRepositoryConnection(userId, password, testApiKey, server.url, softlayerUserId, softlayerPassword);
                    logger.log(Level.INFO, "createLoginInfoResource exit");
                    return loginInfoResource;
                }
            } catch (Exception ex) {
                // Problem accessing server, collect the error to report if all servers fail
                noRepoException.add(server.url, server.apiKey, ex);
                // continue round the loop and try the next server
            }
        }

        // We've hit a problem contacting every server
        // Throw the exception holding the problem details for each server
        logger.log(Level.INFO, "createLoginInfoResource exit");
        throw noRepoException;
    }

    /**
     * Create a new marketplace and return the apiKey
     * 
     * @param repositoryUrl the repository base API key
     * @return the new apiKey
     * @throws IOException if there is a problem communicating with the sever
     * @throws BadVersionException
     */
    private String createApiKey(String repositoryUrl) throws IOException, BadVersionException {
        logger.log(Level.INFO, "createApiKey entry");
        String marketplaceID = createMarketplace(repositoryUrl);

        HttpURLConnection connection = createConnectionFromUrl(repositoryUrl, "/marketplaces/" + marketplaceID + "/apiKeys");
        connection.setRequestMethod("GET");
        connection.setDoOutput(false);

        List<MarketplaceApiKey> mak = DataModelSerializer.deserializeList(connection.getInputStream(), MarketplaceApiKey.class);

        String apiKey = mak.get(0).getKey();

        logger.log(Level.INFO, "createApiKey exit");
        return apiKey;
    }

    /**
     * Create a marketplace on the server at the given URL.
     * 
     * @param repositoryUrl the base URL of the server
     * @return the id of the new marketplace
     * @throws IOException if there's a problem contacting the server
     * @throws BadVersionException
     */
    private String createMarketplace(String repositoryUrl) throws IOException, BadVersionException {

        HttpURLConnection connection = createConnectionFromUrl(repositoryUrl, "/marketplaces");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        Marketplace marketplace = new Marketplace();
        marketplace.setName(createMarketPlaceName());
        marketplace.setDescription("Reposistory Testsuite test marketplace");

        JSONAssetConverter.writeValue(connection.getOutputStream(), marketplace);

        Marketplace newMarketplace = DataModelSerializer.deserializeObject(connection.getInputStream(), Marketplace.class);

        return newMarketplace.get_id();
    }

    /**
     * Create a (hopefully) unique name for the marketplace with a known prefix
     * suffixed with the date and time down to current millisecond plus a random number
     * <p>
     * Eg: RepoTest_YYDDD.hhmmssmmmrand
     */
    private String createMarketPlaceName() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyDDD.HHmmssSSS");
        return MARKETPLACE_PREFIX + dateFormat.format(new Date()) + new Random().nextInt(1000);
    }

    /**
     * Create a HttpURLConnection using the credentials, repository and apiKey provided in the loginInfo object.
     * 
     * @param urlString the path to request within the repository
     * @param loginInfo the LoginInfo object holding the credentials
     * @return the connection object
     * @throws IOException if we are unable to open the connection
     */
    private HttpURLConnection createConnectionFromLoginInfo(String path, RestRepositoryConnection loginInfoResource) throws IOException {
        String connectingString = path.contains("?") ? "&" : "?";
        final String urlString = loginInfoResource.getRepositoryUrl() + path + connectingString + "apiKey=" + loginInfoResource.getApiKey();

        URL url;
        try {
            url = AccessController.doPrivileged(new PrivilegedExceptionAction<URL>() {

                @Override
                public URL run() throws MalformedURLException {
                    return new URL(urlString);
                }
            });
        } catch (PrivilegedActionException e) {
            throw (MalformedURLException) e.getCause();
        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        if (loginInfoResource.getSoftlayerUserId() != null) {
            String userpass = loginInfoResource.getSoftlayerUserId() + ":" + loginInfoResource.getSoftlayerPassword();
            String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes(Charset.forName("UTF-8")));
            connection.setRequestProperty("Authorization", basicAuth);
        }

        if (loginInfoResource.getUserId() != null) {
            connection.addRequestProperty("userId", loginInfoResource.getUserId());
        }
        if (loginInfoResource.getPassword() != null) {
            connection.addRequestProperty("password", loginInfoResource.getPassword());
        }
        return connection;
    }

    /**
     * Create a HttpURLConnection to the repository given by the repositoryUrl, using the authentication credentials provided in the properties file, or the test credentials if
     * none were provided.
     * <p>
     * This method will not add the apiKey to the urlString.
     * 
     * @param repositoryUrl the base repository URL for the repository to connect to
     * @param urlString path to request within the repository
     * @return the connection object
     * @throws IOException if we are unable to open the connection
     */
    private HttpURLConnection createConnectionFromUrl(final String repositoryUrl, final String urlString) throws IOException {
        URL url;
        try {
            url = AccessController.doPrivileged(new PrivilegedExceptionAction<URL>() {

                @Override
                public URL run() throws MalformedURLException {
                    return new URL(repositoryUrl + urlString);
                }
            });
        } catch (PrivilegedActionException e) {
            throw (MalformedURLException) e.getCause();
        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        if (softlayerUserId != null) {
            String userpass = softlayerUserId + ":" + softlayerPassword;
            String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes(Charset.forName("UTF-8")));
            connection.setRequestProperty("Authorization", basicAuth);
        }

        connection.addRequestProperty("userId", userId);
        connection.addRequestProperty("password", password);

        return connection;
    }

    /**
     * This method returns the url for a file that is being hosted on one of the test servers.
     * If SSLSocketFactory is required this should be set up before calling this method.
     * 
     * @param file - file on test server which a url is required for
     * @return The url for the file
     * @throws Exception
     */
    public static String getTestServerFileUrl(String file) throws Exception {
        String fileUrl = null;
        // Find an available test server hosting the test file
        for (String hostname : LoginInfoProvider.getTestServerHostnames()) {
            try {
                String testFileUrl = "https://" + hostname + "/" + file;
                HttpsURLConnection conn = (HttpsURLConnection) new URL(testFileUrl).openConnection();
                conn.setConnectTimeout(10000);
                if (conn.getResponseCode() == 200) {
                    fileUrl = testFileUrl;
                    break;
                }
            } catch (IOException e) {
                // IO exception trying to contact this test server, try the next one
            }
        }

        if (fileUrl == null) {
            throw new FileNotFoundException("Could not find an available test server serving the test file");
        }
        return fileUrl;
    }

    public static List<String> getTestServerHostnames() throws Exception {
        List<String> hostnames = new ArrayList<String>();

        Properties props = new Properties();
        props.load(RestClient.class.getResourceAsStream("/testLogin.properties"));

        for (String key : props.stringPropertyNames()) {
            if (key.endsWith(".url")) {
                URI uri = URI.create(props.getProperty(key));
                hostnames.add(uri.getHost());
            }
        }

        return hostnames;
    }

}
