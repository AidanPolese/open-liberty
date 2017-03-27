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

import javax.xml.bind.annotation.XmlElement;

/**
 * The scaling definitions. See /com.ibm.ws.scaling.controller/resources/OSGI-INF/metatype/metatype.xml
 * 
 */
public class ScalingDefinitions extends ConfigElement {

    @XmlElement(name = "defaultScalingPolicy")
    private DefaultScalingPolicy defaultScalingPolicy;

    @XmlElement(name = "scalingPolicy")
    private ConfigElementList<ScalingPolicy> scalingPolicies;

    /**
     * Retrieves the default scaling policy.
     * 
     * @return The default scaling policy.
     */
    public DefaultScalingPolicy getDefaultScalingPolicy() {
        if (this.defaultScalingPolicy == null) {
            this.defaultScalingPolicy = new DefaultScalingPolicy();
        }

        return this.defaultScalingPolicy;
    }

    /**
     * Retrieves the scaling policies.
     * 
     * @return The scaling policies.
     */
    public ConfigElementList<ScalingPolicy> getScalingPolicies() {
        if (this.scalingPolicies == null) {
            this.scalingPolicies = new ConfigElementList<ScalingPolicy>();
        }
        return this.scalingPolicies;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer(this.getClass().getSimpleName());
        buf.append("{");

        if (this.defaultScalingPolicy != null) {
            buf.append("defaultScalingPolicy: " + this.defaultScalingPolicy.toString() + ", ");
        }
        if (this.scalingPolicies != null) {
            buf.append(" scalingPolicies: { ");
            for (ScalingPolicy policy : scalingPolicies) {
                buf.append(policy.toString() + " ,");
            }
            buf.append("}");
        }
        buf.append("}");
        return buf.toString();
    }

    @Override
    public ScalingDefinitions clone() throws CloneNotSupportedException {
        ScalingDefinitions clone = (ScalingDefinitions) super.clone();
        if (this.defaultScalingPolicy != null) {
            clone.defaultScalingPolicy = this.defaultScalingPolicy.clone();
        }
        if (this.scalingPolicies != null) {
            clone.scalingPolicies = new ConfigElementList<ScalingPolicy>();
            for (ScalingPolicy policy : scalingPolicies) {
                clone.scalingPolicies.add(policy.clone());
            }
        }
        return clone;
    }

}
