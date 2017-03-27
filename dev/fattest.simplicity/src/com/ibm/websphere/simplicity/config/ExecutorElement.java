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

public class ExecutorElement extends ConfigElement {
    private String coreThreads;
    private String name;
    private String maxThreads;
    private String keepAlive;
    private String stealPolicy;
    private String rejectedWorkPolicy;

    @XmlAttribute(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @XmlAttribute(name = "maxThreads")
    public void setMaxThreads(String maxThreads) {
        this.maxThreads = maxThreads;
    }

    public String getMaxThreads() {
        return maxThreads;
    }

    @XmlAttribute(name = "keepAlive")
    public void setKeepAlive(String keepAlive) {
        this.keepAlive = keepAlive;
    }

    public String getKeepAlive() {
        return keepAlive;
    }

    @XmlAttribute(name = "stealPolicy")
    public void setStealPolicy(String stealPolicy) {
        this.stealPolicy = stealPolicy;
    }

    public String getStealPolicy() {
        return stealPolicy;
    }

    @XmlAttribute(name = "rejectedWorkPolicy")
    public void setRejectedWorkPolicy(String rejectedWorkPolicy) {
        this.rejectedWorkPolicy = rejectedWorkPolicy;
    }

    public String getRejectedWorkPolicy() {
        return rejectedWorkPolicy;
    }

    public String getCoreThreads() {
        return coreThreads;
    }

    @XmlAttribute(name = "coreThreads")
    public void setCoreThreads(String s) {
        this.coreThreads = s;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer("ExecutorElement{");
        if (coreThreads != null)
            buf.append("coreThreads=\"" + coreThreads + "\"");
        buf.append("}");

        return buf.toString();
    }

    public class StealPolicyValues {
        public static final String STRICT = "STRICT";
        public static final String LOCAL = "LOCAL";
        public static final String NEVER = "NEVER";
    }

    public class RejectedWorkPolicyValues {
        public static final String ABORT = "ABORT";
        public static final String CALLER_RUNS = "CALLER_RUNS";
    }
}
