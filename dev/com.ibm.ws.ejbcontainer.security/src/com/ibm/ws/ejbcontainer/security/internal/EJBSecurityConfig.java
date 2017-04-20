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
package com.ibm.ws.ejbcontainer.security.internal;

/**
 * Represents security configurable options for EJB.
 */
interface EJBSecurityConfig {

    boolean getUseUnauthenticatedForExpiredCredentials();

    boolean getUseRealmQualifiedUserNames();

    String getChangedProperties(EJBSecurityConfig original);
}