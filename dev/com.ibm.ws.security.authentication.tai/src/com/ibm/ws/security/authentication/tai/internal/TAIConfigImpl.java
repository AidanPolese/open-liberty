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
package com.ibm.ws.security.authentication.tai.internal;

import java.util.Map;

import com.ibm.ws.security.authentication.tai.TAIConfig;

/**
 * Represents security configurable options for web applications.
 */
public class TAIConfigImpl implements TAIConfig {
    //TrustAssocitaion properties
    static final String KEY_INVOKE_FOR_UNPROTECTED_URI = "invokeForUnprotectedURI";
    static final String KEY_INVOKE_FOR_FORM_LOGIN = "invokeForFormLogin";
    static final String KEY_FAIL_OVER_TO_APP_AUTH_TYPE = "failOverToAppAuthType";

    private boolean failOverToAppAuthType = false;
    private boolean invokeForUnprotectedURI = false;
    private boolean invokeForFormLogin = false;

    /** The required shared library */

    public TAIConfigImpl(Map<String, Object> props) {
        processConfig(props);
    }

    /**
     * @param props
     */
    void processConfig(Map<String, Object> props) {
        if (props == null)
            return;
        invokeForUnprotectedURI = (Boolean) props.get(KEY_INVOKE_FOR_UNPROTECTED_URI);
        invokeForFormLogin = (Boolean) props.get(KEY_INVOKE_FOR_FORM_LOGIN);
        failOverToAppAuthType = (Boolean) props.get(KEY_FAIL_OVER_TO_APP_AUTH_TYPE);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isInvokeForFormLogin() {
        return invokeForFormLogin;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFailOverToAppAuthType() {
        return failOverToAppAuthType;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isInvokeForUnprotectedURI() {
        return invokeForUnprotectedURI;
    }
}
