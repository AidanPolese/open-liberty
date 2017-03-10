/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

package com.ibm.ws.event.internal.adapter;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.event.Event;

import com.ibm.websphere.event.EventHandler;

public class OSGiHandlerAdapter implements EventHandler {

    final org.osgi.service.event.EventHandler osgiEventHandler;

    public OSGiHandlerAdapter(org.osgi.service.event.EventHandler eventHandler) {
        osgiEventHandler = eventHandler;
    }

    public void handleEvent(com.ibm.websphere.event.Event event) {
        String topic = event.getTopic();
        Map<String, Object> properties = new HashMap<String, Object>();
        for (String key : event.getPropertyNames()) {
            properties.put(key, event.getProperty(key));
        }

        Event osgiEvent = new org.osgi.service.event.Event(topic, properties);
        osgiEventHandler.handleEvent(osgiEvent);
    }

}
