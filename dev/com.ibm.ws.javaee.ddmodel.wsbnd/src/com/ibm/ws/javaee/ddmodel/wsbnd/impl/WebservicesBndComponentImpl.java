/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.javaee.ddmodel.wsbnd.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.ibm.ws.javaee.ddmodel.wsbnd.HttpPublishing;
import com.ibm.ws.javaee.ddmodel.wsbnd.ServiceRef;
import com.ibm.ws.javaee.ddmodel.wsbnd.WebserviceDescription;
import com.ibm.ws.javaee.ddmodel.wsbnd.WebserviceEndpoint;
import com.ibm.ws.javaee.ddmodel.wsbnd.WebserviceEndpointProperties;
import com.ibm.ws.javaee.ddmodel.wsbnd.WebservicesBnd;
import com.ibm.ws.javaee.ddmodel.wsbnd.internal.StringUtils;
import com.ibm.ws.javaee.ddmodel.wsbnd.internal.WsBndConstants;

@Component(configurationPid = "com.ibm.ws.javaee.ddmodel.wsbnd.WebservicesBnd",
           configurationPolicy = ConfigurationPolicy.REQUIRE,
           immediate = true,
           property = "service.vendor = IBM")
public class WebservicesBndComponentImpl implements WebservicesBnd {

    private WebservicesBnd delegate;

    protected volatile Map<String, ServiceRef> serviceRefTypeMap = new HashMap<String, ServiceRef>();
    protected volatile Map<String, ServiceRef> ejbServiceRefTypeMap = new HashMap<String, ServiceRef>();
    protected volatile HttpPublishing httpPublishing;

    private final Map<String, WebserviceDescription> webserviceDescriptionTypeMap = new HashMap<String, WebserviceDescription>();
    private final Map<String, WebserviceEndpoint> webserviceEndpointTypeMap = new HashMap<String, WebserviceEndpoint>();
    private volatile WebserviceEndpointProperties webserviceEndpointProperties;

    private Map<String, Object> configAdminProperties;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC, name = WebservicesBnd.WEBSERVICE_ENDPOINT_PROPERTIES_ELEMENT_NAME,
               target = WsBndConstants.ID_UNBOUND)
    protected void setWebserviceEndpointProperties(WebserviceEndpointProperties value) {
        this.webserviceEndpointProperties = value;
    }

    protected void unsetWebserviceEndpointProperties(WebserviceEndpointProperties value) {
        this.webserviceEndpointProperties = null;
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, name = WebservicesBnd.WEBSERVICE_ENDPOINT_ELEMENT_NAME,
               target = WsBndConstants.ID_UNBOUND)
    protected void setWebserviceEndpoint(WebserviceEndpoint value) {
        String portComponentName = value.getPortComponentName();

        if (!StringUtils.isEmpty(portComponentName)) {
            webserviceEndpointTypeMap.put(portComponentName.trim(), value);
        }
    }

    protected void unsetWebserviceEndpoint(WebserviceEndpoint value) {
        String portComponentName = value.getPortComponentName();
        if (!StringUtils.isEmpty(portComponentName)) {
            webserviceEndpointTypeMap.remove(portComponentName.trim());
        }
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, name = WebservicesBnd.WEBSERVICE_DESCRIPTION_ELEMENT_NAME,
               target = WsBndConstants.ID_UNBOUND)
    protected void setWebserviceDescriptions(WebserviceDescription value) {
        String serviceName = value.getWebserviceDescriptionName();

        if (!StringUtils.isEmpty(serviceName)) {
            webserviceDescriptionTypeMap.put(serviceName.trim(), value);
        }
    }

    protected void unsetWebserviceDescriptions(WebserviceDescription value) {
        String serviceName = value.getWebserviceDescriptionName();
        if (!StringUtils.isEmpty(serviceName)) {
            webserviceDescriptionTypeMap.remove(serviceName.trim());
        }
    }

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC, name = WebservicesBnd.HTTP_PUBLISHING_ELEMENT_NAME,
               target = WsBndConstants.ID_UNBOUND)
    protected void setHttpPublishing(HttpPublishing value) {
        this.httpPublishing = value;
    }

    protected void unsetHttpPublishing(HttpPublishing value) {
        this.httpPublishing = null;
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, name = WebservicesBnd.SERVICE_REF_ELEMENT_NAME, target = WsBndConstants.ID_UNBOUND)
    protected void setServiceRef(ServiceRef serviceRefType) {
        String serviceRefName = serviceRefType.getName();
        if (StringUtils.isEmpty(serviceRefName)) {
            return;
        }

        String componentName = serviceRefType.getComponentName();
        if (StringUtils.isEmpty(componentName)) {
            serviceRefTypeMap.put(serviceRefName.trim(), serviceRefType);
        } else {
            String serviceRefKey = StringUtils.getEJBServiceRefKey(serviceRefName, componentName);
            ejbServiceRefTypeMap.put(serviceRefKey, serviceRefType);
        }

    }

    protected void unsetServiceRef(ServiceRef value) {
        String serviceRefName = value.getName();
        if (StringUtils.isEmpty(serviceRefName)) {
            return;
        }
        String componentName = value.getComponentName();
        if (StringUtils.isEmpty(componentName)) {
            serviceRefTypeMap.remove(serviceRefName.trim());
        } else {
            String serviceRefKey = StringUtils.getEJBServiceRefKey(serviceRefName, componentName);
            ejbServiceRefTypeMap.remove(serviceRefKey);
        }
    }

    @Activate
    protected void activate(Map<String, Object> config) {
        this.configAdminProperties = config;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.javaee.dd.DeploymentDescriptor#getDeploymentDescriptorPath()
     */
    @Override
    public String getDeploymentDescriptorPath() {
        //FIXME: path determined from container
        return delegate == null ? null : delegate.getDeploymentDescriptorPath();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.javaee.dd.DeploymentDescriptor#getComponentForId(java.lang.String)
     */
    @Override
    public Object getComponentForId(String id) {
        // Not used in Liberty
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.javaee.dd.DeploymentDescriptor#getIdForComponent(java.lang.Object)
     */
    @Override
    public String getIdForComponent(Object ddComponent) {
        // Not used in Liberty
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.javaee.ddmodel.wsbnd.WebservicesBnd#getServiceRefs()
     */
    @Override
    public List<ServiceRef> getServiceRefs() {
        List<ServiceRef> returnValue = delegate == null ? new ArrayList<ServiceRef>() : new ArrayList<ServiceRef>(delegate.getServiceRefs());

        returnValue.addAll(serviceRefTypeMap.values());
        returnValue.addAll(ejbServiceRefTypeMap.values());

        return returnValue;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.javaee.ddmodel.wsbnd.WebservicesBnd#getServiceRef(java.lang.String, java.lang.String)
     */
    @Override
    public ServiceRef getServiceRef(String serviceRefName, String componentName) {
        if (StringUtils.isEmpty(serviceRefName)) {
            return null;
        }

        ServiceRef serviceRef = null;
        //if component name is not empty, find from ejb service ref map first, and then try again from web service ref map
        if (!StringUtils.isEmpty(componentName)) {
            String serviceRefKey = StringUtils.getEJBServiceRefKey(serviceRefName, componentName);
            serviceRef = ejbServiceRefTypeMap.get(serviceRefKey);
        }

        if (null == serviceRef) {
            serviceRef = serviceRefTypeMap.get(serviceRefName.trim());
        }

        if (serviceRef == null && delegate != null) {
            return delegate.getServiceRef(serviceRefName, componentName);
        }

        return serviceRef;

    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.javaee.ddmodel.wsbnd.WebservicesBnd#getHttpPublishing()
     */
    @Override
    public HttpPublishing getHttpPublishing() {
        if (delegate == null) {
            return this.httpPublishing;
        } else {
            return this.httpPublishing == null ? delegate.getHttpPublishing() : this.httpPublishing;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.javaee.ddmodel.wsbnd.WebservicesBnd#getWebserviceDescriptions()
     */
    @Override
    public List<WebserviceDescription> getWebserviceDescriptions() {
        List<WebserviceDescription> webserviceDescriptionList = delegate == null ? new ArrayList<WebserviceDescription>() : new ArrayList<WebserviceDescription>(delegate.getWebserviceDescriptions());
        webserviceDescriptionList.addAll(webserviceDescriptionTypeMap.values());

        return webserviceDescriptionList;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.javaee.ddmodel.wsbnd.WebservicesBnd#getWebserviceDescription(java.lang.String)
     */
    @Override
    public WebserviceDescription getWebserviceDescription(String webserviceDescriptionName) {
        if (webserviceDescriptionName == null)
            return null;

        WebserviceDescription returnValue = webserviceDescriptionTypeMap.get(webserviceDescriptionName);
        if (returnValue == null && delegate != null)
            return delegate.getWebserviceDescription(webserviceDescriptionName);

        return returnValue;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.javaee.ddmodel.wsbnd.WebservicesBnd#getWebserviceEndpointProperties()
     */
    @Override
    public Map<String, String> getWebserviceEndpointProperties() {
        return webserviceEndpointProperties.getAttributes();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.javaee.ddmodel.wsbnd.WebservicesBnd#getWebserviceEndpoints()
     */
    @Override
    public List<WebserviceEndpoint> getWebserviceEndpoints() {
        List<WebserviceEndpoint> returnValue = delegate == null ? new ArrayList<WebserviceEndpoint>() : new ArrayList<WebserviceEndpoint>(delegate.getWebserviceEndpoints());
        returnValue.addAll(webserviceEndpointTypeMap.values());

        return returnValue;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.javaee.ddmodel.wsbnd.WebservicesBnd#getWebserviceEndpoint(java.lang.String)
     */
    @Override
    public WebserviceEndpoint getWebserviceEndpoint(String portComponentName) {
        if (portComponentName == null)
            return null;
        WebserviceEndpoint returnValue = webserviceEndpointTypeMap.get(portComponentName);

        if (returnValue == null && delegate != null)
            return delegate.getWebserviceEndpoint(portComponentName);

        return returnValue;
    }

    public void setDelegate(WebservicesBnd value) {
        this.delegate = value;
    }

    public Map<String, Object> getConfigAdminProperties() {
        return this.configAdminProperties;
    }
}
