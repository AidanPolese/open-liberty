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

import org.osgi.framework.Version;

public class FeatureManifestFile extends UpdatedFile {

    @XmlAttribute
    private String symbolicName;
    @XmlAttribute
    private String version;

    public FeatureManifestFile() {
        //required blank constructor for jaxb
    }

    public FeatureManifestFile(String id, long size, String date, String hash, String symbolicName, Version version) {
        super(id, size, date, hash);
        this.symbolicName = symbolicName;
        this.version = version != null ? version.toString() : null;
    }

    /**
     * @return the symbolicName
     */
    public String getSymbolicName() {
        return this.symbolicName;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return this.version;
    }

}
