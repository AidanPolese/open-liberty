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
package com.ibm.wsspi.logging;

import java.io.PrintWriter;

/**
 * A service that can be notified when the {@code server dump} command is used.
 */
public interface Introspector {
    /**
     * The name of the introspector, which is used for the introspection file
     * name. Names should follow the naming convention for a Java class and
     * typically end with {@code Introspector}; for example, {@code TestComponentIntrospector}.
     */
    String getIntrospectorName();

    /**
     * A description of the introspector, which is added to the introspection file.
     */
    String getIntrospectorDescription();

    /**
     * Performs the introspection. Implementations should be robust, but for
     * convenience, this method allows any exception to be thrown.
     */
    void introspect(PrintWriter out) throws Exception;
}
