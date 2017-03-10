/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.install.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.ibm.ws.install.InstallConstants;
import com.ibm.ws.install.InstallEventListener;
import com.ibm.ws.install.InstallProgressEvent;

public class EventManager {

    private Map<String, Collection<InstallEventListener>> listenersMap;

    public void addListener(InstallEventListener listener, String notificationType) {
        if (listener == null || notificationType == null)
            return;
        if (notificationType.isEmpty())
            return;
        if (listenersMap == null) {
            listenersMap = new HashMap<String, Collection<InstallEventListener>>();
        }
        Collection<InstallEventListener> listeners = listenersMap.get(notificationType);
        if (listeners == null) {
            listeners = new ArrayList<InstallEventListener>(1);
            listenersMap.put(notificationType, listeners);
        }
        listeners.add(listener);
    }

    public void removeListener(InstallEventListener listener) {
        if (listenersMap != null) {
            for (Collection<InstallEventListener> listeners : listenersMap.values()) {
                listeners.remove(listener);
            }
        }
    }

    public void fireProgressEvent(int state, int progress, String message) throws Exception {
        // TODO: log the message
        if (listenersMap != null) {
            Collection<InstallEventListener> listeners = listenersMap.get(InstallConstants.EVENT_TYPE_PROGRESS);
            if (listeners != null) {
                for (InstallEventListener listener : listeners) {
                    listener.handleInstallEvent(new InstallProgressEvent(state, progress, message));
                }
            }
        }
    }

}
