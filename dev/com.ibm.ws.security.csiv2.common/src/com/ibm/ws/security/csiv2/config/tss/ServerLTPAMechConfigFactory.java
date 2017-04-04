/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.csiv2.config.tss;

import java.lang.reflect.Constructor;

import org.omg.CSIIOP.AS_ContextSec;

import com.ibm.ws.transport.iiop.security.config.tss.TSSASMechConfig;

/**
 * Represents the authentication layer configuration for authenticating with LTPA token.
 * It is set as the CompoundSecMech'as_context_mech when building the IOR.
 */
public class ServerLTPAMechConfigFactory {

    private static final String IMPL_CLASS = "com.ibm.ws.security.csiv2.server.config.tss.ServerLTPAMechConfig";
    private static Constructor<?> cons = null;

    /**
     * @param name serverLTPAMechConfig implementation class name to be instanciated.
     * @param context
     */
    public static TSSASMechConfig getServerLTPAMechConfig(AS_ContextSec context) throws Exception {
        if (cons == null) {
            Class<?> implClass = Class.forName(IMPL_CLASS);
            @SuppressWarnings("rawtypes")
            Class[] types = new Class[] { AS_ContextSec.class };
            cons = implClass.getConstructor(types);
        }
        return (TSSASMechConfig) cons.newInstance(new Object[] { context });
    }
}
