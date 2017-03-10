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

import java.util.List;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.config.admin.ConfigID;

/**
 *
 */
public class SingletonElement extends MetaTypeElement {

    /**
     * @param nodeName
     */
    public SingletonElement(String nodeName, String pid) {
        super(nodeName, pid);
    }

    /**
     * @param configElement
     * @param factoryPid
     */
    public SingletonElement(SimpleElement configElement, String pid) {
        super(configElement, pid);
    }

    /**
     * @param elements
     * @throws ConfigMergeException
     */
    public SingletonElement(List<SimpleElement> elements, String pid) throws ConfigMergeException {
        super(elements.get(0).getNodeName(), pid);
        this.mergeBehavior = elements.get(0).mergeBehavior;
        setDocumentLocation(elements.get(0).getDocumentLocation());
        merge(elements);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.config.xml.internal.ConfigElement#isSingleton()
     */
    @Override
    @Trivial
    public boolean isSingleton() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.config.xml.internal.ConfigElement#isFactory()
     */
    @Override
    @Trivial
    public boolean isFactory() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.config.xml.internal.ConfigElement#getId()
     */
    @Override
    @Trivial
    public String getId() {
        return null;
    }

    @Override
    public ConfigID getConfigID() {
        return new ConfigID(this.pid, null);
    }

}
