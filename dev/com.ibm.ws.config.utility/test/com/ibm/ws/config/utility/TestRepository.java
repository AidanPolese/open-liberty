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
package com.ibm.ws.config.utility;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Rule;
import org.junit.rules.TestName;

import com.ibm.ws.repository.connections.RestRepositoryConnection;
import com.ibm.ws.repository.connections.liberty.MainRepository;

/**
 *
 * TestRepository makes sure that the connection to the repository is successful
 */
public class TestRepository {

    private static Class<?> logClass = TestRepository.class;
    private static final String className = logClass.getCanonicalName();
    private static Logger logger = Logger.getLogger(className);
    public static boolean connectedToRepo = false;
    public static String massiveRepoFile;
    private static String repositoryUrl;

    @Rule
    public final TestName method = new TestName();
    private static final String SLASH = System.getProperty("file.separator");

    public boolean testRepositoryConnection() {
        logger.entering(className, method.getMethodName());
        try {
            massiveRepoFile = "publish" + SLASH + "files" + SLASH + "massive" + SLASH + "massiveRepoLocation.props";

            String repositoryDescriptionUrl = new File("publish/files/massive/massiveRepoLocation.props").getCanonicalFile().toURI().toURL().toString();
            System.setProperty("repository.description.url", repositoryDescriptionUrl);

            RestRepositoryConnection connection = MainRepository.createConnection();
            connectedToRepo = connection.isRepositoryAvailable();

            if (connectedToRepo) {
                logger.logp(Level.INFO, className, method.getMethodName(), "Repository connection is OK.");
            } else {
                connectedToRepo = false;
                logger.logp(Level.INFO, className, method.getMethodName(), "Cannot connect to the test repository: " + repositoryUrl);
            }

        } catch (Exception e) {
            logger.logp(Level.INFO, className, method.getMethodName(), "Cannot connect to the repository : " + e.getMessage());
            connectedToRepo = false;
        } finally {
            MainRepository.clearCachedRepoProperties();
        }

        logger.exiting(className, method.getMethodName());
        return connectedToRepo;
    }
}
