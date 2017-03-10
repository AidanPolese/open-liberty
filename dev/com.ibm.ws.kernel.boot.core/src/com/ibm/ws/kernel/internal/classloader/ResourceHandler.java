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

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.util.jar.Manifest;

/**
 */
public interface ResourceHandler extends Closeable {

    ResourceEntry getEntry(String name);

    URL toURL();

    Manifest getManifest() throws IOException;
}
