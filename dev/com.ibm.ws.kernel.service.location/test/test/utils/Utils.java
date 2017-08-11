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
 */
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
    public static final String TEST_DATA_DIR = "bin_test/test data";

    public static final File TEST_DATA = new File(TEST_DATA_DIR);

    public static final String OUTPUT_DATA_DIR = "generated/output data";

    public static final File OUTPUT_DATA = new File(OUTPUT_DATA_DIR);

    private static final File testRoot = new File("generated/tmp");

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
