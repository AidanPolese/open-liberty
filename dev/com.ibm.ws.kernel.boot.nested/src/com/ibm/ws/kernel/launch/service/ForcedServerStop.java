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
package com.ibm.ws.kernel.launch.service;

/**
 * Marker interface. If this is registered in the service registry, then
 * the server was stopped with the {@code --force} option, and {@code ServerQuiesceListener}s
 * and their ilk should not be invoked.
 */
public class ForcedServerStop {

}
