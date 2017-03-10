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
package com.ibm.websphere.metatype;

/**
 * A listener for changes to schema or metatype
 */
public interface SchemaChangeListener {
    /**
     * This method is called when schema is updated.
     * 
     */
    void schemaUpdated();

}
