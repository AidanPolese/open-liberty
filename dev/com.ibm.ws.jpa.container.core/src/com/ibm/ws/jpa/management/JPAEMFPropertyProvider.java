/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jpa.management;

import java.util.Map;

/**
 * 
 * DS components who implement this class will be invoked by the 
 * JPAComponentImpl.addIntegrationProperties method when initializing
 * the JPA provider.  Implementors will then have a chance to add
 * properties that are passed to the provider.
 *
 */
public interface JPAEMFPropertyProvider {

    public void updateProperties(Map<String,Object> props);
}
