/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmodel.wsbnd;

import java.util.List;

import com.ibm.ws.javaee.dd.common.SecurityRole;
import com.ibm.ws.javaee.dd.web.common.LoginConfig;
import com.ibm.ws.javaee.dd.web.common.SecurityConstraint;

public interface WebserviceSecurity {

    public static String SECURITY_CONSTRAINT_ELEMENT_NAME = "security-constraint";

    public static String SECURITY_ROLE_ELEMENT_NAME = "security-role";

    public static String LOGIN_CONFIG_ELEMENT_NAME = "login-config";

    /**
     * @return &lt;security-constraint> as a list
     */
    public List<SecurityConstraint> getSecurityConstraints();

    /**
     * @return &lt;security-role> as a list
     */
    public List<SecurityRole> getSecurityRoles();

    /**
     * @return &lt;login-config>, or null if unspecified
     */
    public LoginConfig getLoginConfig();

}
