/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2009, 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.injectionengine.factory;

import javax.naming.RefAddr;

/**
 * A RefAddr to a ResourceInfo object.
 */
public class ResourceInfoRefAddr extends RefAddr
{
    private static final long serialVersionUID = 1229903471104651956L;

    /**
     * Constant of type for this address.
     **/
    public static final String Addr_Type = "ResourceInfo";

    private final ResourceInfo ivInfo;

    /**
     * Constructs a new instance.
     */
    public ResourceInfoRefAddr(ResourceInfo info)
    {
        super(Addr_Type);
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
