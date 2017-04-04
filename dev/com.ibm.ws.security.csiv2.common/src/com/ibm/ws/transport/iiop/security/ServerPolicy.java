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
/*
 * Some of the code was derived from code supplied by the Apache Software Foundation licensed under the Apache License, Version 2.0.
 */
package com.ibm.ws.transport.iiop.security;

import java.io.Serializable;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;

import com.ibm.ws.transport.iiop.security.config.tss.TSSConfig;

/**
 * @version $Rev: 451417 $ $Date: 2006-09-29 13:13:22 -0700 (Fri, 29 Sep 2006) $
 */
public class ServerPolicy extends LocalObject implements Policy {

    private final TSSConfig TSSConfig;

    public ServerPolicy(Config config) {
        this.TSSConfig = config.getTSSConfig();
    }

    protected ServerPolicy(TSSConfig config) {
        this.TSSConfig = config;
    }

    public TSSConfig getConfig() {
        return TSSConfig;
    }

    @Override
    public int policy_type() {
        return ServerPolicyFactory.POLICY_TYPE;
    }

    @Override
    public void destroy() {}

    @Override
    public Policy copy() {
        return new ServerPolicy(TSSConfig);
    }

    public static class Config implements Serializable {
        private final TSSConfig TSSConfig;

        public Config(TSSConfig TSSConfig) {
            this.TSSConfig = TSSConfig;
        }

        public final TSSConfig getTSSConfig() {
            return TSSConfig;
        }
    }
}
