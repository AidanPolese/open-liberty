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
package com.ibm.wsspi.logging;


/**
 * The incident forwarder is invoked after an incident is logged by ffdc.
 * 
 * @ibm-spi
 */
public interface IncidentForwarder {
    public void process(Incident incident, Throwable th);
}
