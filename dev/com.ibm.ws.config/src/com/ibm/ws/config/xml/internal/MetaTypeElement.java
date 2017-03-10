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
package com.ibm.ws.config.xml.internal;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 *
 */
public abstract class MetaTypeElement extends ConfigElement {

    protected final String pid;

    public abstract boolean isSingleton();

    public abstract boolean isFactory();

    /**
     * @param element
     */
    public MetaTypeElement(SimpleElement element, String pid) {
        super(element);
        this.pid = pid;
    }

    /**
     * @param nodeName
     */
    public MetaTypeElement(String nodeName, String pid) {
        super(nodeName);
        this.pid = pid;
    }

    @Override
    @Trivial
    public String getFullId() {
        return getConfigID().toString();
    }

    @Override
    @Trivial
    public boolean isSimple() {
        return false;
    }

}
