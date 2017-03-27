/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.repository.base;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.ibm.ws.repository.connections.RepositoryConnectionList;
import com.ibm.ws.repository.connections.RestRepositoryConnection;
import com.ibm.ws.repository.resources.RepositoryResource;
import com.ibm.ws.repository.resources.internal.RepositoryResourceImpl;
import com.ibm.ws.repository.transport.client.RestClient;

/**
 *
 */
public class RestRepositoryTestUtils implements RepositoryTestUtils<RestRepositoryConnection> {

    protected static LoginInfoProvider _loginInfoProvider;

    protected final static String LIVE_KEY = "75621234192";

    protected RestRepositoryConnection _loginInfoEntry;

    private static final Logger logger = Logger.getLogger(RestRepositoryTestUtils.class.getName());

    protected RestRepositoryConnection getLoginInfoEntry() throws IOException, NoRepoAvailableException {
        if (_loginInfoProvider == null) {
            // Set up LoginInfoProvider only once to reuse the test repo wherever possible
            Properties props = new Properties();
            props.load(this.getClass().getResourceAsStream("/testLogin.properties"));
            _loginInfoProvider = new LoginInfoProvider(props);
        }
        return _loginInfoProvider.getLoginInfo();
    }

    /**
     * {@inheritDoc}
     * 
     * @throws IOException
     * @throws FileNotFoundException
     * @throws NoRepoAvailableException
     */
    @Override
    public void setUpClass() throws FileNotFoundException, IOException, NoRepoAvailableException {
        logger.log(Level.INFO, "MassiveRepositoryTestUtils.setUpClass entry");
        _loginInfoEntry = getLoginInfoEntry();
        blowUpIfUsingLiveSystem(_loginInfoEntry);
        logger.log(Level.INFO, "MassiveRepositoryTestUtils.setUpClass exit");
    }

    /** {@inheritDoc} */
    @Override
    public void tearDownClass() {}

    /** {@inheritDoc} */
    @Override
    public RestRepositoryConnection setUpTest(boolean wipeBeforeEachTest) throws Exception {
        logger.log(Level.INFO, "setUpTest entry");
        // Must refresh loginInfoEntry before every test in case we need to failover to another test server
        _loginInfoEntry = getLoginInfoEntry();
        if (wipeBeforeEachTest) {
            logger.log(Level.INFO, "Deleting before test");
            DeleteRepo.deleteRepo(_loginInfoEntry);
        }
        logger.log(Level.INFO, "setUpTest exit");
        return _loginInfoEntry;
    }

    /** {@inheritDoc} */
    @Override
    public void tearDownTest(boolean wipeBeforeEachTest) throws Exception {
        if (wipeBeforeEachTest) {
            logger.log(Level.INFO, "Deleting after test");
            DeleteRepo.deleteRepo(_loginInfoEntry);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @throws NoRepoAvailableException
     */
    @Override
    public RestRepositoryConnection createNewRepo() throws NoRepoAvailableException {
        return _loginInfoProvider.createNewRepoFromProperties();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<RepositoryResourceImpl> getAllResourcesWithDupes() throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, InstantiationException, IllegalArgumentException, InvocationTargetException {
        return (Collection<RepositoryResourceImpl>) TestBaseClass.reflectiveCallAnyTypes(_loginInfoEntry, "getAllResourcesWithDupes",
                                                                                         null, null);
    }

    @Override
    public RestRepositoryConnection getRepositoryConnection() {
        return _loginInfoEntry;
    }

    /** {@inheritDoc} */
    @Override
    public void refreshTextIndex(String lastAssetId) throws Exception {
        refreshElasticSearchIndex(_loginInfoEntry, lastAssetId);
    }

    /**
     * <p>Refreshes the elastic search index by calling POST on:</p>
     * <code>http://{host}:9200/assets/_refresh</code>
     * <p>Where {host} is the host of the <code>_loginInfoEntry</code> being used. See the elasticsearch doc here for why this works:</p>
     * <p><a
     * href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/indices-refresh.html">http://www.elasticsearch.org/guide/en/elasticsearch/reference/current
     * /indices-refresh.html</a></p>
     * 
     * @param loginInfoEntry The repo to refresh
     * @param assetId The last asset added, refresh will repeat until this asset appears
     * 
     * @throws MalformedURLException
     * @throws IOException
     * @throws ProtocolException
     * @throws InterruptedException
     */
    public static void refreshElasticSearchIndex(RestRepositoryConnection loginInfoEntry, String assetId) throws MalformedURLException, IOException, ProtocolException {

        boolean refreshed = false;
        int retryCount = 0;
        while (!refreshed && retryCount < 5) {
            try {
                retryCount++;
                // Trigger indexing on elastic search...
                URL urlToRepo = new URL(loginInfoEntry.getRepositoryUrl());
                URL refreshUrl = new URL("http", urlToRepo.getHost(), 9200, "/assets/_refresh");
                HttpURLConnection urlCon = (HttpURLConnection) refreshUrl.openConnection();
                urlCon.setRequestMethod("POST");
                urlCon.getResponseCode();

                URL checkIfRefreshed = new URL("http", urlToRepo.getHost(), 9200, "/assets/asset/" + assetId);
                logger.log(Level.INFO, "Checking refresh on " + checkIfRefreshed);
                HttpURLConnection checkUrlCon = (HttpURLConnection) checkIfRefreshed.openConnection();
                checkUrlCon.setDoInput(true);
                checkUrlCon.setRequestMethod("GET");
                int respCode = checkUrlCon.getResponseCode();

                if (respCode == 200) {
                    InputStream inputStream = checkUrlCon.getInputStream();
                    String inputStreamString = null;
                    if (inputStream != null) {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        int read = -1;
                        while ((read = inputStream.read()) != -1) {
                            outputStream.write(read);
                        }
                        inputStreamString = outputStream.toString(RestClient.getCharset(checkUrlCon.getContentType()));

                        if (inputStreamString.contains("\"found\":true")) {
                            refreshed = true;
                            logger.log(Level.INFO, "Requested item found in index");
                        } else {
                            logger.log(Level.INFO, "Got " + inputStreamString);
                            // Give up after 5 fails
                            if (retryCount >= 5) {
                                logger.log(Level.INFO, "Giving up waiting for indexing to complete.");
                                refreshed = true;
                            } else {
                                logger.log(Level.INFO, "Requested item NOT found in index - retrying index refresh.");
                            }
                            try {
                                // TODO this should be enough ... if it DOES happen again we might add an inner loop 
                                // to retry the checkIfRefreshed URL
                                Thread.sleep(1000);
                            } catch (InterruptedException ie) {
                                // Just swallow this
                            }
                        }
                    }
                } else {
                    logger.log(Level.INFO, "Response code was not 200, it was " + respCode);
                }

                // Now validate that the index that elastic search has created is usable by Massive
                if (refreshed) {
                    refreshed = verifyElasticSearchIndexThroughMassive(loginInfoEntry, assetId);
                }

            } catch (IOException ex) {
                // I think an exception can be thrown if the refresh hasn't finished, wait 1 second and retry. Give up after 5 times
                // Give up after 5 fails and let exception propogate up
                if (retryCount >= 5) {
                    throw ex;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    // Just swallow this
                }

            } finally {
                if (retryCount >= 5 && !refreshed) {
                    throw new IOException("Failed to refresh elastic ");
                }
            }

        }
    }

    /**
     * This is called once elastic search has recreated an index to confirm that the a provided asset can be found
     * through a massive find.
     * 
     * @param loginInfoEntry
     * @param assetId
     * @return true if the specified asset can be read through Massive
     */
    public static boolean verifyElasticSearchIndexThroughMassive(RestRepositoryConnection connection, String assetId) {
        boolean refreshed = true;
        try {
            // check that we can get the target asset through a massive find 
            RepositoryResource mr = connection.getResource(assetId);
            String name = mr.getName();
            Collection<? extends RepositoryResource> results = new RepositoryConnectionList(stripLoginAuthentication(connection)).findResources(name, null, null, null);

            boolean found = false;
            // Loop through the results from the find to ensure the asset we are keying on is in the returned list
            for (RepositoryResource result : results) {
                if (result.getId().equals(assetId)) {
                    found = true;
                    break;
                }
            }
            if (found) {
                logger.log(Level.INFO, "Massive find found the key asset after elastic search refreshed...continuing");
            } else {
                logger.log(Level.INFO, "Massive find could not find the key asset that elastic search said was indexed ... forcing an elastic search re-index");
                refreshed = false; // invalidate the index
            }
        } catch (Exception e) {
            logger.log(Level.INFO, "Exception thrown retrieving a resource through massive find: " + e);
            refreshed = false;
            e.printStackTrace();
        }

        return refreshed;
    }

    /**
     * Run a query to get the weighting of search results back from elastic search based on a pre-prepared query
     * 
     * @throws IOException
     */
    public static void queryElasticSearchWeighting(String encodedSearchString, RestRepositoryConnection loginInfoEntry, String marketplaceId) throws IOException {

        // make the following rest call
        // http://<machine>:9200/assets/_search?q=+description:(keyword1 keyword3) +marketplaceId:(<marketplaceId>);
        URL urlToRepo = new URL(loginInfoEntry.getRepositoryUrl());
        URL queryUrl = new URL("http", urlToRepo.getHost(), 9200, "/assets/_search?q=%28" + encodedSearchString + "%29%20+marketplaceId:%28" + marketplaceId + "%29");
        logger.log(Level.INFO, "about to poke: " + queryUrl.toString());

        HttpURLConnection urlCon = (HttpURLConnection) queryUrl.openConnection();
        urlCon.setRequestMethod("GET");
        int respCode = urlCon.getResponseCode();
        logger.log(Level.INFO, "RC=" + respCode);

        String inputStreamString = null;
        if (respCode == 200) {
            logger.log(Level.INFO, String.format("%nFormatted asset weighting from elastic search"));
            InputStream inputStream = urlCon.getInputStream();
            if (inputStream != null) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                int read = -1;
                while ((read = inputStream.read()) != -1) {
                    outputStream.write(read);
                }
                inputStreamString = outputStream.toString(RestClient.getCharset(urlCon.getContentType()));
                logger.log(Level.INFO, "queryElasticSearchWeighting returned: " + inputStreamString);
            }

            summarizeElasticSearchWeighting(inputStreamString);
            logger.log(Level.INFO, "");
        } else {
            logger.log(Level.INFO, "queryElasticSearchWeighting returned: rc" + respCode);
        }
    }

    /**
     * Parse the JSON doc produced by the query and summarise the results to the log and to a List that is returned.
     * 
     * @param jsonString
     * @return a list of the results produced
     * @throws IOException
     */
    private static List<String> summarizeElasticSearchWeighting(String jsonString) throws IOException {
        /*
         * This parses the output from a JSON query. The structure of that doc is
         * {
         * ..hits: { < JSONObject
         * ....hits: [ < JSONArray
         * ......{ JSONObject #1 }, < JSONObject
         * ......{ JSONObject #2 } < JSONObject
         * ....]
         * ..}
         * }
         */
        List<String> output = new ArrayList<String>();

        JSONObject doc = JSONObject.parse(jsonString);
        JSONObject hits = (JSONObject) doc.get("hits");
        JSONArray hitsArray = (JSONArray) hits.get("hits");
        @SuppressWarnings("unchecked")
        Iterator<JSONObject> it = hitsArray.iterator();
        while (it.hasNext()) {
            JSONObject jObject = it.next();
            Double score = (Double) jObject.get("_score");

            JSONObject jSource = (JSONObject) jObject.get("_source");
            String description = (String) jSource.get("description");
            String name = (String) jSource.get("name");

            String line = "score=" + score + ",name=" + name + ", description=" + description;
            logger.log(Level.INFO, line);
            output.add(line);
        }

        // output list not used at the time of writing but it is there if needed.
        return output;
    }

    /**
     * The find tests have to run with authentication stripped off due to a bug in massive.
     * This is a convenience method to remove the username/password for the LoginInfoEntry
     */
    public static RestRepositoryConnection stripLoginAuthentication(RestRepositoryConnection authenticated) {

        RestRepositoryConnection unauthenticatedLogin = new RestRepositoryConnection(null, null, authenticated.getApiKey(), authenticated.getRepositoryUrl(),
                        authenticated.getSoftlayerUserId(), authenticated.getSoftlayerPassword(),
                        authenticated.getAttachmentBasicAuthUserId(), authenticated.getAttachmentBasicAuthPassword());

        return unauthenticatedLogin;
    }

    public static void blowUpIfUsingLiveSystem(RestRepositoryConnection loginInfoEntry) {
        if (loginInfoEntry.getApiKey().equals(LIVE_KEY)) {
            throw new RuntimeException(
                            "YOU ARE RUNNING AGAINGST THE LIVE SYSTEM, ABORTING. If you want to run againgst the live system please override TestBaseClass.blowUpIfUsingLiveSystem in your test");
        }
    }

}
