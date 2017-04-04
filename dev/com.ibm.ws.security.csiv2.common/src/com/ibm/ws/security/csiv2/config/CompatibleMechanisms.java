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
package com.ibm.ws.security.csiv2.config;

import com.ibm.ws.transport.iiop.security.config.css.CSSCompoundSecMechConfig;
import com.ibm.ws.transport.iiop.security.config.tss.TSSCompoundSecMechConfig;

/**
 * Pair of compatible client and server CSIv2 compound sec mechs.
 */
public class CompatibleMechanisms {

    private final CSSCompoundSecMechConfig clientMech;
    private final TSSCompoundSecMechConfig serverMech;

    /**
     * @param clientMech The client side compound sec mech configuration.
     * @param serverMech The server side compound sec mech configuration.
     */
    public CompatibleMechanisms(CSSCompoundSecMechConfig clientMech, TSSCompoundSecMechConfig serverMech) {
        this.clientMech = clientMech;
        this.serverMech = serverMech;
    }

    public CSSCompoundSecMechConfig getCSSCompoundSecMechConfig() {
        return clientMech;
    }

    public TSSCompoundSecMechConfig getTSSCompoundSecMechConfig() {
        return serverMech;
    }

}
