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
 * Provides access to the System Resources Manager Control Table (RMCT)
 */
public interface NativeRmct {

    /**
     * Get a {@code DirectByteBuffer} that maps the z/OS RMCT
     */
    public ByteBuffer mapMyRmct();

    /**
     * Get the RMCTADJC field of the RMCT
     *
     * @return The RMCTADJC field
     */
    public int getRMCTADJC();

    /**
     * Get the RMCTRCT Pointer from RMCT, points to RCT
     *
     * @return The RCT pointer
     */
    public long getRMCTRCT();

}
