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
package com.ibm.ws.security.registry;

/**
 * Notification mechanism to inform components that need to respond to
 * user registry configuration changes that a change has occurred.
 * <p>
 * Note knowledge of the user registry configuration is only required in
 * specific cases and should be avoided whenever possible.
 */
public interface UserRegistryChangeListener {

    /**
     * Callback method to be invoked by the UserRegistryService upon a
     * user registry configuration change.
     * <p>
     * Implementation note: Currently <b>any</b> change to <b>any</b>
     * user registry configuration triggers this change. This logic may
     * be improved for multi-domain.
     */
    void notifyOfUserRegistryChange();
}
