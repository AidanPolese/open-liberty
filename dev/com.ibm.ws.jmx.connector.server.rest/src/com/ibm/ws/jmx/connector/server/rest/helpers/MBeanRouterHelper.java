/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jmx.connector.server.rest.helpers;

import java.io.IOException;
import java.util.List;

import javax.management.AttributeList;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jmx.connector.converter.JSONConverter;
import com.ibm.ws.jmx.connector.converter.NotificationTargetInformation;
import com.ibm.ws.jmx.connector.server.rest.APIConstants;
import com.ibm.wsspi.collective.plugins.CollectivePlugin;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;
import com.ibm.wsspi.rest.handler.RESTRequest;
import com.ibm.wsspi.rest.handler.RESTResponse;

@Component(service = { MBeanRouterHelper.class }, configurationPolicy = ConfigurationPolicy.IGNORE, immediate = true, property = { "service.vendor=IBM" })
public class MBeanRouterHelper {

    private static final TraceComponent tc = Tr.register(MBeanRouterHelper.class);
    private static final String KEY_COLLECTIVE_PLUGIN = "collectivePlugin";

    private static final AtomicServiceReference<CollectivePlugin> collectivePluginRef = new AtomicServiceReference<CollectivePlugin>(KEY_COLLECTIVE_PLUGIN);

    @Activate
    protected void activate(ComponentContext cc) {
        collectivePluginRef.activate(cc);

        if (tc.isEventEnabled()) {
            Tr.event(tc, this.getClass().getSimpleName() + " has been activated.");
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext cc) {
        if (tc.isEventEnabled()) {
            Tr.event(tc, this.getClass().getSimpleName() + " has been deactivated.");
        }

        collectivePluginRef.deactivate(cc);
    }

    /**
     * Builds an instance of NotificationTargetInformation from the headers of an RESTRequest and a JMX ObjectName (as a string).
     */
    public static NotificationTargetInformation toNotificationTargetInformation(RESTRequest request, String objectName) {
        //Handle incoming routing context (if applicable)
        String[] routingContext = RESTHelper.getRoutingContext(request, false);

        if (routingContext != null) {
            return new NotificationTargetInformation(objectName, routingContext[0], routingContext[2], routingContext[1]);
        }

        return new NotificationTargetInformation(objectName);
    }

    @Reference(name = KEY_COLLECTIVE_PLUGIN, service = CollectivePlugin.class)
    protected void setCollectivePlugin(ServiceReference<CollectivePlugin> ref) {
        collectivePluginRef.setReference(ref);
    }

    protected void unsetCollectivePlugin(ServiceReference<CollectivePlugin> ref) {
        collectivePluginRef.unsetReference(ref);
    }

    protected static CollectivePlugin getReader() throws IOException {
        CollectivePlugin reader = collectivePluginRef.getService();
        if (reader == null) {
            throw new IOException(MBeanRouterMessageUtil.getMessage(MBeanRouterMessageUtil.COLLECTIVE_PLUGIN_NOT_AVAILABLE));
        }
        return reader;
    }

    /**
     * Gets the cached attribute(s) from collective cache
     * 
     * @param response
     * @param objectName objectName
     * @param attribute attribute, if it is null, will get all attributes for the object
     */
    public static boolean getCachedAttribute(RESTRequest request, RESTResponse response, String objectName, String attribute,
                                             List<String> attributeList) {
        JSONConverter converter = JSONConverter.getConverter();
        boolean dataInCache = true;
        try {
            //Handle incoming routing context
            String[] routingContext = RESTHelper.getRoutingContext(request, true);

            final String targetHost = routingContext[0];
            final String targetUserDir = routingContext[1];
            final String targetServer = routingContext[2];

            if (attribute != null && attribute.isEmpty() == false) {
                Object data = getReader().getAttribute(targetHost, targetUserDir, targetServer, objectName, attribute);
                if (data != null) {
                    converter.writePOJO(response.getOutputStream(), data);
                    response.getOutputStream().flush();
                } else {
                    if (tc.isDebugEnabled()) {
                        Tr.debug(tc, "The MBean attribute could not be loaded", targetHost, targetUserDir, targetServer, objectName, attribute);
                    }
                    dataInCache = false;
                }
            } else {
                // need to get attributeList
                AttributeList al = getReader().getAttributes(targetHost, targetUserDir, targetServer, objectName);
                if (al != null) {
                    if (attributeList != null && !attributeList.isEmpty()) {
                        //The AttributeList that we got from the CollectivePlugin will contain ALL the attributes for this MBean. So
                        //only retain the ones that were queried.
                        al.retainAll(attributeList);
                    }
                    converter.writeAttributeList(response.getOutputStream(), al);
                    response.getOutputStream().flush();
                } else {
                    if (tc.isDebugEnabled()) {
                        Tr.debug(tc, "The MBean attributes could not be loaded", targetHost, targetUserDir, targetServer, objectName);
                    }
                    dataInCache = false;
                }

            }
        } catch (Exception e) {
            throw ErrorHelper.createRESTHandlerJsonException(e, converter, APIConstants.STATUS_INTERNAL_SERVER_ERROR);
        } finally {
            JSONConverter.returnConverter(converter);
        }
        return dataInCache;
    }
}