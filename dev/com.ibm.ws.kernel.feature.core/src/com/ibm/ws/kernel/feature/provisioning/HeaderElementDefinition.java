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
package com.ibm.ws.kernel.feature.provisioning;

import java.util.Map;

public interface HeaderElementDefinition {

    public String getSymbolicName();

    public Map<String, String> getAttributes();

    public Map<String, String> getDirectives();
}
