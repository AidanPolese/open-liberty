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
package com.ibm.ws.ejbcontainer.remote.client.internal.injection;

import javax.naming.RefAddr;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Trivial;

/**
 * A RefAddr to a EJBLinkInfo object. (Client version)
 */
public class EJBLinkClientInfoRefAddr extends RefAddr {
    private static final TraceComponent tc = Tr.register(EJBLinkClientInfoRefAddr.class);

    private static final long serialVersionUID = -1172693812040793208L;

    static final String ADDR_TYPE = "EJBLinkClientInfo";

    private final EJBLinkClientInfo ivInfo;

    /**
     * Constructs a new instance.
     */
    @Trivial
    public EJBLinkClientInfoRefAddr(EJBLinkClientInfo info) {
        super(ADDR_TYPE);
        ivInfo = info;

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "EJBLinkClientInfoRefAddr.<init> : " + ivInfo);
        }
    }

    /**
     * @see javax.naming.RefAddr#getContent()
     */
    @Override
    @Trivial
    public Object getContent() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "EJBLinkClientInfoRefAddr.getContent() returning : " + ivInfo);
        }

        return ivInfo;
    }
}
