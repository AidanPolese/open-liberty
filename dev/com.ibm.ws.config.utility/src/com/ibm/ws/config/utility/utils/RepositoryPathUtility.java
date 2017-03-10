/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.config.utility.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.wsspi.kernel.service.utils.PathUtils;

/**
 *
 */
public class RepositoryPathUtility {

    /**
     * Determines if the character is a lower-case alphabet character.
     * 
     * @param c
     * @return
     */
    @Trivial
    private static final boolean isLowerAlpha(char c) {
        return c >= 'a' && c <= 'z';
    }

    /**
     * Determines if the character is an upper-case alphabet character.
     * 
     * @param c
     * @return
     */
    @Trivial
    private static final boolean isUpperAlpha(char c) {
        return c >= 'A' && c <= 'Z';
    }

    /**
     * Determines if the character is an alphabet character.
     * 
     * @param c
     * @return
     */
    @Trivial
    private static final boolean isAlpha(char c) {
        return isLowerAlpha(c) || isUpperAlpha(c);
    }

    /**
     * Determines if the path is a Windows path, by checking to see if it
     * starts with a Windows drive letter, e.g. C:/
     * <p>
     * Note that the slash is required and expected to be a unix-style slash.
     * 
     * @param path
     * @return
     */
    @Trivial
    private static final boolean hasWindowsDrivePrefix(String path) {
        if (path.length() < 3) {
            return false;
        } else if (isAlpha(path.charAt(0)) &&
                   (path.charAt(1) == ':') &&
                   (path.charAt(2) == '/')) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Capitalize the case of the drive letter for the given Windows path.
     * If the path is not a Windows path, no work is done.
     * 
     * @param path a path, possibly absolute. Must not be {@code null}.
     * @return the path with a normalized drive letter
     */
    @Trivial
    private static final String capitalizeDriveLetter(String path) {
        if (hasWindowsDrivePrefix(path) && isLowerAlpha(path.charAt(0))) {
            return Character.toUpperCase(path.charAt(0)) + path.substring(1);
        } else {
            return path;
        }
    }

    /**
     * @see com.ibm.websphere.collective.repository.RepositoryPathUtilityMBean#normalizePath(String)
     */
    public static final String normalizePath(String path) {
        if (path == null) {
            return path;
        }

        // Path is empty, don't bother doing work
        if (path.isEmpty()) {
            return path;
        }

        // Normalize the path - this changes all \ to /
        path = PathUtils.normalize(path);
        // Remove duplicate slashes - ?
        path = path.replaceAll("//+", "/");

        // If we start with file: URL prefix, we were a URL... need to process.
        if (path.startsWith("file:")) {
            path = path.substring(5);
            // Remove leading slash if windows path
            if (path.length() > 3 && path.charAt(0) == '/' && path.charAt(2) == ':') {
                path = path.substring(1);
            }
        }
        path = capitalizeDriveLetter(path);

        // Remove any trailing slash in the right cases
        if (path.length() == 3 && hasWindowsDrivePrefix(path)) {
            // We have something like C:/, don't do anything!
        } else if (path.length() > 1 && path.charAt(path.length() - 1) == '/') {
            path = path.substring(0, path.length() - 1);
        }

        return path;
    }

    /**
     * @see com.ibm.websphere.collective.repository.RepositoryPathUtilityMBean#getURLEncodedPath(String)
     */
    public static final String getURLEncodedPath(String path) {
        if (path == null) {
            throw new IllegalArgumentException("getURLEncodedPath: path is null");
        }

        try {
            path = normalizePath(path);

            // Encode and return
            return URLEncoder.encode(path, "UTF8");
        } catch (UnsupportedEncodingException e) {
            String msg = "Got a really un expected UnsupportedEncodingException. A JVM with no UTF8 support!";
            throw new IllegalStateException(msg, e);
        }
    }

    /**
     * Internal method to decode a URL encoded directory. There is no
     * identified scenario where this would be needed externally, but we could
     * expose it in the future.
     * 
     * @param urlEncodedUserDir
     * @return The URL decoded String
     */
    public static final String decodeURLEncodedDir(String urlEncodedUserDir) {
        try {
            return URLDecoder.decode(urlEncodedUserDir, "UTF8");
        } catch (UnsupportedEncodingException e) {
            String msg = "Got a really un expected UnsupportedEncodingException. A JVM with no UTF8 support!";
            throw new IllegalStateException(msg, e);
        }
    }
}
