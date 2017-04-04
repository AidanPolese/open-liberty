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
package com.ibm.ws.security.authorization.jacc.web;

import java.security.Permission;

import javax.security.auth.Subject;
import javax.security.jacc.WebUserDataPermission;

/**
 ** this class is for enforcing the security constraints for Web servlet.
 ** since Servlet-3.x feature might not exist, all of servlet related code is located
 ** to the separate feature which only activated when servlet feature exists.
 **/
public interface WebSecurityValidator {
    boolean checkDataConstraints(String contextId, Object req, WebUserDataPermission webUDPermission);

    boolean checkResourceConstraints(String contextId, Object req, Permission webPerm, Subject subject);

}
