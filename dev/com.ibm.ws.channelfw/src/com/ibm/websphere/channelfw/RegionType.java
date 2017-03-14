//* ===========================================================================
//*
//* IBM SDK, Java(tm) 2 Technology Edition, v5.0
//* (C) Copyright IBM Corp. 2005, 2006
//*
//* The source code for this program is not published or otherwise divested of
//* its trade secrets, irrespective of what has been deposited with the U.S.
//* Copyright office.
//*
//* ===========================================================================
//
/**
 * IBM Confidential OCO Source Material 
 * 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 2003,2004
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
//@(#) 1.1 SERV1/ws/code/channelfw/src/com/ibm/websphere/channel/framework/RegionType.java, WAS.channelfw, WASX.SERV1 8/20/04 10:39:41 [8/28/04 13:40:14]
package com.ibm.websphere.channelfw;

/**
 * The RegionType class is used among the entire framework to specify
 * different values related to the different Z regions.
 * 
 * @ibm-api
 */
public class RegionType {

    // -------------------------------------------------------------------------
    // Public Constants
    // -------------------------------------------------------------------------

    // These values need to be bit-wise exclusive
    // so binary logic can be used on variables which use these constants

    /**
     * Neither the CR_REGION or CRA_REGION, will be called "NO_BOUND_REGION".
     */
    public static final int NO_BOUND_REGION = 1;

    /**
     * Controller or Control Region.
     */
    public static final int CR_REGION = 2;

    /**
     * Adjuct or Control Region Adjunct.
     */
    public static final int CRA_REGION = 4;

    /**
     * Servant Region
     */
    public final static int SR_REGION = 8;

    /**
     * Not running on a Z platform
     */
    public final static int NOT_ON_Z = 16;

}
