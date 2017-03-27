/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.zos.command.processing.zfat;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ibm.websphere.simplicity.log.Log;

@RunWith(Suite.class)
@SuiteClasses({ FATTest.class, 
	            ZosCommandHandlerTest.class })
/**
 * Suite.
 */
public class FATSuite {

    /**
     * For use with logging
     */
    private static final Class<?> c = FATSuite.class;
    // User product extension variables.
    public static final String USER_FEATURE_PATH = "usr/extension/lib/features/";
    public static final String USER_BUNDLE_PATH = "usr/extension/lib";
    public static final String USER_FEATURE_PRODTEST_MF = "productExtensions/features/userProdtest-1.0.mf";
    public static final String USER_BUNDLE_JAR = "bundles/test.user.prod.extension_1.0.0.jar";


    /**
     * Pre FAT processing.
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void setup() throws Exception {
        final String METHOD_NAME = "setup";
        Log.info(c, METHOD_NAME, "Entry.");

        // Install user extension.
        installUserProductExtension();

        Log.info(c, METHOD_NAME, "Exit.");
    }

    /**
     * Installs a specific product extension if the default USR location.
     * 
     * @throws Exception
     */
    public static void installUserProductExtension() throws Exception {
        String method = "installUserProductExtension";
        Log.info(c, method, "Entry. Intalling user product extension.");

        ZosCommandHandlerTest.server.copyFileToLibertyInstallRoot(USER_FEATURE_PATH, USER_FEATURE_PRODTEST_MF);
        assertTrue("User product feature: " + USER_FEATURE_PRODTEST_MF + " should have been copied to: " + USER_FEATURE_PATH,
                   ZosCommandHandlerTest.server.fileExistsInLibertyInstallRoot(USER_FEATURE_PATH + "userProdtest-1.0.mf"));

        ZosCommandHandlerTest.server.copyFileToLibertyInstallRoot(USER_BUNDLE_PATH, USER_BUNDLE_JAR);
        assertTrue("User product bundle: " + USER_BUNDLE_JAR + " should have been copied to: " + USER_BUNDLE_PATH,
                   ZosCommandHandlerTest.server.fileExistsInLibertyInstallRoot(USER_BUNDLE_PATH + "/test.user.prod.extension_1.0.0.jar"));

        Log.info(c, method, "Exit. User product extension using feature: " + USER_FEATURE_PRODTEST_MF + " has been installed.");
    }

}