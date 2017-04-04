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
package com.ibm.ws.jca.utils.xml.ra.v10;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 */
@XmlType(name = "securityPermissionType", propOrder = { "description", "securityPermissionSpec" })
public class Ra10SecurityPermission {

    @XmlElement(name = "description")
    private String description;
    @XmlElement(name = "security-permission-spec", required = true)
    private String securityPermissionSpec;

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the securityPermissionSpec
     */
    public String getSecurityPermissionSpec() {
        return securityPermissionSpec;
    }

}
