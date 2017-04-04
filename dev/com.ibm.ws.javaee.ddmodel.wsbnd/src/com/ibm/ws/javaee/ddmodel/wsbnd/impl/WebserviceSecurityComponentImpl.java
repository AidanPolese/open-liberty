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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import com.ibm.ws.config.xml.internal.nester.Nester;
import com.ibm.ws.javaee.dd.common.SecurityRole;
import com.ibm.ws.javaee.dd.web.common.LoginConfig;
import com.ibm.ws.javaee.dd.web.common.SecurityConstraint;
import com.ibm.ws.javaee.ddmodel.wsbnd.WebserviceSecurity;

@Component(configurationPid = "com.ibm.ws.javaee.ddmodel.wsbnd.WebserviceSecurity",
           configurationPolicy = ConfigurationPolicy.REQUIRE,
           immediate = true,
           property = "service.vendor = IBM")
public class WebserviceSecurityComponentImpl implements WebserviceSecurity {

    private LoginConfig loginConfig;
    private final List<SecurityRole> securityRoles = new ArrayList<SecurityRole>();
    private final List<SecurityConstraint> securityConstraints = new ArrayList<SecurityConstraint>();

    @Activate
    protected void activate(Map<String, Object> config) {
        List<Map<String, Object>> loginConfigs = Nester.nest(WebserviceSecurity.LOGIN_CONFIG_ELEMENT_NAME, config);
        if (loginConfigs != null && !loginConfigs.isEmpty())
            this.loginConfig = new LoginConfigImpl(loginConfigs.get(0));

        List<Map<String, Object>> securityConstraintConfigs = Nester.nest(WebserviceSecurity.SECURITY_CONSTRAINT_ELEMENT_NAME, config);
        if (securityConstraintConfigs != null) {
            for (Map<String, Object> securityConstraintConfig : securityConstraintConfigs) {
                securityConstraints.add(new SecurityConstraintImpl(securityConstraintConfig));
            }
        }

        List<Map<String, Object>> securityRoleConfigs = Nester.nest(WebserviceSecurity.SECURITY_ROLE_ELEMENT_NAME, config);
        if (securityRoleConfigs != null) {
            for (Map<String, Object> securityRoleConfig : securityRoleConfigs) {
                securityRoles.add(new SecurityRoleImpl(securityRoleConfig));
            }
        }
    }

    protected void deactivate() {
        this.loginConfig = null;
        this.securityConstraints.clear();
        this.securityRoles.clear();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.javaee.ddmodel.wsbnd.WebserviceSecurity#getSecurityConstraints()
     */
    @Override
    public List<SecurityConstraint> getSecurityConstraints() {
        return securityConstraints;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.javaee.ddmodel.wsbnd.WebserviceSecurity#getSecurityRoles()
     */
    @Override
    public List<SecurityRole> getSecurityRoles() {
        return securityRoles;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.javaee.ddmodel.wsbnd.WebserviceSecurity#getLoginConfig()
     */
    @Override
    public LoginConfig getLoginConfig() {
        return loginConfig;
    }

}
