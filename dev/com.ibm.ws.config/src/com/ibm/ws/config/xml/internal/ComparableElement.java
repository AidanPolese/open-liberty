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
public class ComparableElement extends SimpleElement {

    private final String id;
    private final String pid;

    public ComparableElement(SimpleElement element, int index, String pid, String defaultId) {
        super(element);

        // Replace the ID attribute if it's null or was generated
        String idAttribute = element.getId();
        if ((idAttribute == null) || (idAttribute.startsWith("default"))) {
            if (defaultId == null) {
                idAttribute = (index < 1) ? "default-0" : "default-" + index;
                this.usingDefaultId = true;
            } else {
                idAttribute = defaultId;
            }
        }
        this.id = idAttribute;
        this.pid = pid;
    }

    /*
     * This furthers the confusion about what might be returned from this method.
     * child-first processing in ConfigEvaluator apparently needs to deal with both 
     * the original xml element node name or the pid being returned.
     * (non-Javadoc)
     *
     * @see com.ibm.ws.config.xml.internal.ConfigElement#getNodeName()
     */
    @Override
    public String getNodeName() {
        return this.pid == null ? super.getNodeName() : pid;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.config.xml.internal.ConfigElement#getId()
     */
    @Override
    @Trivial
    public String getId() {
        return this.id;
    }
}
