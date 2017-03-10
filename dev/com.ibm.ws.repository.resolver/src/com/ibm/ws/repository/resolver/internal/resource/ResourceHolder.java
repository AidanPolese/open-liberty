/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.ibm.ws.repository.resolver.internal.resource;

import org.osgi.resource.Resource;

/**
 * Implementors of this interface are capable of holding a link to a {@link Resource}
 */
public interface ResourceHolder {

    /**
     * Set the resources that this object is holding a reference to.
     * 
     * @param resource
     */
    public void setResource(Resource resource);

}
