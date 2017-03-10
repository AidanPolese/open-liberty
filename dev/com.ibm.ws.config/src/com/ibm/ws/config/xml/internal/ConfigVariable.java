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
package com.ibm.ws.config.xml.internal;

import com.ibm.ws.config.xml.internal.XMLConfigParser.MergeBehavior;

/**
 *
 */
class ConfigVariable {

    private final String name;
    private final String value;
    private final MergeBehavior mergeBehavior;
    private final String location;

    public ConfigVariable(String name, String value, MergeBehavior mb, String l) {
        this.name = name;
        this.value = value;
        this.mergeBehavior = mb;
        this.location = l;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public MergeBehavior getMergeBehavior() {
        return this.mergeBehavior;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("ConfigVariable[");
        builder.append("name=").append(name).append(", ");
        builder.append("value=").append(value);
        builder.append("]");
        return builder.toString();
    }

    /**
     * @return
     */
    public String getDocumentLocation() {
        return location;
    }
}
