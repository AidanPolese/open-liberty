//////////////////////////////////////////////////////////////////////////////
//
// COMPONENT_NAME: WAS.utils
//
// ORIGINS: 27
//
// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U. S. Copyright Office.
//
//////////////////////////////////////////////////////////////////////////////
//
// %Z% %I% %W% %G% %U% [%H% %T%]
/*
 * @(#)file      StatefulBeanEnqDeq.java
 * @(#)author    Mark Dinges
 * @(#)version   1.1
 * @(#)date      4/16/2004
 */
/* Change History:
 *
 * Reason     Version Date     User id   Description
 * ----------------------------------------------------------------------------
 * $19????    6.0     20040416 dinges    Non Platform specific Enq Deq interface
 */
package com.ibm.ws.util;

public interface StatefulBeanEnqDeq {

    /**
     * SSBeanEnq is a deprecated method, but left in for scaffolding.
     * It will assume a DIRECT IOR and an ENQ scope of SYSTEM is required.
     * Also, assumes routing by SR Affinity is desired.
     */
    public int SSBeanEnq(byte[] pk);

    /**
     * SSBeanDeq is a deprecated method, but left in for scaffolding.
     * It will assume a DIRECT IOR and an ENQ scope of SYSTEM is required.
     * Also, assumes routing by SR Affinity is desired.
     */
    public int SSBeanDeq(byte[] pk);

    /*
     * SSBeanEnq is called by the container to obtain a SYSTEMs
     * ENQ called SYSZBBO.<binary primary key uuid> which is used by
     * the control region to find the server region which currently
     * contains the stateful session instance identified by its primary key.
     * The PK is used to create the rname of the ENQ, thus the CR can find
     * the SR given the primary key of the target stateful session object.
     * 
     * @param pk A byte array containing the stateful session bean primary key UUID
     * 
     * @param srAffinity TRUE (1) if object has affinity to the SR (implies an ENQ
     * of a SYSTEM scope). False (0) if object has no affinity to a particular SR
     * (implies an ENQ of SYSTEMS scope).
     * 
     * @param WLMTempAff TRUE (1) if WLM is to be told about object Temporal Affinity
     * 
     * @return SUCCESS (0) if ENQ was successfully obtained, FAILED (4) if a problem
     * was encountered obtaining the ENQ
     * 
     * @exception none
     */
    public int SSBeanEnq(byte[] pk, boolean srAffinity, boolean WLMTempAff);

    /**
     * SSBeanDeq is called by the container to release the ENQ
     * obtained by SSBeanEnq above.
     * 
     * @param pk A byte array containing the stateful session bean primary key UUID
     * @param srAffinity TRUE (1) if object has affinity to the SR (implies an ENQ
     *            of a SYSTEM scope). False (0) if object has no affinity to a particular SR
     *            (implies an ENQ of SYSTEMS scope).
     * @param WLMTempAff TRUE (1) if WLM is to be told about object Temporal Affinity
     * 
     * @return SUCCESS (0) if ENQ was successfully released, FAILED (4) if a problem
     *         was encountered releasing the ENQ
     * 
     * @exception none
     */
    public int SSBeanDeq(byte[] pk, boolean srAffinity, boolean WLMTempAff);

}