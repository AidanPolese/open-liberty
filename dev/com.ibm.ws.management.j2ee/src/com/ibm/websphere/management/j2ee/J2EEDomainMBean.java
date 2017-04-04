/**
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.management.j2ee;

/**
 * The J2EEDomain managed object type represents a management domain. There
 * must be one managed object that implements the J2EEDomain model per
 * management domain. All servers and applications associated with the same domain
 * must be accessible from the J2EEDomain managed object.
 */
public interface J2EEDomainMBean extends J2EEManagedObjectMBean {

    /**
     * A list of all J2EE Servers in this domain. For each J2EE Server running in the
     * domain, there must be one J2EEServer OBJECT_NAME in the servers list that
     * identifies it.
     */
    String[] getservers();

}
