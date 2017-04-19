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
 * ---------------  ------ -------- -------------------------------------------------
 * SIB0009.mp.03    020905 gatfora  Extend unit test framework to enable restarts of ME
 * 302928           070905 tpm      create dest with RFH2 
 * 324383           171105 cwilkin  Durable subscriptions to aliases
 * SIB0113a.mp.1    240707 cwilkin  Gathered Consumer foundation 
 * ============================================================================
 */
package com.ibm.ws.sib.processor.test.utils;

import java.util.HashMap;

import com.ibm.websphere.sib.Reliability;
import com.ibm.ws.sib.admin.DestinationDefinition;
import com.ibm.ws.sib.admin.internal.JsAdminFactory;
import com.ibm.ws.sib.utils.SIBUuid12;
import com.ibm.wsspi.sib.core.DestinationType;

public class UnitTestDestinationUtils {
    /**
     * Create a basic DestinationDefinition of queue and reliabile persistent
     * <p>
     * Utility function for tests.
     * 
     * @param name
     */
    public static DestinationDefinition createDestinationDefinition(
                                                                    String name) {
        return createDestinationDefinition(
                                           name, DestinationType.QUEUE, Reliability.RELIABLE_PERSISTENT);
    }

    /**
     * Create a basic DestinationDefinition.
     * <p>
     * Utility function for tests.
     * 
     * @param name
     * @param dist is distribution type (ptp, pubsub, etc.)
     * @param rel is reliability (Express, Assured, etc.)
     * 
     */
    public static DestinationDefinition createDestinationDefinition(
                                                                    String name, DestinationType destinationType, Reliability rel) {
        DestinationDefinition dd = createDestinationDefinition(destinationType, name);
        dd.setMaxReliability(rel);
        dd.setDefaultReliability(rel);
        dd.setUUID(new SIBUuid12());

        /*
         * HashMap context = new HashMap();
         * context.put("_MQRFH2Allowed", new Boolean(true));
         * dd.setDestinationContext(context);
         */
        return dd;
    }

    /**
     * Create a basic DestinationDefinition with RFH2 allowed in the context
     * <p>
     * Utility function for tests.
     * 
     * @param name
     * @param dist is distribution type (ptp, pubsub, etc.)
     * @param rel is reliability (Express, Assured, etc.)
     * 
     */
    public static DestinationDefinition createDestinationDefinitionWithRFH2(
                                                                            String name, DestinationType destinationType, Reliability rel) {
        DestinationDefinition dd = createDestinationDefinition(destinationType, name);
        dd.setMaxReliability(rel);
        dd.setDefaultReliability(rel);
        dd.setUUID(new SIBUuid12());

        HashMap context = new HashMap();
        context.put("_MQRFH2Allowed", new Boolean(true));
        dd.setDestinationContext(context);
        return dd;
    }

    /**
     * Create a basic DestinationDefinition.
     * <p>
     * Utility function for tests.
     * 
     * @param name
     * @param dist is distribution type (ptp, pubsub, etc.)
     * @param rel is reliability (Express, Assured, etc.)
     * 
     */
    public static DestinationDefinition createDestinationDefinitionNoRFH2(
                                                                          String name, DestinationType destinationType, Reliability rel) {
        DestinationDefinition dd = createDestinationDefinition(destinationType, name);
        dd.setMaxReliability(rel);
        dd.setDefaultReliability(rel);
        dd.setUUID(new SIBUuid12());

        return dd;
    }

    public static DestinationDefinition createDestinationDefinition(DestinationType destinationType, String destinationName) {
        JsAdminFactory factory = null;
        try {
            factory = JsAdminFactory.getInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        DestinationDefinition destDef = factory.createDestinationDefinition(destinationType, destinationName);

        return destDef;
    }

}
