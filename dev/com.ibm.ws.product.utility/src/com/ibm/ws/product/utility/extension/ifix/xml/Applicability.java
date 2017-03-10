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

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * Representation of the &lt;applicability&gt; XML element in an iFix XML file.
 */
public class Applicability {

    @XmlElement(name = "offering")
    private List<Offering> offerings;

    public Applicability() {
        //blank constructor required for jaxb to work
    }

    public Applicability(List<Offering> offeringList) {
        offerings = offeringList;
    }

    /**
     * @return the offerings in this applicability element or <code>null</code> if there aren't any
     */
    public List<Offering> getOfferings() {
        return offerings;
    }
}
