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
 * Provides access to the z/OS Address Space Secondary Block (ASSB)
 */
public interface NativeAssb {

    /**
     * Get a {@code DirectByteBuffer} that maps the z/OS Address Space Secondary Block (ASSB)
     */
    public ByteBuffer mapMyAssb();

    /**
     * Get the stoken for this address space
     * 
     * @return The stoken
     */
    public byte[] getASSBSTKN();

    /**
     * Get the JSAB address
     * 
     * @return The JSAB address
     */
    public long getASSBJSAB();

}
