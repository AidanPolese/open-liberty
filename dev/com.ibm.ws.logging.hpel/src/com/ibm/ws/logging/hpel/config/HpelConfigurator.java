/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.logging.hpel.config;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.ibm.ws.logging.internal.hpel.HpelTraceServiceConfig;

/**
 *
 */
public class HpelConfigurator {
    /** Mark the initialization of the Tr */
    static final AtomicReference<HpelTraceServiceConfig> loggingConfig = new AtomicReference<HpelTraceServiceConfig>(null);

    /**
     * Initializes HPEL Configuration proxy
     */
    public static synchronized void init(HpelTraceServiceConfig config) {
        if (config == null)
            throw new NullPointerException("LogProviderConfig must not be null");

        loggingConfig.compareAndSet(null, config);
    }

    /**
     * Update Log part of HPEL with new configuration values (based on injection via config
     * admin). The parameter map should be modified to match actual values used
     * (e.g. substitution in case of error).
     * 
     * @param newConfig
     */
    public static synchronized void updateLog(Map<String, Object> newConfig) {
        if (newConfig == null)
            throw new NullPointerException("Updated config must not be null");

        HpelTraceServiceConfig config = loggingConfig.get();
        if (config != null) {
            config.updateLog(newConfig);
            config.getTrDelegate().update(config);
        }
    }

    /**
     * Update Log part of HPEL with new configuration values (based on injection via config
     * admin). The parameter map should be modified to match actual values used
     * (e.g. substitution in case of error).
     * 
     * @param newConfig
     */
    public static synchronized void updateTrace(Map<String, Object> newConfig) {
        if (newConfig == null)
            throw new NullPointerException("Updated config must not be null");

        HpelTraceServiceConfig config = loggingConfig.get();
        if (config != null) {
            config.updateTrace(newConfig);
            config.getTrDelegate().update(config);
        }
    }

//    /**
//     * Update Text part of HPEL with new configuration values (based on injection via config
//     * admin). The parameter map should be modified to match actual values used
//     * (e.g. substitution in case of error).
//     * 
//     * @param newConfig
//     */
//    public static synchronized void updateText(Map<String, Object> newConfig) {
//        if (newConfig == null)
//            throw new NullPointerException("Updated config must not be null");
//
//        HpelTraceServiceConfig config = loggingConfig.get();
//        if (config != null) {
//            config.updateText(newConfig);
//            config.getTrDelegate().update(config);
//        }
//    }
}
