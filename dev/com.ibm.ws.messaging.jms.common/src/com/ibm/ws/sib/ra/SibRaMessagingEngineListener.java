/**
 * 
 * 
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
 *
 * Change activity:
 *
 * Reason          Date      Origin   Description
 * --------------- ------    -------- ---------------------------------------
 * 195461.3        28-Apr-04 dcurrie  Original
 * 213666          28-Jul-04 dcurrie  Add messagingEngineDestroyed/Reloaded
 * 226510          09-Sep-04 dcurrie  Add messagingEngineInitialized
 * ============================================================================
 */

package com.ibm.ws.sib.ra;

import com.ibm.ws.sib.admin.JsMessagingEngine;

/**
 * Interface used to notified interested parties about the starting and stopping
 * of messaging engines.
 */
public interface SibRaMessagingEngineListener {

    /**
     * Notifies the listener that the given messaging engine is initializing.
     * 
     * @param messagingEngine
     *            the messaging engine
     */
    void messagingEngineInitializing(JsMessagingEngine messagingEngine);

    /**
     * Notifies the listener that the given messaging engine is starting.
     * 
     * @param messagingEngine
     *            the messaging engine
     */
    void messagingEngineStarting(JsMessagingEngine messagingEngine);

    /**
     * Notifies the listener that the given messaging engine is stopping.
     * 
     * @param messagingEngine
     *            the messaging engine
     * @param mode
     *            the mode with which the engine is stopping
     */
    void messagingEngineStopping(JsMessagingEngine messagingEngine, int mode);

    /**
     * Notifies the listener that the given messaging engine is being destroyed.
     * 
     * @param messagingEngine
     *            the messaging engine
     */
    void messagingEngineDestroyed(JsMessagingEngine messagingEngine);

    /**
     * Notifies the listener that the given messaging engine has been reloaded
     * following a configuration change to the bus on which the engine resides.
     * 
     * @param engine
     *            the messaging engine that has been reloaded
     */
    void messagingEngineReloaded(JsMessagingEngine engine);

}
