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

import java.util.Map;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.component.ComponentContext;

/**
 * This class provides the functionality to consume the messaging properties sent by the configuration admin
 * and to use it
 */
public abstract class JsMainAdminService {

    /**
     * Will be called to clean up all the resources.Is invoked when deactivate() is called by DS
     * 
     * @param context
     * @param properties
     */
    public abstract void deactivate(ComponentContext context,
                                    Map<String, Object> properties);

    /**
     * Is used to handle the modification in server.xml
     * 
     * @param context
     * @param properties
     */
    public abstract void modified(ComponentContext context,
                                  Map<String, Object> properties, ConfigurationAdmin configAdmin);

    /**
     * Is invoked to consume the messaging properties and construct JSMEconfig Object
     * 
     * @param context
     * @param properties
     * @param serviceList
     */
    public abstract void activate(ComponentContext context, Map<String, Object> properties,
                                  ConfigurationAdmin configAdmin);

    /**
     * Get the state of the Messaging Engine.
     * 
     * @return String
     */
    public abstract String getMeState();

    /**
     * forward configuration events from the listener to the object tracking pids.
     * 
     * @param event configuration event
     * @param configAdmin configuration admin service
     */
    public abstract void configurationEvent(ConfigurationEvent event, ConfigurationAdmin configAdmin);

}
