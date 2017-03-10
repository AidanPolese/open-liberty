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
 * Representation of the &lt;offering&gt; XML element in an iFix XML file.
 */
public class Offering {

    private String id;

    private String tolerance;

    public Offering() {
        //required empty constructor for jaxb
    }

    public Offering(String id, String tolerance) {
        this.id = id;
        this.tolerance = tolerance;
    }

    public String getId() {
        return this.id;
    }

    @XmlAttribute
    public void setId(String id) {
        this.id = id;
    }

    public String getTolerance() {
        return this.tolerance;
    }

    @XmlAttribute
    public void setTolerance(String tolerance) {
        this.tolerance = tolerance;
    }
}
