/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxws.ejb;

import java.util.List;
import java.util.Set;

import com.ibm.ws.ejbcontainer.EJBEndpoint;
import com.ibm.ws.jaxws.JaxWsConstants;
import com.ibm.ws.jaxws.metadata.EndpointInfo;
import com.ibm.ws.jaxws.metadata.EndpointType;
import com.ibm.ws.jaxws.metadata.JaxWsModuleInfo;
import com.ibm.ws.jaxws.metadata.JaxWsServerMetaData;
import com.ibm.ws.jaxws.metadata.builder.EndpointInfoBuilder;
import com.ibm.ws.jaxws.metadata.builder.EndpointInfoBuilderContext;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;

/**
 *
 */
public class EJBJaxWsModuleInfoBuilderHelper {
    /**
     * build Web Service endpoint infos
     * 
     * @param endpointInfoBuilder
     * @param ctx
     * @param jaxWsServerMetaData
     * @param ejbEndpoints
     * @param jaxWsModuleInfo
     * @return
     * @throws UnableToAdaptException
     */
    static void buildEjbWebServiceEndpointInfos(EndpointInfoBuilder endpointInfoBuilder, EndpointInfoBuilderContext ctx, JaxWsServerMetaData jaxWsServerMetaData,
                                                   List<EJBEndpoint> ejbEndpoints, JaxWsModuleInfo jaxWsModuleInfo) throws UnableToAdaptException {
        Set<String> presentedServices = jaxWsModuleInfo.getEndpointImplBeanClassNames();

        for (EJBEndpoint ejbEndpoint : ejbEndpoints) {
            if (!ejbEndpoint.isWebService()) {
                continue;
            }
            if (presentedServices.contains(ejbEndpoint.getClassName())) {
                continue;
            }

            String ejbName = ejbEndpoint.getJ2EEName().getComponent();
            ctx.addContextEnv(JaxWsConstants.ENV_ATTRIBUTE_ENDPOINT_BEAN_NAME, ejbName);
            EndpointInfo endpointInfo = endpointInfoBuilder.build(ctx, ejbEndpoint.getClassName(), EndpointType.EJB);
            if (endpointInfo != null) {
                jaxWsModuleInfo.addEndpointInfo(ejbEndpoint.getName(), endpointInfo);
                jaxWsServerMetaData.putEndpointNameAndJ2EENameEntry(ejbName, ejbEndpoint.getJ2EEName());
            }
        }
    }
}
