/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.util;

import java.lang.reflect.Field;

/**
 * Wrapper around {@code ClassValue<Field>} that returns a declared field that
 * has been made accessible from the specified class. This abstraction exists
 * for portability to Java 6, which does not have {@code ClassValue}.
 */
public interface FieldClassValue {
    /**
     * Returns a specific declared field from the class.
     *
     * @param klass the class that must contain the field
     * @return the declared field
     * @throws IllegalStateException if the class does not contain the field
     */
    Field get(Class<?> klass);
}
