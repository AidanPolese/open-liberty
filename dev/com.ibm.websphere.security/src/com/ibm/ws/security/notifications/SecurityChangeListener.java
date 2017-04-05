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
 * Components desiring to receive notifications for changes in other
 * security components need to implement this interface. The notifying
 * component must declare the listeners in its bnd file. For example,
 * <pre>
 * com.ibm.ws.security.MyChangeNotifier; \
 * implementation:=com.ibm.ws.security.MyChangeNotifier; \
 * provide:='com.ibm.ws.security.MyChangeNotifier'; \
 * activate:=activate; \
 * deactivate:=deactivate; \
 * configuration-policy:=ignore; \
 * immediate:=true; \
 * changeListener=com.ibm.ws.security.notifications.SecurityChangeListener; \
 * optional:='changeListener'; \
 * multiple:='changeListener'; \
 * dynamic:='changeListener'; \
 * properties:='service.vendor=IBM'
 * </pre>
 */
public interface SecurityChangeListener {

    /**
     * Callback method invoked by a BaseSecurityChangeNotifier object when there
     * is change the listener is interested in.
     */
    void notifyChange();

}
