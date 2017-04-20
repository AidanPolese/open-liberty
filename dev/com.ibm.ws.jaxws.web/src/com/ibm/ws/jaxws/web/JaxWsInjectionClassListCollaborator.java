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
package com.ibm.ws.jaxws.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ibm.ws.jaxws.metadata.EndpointInfo;
import com.ibm.ws.jaxws.metadata.HandlerInfo;
import com.ibm.ws.jaxws.metadata.JaxWsModuleInfo;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.webcontainer.collaborator.WebAppInjectionClassListCollaborator;

/**
 *
 */
public class JaxWsInjectionClassListCollaborator implements WebAppInjectionClassListCollaborator {

    /** {@inheritDoc} */
    @Override
    public List<String> getInjectionClasses(Container moduleContainer) {

        try {
            JaxWsModuleInfo jaxWsModuleInfo = moduleContainer.adapt(JaxWsModuleInfo.class);
            if (jaxWsModuleInfo != null && jaxWsModuleInfo.endpointInfoSize() > 0) {
                Set<String> retClassNames = new HashSet<String>();
                for (EndpointInfo endpointInfo : jaxWsModuleInfo.getEndpointInfos()) {
                    retClassNames.add(endpointInfo.getImplBeanClassName());
                }
                retClassNames.addAll(getAllHandlerClassNames(jaxWsModuleInfo));
                return new ArrayList<String>(retClassNames);
            }
        } catch (UnableToAdaptException e) {
            throw new IllegalStateException(e);
        }
        return Collections.<String> emptyList();
    }

    private List<String> getAllHandlerClassNames(JaxWsModuleInfo jaxWsModuleInfo) {
        List<String> handlerClassNames = new ArrayList<String>();

        for (EndpointInfo edpInfo : jaxWsModuleInfo.getEndpointInfos()) {
            for (HandlerInfo hInfo : edpInfo.getHandlerChainsInfo().getAllHandlerInfos()) {
                handlerClassNames.add(hInfo.getHandlerClass());
            }
        }
        return handlerClassNames;
    }
}
