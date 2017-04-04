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
package com.ibm.ws.classloading.configuration;

import java.util.Map;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

@Component(service = GlobalClassloadingConfiguration.class, immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE, configurationPid = "com.ibm.ws.classloading.global", property = "service.vendor=IBM")
public class GlobalClassloadingConfiguration {

    private Map<String, Object> properties;

    @Activate
    protected void activate(ComponentContext cCtx, Map<String, Object> properties) {

        this.properties = properties;
    }

    @Deactivate
    protected void deactivate(ComponentContext cCtx) {}

    @Modified
    protected void modified(ComponentContext ctx, Map<String, Object> props) {
        this.properties = props;
    }

    /**
     * @return
     */
    public boolean useJarUrls() {
        return (Boolean) properties.get("useJarUrls");
    }

}
