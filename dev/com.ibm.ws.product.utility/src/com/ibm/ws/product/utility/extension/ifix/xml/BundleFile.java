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

public class BundleFile extends UpdatedFile {

    @XmlAttribute
    private String symbolicName;
    @XmlAttribute
    private String version;
    @XmlAttribute
    private boolean isBaseBundle;

    public BundleFile() {
        //required blank constructor for jaxb
    }

    public BundleFile(String id, long size, String date, String hash, String symbolicName, String version, boolean isBaseBundle) {
        super(id, size, date, hash);
        this.symbolicName = symbolicName;
        this.version = version;
        this.isBaseBundle = isBaseBundle;
    }

    public String getSymbolicName() {
        return this.symbolicName;
    }

    public String getVersion() {
        return this.version;
    }

    public boolean isBaseBundle() {
        return this.isBaseBundle;
    }
}
