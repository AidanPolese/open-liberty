package com.ibm.websphere.simplicity.config;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * A file on the file system local to the server configuration. Note
 * that these file may be remote to the local JVM.
 * 
 * 
 */
public class File extends ConfigElement {

    private String name;

    /**
     * @return value of the name attribute
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name value to use for the name attribute
     */
    @XmlAttribute
    public void setName(String name) {
        this.name = ConfigElement.getValue(name);
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer("File{");
        if (name != null)
            buf.append("name=\"" + name + "\" ");
        buf.append("}");
        return buf.toString();
    }
}
