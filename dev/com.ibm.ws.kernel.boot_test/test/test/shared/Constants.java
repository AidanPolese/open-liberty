/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
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
    public static final String TEST_DATA_DIR = "../com.ibm.ws.kernel.boot_test/bin_test/test data/";

    public static final File TEST_DATA_FILE = new File(TEST_DATA_DIR);

    /** Test dist dir: where our generated sample jars live */
    public static final String TEST_DIST_DIR = "../com.ibm.ws.kernel.boot_test/bin_test/test data/lib/";

    /** Test dist dir: where our generated sample jars live */
    public static final File TEST_DIST_DIR_FILE = new File(TEST_DIST_DIR);

    public static final String TEST_PLATFORM_DIR = "../com.ibm.ws.kernel.boot_test/bin_test/test data/lib/platform/";

    public static final String TEST_TMP_ROOT = "../com.ibm.ws.kernel.boot_test/build/tmp/";

    public static final File TEST_TMP_ROOT_FILE = new File(TEST_TMP_ROOT);

    /** The dist dir containing the real jar file for the bootstrap */
    public static final String BOOTSTRAP_LIB_DIR = "../com.ibm.ws.kernel.boot/generated/";

    public static final String BOOTSTRAP_PUBLISH_DIR = "../com.ibm.ws.kernel.boot/publish/";

    public static final String MOCK_FRAMEWORK_LAUNCH = "MOCK_FRAMEWORK_LAUNCH";
}
