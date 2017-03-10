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
package com.ibm.ws.repository.resolver.internal;

import java.util.List;

import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;

public interface ResolutionFilter {

    public boolean allowResolution(Requirement requirement, List<Capability> potentialProviders);

}
