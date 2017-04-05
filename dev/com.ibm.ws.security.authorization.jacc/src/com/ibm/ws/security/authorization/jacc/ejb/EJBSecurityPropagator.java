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

import java.util.List;
import java.util.Map;

import javax.security.jacc.PolicyConfigurationFactory;

import com.ibm.ws.security.authorization.jacc.MethodInfo;
import com.ibm.ws.security.authorization.jacc.RoleInfo;

/**
 ** this class is for propagating the security constraints for EJB.
 ** since EJB feature might not exist, all of EJB related code is located
 ** to the separate feature which only activated when ejb feature exists.
 **/

public interface EJBSecurityPropagator {

    void propagateEJBRoles(String contextId,
                           String appName,
                           String beanName,
                           Map<String, String> roleLinkMap,
                           Map<RoleInfo, List<MethodInfo>> methodMap);

    void processEJBRoles(PolicyConfigurationFactory pcf, String contextId);
}
