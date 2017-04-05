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
package com.ibm.ws.serialization;

/**
 * A service interface for declaring classes and packages that can be
 * deserialized from the registering bundle.
 */
public final class DeserializationClassProvider {
    /**
     * A service property containing the class names that may be loaded for
     * deserialization from the registering bundle. Only a single bundle may
     * provide a class. The property value should be either a String or
     * String[] ("|"-delimited in bnd).
     */
    public static final String CLASSES_ATTRIBUTE = "classes";

    /**
     * A service property containing the package names that may be used to
     * load classes for deserialization from the registering bundle. Only a
     * single bundle may provide a package. The property value should either
     * be a String or String[] ("|"-delimited in bnd).
     */
    public static final String PACKAGES_ATTRIBUTE = "packages";
}
