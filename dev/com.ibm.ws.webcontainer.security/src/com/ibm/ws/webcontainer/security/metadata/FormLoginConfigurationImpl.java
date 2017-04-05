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
package com.ibm.ws.webcontainer.security.metadata;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * Represents a form-login-config element in web.xml.
 * <p/>
 * <pre>
 * &lt;form-login-config&gt;
 * &lt;form-login-page&gt;form-login-page&lt;/form-login-page&gt;
 * &lt;form-error-page>form-error-page&lt;/form-error-page&gt;
 * &lt;/form-login-config&gt;
 * </pre>
 */
public class FormLoginConfigurationImpl implements FormLoginConfiguration {
    private static final TraceComponent tc = Tr.register(FormLoginConfigurationImpl.class);

    private final String loginPage;
    private final String errorPage;

    public FormLoginConfigurationImpl(String loginPage, String errorPage) {
        // TODO: validate the form login configuration. It is not valid for
        // the configuration to be partial or otherwise incomplete

        this.loginPage = loginPage;
        this.errorPage = errorPage;
        if (loginPage == null) {
            Tr.warning(tc, "INVALID_FORM_LOGIN_CONFIGURATION", new Object[] { "form-login-page" });
        }
        if (errorPage == null) {
            Tr.warning(tc, "INVALID_FORM_LOGIN_CONFIGURATION", new Object[] { "form-error-page" });
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getLoginPage() {
        return loginPage;
    }

    /** {@inheritDoc} */
    @Override
    public String getErrorPage() {
        return errorPage;
    }

}
