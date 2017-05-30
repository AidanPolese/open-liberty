/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.jbatch.container.services;

import javax.persistence.TypedQuery;

import com.ibm.jbatch.container.persistence.jpa.JobInstanceEntity;

/**
 * Helper to translate search parameters into usable JPQL
 */
public interface IJPAQueryHelper {

    /**
     * Get the JPQL query string
     */
    String getQuery();

    /**
     * Populate the query parameters
     */
    void setQueryParameters(TypedQuery<JobInstanceEntity> query);

    /**
     * Set a submitter id to handle authorization
     */
    void setAuthSubmitter(String submitter);

}
