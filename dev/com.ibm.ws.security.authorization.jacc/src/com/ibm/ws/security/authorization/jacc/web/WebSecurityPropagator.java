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

import javax.security.jacc.PolicyConfigurationFactory;

/**
 ** this class is for propagating the security constraints for Web servlet.
 ** since Servlet-3.x feature might not exist, all of servlet related code is located
 ** to the separate feature which only activated when servlet feature exists.
 **/

public interface WebSecurityPropagator {

    public void propagateWebConstraints(PolicyConfigurationFactory pcf,
                                        String contextId,
                                        Object webAppConfig);
}
