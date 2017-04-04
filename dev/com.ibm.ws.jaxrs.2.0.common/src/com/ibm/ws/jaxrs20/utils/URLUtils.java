/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxrs20.utils;

/**
 *
 */
public class URLUtils {
    /**
     * Checks to see if URI is absolute or relative.
     * 
     * @param uri
     * @return true it is absolute or false if not
     */
    public static boolean isAbsolutePath(String uri) {
        boolean absolute = false;
        if (uri != null) {
            if (uri.indexOf(":/") != -1) {
                absolute = true;
            } else if (uri.indexOf(":\\") != -1) {
                absolute = true;
            }
        }

        return absolute;
    }

    /**
     * Normalize the path, for example, the context root path. The rules are:
     * 1. Return null if the path is null
     * 2. Return "" if the path is an empty string.
     * 3. Replace all "\" by "/" in the path string.
     * 4. Add "/" if the path does not start with "/"
     * 
     * @param path
     * @return the normalized path string.
     * 
     */
    public static String normalizePath(String path) {
        if (path == null) {
            return null;
        }

        path = path.trim();

        if (path.isEmpty()) {
            return "";
        }

        path = path.replace("\\", "/");

        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }
}
