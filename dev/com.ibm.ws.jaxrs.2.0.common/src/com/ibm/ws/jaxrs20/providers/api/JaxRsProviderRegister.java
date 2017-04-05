/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxrs20.providers.api;

import java.util.List;
import java.util.Set;

public interface JaxRsProviderRegister {

    public void installProvider(boolean clientSide, List<Object> providers, Set<String> features);
}
