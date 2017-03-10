/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.launch.service;

import java.io.File;

import com.ibm.ws.kernel.provisioning.ServiceFingerprint;

/**
 * Product extension locations are resolved in the OSGi framework, but the ServiceFingerprint
 * is managed in the bootstrap code (i.e. outside the OSGi framework).
 * 
 * This class is a bridge between the OSGi framework and the ServiceFingerprint loaded in the
 * bootstrap code. It is loaded in a classloader outside of the OSGi framework and delegated to
 * from inside the OSGi framework.
 */
public class ProductExtensionServiceFingerprint {

    public static void putProductExtension(String productExtensionName, String productExtensionInstallLocation) {
        ServiceFingerprint.putInstallDir(productExtensionName, new File(productExtensionInstallLocation));
    }

}
