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
import javax.xml.bind.annotation.XmlValue;

public class Information
{
    @XmlValue
    private String content;
    @XmlAttribute
    private String version;
    @XmlAttribute
    private String name;

    public Information()
    {
        //required blank constructor
    }

    public Information(String name, String version, String content)
    {
        this.name = name;
        this.version = version;
        this.content = content;
    }
}
