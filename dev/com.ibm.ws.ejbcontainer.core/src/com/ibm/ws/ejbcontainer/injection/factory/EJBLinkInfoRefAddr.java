/*******************************************************************************
 * Copyright (c) 2007, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
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
