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

import java.util.concurrent.TimeUnit;

public class ModelField {
    public String name;

    /**
     * The modeled type of the return value of the method.
     *
     * @see #list
     */
    public final ModelType type;

    /**
     * The name of the addX method if this field is a List of {@link #type}.
     */
    public String listAddMethodName;

    /**
     * True if this field can be private.
     */
    public final boolean privateAccess;

    private TimeUnit durationTimeUnit;

    private String libertyReference;

    public ModelField(String name, ModelType type, String listAddMethodName, boolean privateAccess) {
        this.name = name;
        this.type = type;
        this.listAddMethodName = listAddMethodName;
        this.privateAccess = privateAccess;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + name + ", " + type + ", list=" + listAddMethodName + ']';
    }

    public String getJavaTypeName() {
        if (listAddMethodName != null) {
            return "DDParser.ParsableListImplements<" + type.getJavaImplTypeName() + ", " + type.getJavaTypeName() + '>';
        }
        return type.getJavaImplTypeName();
    }

    public String getJavaImplTypeName() {
        if (listAddMethodName != null) {
            return type.getJavaListImplTypeName();
        }
        return type.getJavaImplTypeName();
    }

    /**
     * If set, will result in ibm:type=duration(timeUnit)
     */
    public void setDuration(TimeUnit timeUnit) {
        this.durationTimeUnit = timeUnit;
    }

    public TimeUnit getDurationTimeUnit() {
        return this.durationTimeUnit;
    }

    /**
     * If set, will result in ibm:reference="refName"
     */
    public void setLibertyReference(String refName) {
        this.libertyReference = refName;
    }

    public String getLibertyReference() {
        return this.libertyReference;
    }
}
