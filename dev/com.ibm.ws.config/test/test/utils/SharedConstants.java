/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
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
public class SharedConstants {

    public static final char backSlash = '\\';
    public static final char forwardSlash = '/';

    public static final String SERVER_INSTALL_ROOT;
    public static final File SERVER_INSTALL_ROOT_FILE;

    public static final String SERVER_XML_INSTALL_ROOT;
    public static final File SERVER_XML_INSTALL_ROOT_FILE;

    static {
        String serverInstallRoot = "test-resources/test_config/";
        String serverXmlInstallRoot = "test-resources/test_xml_config/";

        try {
            serverInstallRoot = new File(serverInstallRoot).getCanonicalPath().trim().replace(backSlash, forwardSlash);
        } catch (IOException ioe) {
            serverInstallRoot = new File(serverInstallRoot).getAbsolutePath();
        }

        try {
            serverXmlInstallRoot = new File(serverXmlInstallRoot).getCanonicalPath().trim().replace(backSlash, forwardSlash);
        } catch (IOException ioe) {
            serverXmlInstallRoot = new File(serverXmlInstallRoot).getAbsolutePath();
        }

        SERVER_INSTALL_ROOT = serverInstallRoot;
        SERVER_XML_INSTALL_ROOT = serverXmlInstallRoot;

        SERVER_INSTALL_ROOT_FILE = new File(serverInstallRoot);
        SERVER_XML_INSTALL_ROOT_FILE = new File(serverXmlInstallRoot);
    }

}
