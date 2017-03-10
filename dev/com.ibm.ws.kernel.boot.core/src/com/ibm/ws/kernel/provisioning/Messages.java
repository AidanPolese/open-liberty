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
package com.ibm.ws.kernel.provisioning;

/**
 * The classes in this package can not have a dependency on Tr because it is used
 * in environments where Tr is unavailable. So calls from this package to output
 * messages to a log use this interface, allowing multiple mechanisms of outputting
 * errors to be used.
 */
public interface Messages {
    public void warning(String key, Object... inserts);
}