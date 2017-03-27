/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change activity:
 *
 * Issue       Date        Name      Description
 * ----------- ----------- --------- ------------------------------------
 *                                   Initial version
 */
package test.shared;

import java.io.File;

/**
 *
 */
public class Constants {
    /**
     * Test data directory: note the space! always test paths with spaces.
     * Dratted windows.
     */
    public static final String TEST_DATA_DIR = "../com.ibm.ws.kernel.boot_test/build/unittest/test data/";

    public static final File TEST_DATA_FILE = new File(TEST_DATA_DIR);

    /** Test dist dir: where our generated sample jars live */
    public static final String TEST_DIST_DIR = "../com.ibm.ws.kernel.boot_test/build/unittest/test data/lib/";

    /** Test dist dir: where our generated sample jars live */
    public static final File TEST_DIST_DIR_FILE = new File(TEST_DIST_DIR);

    public static final String TEST_PLATFORM_DIR = "../com.ibm.ws.kernel.boot_test/build/unittest/test data/lib/platform/";

    public static final String TEST_TMP_ROOT = "../com.ibm.ws.kernel.boot_test/build/tmp/";

    public static final File TEST_TMP_ROOT_FILE = new File(TEST_TMP_ROOT);

    /** The dist dir containing the real jar file for the bootstrap */
    public static final String BOOTSTRAP_LIB_DIR = "../com.ibm.ws.kernel.boot/build/lib/";

    public static final String BOOTSTRAP_PUBLISH_DIR = "../com.ibm.ws.kernel.boot/publish/";

    public static final String MOCK_FRAMEWORK_LAUNCH = "MOCK_FRAMEWORK_LAUNCH";
}
