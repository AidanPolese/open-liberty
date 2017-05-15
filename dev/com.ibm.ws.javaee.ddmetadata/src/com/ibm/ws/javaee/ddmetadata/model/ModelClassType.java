/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmetadata.model;

/**
 * Models a Java class type.
 */
public class ModelClassType implements ModelType {
    /**
     * The Java class name.
     */
    protected final String className;

    public ModelClassType(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + className + ']';
    }

    @Override
    public String getJavaTypeName() {
        return className;
    }

    @Override
    public String getJavaImplTypeName() {
        return className;
    }

    @Override
    public String getJavaListImplTypeName() {
        throw new UnsupportedOperationException(toString());
    }

    @Override
    public String getDefaultValue(String string) {
        return null;
    }
}
