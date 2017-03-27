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
package com.ibm.ws.config.utility.fat;

import java.io.File;

import org.junit.Rule;
import org.junit.rules.TestName;

import com.ibm.websphere.simplicity.log.Log;
import com.ibm.ws.repository.connections.RestRepositoryConnection;
import com.ibm.ws.repository.connections.liberty.MainRepository;

import componenttest.topology.impl.LibertyServer;
import componenttest.topology.impl.LibertyServerFactory;

/**
 * 
 * TestRepository makes sure that the connection to the repository is successful
 */
public class TestRepository {

    private static Class<?> logClass = TestRepository.class;
    public static boolean connectedToRepo = false;
    public static String massiveRepoFile;
    private static String repositoryUrl;
    public static LibertyServer server = LibertyServerFactory.getLibertyServer("com.ibm.ws.config.utility.fat");

    @Rule
    public final TestName method = new TestName();
    private static final String SLASH = System.getProperty("file.separator");

    @SuppressWarnings("deprecation")
    public boolean testRepositoryConnection() {
        Log.entering(logClass, method.getMethodName());
        try {
            massiveRepoFile = server.pathToAutoFVTTestFiles + "massive" + SLASH + "massiveRepoLocation.props";

            String repositoryDescriptionUrl = new File(massiveRepoFile).getCanonicalFile().toURI().toURL().toString();
            System.setProperty("repository.description.url", repositoryDescriptionUrl);

            RestRepositoryConnection connection = MainRepository.createConnection();
            connectedToRepo = connection.isRepositoryAvailable();

            if (connectedToRepo) {
                Log.info(logClass, method.getMethodName(), "Repository connection is OK.");
            } else {
                connectedToRepo = false;
                Log.info(logClass, method.getMethodName(), "Cannot connect to the test repository: " + repositoryUrl);
            }

        } catch (Exception e) {
            Log.info(logClass, method.getMethodName(), "Cannot connect to the repository : " + e.getMessage());
            connectedToRepo = false;
        } finally {
            MainRepository.clearCachedRepoProperties();
        }

        Log.exiting(logClass, method.getMethodName());
        return connectedToRepo;
    }

}
