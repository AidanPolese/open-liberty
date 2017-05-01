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
package test.utils;

import java.io.File;

/**
 *
 */
public class SharedConstants {
    /**
     * Test data directory: note the space! always test paths with spaces. Dratted
     * windows.
     */
    public static final String TEST_DATA_DIR = "generated/test/test data";

    public static final File TEST_DATA_FILE = new File(TEST_DATA_DIR);

    /** Test dist dir: where our generated sample jars live */
    public static final String TEST_DIST_DIR = "generated/test/test data/lib";
}
