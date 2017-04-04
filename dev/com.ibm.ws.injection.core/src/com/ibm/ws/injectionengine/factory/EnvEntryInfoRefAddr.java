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

public class EnvEntryInfoRefAddr
                extends RefAddr
{
    private static final long serialVersionUID = 8489781296514684581L;

    static final String ADDR_TYPE = "EnvEntryInfo";

    private final EnvEntryInfo ivInfo;

    /**
     * Constructs a new instance.
     */
    public EnvEntryInfoRefAddr(EnvEntryInfo info)
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
