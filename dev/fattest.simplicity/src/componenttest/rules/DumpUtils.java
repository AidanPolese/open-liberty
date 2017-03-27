/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package componenttest.rules;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.runner.Description;

import com.ibm.websphere.simplicity.log.Log;
import com.ibm.ws.jmx.connector.client.rest.ClientProvider;
import componenttest.topology.impl.LibertyServer;

/**
 *
 */
public class DumpUtils {

    private static final String CollectiveRepositoryMBean_OBJECT_NAME = "WebSphere:feature=collectiveController,type=CollectiveRepository,name=CollectiveRepository";
    private static final String COLLECTIVE_DUMP_URL = "/ibm/api/collective/v1/dump";
    private static final String BASIC_AUTH_CREDS = "YWRtaW46YWRtaW5wd2Q="; // base64 encoded "admin:adminpwd" if this ever changes this needs to be updated
    private static final File DEFAULT_DUMP_DIR = new File(System.getProperty("user.dir"), "results/dumps");

    private final LibertyServer server;
    private final String adminUser;
    private final String adminPassword;

    public DumpUtils(final LibertyServer server, final String adminUser, final String adminPassword) {
        this.server = server;
        this.adminUser = adminUser;
        this.adminPassword = adminPassword;
    }

    /**
     * Captures the Collective REST API and writes it to a file.
     */
    void captureCollectiveAPIDiagnostics(final Description desc, final String dumpFilePrefix) {
        captureCollectiveAPIDiagnostics(desc.getTestClass(), dumpFilePrefix);
    }

    /**
     * This method allows non-JUnit code to perform collective API dumps.
     */
    public void captureCollectiveAPIDiagnostics(final Class<?> c, final String dumpFilePrefix) {
        final File dumpFile = createFile(dumpFilePrefix + "api.json");
        Log.info(c, "captureCollectiveAPIDiagnostics", "Dumping collective REST API to file " + dumpFile.getAbsolutePath());
        BufferedReader in = null;
        BufferedWriter out = null;
        try {
            setupHttpsConnection();
            URL url = new URL("https", server.getHostname(), server.getHttpDefaultSecurePort(), COLLECTIVE_DUMP_URL);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

            httpConnection.setRequestProperty("Authorization", "Basic " + BASIC_AUTH_CREDS);
            httpConnection.setUseCaches(false);

            in = new BufferedReader(new InputStreamReader(httpConnection.getInputStream(), "UTF-8"));
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dumpFile)));
            for (String s = in.readLine(); s != null; s = in.readLine()) {
                out.write(s);
            }
        } catch (Throwable t) {
            // Catch and supress all errors, this is debug and we don't want to mess up the JUnit error handling
            Log.error(c, "captureCollectiveAPIDiagnostics", t, "Unable to dump collective REST API. Caught exception: " + t.getMessage());
        } finally {
            close(in);
            close(out);
        }
    }

    /**
     * Creates the File object, and any necessary parent directories.
     * 
     * @param filename The file name
     * @return The created File object
     */
    private File createFile(final String filename) {
        DEFAULT_DUMP_DIR.mkdirs();
        return new File(DEFAULT_DUMP_DIR, filename);
    }

    /**
     * Dump the Collective Repository.
     */
    void dumpCollectiveRepository(final Description desc, final String dumpFilePrefix) {
        dumpCollectiveRepository(desc.getTestClass(), dumpFilePrefix);
    }

    /**
     * This method allows non-JUnit code to perform collective repository dumps
     */
    public void dumpCollectiveRepository(final Class<?> c, final String dumpFilePrefix) {
        JMXConnector jmxConnector = null;
        try {
            final File dumpFile = createFile(dumpFilePrefix + "repository.txt");
            Log.info(c, "dumpCollectiveRepository", "Dumping collective repository to file " + dumpFile.getAbsolutePath());

            jmxConnector = createJMXConnection();
            final MBeanServerConnection mbsc = jmxConnector.getMBeanServerConnection();
            final ObjectName repository = new ObjectName(CollectiveRepositoryMBean_OBJECT_NAME);
            mbsc.invoke(repository, "dump", new Object[] { "/", dumpFile.getAbsolutePath(), null }, new String[] { "java.lang.String", "java.lang.String", "java.lang.String" });
        } catch (Throwable t) {
            // Catch and supress all errors, this is debug and we don't want to mess up the JUnit error handling
            Log.error(c, "dumpCollectiveRepository", t, "Unable to dump collective repository. Caught exception: " + t.getMessage());
        } finally {
            if (jmxConnector != null) {
                close(jmxConnector);
            }
        }
    }

    /**
     * Tries to close the Closeable.
     * 
     * @param c The Closeable, may be {@code null}.
     */
    private void close(Closeable c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (Exception e) {
            // Ignore it
        }
    }

    /**
     * Initialize the HTTP connection for this JVM to ignore SSL security.
     * These tests are not SSL centric and we can safely ignore SSL in our tests.
     */
    private void setupHttpsConnection() throws NoSuchAlgorithmException, KeyManagementException {

        // Ignore SSL certificate trust
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {}

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
        } };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Ignore host names during SSL validation
        HostnameVerifier hv = new HostnameVerifier() {
            @Override
            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }

    /**
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws MalformedURLException
     * @throws IOException
     */
    private JMXConnector createJMXConnection() throws NoSuchAlgorithmException, KeyManagementException, MalformedURLException, IOException {
        // Ignore SSL certificate trust
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {}

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
        } };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Ignore host names during SSL validation
        HostnameVerifier hv = new HostnameVerifier() {
            @Override
            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);

        // Establish up JMX connection
        Map<String, Object> environment = new HashMap<String, Object>();
        environment.put("jmx.remote.protocol.provider.pkgs", "com.ibm.ws.jmx.connector.client");
        environment.put(JMXConnector.CREDENTIALS, new String[] { adminUser, adminPassword });
        environment.put(ClientProvider.DISABLE_HOSTNAME_VERIFICATION, true);
        environment.put(ClientProvider.READ_TIMEOUT, 2 * 60 * 1000);
        JMXServiceURL url = new JMXServiceURL("REST", "localhost", server.getHttpDefaultSecurePort(), "/IBMJMXConnectorREST");
        return JMXConnectorFactory.connect(url, environment);
    }
}
