/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jca.processor.jms.util;

import java.util.Collection;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import com.ibm.websphere.config.ConfigEvaluatorException;
import com.ibm.ws.jca.rar.ResourceAdapterBundleService;

public class JMSResourceDefinitionHelper {

    private static final String BUNDLE_LOCATION = "ConnectorModuleMetatype@ConnectorModule:";

    /**
     * Get the bundle where the resource adapter's Metatype Information exists.
     * 
     * @param resourceAdapter
     * @return
     * @throws ConfigEvaluatorException
     */
    public static Bundle getBundle(BundleContext bundleContext, String resourceAdapter) throws ConfigEvaluatorException {

        Bundle bundle = null;

        if (JMSResourceDefinitionConstants.RESOURCE_ADAPTER_WASJMS.equals(resourceAdapter)
            || JMSResourceDefinitionConstants.RESOURCE_ADAPTER_WMQJMS.equals(resourceAdapter)) {
            Collection<ServiceReference<ResourceAdapterBundleService>> resourceAdapterServices;
            try {
                resourceAdapterServices = bundleContext.getServiceReferences(ResourceAdapterBundleService.class,
                                                                             "(type=".concat(resourceAdapter).concat(")"));
                bundle = resourceAdapterServices.iterator().next().getBundle();

            } catch (InvalidSyntaxException e) {
            }
        } else {
            bundle = bundleContext.getBundle(BUNDLE_LOCATION + resourceAdapter);
        }

        return bundle;
    }

}
