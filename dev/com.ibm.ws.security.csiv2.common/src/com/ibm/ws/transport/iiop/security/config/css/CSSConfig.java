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
package com.ibm.ws.transport.iiop.security.config.css;

import java.io.Serializable;
import java.util.LinkedList;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.security.csiv2.config.CompatibleMechanisms;
import com.ibm.ws.transport.iiop.security.config.tss.TSSCompoundSecMechListConfig;

/**
 * @version $Revision: 503274 $ $Date: 2007-02-03 10:19:18 -0800 (Sat, 03 Feb 2007) $
 */
public final class CSSConfig implements Serializable {
    private final CSSCompoundSecMechListConfig mechList = new CSSCompoundSecMechListConfig();
    private String sslRef;

    public CSSCompoundSecMechListConfig getMechList() {
        return mechList;
    }

    public LinkedList<CompatibleMechanisms> findCompatibleList(TSSCompoundSecMechListConfig mechListConfig) {
        return mechList.findCompatibleList(mechListConfig);
    }

    /**
     * @return the sslRef
     */
    public String getSslRef() {
        return sslRef;
    }

    /**
     * @param sslRef the sslRef to set
     */
    public void setSslRef(String sslRef) {
        this.sslRef = sslRef;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        toString("", buf);
        return buf.toString();
    }

    @Trivial
    void toString(String spaces, StringBuilder buf) {
        buf.append(spaces).append("CSSConfig: [\n");
        mechList.toString(spaces + "  ", buf);
        buf.append(spaces).append("]\n");
    }
}
