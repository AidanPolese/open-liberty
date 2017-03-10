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
 * Provides access to the z/OS Prefix Save Area (PSA). Use NativeUtils.getMyPsa( ) to create one
 */
public interface NativePsa {

    /**
     * Get a {@code DirectByteBuffer} that maps the z/OS Prefix Save Area (PSA)
     */
    public ByteBuffer mapMyPsa();

    /**
     * Get the address of the CVT
     * 
     * @return the CVT address
     */
    public long getFLCCVT();

    /**
     * Get the address of the ASCB
     * 
     * @return the ASCB Address
     */
    public long getPSAAOLD();

    /**
     * Get the address of the current TCB
     * 
     * @return address of the current TCB
     */
    public long getPSATOLD();
}
