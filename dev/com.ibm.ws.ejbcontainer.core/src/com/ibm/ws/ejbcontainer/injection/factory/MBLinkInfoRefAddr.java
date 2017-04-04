/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.injection.factory;

import javax.naming.RefAddr;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;

/**
 * A RefAddr to a MBLinkInfo object.
 */
public class MBLinkInfoRefAddr extends RefAddr
{
    private static final long serialVersionUID = -1172693812040793208L;

    private static final String CLASS_NAME = MBLinkInfoRefAddr.class.getName();

    private static final TraceComponent tc = Tr.register(CLASS_NAME, "EJBContainer",
                                                         "com.ibm.ejs.container.container");

    static final String ADDR_TYPE = "MBLinkInfo";

    private final MBLinkInfo ivInfo;

    /**
     * Constructs a new instance.
     */
    public MBLinkInfoRefAddr(MBLinkInfo info)
    {
        super(ADDR_TYPE);
        ivInfo = info;

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "MBLinkInfoRefAddr.<init> : " + ivInfo);
    }

    /**
     * @see javax.naming.RefAddr#getContent()
     */
    @Override
    public Object getContent()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "MBLinkInfoRefAddr.getContent() returning : " + ivInfo);

        return ivInfo;
    }
}
