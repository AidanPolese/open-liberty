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
package com.ibm.ws.security.authorization.jacc.ejb;

import java.security.Permission;
import java.util.List;

import javax.security.auth.Subject;

/**
 ** this class is for enforcing the security constraints for EJB.
 ** since EJB feature might not exist, all of EJB related code is located
 ** to the separate feature which only activated when ejb feature exists.
 **/
public interface EJBSecurityValidator {
    boolean checkResourceConstraints(String contextId, List<Object> methodParameters, Object bean, Permission ejbPerm, Subject subject);
}
