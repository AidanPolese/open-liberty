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

import com.ibm.ws.container.service.app.deploy.ApplicationInfo;
import com.ibm.ws.container.service.state.ApplicationStateListener;
import com.ibm.ws.container.service.state.StateChangeException;

class ApplicationStateManager extends StateChangeManager<ApplicationStateListener> {
    ApplicationStateManager(String listenerRefName) {
        super(listenerRefName);
    }

    /**
     * @param info
     */
    public void fireStarting(ApplicationInfo info) throws StateChangeException {
        for (ApplicationStateListener listener : listeners.services()) {
            try {
                listener.applicationStarting(info);
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
    public void fireStarted(ApplicationInfo info) throws StateChangeException {
        for (ApplicationStateListener listener : listeners.services()) {
            try {
                listener.applicationStarted(info);
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
    public void fireStopping(ApplicationInfo info) {
        for (ApplicationStateListener listener : listeners.services()) {
            try {
                listener.applicationStopping(info);
            } catch (Throwable t) {
                // Nothing (except automatically inserted FFDC).
            }
        }
    }

    /**
     * @param info
     */
    public void fireStopped(ApplicationInfo info) {
        for (ApplicationStateListener listener : listeners.services()) {
            try {
                listener.applicationStopped(info);
            } catch (Throwable t) {
                // Nothing (except automatically inserted FFDC).
            }
        }
    }
}
