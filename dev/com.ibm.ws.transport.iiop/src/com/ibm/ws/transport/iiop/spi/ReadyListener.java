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
package com.ibm.ws.transport.iiop.spi;

/**
 *
 */
public interface ReadyListener {

    void readyChanged(SubsystemFactory id, boolean ready);

    String listenerId();

}
