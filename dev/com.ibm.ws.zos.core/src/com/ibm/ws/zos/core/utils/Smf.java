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
package com.ibm.ws.zos.core.utils;

/**
 * A wrapper to call the z/OS SMF service
 */
public interface Smf {

    /**
     * Write a z/OS SMF record type 120, subtype 11
     * 
     * @param data The record data
     * @return return code from SMF
     */
    public int smfRecordT120S11Write(byte[] data);

    /**
     * @param data
     * @return
     */
    int smfRecordT120S12Write(byte[] data);

}
