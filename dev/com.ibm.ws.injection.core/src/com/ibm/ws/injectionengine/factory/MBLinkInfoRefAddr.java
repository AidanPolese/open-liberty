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
package com.ibm.ws.injectionengine.factory;

import javax.naming.RefAddr;

/**
 * A RefAddr to a MBLinkInfo object.
 */
public class MBLinkInfoRefAddr extends RefAddr
{
    private static final long serialVersionUID = -4514350245445974238L;

    static final String ADDR_TYPE = "MBLinkInfo";

    final private MBLinkInfo ivInfo;

    /**
     * Constructs a new instance.
     */
    public MBLinkInfoRefAddr(MBLinkInfo info)
    {
        super(ADDR_TYPE);
        ivInfo = info;
    }

    /**
     * @see javax.naming.RefAddr#getContent()
     */
    @Override
    public Object getContent()
    {
        return ivInfo;
    }
}
