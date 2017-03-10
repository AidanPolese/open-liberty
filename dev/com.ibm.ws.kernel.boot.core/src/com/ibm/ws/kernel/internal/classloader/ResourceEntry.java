/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.ws.kernel.internal.classloader;

import java.io.IOException;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.jar.Manifest;

/**
 */
public interface ResourceEntry {

    ResourceHandler getResourceHandler();

    Manifest getManifest() throws IOException;

    Certificate[] getCertificates();

    byte[] getBytes() throws IOException;

    URL toURL();

}
