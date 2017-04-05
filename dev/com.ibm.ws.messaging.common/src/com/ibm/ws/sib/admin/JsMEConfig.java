/*
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 *                                 Original
 * ============================================================================
 */
package com.ibm.ws.sib.admin;

import java.io.Serializable;

/**
 * An object representing the messaging engine properties sent by the Configuration Admin
 *
 */
public class JsMEConfig implements Serializable,LWMConfig {

    private static final long serialVersionUID = 3771927699307638584L;

    // Object to hold the filestore properties
    private SIBFileStore sibFilestore;
    // Object to hold some of the ME specific properties 
    private SIBMessagingEngine messagingEngine;
    // A default bus for the ME
    private SIBus sibus;

    /**
     * Get the filestore object
     * @return SIBFileStore
     */
    public SIBFileStore getSibFilestore() {
        return sibFilestore;
    }

    /**
     * Set the filestore object
     * @param sibFilestore
     */
    public void setSIBFilestore(SIBFileStore sibFilestore) {
        this.sibFilestore = sibFilestore;
    }

    /**
     * Get the SIBMessagingEngine object
     * @return
     */
    public SIBMessagingEngine getMessagingEngine() {
        return messagingEngine;
    }

    /**
     * Set the messaging engine object
     * @param messagingEngine
     */
    public void setMessagingEngine(SIBMessagingEngine messagingEngine) {
        this.messagingEngine = messagingEngine;
    }

    /**
     * Get the default bus
     * @return
     */
    
    public SIBus getSIBus() {
        return sibus;
    }

    /**
     * Set the default bus 
     * @param sibus
     */
    public void setSIBus(SIBus sibus) {
        this.sibus = sibus;
    }

 
}
