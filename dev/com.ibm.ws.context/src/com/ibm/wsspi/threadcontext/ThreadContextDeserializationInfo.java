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
package com.ibm.wsspi.threadcontext;

/**
 * This interface describes information that is made available to a ThreadContextProvider to help deserialize a ThreadContext from its
 * serialized state.
 */
public interface ThreadContextDeserializationInfo {
    /**
     * Returns the value of a property that describes the contextual task and provides additional details
     * about how the task submitter wants it to run.
     * 
     * @param name The name of the property that should be retrieved.
     * @return The value of the property, or null if the property does not exist.
     */
    public String getExecutionProperty(String name);

    /**
     * Returns the MetaDataIdentifier of the thread from which context was captured.
     * 
     * @return the MetaDataIdentifier of the thread from which context was captured.
     */
    public String getMetadataIdentifier();
}
