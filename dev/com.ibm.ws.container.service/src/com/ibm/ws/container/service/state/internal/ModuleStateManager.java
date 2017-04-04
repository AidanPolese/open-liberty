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
package com.ibm.ws.container.service.state.internal;

import com.ibm.ws.container.service.app.deploy.ModuleInfo;
import com.ibm.ws.container.service.state.ModuleStateListener;
import com.ibm.ws.container.service.state.StateChangeException;

public class ModuleStateManager extends StateChangeManager<ModuleStateListener> {
    ModuleStateManager(String listenerRefName) {
        super(listenerRefName);
    }

    /**
     * @param info
     */
    public void fireStarting(ModuleInfo info) throws StateChangeException {
        for (ModuleStateListener listener : listeners.services()) {
            try {
                listener.moduleStarting(info);
            } catch (StateChangeException t) {
                throw t;
            } catch (Throwable t) {
                throw new StateChangeException(t);
            }
        }
    }

    /**
     * @param info
     */
    public void fireStarted(ModuleInfo info) throws StateChangeException {
        for (ModuleStateListener listener : listeners.services()) {
            try {
                listener.moduleStarted(info);
            } catch (StateChangeException t) {
                throw t;
            } catch (Throwable t) {
                throw new StateChangeException(t);
            }
        }
    }

    /**
     * @param info
     */
    public void fireStopping(ModuleInfo info) {
        for (ModuleStateListener listener : listeners.services()) {
            try {
                listener.moduleStopping(info);
            } catch (Throwable t) {
                // Nothing (except automatically inserted FFDC).
            }
        }
    }

    /**
     * @param info
     */
    public void fireStopped(ModuleInfo info) {
        for (ModuleStateListener listener : listeners.services()) {
            try {
                listener.moduleStopped(info);
            } catch (Throwable t) {
                // Nothing (except automatically inserted FFDC).
            }
        }
    }
}
