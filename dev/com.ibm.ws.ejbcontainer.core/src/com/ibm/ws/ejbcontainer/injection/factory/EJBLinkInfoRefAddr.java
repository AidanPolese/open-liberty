/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2007, 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.injection.factory;

import javax.naming.RefAddr;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * A RefAddr to a EJBLinkInfo object.
 */
public class EJBLinkInfoRefAddr extends RefAddr
{
    private static final long serialVersionUID = -1172693812040793208L;

    private static final TraceComponent tc = Tr.register(EJBLinkInfoRefAddr.class, "EJBContainer",
                                                         "com.ibm.ejs.container.container");

    static final String ADDR_TYPE = "EJBLinkInfo";

    private final EJBLinkInfo ivInfo;

    /**
     * Constructs a new instance.
     */
    public EJBLinkInfoRefAddr(EJBLinkInfo info)
    {
        super(ADDR_TYPE);
        ivInfo = info;

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "EJBLinkInfoRefAddr.<init> : " + ivInfo);
    }

    /**
     * @see javax.naming.RefAddr#getContent()
     */
    @Override
    public Object getContent()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "EJBLinkInfoRefAddr.getContent() returning : " + ivInfo);

        return ivInfo;
    }

}
