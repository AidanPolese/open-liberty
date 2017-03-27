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
package com.ibm.websphere.simplicity.config;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * <p>
 * This represents an XML element for the server configuration monitor such as:
 * </p>
 * <code>&lt;config monitorInterval="500ms" updateTrigger="polled"/&gt;</code>
 */
public class ConfigMonitorElement extends ConfigElement {

    private String updateTrigger;

    private String monitorInterval;

    /**
     * @return the updateTrigger
     */
    public String getUpdateTrigger() {
        return updateTrigger;
    }

    /**
     * @param updateTrigger the updateTrigger to set
     */
    @XmlAttribute(name = "updateTrigger")
    public void setUpdateTrigger(String updateTrigger) {
        this.updateTrigger = updateTrigger;
    }

    /**
     * @return the monitorInterval
     */
    public String getMonitorInterval() {
        return monitorInterval;
    }

    /**
     * @param monitorInterval the monitorInterval to set
     */
    @XmlAttribute(name = "monitorInterval")
    public void setMonitorInterval(String monitorInterval) {
        this.monitorInterval = monitorInterval;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer("ConfigMonitorElement{");
        if (updateTrigger != null)
            buf.append("updateTrigger=\"" + updateTrigger + "\" ");
        if (monitorInterval != null)
            buf.append("monitorInterval=\"" + monitorInterval + "\" ");
        buf.append("}");

        return buf.toString();
    }
}
