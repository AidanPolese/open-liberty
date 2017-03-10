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

public class Updates {
    @XmlElement(name = "file")
    private Set<UpdatedFile> files;

    public Updates() {
        //required empty constructor for jaxb
    }

    public Updates(Set<UpdatedFile> files) {
        this.files = files;
    }

    /**
     * @return the files
     */
    public Set<UpdatedFile> getFiles() {
        return files;
    }
}
