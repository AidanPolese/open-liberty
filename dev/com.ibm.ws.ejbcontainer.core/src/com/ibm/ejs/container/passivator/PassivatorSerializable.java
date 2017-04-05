/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2009
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container.passivator;

/**
 * Objects implementing this interface will be replaced during passivation.
 */
public interface PassivatorSerializable
{
    /**
     * Returns an object that should be serialized instead of this object.
     * 
     * <p>The return value is intended to help implementors via type-safety.
     * The current implementation does not depend on the value being a handle.
     * 
     * @return a replacement object
     */
    public PassivatorSerializableHandle getSerializableObject();
}
