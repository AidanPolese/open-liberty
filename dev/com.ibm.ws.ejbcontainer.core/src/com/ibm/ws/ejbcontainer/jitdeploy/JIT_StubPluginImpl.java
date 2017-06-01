/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.jitdeploy;

/**
 * Override JIT_StubPluginImpl as provided by traditional WAS. Liberty doesn't support
 * plugin stubs.
 */
public final class JIT_StubPluginImpl {

    public static boolean register(ClassLoader classLoader) {
        return false;
    }

}
