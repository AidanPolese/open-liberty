/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.simplicity.application.loose;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Represents a single entry in a virtual archive XML document.
 */
public class VirtualEntry {

    private String targetInArchive;

    /**
     * No-argument constructor (required by JAXB)
     */
    public VirtualEntry() {
        super(); // required by JAXB
    }

    /**
     * Primary constructor for virtual entries
     * 
     * @param targetInArchive the virtual location of this entry in the parent archive
     */
    public VirtualEntry(String targetInArchive) {
        this.setTargetInArchive(targetInArchive);
    }

    /**
     * @return the virtual location of this entry in the parent archive
     */
    public String getTargetInArchive() {
        return targetInArchive;
    }

    /**
     * Defines the virtual location of this entry in the parent archive
     * 
     * @param targetInArchive the virtual location of this entry in the parent archive
     */
    @XmlAttribute
    public void setTargetInArchive(String targetInArchive) {
        this.targetInArchive = targetInArchive;
    }

}
