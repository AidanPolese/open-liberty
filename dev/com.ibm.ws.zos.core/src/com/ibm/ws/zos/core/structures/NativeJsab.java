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
 * Access the z/OS JSAB
 */
public interface NativeJsab {
    /**
     * Get a {@code DirectByteBuffer} that maps the z/OS JSAB
     */
    public ByteBuffer mapMyJsab();

    /**
     * Get the JSAB Jobname
     * 
     * @return The jobname name, in EBCDIC
     */
    public byte[] getJSABJBNM();

    /**
     * Get the jobid
     * 
     * @return The jobid in EBCDIC
     */
    public byte[] getJSABJBID();
}
