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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.JsonGenerator.Feature;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;

import com.ibm.ws.repository.connections.RestRepositoryConnection;

/**
 * Deletes all the entries in a repo. This is a standalone util that does not
 * rely on the MassiveClient so that it will work even if the repo being deleted
 * has backwards breaking changes, sadly this leads to some code duplication but
 * this is a necessary evil to be able to delete "broken" repos.
 */
public class DeleteRepo {

    private static final String URL_BASE = "https://5.153.49.84/ma/v1";
    private static final String SOFTLAYER_USER_ID = "liberty";
    private static final String SOFTLAYER_USER_PASSWORD = "W3bSp#3reD!";
    private static final String USER_ID = "svt2@ibm.com";
    private static final String PASSWORD = "g0pher##";
    private static final String API_KEY = "18965347087";
    private final static ObjectMapper MAPPER = new ObjectMapper();
    static {
        // Setup the mapper config, have one mapper for all
        // Printing to sysout at the mo so don't want to close it
        MAPPER.configure(Feature.AUTO_CLOSE_TARGET, false);

        // Use the date format dictated by Massive
        MAPPER.getSerializationConfig().setSerializationInclusion(
                                                                  Inclusion.NON_NULL);
        SimpleDateFormat df = new SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'");
        MAPPER.getDeserializationConfig().setDateFormat(df);
        MAPPER.getSerializationConfig().setDateFormat(df);

        // Ignore unknown attributes, we just care about IDs
        MAPPER.getDeserializationConfig()
                        .disable(
                                 org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public static void main(String[] args) throws JsonParseException,
                    JsonMappingException, IOException {
        RestRepositoryConnection loginInfo = new RestRepositoryConnection(USER_ID, PASSWORD, API_KEY,
                        URL_BASE, SOFTLAYER_USER_ID, SOFTLAYER_USER_PASSWORD);
        deleteRepo(loginInfo);
    }

    public static void deleteRepo(RestRepositoryConnection loginInfoResource) throws IOException {
        // Get all
        HttpURLConnection getAllConnection = createHttpURLConnection(loginInfoResource.getRepositoryUrl() + "/assets", loginInfoResource);
        getAllConnection.setRequestMethod("GET");
        List<IdHolder> assets = readValues(getAllConnection.getInputStream(), IdHolder.class);

        // Get all doesn't return attachments so call get attachments on each to
        // get them as there isn't a delete asset and attachment method on
        // massive so need to delete attachments first
        for (IdHolder asset : assets) {
            HttpURLConnection getAttachmentsConnection = createHttpURLConnection(loginInfoResource.getRepositoryUrl()
                                                                                 + "/assets/" + asset.get_id()
                                                                                 + "/attachments", loginInfoResource);
            getAttachmentsConnection.setRequestMethod("GET");
            List<IdHolder> attachments = readValues(getAttachmentsConnection.getInputStream(), IdHolder.class);
            Logger logger = Logger.getLogger(DeleteRepo.class.getName());
            for (IdHolder attachment : attachments) {
                logger.log(Level.INFO, "Deleting attachment " + attachment.get_id());
                HttpURLConnection deleteAttachmentConnection = createHttpURLConnection(loginInfoResource.getRepositoryUrl() + "/assets/"
                                                                                       + asset.get_id() + "/attachments/"
                                                                                       + attachment.get_id(), loginInfoResource);
                deleteAttachmentConnection.setRequestMethod("DELETE");
                if (HttpURLConnection.HTTP_OK != deleteAttachmentConnection.getResponseCode()) {
                    throw new RuntimeException("Unable to delete "
                                               + attachment.get_id() + " from " + asset.get_id()
                                               + ", got return code: "
                                               + deleteAttachmentConnection.getResponseCode());
                }
            }

            // Now delete asset
            logger.log(Level.INFO, "Deleting " + asset.get_id());
            HttpURLConnection deleteAssetConnection = createHttpURLConnection(loginInfoResource.getRepositoryUrl() + "/assets/" + asset.get_id(),
                                                                              loginInfoResource);
            deleteAssetConnection.setRequestMethod("DELETE");
            int responseCode = deleteAssetConnection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
                throw new RuntimeException("Unable to delete " + asset.get_id()
                                           + ", got return code: "
                                           + deleteAssetConnection.getResponseCode());
            }
        }
    }

    public static <T> List<T> readValues(InputStream inputStream, Class<T> type)
                    throws JsonParseException, JsonMappingException, IOException {
        JavaType jacksonType = TypeFactory.collectionType(List.class, type);
        return MAPPER.readValue(inputStream, jacksonType);
    }

    private static class IdHolder {
        private String _id;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

    }

    private static HttpURLConnection createHttpURLConnection(
                                                             final String urlString, final RestRepositoryConnection loginInfoResource) throws IOException {
        // Add the api key, might already have query parameters so check
        final String connectingString = urlString.contains("?") ? "&" : "?";
        URL url;
        try {
            url = AccessController.doPrivileged(new PrivilegedExceptionAction<URL>() {

                @Override
                public URL run() throws MalformedURLException {
                    return new URL(urlString + connectingString + "apiKey=" + loginInfoResource.getApiKey());
                }
            });
        } catch (PrivilegedActionException e) {
            throw (MalformedURLException) e.getCause();
        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        String basicAuthUserPass = null;

        if (loginInfoResource.getSoftlayerUserId() != null) {
            basicAuthUserPass = loginInfoResource.getSoftlayerUserId() + ":" + loginInfoResource.getSoftlayerPassword();
        } else if (loginInfoResource.getUserId() != null && loginInfoResource.getPassword() != null) {
            basicAuthUserPass = loginInfoResource.getUserId() + ":" + loginInfoResource.getPassword();
        }

        if (basicAuthUserPass != null) {
            String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(basicAuthUserPass.getBytes(Charset.forName("UTF-8")));
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

}
