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
package test.utils;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class Utils {
    /**
     * Test data directory: note the space! always test paths with spaces. Dratted
     * windows.
     */
    public static final String TEST_DATA_DIR = "../com.ibm.ws.kernel.service_test/unittest/test data";

    public static final File TEST_DATA = new File(TEST_DATA_DIR);

    public static final String OUTPUT_DATA_DIR = "../com.ibm.ws.kernel.service_test/build/output data";

    public static final File OUTPUT_DATA = new File(OUTPUT_DATA_DIR);

    private static final File testRoot = new File("../com.ibm.ws.kernel.service_test/build/unittest/tmp");

    public static void recursiveClean(final File fileToRemove) {
        if (fileToRemove == null)
            return;

        if (!fileToRemove.exists())
            return;

        if (fileToRemove.isDirectory()) {
            File[] files = fileToRemove.listFiles();
            for (File file : files) {
                if (file.isDirectory())
                    recursiveClean(file);
                else
                    file.delete();
            }
        }

        fileToRemove.delete();
    }

    public static File createTempFile(String name, String suffix) throws IOException {
        if (!testRoot.exists()) {
            testRoot.mkdirs();
        }
        return File.createTempFile(name, suffix, testRoot);
    }

    public static File createTempFile(String name, String suffix, File dir) throws IOException {
        if (!testRoot.exists()) {
            testRoot.mkdirs();
        }
        return File.createTempFile(name, suffix, dir);
    }
}
