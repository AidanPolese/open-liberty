/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.resource;

import java.util.List;

import com.ibm.wsspi.resource.ResourceInfo;

/**
 * Information about a resource reference.
 * <p>
 * This interface is not intended to be implemented by clients.
 */
public interface ResourceRefInfo
                extends ResourceInfo
{
    /**
     * @return the binding name
     */
    String getJNDIName();

    @Override
    List<? extends Property> getLoginPropertyList();

    interface Property
                    extends ResourceInfo.Property
    {
        // Nothing
    }
}
