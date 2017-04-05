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
package com.ibm.ws.jaxws.metadata.builder;

import com.ibm.ws.javaee.ddmodel.wsbnd.HttpPublishing;
import com.ibm.ws.javaee.ddmodel.wsbnd.WebservicesBnd;
import com.ibm.ws.jaxws.metadata.JaxWsModuleInfo;
import com.ibm.ws.jaxws.metadata.JaxWsModuleType;
import com.ibm.ws.jaxws.utils.URLUtils;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;

/**
 * get the context root from custom binding file ibm-ws-bnd.xml for EJB based Web services.
 */
public class EJBContextRootJaxWsModuleInfoBuilderExtension extends AbstractJaxWsModuleInfoBuilderExtension {

    public EJBContextRootJaxWsModuleInfoBuilderExtension() {
        super(JaxWsModuleType.EJB);
    }

    @Override
    public void preBuild(JaxWsModuleInfoBuilderContext jaxWsModuleInfoBuilderContext, JaxWsModuleInfo jaxWsModuleInfo) throws UnableToAdaptException {}

    @Override
    public void postBuild(JaxWsModuleInfoBuilderContext jaxWsModuleInfoBuilderContext, JaxWsModuleInfo jaxWsModuleInfo) throws UnableToAdaptException {
        WebservicesBnd webservicesBnd = jaxWsModuleInfoBuilderContext.getContainer().adapt(WebservicesBnd.class);
        if (webservicesBnd == null) {
            return;
        }

        // get context root for EJB services
        HttpPublishing httpPublishing = webservicesBnd.getHttpPublishing();
        if (httpPublishing != null) {
            String contextRoot = httpPublishing.getContextRoot();
            contextRoot = URLUtils.normalizePath(contextRoot);
            if (contextRoot != null && !contextRoot.isEmpty()) {
                jaxWsModuleInfo.setContextRoot(contextRoot);
            }
        }

    }

}
