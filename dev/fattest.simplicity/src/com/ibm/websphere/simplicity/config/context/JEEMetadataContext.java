/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.simplicity.config.context;

import com.ibm.websphere.simplicity.config.ConfigElement;

/**
 * Represents the <jeeMetadataContext> element which can be nested under <contextService>
 */
public class JEEMetadataContext extends ConfigElement {
    /**
     * Returns a string containing a list of the properties and their values.
     * 
     * @return String representing the data
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(getClass().getSimpleName()).append('{');
        if (getId() != null)
            buf.append("id=").append(getId()).append(' ');
        buf.append("}");
        return buf.toString();
    }
}