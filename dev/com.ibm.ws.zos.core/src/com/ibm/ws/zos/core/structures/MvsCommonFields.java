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

/**
 * This class provides a wrapper around the various control block helper classes.
 * If you need just a field from a particular control block or non-mapped fields
 * then use the control block class, but if you need a bunch of common fields
 * from various control blocks (e.g. to put in an SMF record) then this wrapper
 * is easier to use.
 */
public interface MvsCommonFields {

    /**
     * Get the z/OS System name from the CVT
     *
     * @return The system name, in EBCDIC
     */
    public byte[] getCVTSNAME();

    /**
     * Get the z/OS Syplex name from the CVT
     *
     * @return The sysplex name, in EBCDIC
     */
    public byte[] getECVTSPLX();

    /**
     * Get the z/OS RMCTADJC
     *
     * @return The system name, in EBCDIC
     */
    public int getRMCTADJC();

    /**
     * Get the z/OS RMCTADJC
     *
     * @return The system name, in EBCDIC
     */
    public int getRCTPCPUA();

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

    /**
     * Get the stoken for this address space
     *
     * @return The stoken
     */
    public byte[] getASSBSTKN();

    /**
     * Get the JSAB Jobname
     *
     * @return The jobname name, in EBCDIC
     */
    public byte[] getJSABJBNM();

    /**
     * Get the job id from JSAB
     *
     * @return The job id in EBCDIC
     */
    public byte[] getJSABJBID();

    /**
     * Get the address of the current TCB
     *
     * @return address of the current TCB
     */
    public long getPSATOLD();
}
