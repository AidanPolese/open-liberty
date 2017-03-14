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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import test.common.SharedOutputManager;

import com.ibm.ws.config.utility.TestRepository;

/**
 *
 */
public class RepositoryPathUtilityTest extends TestRepository {
    static SharedOutputManager outputMgr = SharedOutputManager.getInstance();
    @Rule
    public TestRule managerRule = outputMgr;

    private static final String EXPECTED_UNIX_PATH = "%2Fwlp%2Fusr";
    private static final String EXPECTED_WINDOWS_PATH = "C%3A%2Fwlp%2Fusr";

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#normalizePath(java.lang.String)}.
     * 
     * @throws Exception
     */
    @Test
    public void normalizePath_null() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertNull("Path is allowed to be null",
                   RepositoryPathUtility.normalizePath(null));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#normalizePath(java.lang.String)}.
     */
    @Test
    public void normalizePath_empty() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("Path is allowed to be empty",
                     "", RepositoryPathUtility.normalizePath(""));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#normalizePath(java.lang.String)}.
     */
    @Test
    public void normalizePath_extraSlashesCase() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("Path normalization did not go as expected",
                     "/wlp/usr", RepositoryPathUtility.normalizePath("//wlp//usr//"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#normalizePath(java.lang.String)}.
     */
    @Test
    public void normalizePath_shortLibertyProfile() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("Path encoding did not go as expected",
                     "/lp", RepositoryPathUtility.normalizePath("/lp"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#normalizePath(java.lang.String)}.
     */
    @Test
    public void normalizePath_windowsJustLowerCaseDriveLetter() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("Path encoding did not go as expected",
                     "C:/", RepositoryPathUtility.normalizePath("c:\\\\"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#normalizePath(java.lang.String)}.
     */
    @Test
    public void normalizePath_urlFilePrefixWindows() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("Path encoding did not go as expected",
                     "C:/wlp/usr", RepositoryPathUtility.normalizePath("file:/c:/wlp/usr/"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#getURLEncodedPath(java.lang.String)}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void getURLEncodedPath_null() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        RepositoryPathUtility.getURLEncodedPath(null);
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#getURLEncodedPath(java.lang.String)}.
     */
    @Test
    public void getURLEncodedPath_empty() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("Path encoding did not go as expected, an empty path should do nothing",
                     "", RepositoryPathUtility.getURLEncodedPath(""));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#getURLEncodedPath(java.lang.String)}.
     */
    @Test
    public void getURLEncodedPath_simpleCase() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("Path encoding did not go as expected",
                     EXPECTED_UNIX_PATH, RepositoryPathUtility.getURLEncodedPath("/wlp/usr"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#getURLEncodedPath(java.lang.String)}.
     */
    @Test
    public void getURLEncodedPath_trailingSlashCase() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("Path encoding did not go as expected",
                     EXPECTED_UNIX_PATH, RepositoryPathUtility.getURLEncodedPath("/wlp/usr/"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#getURLEncodedPath(java.lang.String)}.
     */
    @Test
    public void getURLEncodedPath_extraSlashesCase() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("Path encoding did not go as expected",
                     EXPECTED_UNIX_PATH, RepositoryPathUtility.getURLEncodedPath("//wlp//usr//"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#getURLEncodedPath(java.lang.String)}.
     */
    @Test
    public void getURLEncodedPath_windowsJustDriveLetter() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("Path encoding did not go as expected",
                     "C%3A%2F", RepositoryPathUtility.getURLEncodedPath("C:\\\\"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#getURLEncodedPath(java.lang.String)}.
     */
    @Test
    public void getURLEncodedPath_windowsJustLowerCaseDriveLetter() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("Path encoding did not go as expected",
                     "C%3A%2F", RepositoryPathUtility.getURLEncodedPath("c:\\\\"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#getURLEncodedPath(java.lang.String)}.
     */
    @Test
    public void getURLEncodedPath_windowsSlashCase() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("Path encoding did not go as expected",
                     EXPECTED_WINDOWS_PATH, RepositoryPathUtility.getURLEncodedPath("C:\\wlp\\usr\\"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#getURLEncodedPath(java.lang.String)}.
     */
    @Test
    public void getURLEncodedPath_windowsWithExtraSlashesCase() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("Path encoding did not go as expected",
                     EXPECTED_WINDOWS_PATH, RepositoryPathUtility.getURLEncodedPath("C:\\\\wlp\\\\usr\\\\"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#getURLEncodedPath(java.lang.String)}.
     */
    @Test
    public void getURLEncodedPath_windowsLowerCaseDriveLetterConvertedToUpperCase() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("Path encoding did not go as expected",
                     EXPECTED_WINDOWS_PATH, RepositoryPathUtility.getURLEncodedPath("c:\\\\wlp\\\\usr\\\\"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#getURLEncodedPath(java.lang.String)}.
     */
    @Test
    public void getURLEncodedPath_windowsPathWithUnixSlash() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("Path encoding did not go as expected",
                     EXPECTED_WINDOWS_PATH, RepositoryPathUtility.getURLEncodedPath("c:/wlp/usr/"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#getURLEncodedPath(java.lang.String)}.
     */
    @Test
    public void getURLEncodedPath_urlFilePrefixUnix() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("Path encoding did not go as expected",
                     EXPECTED_UNIX_PATH, RepositoryPathUtility.getURLEncodedPath("file:/wlp/usr/"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#getURLEncodedPath(java.lang.String)}.
     */
    @Test
    public void getURLEncodedPath_urlFilePrefixUnixMultiSlash() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("Path encoding did not go as expected",
                     EXPECTED_UNIX_PATH, RepositoryPathUtility.getURLEncodedPath("file://wlp/usr/"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#getURLEncodedPath(java.lang.String)}.
     */
    @Test
    public void getURLEncodedPath_urlFilePrefixWindows() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("Path encoding did not go as expected",
                     EXPECTED_WINDOWS_PATH, RepositoryPathUtility.getURLEncodedPath("file:/c:/wlp/usr/"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#getURLEncodedPath(java.lang.String)}.
     */
    @Test
    public void getURLEncodedPath_urlFilePrefixWindowsMultiSlash() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("Path encoding did not go as expected",
                     EXPECTED_WINDOWS_PATH, RepositoryPathUtility.getURLEncodedPath("file://c:/wlp/usr/"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#getURLEncodedPath(java.lang.String)}.
     * <p>
     * Why would a customer do this? I have NO idea.... but if they do, we should honor it.
     * If a customer chooses to do this, are they're going to hit some latent bugs? Not sure.
     */
    @Ignore("This will not work because PathUtils.normalize considers this a Windows path")
    @Test
    public void getURLEncodedPath_windowsLikePathNotMistakenForWindowsPath() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("Path encoding did not go as expected",
                     "%2Fc%3A%2Fwlp%2Fusr", RepositoryPathUtility.getURLEncodedPath("/c:/wlp/usr/"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#getURLEncodedPath(java.lang.String)}.
     */
    @Test
    public void getURLEncodedPath_root() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("Path encoding did not go as expected",
                     "%2F", RepositoryPathUtility.getURLEncodedPath("/"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#getURLEncodedPath(java.lang.String)}.
     */
    @Test
    public void getURLEncodedPath_rootDoubleSlashed() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("Path encoding did not go as expected",
                     "%2F", RepositoryPathUtility.getURLEncodedPath("//"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#getURLEncodedPath(java.lang.String)}.
     */
    @Test
    public void getURLEncodedPath_rootOverSlashed() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("Path encoding did not go as expected",
                     "%2F", RepositoryPathUtility.getURLEncodedPath("/////"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#getURLEncodedPath(java.lang.String)}.
     */
    @Test
    public void getURLEncodedPath_twoCharUnix() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("Path encoding did not go as expected",
                     "%2Fa", RepositoryPathUtility.getURLEncodedPath("/a"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#getURLEncodedPath(java.lang.String)}.
     */
    @Test
    public void getURLEncodedPath_withCommas() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("Path encoding did not go as expected",
                     "%2Fwlp%2Fmy%2Cusr", RepositoryPathUtility.getURLEncodedPath("/wlp/my,usr"));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.RepositoryPathUtility#decodeURLEncodedDir(String)}.
     */
    @Test
    public void decodeURLEncodedDir_simpleCase() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        assertEquals("String decoding did not go as expected",
                     "a", RepositoryPathUtility.decodeURLEncodedDir("a"));
    }

    /**
     * Test to make sure that a encode -> decode results in a simple and
     * consistent path.
     */
    @Test
    public void pathConversionViaEncoding_linuxSimple() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        String encoded = RepositoryPathUtility.getURLEncodedPath("/wlp/usr/");
        assertEquals("String decoding did not go as expected",
                     "/wlp/usr", RepositoryPathUtility.decodeURLEncodedDir(encoded));
    }

    /**
     * Test to make sure that a encode -> decode results in a simple and
     * consistent path.
     */
    @Test
    public void pathConversionViaEncoding_linuxURL() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        String encoded = RepositoryPathUtility.getURLEncodedPath("file:/wlp/usr/");
        assertEquals("String decoding did not go as expected",
                     "/wlp/usr", RepositoryPathUtility.decodeURLEncodedDir(encoded));
    }

    /**
     * Test to make sure that a encode -> decode results in a simple and
     * consistent path.
     */
    @Test
    public void pathConversionViaEncoding_windowsSimple() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        String encoded = RepositoryPathUtility.getURLEncodedPath("C:/wlp/usr/");
        assertEquals("String decoding did not go as expected",
                     "C:/wlp/usr", RepositoryPathUtility.decodeURLEncodedDir(encoded));
    }

    /**
     * Test to make sure that a encode -> decode results in a simple and
     * consistent path.
     */
    @Test
    public void pathConversionViaEncoding_windowsURL() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        String encoded = RepositoryPathUtility.getURLEncodedPath("file:/c:/wlp/usr/");
        assertEquals("String decoding did not go as expected",
                     "C:/wlp/usr", RepositoryPathUtility.decodeURLEncodedDir(encoded));
    }
}
