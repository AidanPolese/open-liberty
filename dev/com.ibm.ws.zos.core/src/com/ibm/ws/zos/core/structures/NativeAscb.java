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
 * Provides access to the z/OS Address Space Control Block (ASCB)
 */
public interface NativeAscb {

    /**
     * Get a {@code DirectByteBuffer} that maps the z/OS Address Space Control Block (ASCB)
     */
    public ByteBuffer mapMyAscb();

    /**
     * Get the ASSB pointer from the ASCB
     * 
     * @return ASSBASCB value
     */
    public long getASCBASSB();

    /**
     * Get the started task job name from the ASCB
     * 
     * @return The started task job name (in EBCDIC)
     */
    public byte[] getASCBJBNS();

    /**
     * Get the jobname from ASCBJBNI
     * 
     * @return The job name (in EBCDIC)
     */
    public byte[] getASCBJBNI();

    /**
     * Get the ASID
     * 
     * @return The ASID
     */
    public short getASCBASID();

}
