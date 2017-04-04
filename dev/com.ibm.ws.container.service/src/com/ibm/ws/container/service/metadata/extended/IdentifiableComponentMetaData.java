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
package com.ibm.ws.container.service.metadata.extended;

/**
 * This interface allows component metadata to customize its persistent identifier.
 */
public interface IdentifiableComponentMetaData {
    /**
     * Returns an identifier for the component metadata. The identifier must remain valid across all instances of the server,
     * and for all releases where this type of component metadata is supported.
     * The identifier must start with a prefix of alphanumeric characters followed by the # character.
     * A DeferredMetaDataFactory implementation must be registered to handle metadata with the chosen prefix.
     * The following prefixes are already taken (EJB, WEB, CONNECTOR)
     * 
     * @return an identifier for the component metadata.
     */
    String getPersistentIdentifier();
}
