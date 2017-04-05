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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ibm.ws.config.xml.internal.nester.Nester;
import com.ibm.ws.javaee.dd.common.Description;
import com.ibm.ws.javaee.dd.web.common.AuthConstraint;

/**
 *
 */
public class AuthConstraintImpl implements AuthConstraint {

    private List<String> roleNames;
    private final List<Description> descriptions = new ArrayList<Description>();

    /**
     * @param map
     */
    public AuthConstraintImpl(Map<String, Object> config) {
        String[] value = (String[]) config.get("role-name");
        if (value != null)
            roleNames = Arrays.asList(value);

        List<Map<String, Object>> descriptionConfigs = Nester.nest("description", config);
        if (descriptionConfigs != null) {
            for (Map<String, Object> descriptionConfig : descriptionConfigs) {
                descriptions.add(new DescriptionImpl(descriptionConfig));
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.javaee.dd.common.Describable#getDescriptions()
     */
    @Override
    public List<Description> getDescriptions() {
        return descriptions;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.javaee.dd.web.common.AuthConstraint#getRoleNames()
     */
    @Override
    public List<String> getRoleNames() {
        return roleNames;
    }

}
