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
package com.ibm.ws.crypto.util;

import com.ibm.ws.crypto.util.AESKeyManager.KeyStringResolver;
import com.ibm.wsspi.kernel.service.location.VariableRegistry;

/**
 *
 */
public class VariableResolver implements KeyStringResolver {
    private VariableRegistry registry;

    public void setVariableRegistry(VariableRegistry vr) {
        registry = vr;
        AESKeyManager.setKeyStringResolver(this);
    }

    public void unsetVariableRegistry(VariableRegistry vr) {
        AESKeyManager.setKeyStringResolver(null);
    }

    @Override
    public char[] getKey(String val) {
        return registry.resolveString(val).toCharArray();
    }
}