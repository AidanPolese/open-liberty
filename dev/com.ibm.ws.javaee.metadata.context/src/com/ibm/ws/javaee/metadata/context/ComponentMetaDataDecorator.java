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
package com.ibm.ws.javaee.metadata.context;

import com.ibm.ws.runtime.metadata.ComponentMetaData;

/**
 * Allows component metadata to be replaced before establishing it on a thread
 */
public interface ComponentMetaDataDecorator {
    ComponentMetaData decorate(ComponentMetaData metadata);
}
