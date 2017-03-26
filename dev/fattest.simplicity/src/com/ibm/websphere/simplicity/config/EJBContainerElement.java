/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.simplicity.config;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 */
public class EJBContainerElement extends ConfigElement {

    private Integer cacheSize;
    private Long poolCleanupInterval;
    private Long cacheCleanupInterval;
    private Boolean startEJBsAtAppStart;
    private EJBAsynchronousElement asynchronous;
    private EJBTimerServiceElement timerService;

    public Integer getCacheSize() {
        return cacheSize;
    }

    @XmlAttribute(name = "cacheSize")
    public void setCacheSize(Integer cacheSize) {
        this.cacheSize = cacheSize;
    }

    public Long getPoolCleanupInterval() {
        return poolCleanupInterval;
    }

    @XmlAttribute(name = "poolCleanupInterval")
    public void setPoolCleanupInterval(Long poolCleanupInterval) {
        this.poolCleanupInterval = poolCleanupInterval;
    }

    public Long getCacheCleanupInterval() {
        return cacheCleanupInterval;
    }

    @XmlAttribute(name = "cacheCleanupInterval")
    public void setCacheCleanupInterval(Long cacheCleanupInterval) {
        this.cacheCleanupInterval = cacheCleanupInterval;
    }

    public Boolean getStartEJBsAtAppStart() {
        return startEJBsAtAppStart;
    }

    @XmlAttribute(name = "startEJBsAtAppStart")
    public void setStartEJBsAtAppStart(Boolean startEJBsAtAppStart) {
        this.startEJBsAtAppStart = startEJBsAtAppStart;
    }

    public EJBAsynchronousElement getAsynchronous() {
        return asynchronous;
    }

    @XmlElement(name = "asynchronous")
    public void setAsynchronous(EJBAsynchronousElement asynchronous) {
        this.asynchronous = asynchronous;
    }

    public EJBTimerServiceElement getTimerService() {
        return timerService;
    }

    @XmlElement(name = "timerService")
    public void setTimerService(EJBTimerServiceElement timerService) {
        this.timerService = timerService;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer("EJBContainerElement {");

        if (cacheSize != null)
            buf.append("cacheSize=\"" + cacheSize + "\" ");
        if (poolCleanupInterval != null)
            buf.append("poolCleanupInterval=\"" + poolCleanupInterval + "\" ");
        if (cacheCleanupInterval != null)
            buf.append("cacheCleanupInterval=\"" + cacheCleanupInterval + "\" ");
        if (asynchronous != null)
            buf.append(", " + asynchronous);
        if (timerService != null)
            buf.append(", " + timerService);

        buf.append("}");
        return buf.toString();
    }
}
