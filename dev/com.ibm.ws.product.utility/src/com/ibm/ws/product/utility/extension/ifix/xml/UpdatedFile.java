/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.product.utility.extension.ifix.xml;

import javax.xml.bind.annotation.XmlAttribute;

public class UpdatedFile {
    @XmlAttribute
    private String id;
    @XmlAttribute
    private long size;
    @XmlAttribute
    private String hash;
    @XmlAttribute
    private String date;

    public UpdatedFile() {
        //required blank constructor for jaxb
    }

    public UpdatedFile(String id, long size, String date, String hash) {
        this.id = id;
        this.size = size;
        this.date = date;
        this.hash = hash;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the size
     */
    public long getSize() {
        return size;
    }

    /**
     * @return the hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

}
