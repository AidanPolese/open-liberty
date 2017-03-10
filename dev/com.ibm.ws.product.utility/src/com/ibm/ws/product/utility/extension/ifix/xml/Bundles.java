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

public class Bundles {
    @XmlElement(name = "bundle")
    private List<BundleFile> files;

    public Bundles() {
        //no-op
        //default constructor required by jaxb
    }

    public Bundles(List<BundleFile> files) {
        this.files = files;
    }

    public List<BundleFile> getBundleFiles() {
        return this.files;
    }
}
