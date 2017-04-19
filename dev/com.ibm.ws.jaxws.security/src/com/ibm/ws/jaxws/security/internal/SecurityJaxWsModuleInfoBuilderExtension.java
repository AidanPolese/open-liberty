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
package com.ibm.ws.jaxws.security.internal;

import java.util.List;

import com.ibm.ws.javaee.dd.common.SecurityRole;
import com.ibm.ws.javaee.dd.web.common.LoginConfig;
import com.ibm.ws.javaee.dd.web.common.SecurityConstraint;
import com.ibm.ws.javaee.ddmodel.wsbnd.HttpPublishing;
import com.ibm.ws.javaee.ddmodel.wsbnd.WebserviceSecurity;
import com.ibm.ws.javaee.ddmodel.wsbnd.WebservicesBnd;
import com.ibm.ws.jaxws.metadata.JaxWsModuleInfo;
import com.ibm.ws.jaxws.metadata.JaxWsModuleType;
import com.ibm.ws.jaxws.metadata.ServiceSecurityInfo;
import com.ibm.ws.jaxws.metadata.builder.AbstractJaxWsModuleInfoBuilderExtension;
import com.ibm.ws.jaxws.metadata.builder.JaxWsModuleInfoBuilderContext;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;

/**
 * The builder extension builds the security constraints info to JaxwsModuleInfo.
 */
public class SecurityJaxWsModuleInfoBuilderExtension extends AbstractJaxWsModuleInfoBuilderExtension {

    public SecurityJaxWsModuleInfoBuilderExtension() {
        super(JaxWsModuleType.EJB, JaxWsModuleType.WEB);
    }

    @Override
    public void preBuild(JaxWsModuleInfoBuilderContext jaxWsModuleInfoBuilderContext, JaxWsModuleInfo jaxWsModuleInfo) throws UnableToAdaptException {}

    @Override
    public void postBuild(JaxWsModuleInfoBuilderContext jaxWsModuleInfoBuilderContext, JaxWsModuleInfo jaxWsModuleInfo) throws UnableToAdaptException {
        WebservicesBnd webservicesBnd = jaxWsModuleInfoBuilderContext.getContainer().adapt(WebservicesBnd.class);
        if (webservicesBnd == null || jaxWsModuleInfo == null || jaxWsModuleInfo.getServiceSecurityInfo() != null) {
            return;
        }

        // get security constraints info for web services
        HttpPublishing httpPublishing = webservicesBnd.getHttpPublishing();
        if (httpPublishing != null) {
            WebserviceSecurity webserviceSecurity = httpPublishing.getWebserviceSecurity();

            if (webserviceSecurity != null) {
                List<SecurityConstraint> securityConstraints = webserviceSecurity.getSecurityConstraints();
                List<SecurityRole> securityRoles = webserviceSecurity.getSecurityRoles();
                LoginConfig loginConfig = webserviceSecurity.getLoginConfig();

                ServiceSecurityInfo serviceSecurityInfo = new ServiceSecurityInfo(securityConstraints, securityRoles, loginConfig);
                jaxWsModuleInfo.setServiceSecurityInfo(serviceSecurityInfo);
            }
        }
    }
}
