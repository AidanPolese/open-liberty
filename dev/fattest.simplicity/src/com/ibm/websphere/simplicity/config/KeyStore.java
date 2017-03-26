/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.simplicity.config;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * KeyStore element is defined here:<br>
 * /com.ibm.ws.ssl/resources/OSGI-INF/metatype/metatype.xml
 */
public class KeyStore extends ConfigElement {

    private String password;
    private String location;
    private String type;

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    @XmlAttribute(name = "password")
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    @XmlAttribute(name = "location")
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    @XmlAttribute(name = "type")
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer("KeyStore{");
        if (password != null)
            buf.append("password=\"" + password + "\" ");
        if (location != null)
            buf.append("location=\"" + location + "\" ");
        if (type != null)
            buf.append("type=\"" + type + "\" ");
        buf.append("}");
        return buf.toString();
    }

}
