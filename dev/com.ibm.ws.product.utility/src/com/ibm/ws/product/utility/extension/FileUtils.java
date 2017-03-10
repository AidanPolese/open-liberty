package com.ibm.ws.product.utility.extension;

import java.io.Closeable;
import java.io.IOException;

public class FileUtils {
    public static boolean tryToClose(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
                return true;
            } catch (IOException e) {
            }
        }
        return false;
    }

    /**
     * Returns <code>true</code> if the supplied <code>fileName</code> has an
     * extension that matches the expected extension. This is not case
     * sensitive.
     * 
     * @param expectedFileExtension
     *            The expected file name extension in lower case and including
     *            the '.'
     * @param fileName
     *            The file name to test
     * @return <code>true</code> if the file extension matches
     */
    public static boolean matchesFileExtension(String expectedFileExtension,
                                               String fileName) {
        // case insensitive match of the last x characters.
        return fileName.regionMatches(true,
                                      fileName.length() - expectedFileExtension.length(),
                                      expectedFileExtension,
                                      0,
                                      expectedFileExtension.length());
    }
}
