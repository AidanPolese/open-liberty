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
 * Provides access to the RCT, pointed to RMCTRCT field of the RMCT data area
 */
public interface NativeRct {

    /**
     * Get a {@code DirectByteBuffer} that maps the z/OS RCT
     */
    public ByteBuffer mapMyRct();

    /**
     * Get the RCTPCPUA field from the RCT
     *
     * @return the RCTPCPUA field
     */
    public int getRCTPCPUA();

}