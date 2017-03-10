/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.zos.core.structures;

import java.nio.ByteBuffer;

/**
 * Provides access to the z/OS Extended Common Vectory Table (ECVT)
 */
public interface NativeEcvt {

    /**
     * Get a {@code DirectByteBuffer} that maps the z/OS Extended Common Vector Table (ECVT)
     */
    public ByteBuffer mapMyEcvt();

    /**
     * Get the z/OS Syplex name from the CVT
     * 
     * @return The sysplex name, in EBCDIC
     */
    public byte[] getECVTSPLX();

}
