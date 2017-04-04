/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.javaee.ddmodel.wsbnd.impl;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;

import com.ibm.ws.javaee.ddmodel.wsbnd.WebserviceEndpointProperties;

@Component(configurationPid = "com.ibm.ws.javaee.ddmodel.wsbnd.WebserviceEndpointProperties",
           configurationPolicy = ConfigurationPolicy.REQUIRE,
           immediate = true,
           property = "service.vendor = IBM")
public class WebserviceEndpointPropertiesComponentImpl implements WebserviceEndpointProperties {

    private final Map<String, String> attributes = new HashMap<String, String>();

    // These are properties added by the config runtime -- ignore them.
    private final String[] ignoredPrefixes = { "service.", "config.", "component." };

    @Activate
    protected void activate(Map<String, Object> config) {
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            String name = entry.getKey();
            if (!isInternal(name))
                attributes.put(entry.getKey(), (String) entry.getValue());
        }
    }

    @Deactivate
    protected void deactivate() {
        attributes.clear();
    }

    /**
     * @param name
     * @return
     */
    private boolean isInternal(String name) {
        for (String prefix : ignoredPrefixes) {
            if (name.startsWith(prefix))
                return true;
        }

        if (name.equals("id"))
            return true;

        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.javaee.ddmodel.wsbnd.WebserviceEndpointProperties#getAttributes()
     */
    @Override
    public Map<String, String> getAttributes() {
        return this.attributes;
    }

}
