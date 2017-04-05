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
package com.ibm.ws.security.notifications;

/**
 * Components desiring to send change notifications need to
 * implement this interface.
 */
public interface SecurityChangeNotifier {

    /**
     * Notifies listeners that there was a change.
     */
    public abstract void notifyListeners();

}