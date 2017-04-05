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
@XmlType(name = "licenseType", propOrder = { "description", "licenseRequired" })
public class Ra10License {

    @XmlElement(name = "description")
    private String description;
    @XmlElement(name = "license-required", required = true)
    private String licenseRequired;

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the licenseRequired
     */
    public String getLicenseRequired() {
        return licenseRequired;
    }

}
