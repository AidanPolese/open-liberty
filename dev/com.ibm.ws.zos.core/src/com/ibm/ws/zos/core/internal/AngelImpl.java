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
package com.ibm.ws.zos.core.internal;

import com.ibm.ws.zos.core.Angel;

/**
 * Implementation of {@code Angel} that is registered.
 */
public class AngelImpl implements Angel {

    final private int drmVersion;

    AngelImpl(int drmVersion) {
        this.drmVersion = drmVersion;
    }

    /**
     * @see com.ibm.ws.zos.core.Angel#getDRM_Version()
     */
    @Override
    public int getDRM_Version() {
        return drmVersion;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(";drmVersion=").append(drmVersion);
        return sb.toString();
    }
}
