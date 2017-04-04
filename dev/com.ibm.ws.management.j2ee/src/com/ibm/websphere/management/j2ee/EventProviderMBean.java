/**
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
package com.ibm.websphere.management.j2ee;

/**
 * The EventProvider model specifies the eventTypes attribute, which must be
 * implemented by all managed objects that emit events.
 */
public interface EventProviderMBean {

    /**
     * A list of the types of events the managed object emits. The contents of the list
     * are type strings.
     */
    String[] geteventTypes();

}
