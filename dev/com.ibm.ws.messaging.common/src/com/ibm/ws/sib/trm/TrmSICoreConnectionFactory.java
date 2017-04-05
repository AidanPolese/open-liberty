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
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * LIDB2117        030429 vaughton Original
 * 281683.10       051005 gelderd  Renamed Impl class
 * 290290.2        051101 gelderd  Improved entry/exit trace for sib.trm.client
 * ============================================================================
 */

/*
 * The Topology Routing & Management client sub-component.
 *
 * This is the factory class which is used to obtain a new instance of a
 * SICoreConnection object.
 */

package com.ibm.ws.sib.trm;

import com.ibm.wsspi.sib.core.SICoreConnectionFactory;

public abstract class TrmSICoreConnectionFactory implements SICoreConnectionFactory {

    private static final String className = TrmSICoreConnectionFactory.class.getName();
    private static final String CLIENT_FACTORY_IMPL = "com.ibm.ws.sib.trm.client.TrmSICoreConnectionFactoryImpl";
    private static TrmSICoreConnectionFactory instance = null;

    static {

        try {
            Class cls = Class.forName(CLIENT_FACTORY_IMPL);
            instance = (TrmSICoreConnectionFactory) cls.newInstance();
        } catch (Exception e) {

        }
    }

    public static TrmSICoreConnectionFactory getInstance() {
        return instance;
    }
}
