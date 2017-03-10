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
package com.ibm.ws.zos.core.utils;

import java.nio.ByteBuffer;

/**
 * Helper to format byte[]/ByteBuffer in a double gutter format
 */
public interface DoubleGutter {

    /**
     * Format a byte array into a double-gutter hex dump that's more
     * suitable for human consumption. This format closely resembles
     * the one used in traditional WAS for z/OS.
     * 
     * @param address the address of the buffer
     * @param data the contents of the area to be traced
     * @return String representing formatted byte[] with ASCII/EBCDIC Gutters
     */
    public String asDoubleGutter(long address, byte[] data);

    /**
     * Format the given ByteBuffer into a double-gutter hex dump.
     * 
     * Note: only the data between ByteBuffer.position() and ByteBuffer.limit() is formatted.
     * 
     * @param address the address of the buffer data
     * @param data the data to be formatted.
     * 
     * @return The data formatted with ASCII/EBCDIC gutters.
     */
    public String asDoubleGutter(long address, ByteBuffer data);
}
