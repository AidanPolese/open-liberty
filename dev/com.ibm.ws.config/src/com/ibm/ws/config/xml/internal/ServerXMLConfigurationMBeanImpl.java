/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.config.xml.internal;

import java.util.Collection;

import javax.management.DynamicMBean;
import javax.management.StandardMBean;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

import com.ibm.websphere.config.mbeans.ServerXMLConfigurationMBean;

@Component(service = { ServerXMLConfigurationMBean.class, DynamicMBean.class },
           immediate = false,
           configurationPolicy = ConfigurationPolicy.IGNORE,
           property = "jmx.objectname=" + ServerXMLConfigurationMBean.OBJECT_NAME)
public class ServerXMLConfigurationMBeanImpl extends StandardMBean implements ServerXMLConfigurationMBean {

    private volatile SystemConfiguration systemConfiguration;

    public ServerXMLConfigurationMBeanImpl() {
        super(ServerXMLConfigurationMBean.class, false);
    }

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected void setSystemConfiguration(SystemConfiguration sc) {
        this.systemConfiguration = sc;
    }

    protected void unsetSystemConfiguration(SystemConfiguration sc) {
        if (sc == this.systemConfiguration) {
            this.systemConfiguration = null;
        }
    }

    @Override
    public Collection<String> fetchConfigurationFilePaths() {
        return systemConfiguration.fetchConfigurationFilePaths();
    }
}
