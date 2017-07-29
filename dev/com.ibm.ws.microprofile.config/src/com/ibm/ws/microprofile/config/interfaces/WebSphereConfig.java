/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.microprofile.config.interfaces;

import java.io.Closeable;

import org.eclipse.microprofile.config.Config;

/**
 *
 */
public interface WebSphereConfig extends Config, Closeable {

    public <T> T convertValue(String rawValue, Class<T> type);

}
