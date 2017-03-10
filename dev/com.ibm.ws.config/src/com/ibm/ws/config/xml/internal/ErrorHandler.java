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
package com.ibm.ws.config.xml.internal;

import com.ibm.ws.config.admin.ExtendedConfiguration;
import com.ibm.wsspi.kernel.service.utils.OnErrorUtil.OnError;

/**
 *
 */
enum ErrorHandler {

    INSTANCE;

    /** Whether or not Liberty should continue if config error is detected */
    private OnError onError = OnError.WARN;

    /**
     * @param onError
     */
    private ErrorHandler() {}

    /**
     * @param onError
     */
    public void setOnError(OnError onError) {
        this.onError = onError;
    }

    public OnError getOnError() {
        return this.onError;
    }

    public String toTraceString(ExtendedConfiguration config, ConfigElement configElement) {
        if (config.getFactoryPid() == null) {
            return config.getPid();
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(config.getFactoryPid());

            if (configElement != null) {
                builder.append("-");
                builder.append(configElement.getId());
            }

            builder.append(" (");
            builder.append(config.getPid());
            builder.append(")");

            return builder.toString();
        }
    }

    /**
     * @return
     */
    public boolean fail() {
        return onError.equals(OnError.FAIL);
    }

}
