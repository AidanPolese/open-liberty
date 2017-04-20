/*
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
package com.ibm.ws.ejbcontainer.security.internal;

import javax.security.auth.Subject;

import com.ibm.ws.ejbcontainer.EJBComponentMetaData;
import com.ibm.ws.ejbcontainer.EJBRequestData;

/**
 * Encapsulate jacc related methods which are consumed by EJBSecurityCollaborator.
 */
public interface EJBAuthorizationHelper {

    void authorizeEJB(EJBRequestData request, Subject subject) throws EJBAccessDeniedException;

    boolean isCallerInRole(EJBComponentMetaData cmd, EJBRequestData request, String roleName, String roleLink, Subject subject);
}
