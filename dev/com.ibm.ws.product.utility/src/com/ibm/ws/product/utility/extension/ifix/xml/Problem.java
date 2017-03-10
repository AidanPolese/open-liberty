/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.product.utility.extension.ifix.xml;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Representation of the &lt;problem&gt; XML element in an iFix XML file.
 */
public class Problem {

    private String displayId;
    private String description;
    private String id;

    public Problem() {
        //needed as Jaxb needs a blank constructor
    }

    public Problem(String setId, String setDisplayId, String setDescription) {
        id = setId;
        displayId = setDisplayId;
        description = setDescription;
    }

    /**
     * @return the displayId
     */
    public String getDisplayId() {
        return displayId;
    }

    /**
     * @param displayId the displayId to set
     */
    @XmlAttribute
    public void setDisplayId(String displayId) {
        this.displayId = displayId;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    @XmlAttribute
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    @XmlAttribute
    public void setId(String id) {
        this.id = id;
    }

}
