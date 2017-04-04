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
package com.ibm.ws.app.manager;

import java.util.Map;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;

import com.ibm.ws.app.manager.internal.AppManagerConstants;

@Component(service = ApplicationManager.class,
           immediate = true,
           configurationPid = AppManagerConstants.MANAGEMENT_PID,
           configurationPolicy = ConfigurationPolicy.REQUIRE,
           property = "service.vendor=IBM")
public class ApplicationManager {

    private boolean expandApps;
    private long startTimeout;
    private long stopTimeout;

    protected void activate(ComponentContext compcontext, Map<String, Object> properties) {
        modified(compcontext, properties);
    }

    /**
     * DS method to deactivate this component
     * 
     * @param compcontext the context of this component
     */
    protected void deactivate(ComponentContext compcontext) {

    }

    /**
     * DS method to modify the configuration of this component
     * 
     * @param compcontext the context of this component
     * @param properties the updated configuration properties
     */
    @Modified
    protected void modified(ComponentContext compcontext, Map<String, Object> properties) {
        Boolean autoExpandValue = (Boolean) properties.get("autoExpand");
        setExpandApps(autoExpandValue == null ? false : autoExpandValue);

        long startTimeoutValue = getProperty(properties, "startTimeout", 30L);
        setStartTimeout(startTimeoutValue);
        long stopTimeoutValue = getProperty(properties, "stopTimeout", 30L);
        setStopTimeout(stopTimeoutValue);
        ApplicationStateCoordinator.setApplicationStartTimeout(startTimeoutValue);
        ApplicationStateCoordinator.setApplicationStopTimeout(stopTimeoutValue);
    }

    //get a property and if not set, use the supplied default
    @SuppressWarnings("unchecked")
    private <T> T getProperty(Map<String, Object> properties, String name, T deflt) {
        T value = deflt;
        try {
            T prop = (T) properties.get(name);
            if (prop != null) {
                value = prop;
            }
        } catch (ClassCastException e) {
            //auto FFDC and allow the default value to be returned so that the server still starts
        }
        return value;
    }

    /**
     * @return
     */
    public boolean getExpandApps() {
        return this.expandApps;
    }

    /**
     * @param b
     */
    private void setExpandApps(boolean b) {
        this.expandApps = b;
    }

    /**
     * @return
     */
    public long getStartTimeout() {
        return this.startTimeout;
    }

    /**
     * @param b
     */
    private void setStartTimeout(long b) {
        this.startTimeout = b;
    }

    /**
     * @return
     */
    public long getStopTimeout() {
        return this.stopTimeout;
    }

    /**
     * @param b
     */
    private void setStopTimeout(long b) {
        this.stopTimeout = b;
    }
}
