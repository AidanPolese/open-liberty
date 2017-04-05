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
 * Objects implementing this interface will be replaced during activation.
 * 
 * @see PassivatorSerializable
 */
public interface PassivatorSerializableHandle
{
    /**
     * Returns an object that should replace this object during deserialization.
     * The return value can be the called object.
     * 
     * @return a replacement object
     */
    public Object getSerializedObject();
}
