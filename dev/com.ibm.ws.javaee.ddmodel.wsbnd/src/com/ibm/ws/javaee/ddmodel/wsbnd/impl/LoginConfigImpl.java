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

import java.util.List;
import java.util.Map;

import com.ibm.ws.config.xml.internal.nester.Nester;
import com.ibm.ws.javaee.dd.web.common.FormLoginConfig;
import com.ibm.ws.javaee.dd.web.common.LoginConfig;

class LoginConfigImpl implements LoginConfig {
    private final String authMethod;
    private final String realmName;
    private FormLoginConfig formLoginConfig;

    /**
     * @param nest
     */
    public LoginConfigImpl(Map<String, Object> config) {
        this.authMethod = (String) config.get("auth-method");
        this.realmName = (String) config.get("realm-name");
        List<Map<String, Object>> flConfig = Nester.nest("form-login-config", config);
        if (flConfig != null && !flConfig.isEmpty()) {
            this.formLoginConfig = new FormLoginConfigImpl(flConfig.get(0));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.javaee.dd.web.common.LoginConfig#getAuthMethod()
     */
    @Override
    public String getAuthMethod() {
        return this.authMethod;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.javaee.dd.web.common.LoginConfig#getRealmName()
     */
    @Override
    public String getRealmName() {
        return this.realmName;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.javaee.dd.web.common.LoginConfig#getFormLoginConfig()
     */
    @Override
    public FormLoginConfig getFormLoginConfig() {
        return this.formLoginConfig;
    }

    private class FormLoginConfigImpl implements FormLoginConfig {

        private final String formErrorPage;
        private final String formLoginPage;

        /**
         * @param nest
         */
        public FormLoginConfigImpl(Map<String, Object> config) {
            this.formLoginPage = (String) config.get("form-login-page");
            this.formErrorPage = (String) config.get("form-error-page");
        }

        /*
         * (non-Javadoc)
         *
         * @see com.ibm.ws.javaee.dd.web.common.FormLoginConfig#getFormLoginPage()
         */
        @Override
        public String getFormLoginPage() {
            return formLoginPage;
        }

        /*
         * (non-Javadoc)
         *
         * @see com.ibm.ws.javaee.dd.web.common.FormLoginConfig#getFormErrorPage()
         */
        @Override
        public String getFormErrorPage() {
            return formErrorPage;
        }
    }
}
