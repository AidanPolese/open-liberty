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
package com.ibm.websphere.simplicity.config;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * The default scaling policy. See /com.ibm.ws.security.registry.basic/resources/OSGI-INF/metatype/metatype.xml
 * 
 */
public class DefaultScalingPolicy extends ConfigElement {

    private boolean enabled = true;
    private int min = 2;
    private int max = -1;

    // Other nested elements to be implemented

    /**
     * Returns the 'enabled' attribute
     * 
     * @return The 'enabled' attribute
     */
    public boolean getEnabled() {
        return this.enabled;
    }

    /**
     * Set the 'enabled' attribute value.
     * 
     * @param enabled The new value for the enabled attribute
     */
    @XmlAttribute(name = "enabled")
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Shortcut to enable the scaling policy.
     */
    public void enable() {
        this.enabled = true;
    }

    /**
     * Shortcut to disable the scaling policy.
     */
    public void disable() {
        this.enabled = false;
    }

    /**
     * Returns the 'min' attribute
     * 
     * @return The 'min' attribute
     */
    public int getMin() {
        return this.min;
    }

    /**
     * Set the 'min' attribute value.
     * 
     * @param min The new value for the 'min' attribute
     */
    @XmlAttribute(name = "min")
    public void setMin(final int min) {
        this.min = min;
    }

    /**
     * Returns the 'max' attribute
     * 
     * @return The 'max' attribute
     */
    public int getMax() {
        return this.max;
    }

    /**
     * Set the 'max' attribute value.
     * 
     * @param max The new value for the 'max' attribute
     */
    @XmlAttribute(name = "max")
    public void setMax(final int max) {
        this.max = max;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer(this.getClass().getSimpleName());
        buf.append("{");
        buf.append("enabled=\"" + this.enabled + "\" ");
        buf.append("min=\"" + this.min + "\" ");
        buf.append("max=\"" + this.max + "\" ");
        buf.append("}");
        return buf.toString();
    }

    @Override
    public DefaultScalingPolicy clone() throws CloneNotSupportedException {
        DefaultScalingPolicy clone = (DefaultScalingPolicy) super.clone();
        return clone;
    }

}
