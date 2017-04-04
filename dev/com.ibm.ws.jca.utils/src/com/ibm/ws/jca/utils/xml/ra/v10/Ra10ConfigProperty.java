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
@XmlType(name = "configPropertyType", propOrder = { "description", "configPropertyName", "configPropertyType", "configPropertyValue" })
public class Ra10ConfigProperty {

    @XmlElement(name = "description")
    private String description;
    @XmlElement(name = "config-property-name", required = true)
    private String configPropertyName;
    @XmlElement(name = "config-property-type", required = true)
    private String configPropertyType;
    @XmlElement(name = "config-property-value")
    private String configPropertyValue;

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the configPropertyName
     */
    public String getConfigPropertyName() {
        return configPropertyName;
    }

    /**
     * @return the configPropertyType
     */
    public String getConfigPropertyType() {
        return configPropertyType;
    }

    /**
     * @return the configPropertyValue
     */
    public String getConfigPropertyValue() {
        return configPropertyValue;
    }

}
