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

import com.ibm.ws.config.xml.internal.nester.Nester;
import com.ibm.ws.javaee.dd.common.DisplayName;
import com.ibm.ws.javaee.dd.web.common.AuthConstraint;
import com.ibm.ws.javaee.dd.web.common.SecurityConstraint;
import com.ibm.ws.javaee.dd.web.common.UserDataConstraint;
import com.ibm.ws.javaee.dd.web.common.WebResourceCollection;

/**
 *
 */
public class SecurityConstraintImpl implements SecurityConstraint {

    private final List<DisplayName> displayNames = new ArrayList<DisplayName>();
    private final List<WebResourceCollection> webResourceCollections = new ArrayList<WebResourceCollection>();
    private AuthConstraint authConstraint;
    private UserDataConstraint userDataConstraint;

    /**
     * @param securityConstraintConfig
     */
    public SecurityConstraintImpl(Map<String, Object> config) {
        List<Map<String, Object>> displayNameConfigs = Nester.nest("display-name", config);
        if (displayNameConfigs != null) {
            for (Map<String, Object> displayNameConfig : displayNameConfigs) {
                displayNames.add(new DisplayNameImpl(displayNameConfig));
            }
        }

        List<Map<String, Object>> wrcConfigs = Nester.nest("web-resource-collection", config);
        if (wrcConfigs != null) {
            for (Map<String, Object> wrcConfig : wrcConfigs) {
                webResourceCollections.add(new WebResourceCollectionImpl(wrcConfig));
            }
        }

        List<Map<String, Object>> authConstraintConfigs = Nester.nest("auth-constraint", config);
        if (authConstraintConfigs != null && !authConstraintConfigs.isEmpty()) {
            authConstraint = new AuthConstraintImpl(authConstraintConfigs.get(0));
        }

        List<Map<String, Object>> userDataConstraintConfigs = Nester.nest("user-data-constraint", config);
        if (userDataConstraintConfigs != null && !userDataConstraintConfigs.isEmpty()) {
            userDataConstraint = new UserDataConstraintImpl(userDataConstraintConfigs.get(0));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.javaee.dd.web.common.SecurityConstraint#getDisplayNames()
     */
    @Override
    public List<DisplayName> getDisplayNames() {
        return this.displayNames;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.javaee.dd.web.common.SecurityConstraint#getWebResourceCollections()
     */
    @Override
    public List<WebResourceCollection> getWebResourceCollections() {
        return this.webResourceCollections;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.javaee.dd.web.common.SecurityConstraint#getAuthConstraint()
     */
    @Override
    public AuthConstraint getAuthConstraint() {
        return this.authConstraint;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.javaee.dd.web.common.SecurityConstraint#getUserDataConstraint()
     */
    @Override
    public UserDataConstraint getUserDataConstraint() {
        return this.userDataConstraint;
    }

}
