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

/**
 * A bind-able scaling policy. See /com.ibm.ws.scaling.controller/resources/OSGI-INF/metatype/metatype.xml
 * 
 */
public class ScalingPolicy extends ConfigElement {

    // To be implemented

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer(this.getClass().getSimpleName());
        buf.append("{");

        // To be implemented

        buf.append("}");
        return buf.toString();
    }

    @Override
    public ScalingPolicy clone() throws CloneNotSupportedException {
        ScalingPolicy clone = (ScalingPolicy) super.clone();

        // To be implemented

        return clone;
    }

}
