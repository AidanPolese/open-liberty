/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- ------------------------------------------
 * 190632.0.21      230604 caseyj   Link handler corruption reset
 * 190632.0.23      010704 caseyj   Corrupt dursubs not signalled before del ts 
 * 255137           180205 gatfora  Speed improvements in the unittests
 * 267686           120405 tpm      Null propogation of TSMapping
 * SIB0009.mp.03    020905 gatfora  Extend unit test framework to enable restarts of ME
 * SIB0211.mp.1     260207 nyoung   Dynamic Link Configuration.
 * 460662           230608 nyoung   70FVT: Link transmitter status wrong for MQLinks   
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.test.utils;

import com.ibm.ws.sib.admin.DestinationDefinition;

/**
 * @author caseyj
 * 
 *         Foreign bus/link test utility methods.
 */
public class UnitTestBusUtils {
    /**
     * Create a bus entry in WCCM.
     * 
     * @param busName
     * @param linkName
     * @throws Exception
     */
    public static void createBus(String busName, String linkName)
                    throws Exception {
        DestinationDefinition busDefinition = UnitTestDestinationUtils.createDestinationDefinition(busName);
        //  UnitTestWCCM.createBus(busDefinition, linkName);
    }

    /**
     * Create a link entry in WCCM. Currently a warm restart is required after
     * this method to complete link creation.
     * 
     * @param linkName
     * @return
     * 
     * @throws Exception
     */
    public static void createLink(String linkName)
                    throws Exception {
        DestinationDefinition linkDefinition = UnitTestDestinationUtils.createDestinationDefinition(linkName);
        //    UnitTestWCCM.createLocalLink(linkDefinition, null);
    }

}
