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

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

public class FeatureManifests {
    @XmlElement(name = "manifest")
    private Set<FeatureManifestFile> files;

    public FeatureManifests() {
        //no-op
        //default constructor required by jaxb
    }

    public FeatureManifests(Set<FeatureManifestFile> files) {
        this.files = files;
    }

    /**
     * @return the list of manifest files
     */
    public Set<FeatureManifestFile> getManifests() {
        return this.files;
    }

}
