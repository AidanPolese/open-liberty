/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.channelfw;

import java.util.Collections;
import java.util.Map;

import org.osgi.framework.Constants;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.channelfw.internal.ChannelFrameworkConstants;

/**
 *
 */
public class ChannelConfiguration {

    /** Trace service */
    private static final TraceComponent tc =
                    Tr.register(ChannelConfiguration.class,
                                ChannelFrameworkConstants.BASE_TRACE_NAME,
                                ChannelFrameworkConstants.BASE_BUNDLE);

    private volatile Map<String, Object> config = null;

    protected void activate(Map<String, Object> config) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(this, tc, "Activating " + config.get(Constants.SERVICE_PID), config);
        }
        this.config = Collections.unmodifiableMap(config);
    }

    protected void deactivate(Map<String, Object> config, int reason) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(this, tc, "Deactivating " + config.get(Constants.SERVICE_PID) + ", reason=" + reason, config);
        }
    }

    protected void modified(Map<String, Object> config) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(this, tc, "Modified " + config.get(Constants.SERVICE_PID), config);
        }
        this.config = Collections.unmodifiableMap(config);
    }

    public Map<String, Object> getConfiguration() {
        return config;
    }

    public Object getProperty(String key) {
        Map<String, Object> map = config;

        return map == null ? null : map.get(key);
    }

    @Override
    public String toString() {
        return config.toString();
    }
}
