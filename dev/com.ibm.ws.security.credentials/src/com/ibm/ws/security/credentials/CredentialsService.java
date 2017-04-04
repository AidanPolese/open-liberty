/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.credentials;

import javax.security.auth.Subject;
import javax.security.auth.login.CredentialException;

/**
 * The Credentials Service Interface - it provides a mechanism to
 * construct credentials.
 */
public interface CredentialsService {

    /**
     * Set the credentials (public and/or private) into the
     * given Subject. The CredentialsService delegates to the
     * registered CredentialProvider services.
     * 
     * @param subject
     * @throws CredentialException
     */
    void setCredentials(Subject subject) throws CredentialException;

    /**
     * Set the unauthenticated userid
     * 
     * @param unauthenticatedUser the unauthenticated userid
     */
    public void setUnauthenticatedUserid(String unauthenticatedUser);

    /**
     * Get the unauthenticated userid
     * 
     * @return the unauthenticated userid
     */
    public String getUnauthenticatedUserid();

    /**
     * Determines if the subject is valid given the set of credentials.
     * <p>
     * A subject is considered valid if all CredentialProvider agree that
     * the Subject is valid. If any provider considers the subject invalid,
     * then the entire subject is considered invalid.
     * 
     * @param subject
     * @return {@code true} if the all CredentialProvider agree the Subject
     *         is valid; {@code false} otherwise.
     */
    boolean isSubjectValid(Subject subject);

    /**
     * Set the basic auth credential into the given Subject.
     * The CredentialsService delegates to the
     * registered BasicAuthCredentialProvider service.
     * 
     * @param realm
     * @param username
     * @param password
     * @throws CredentialException
     */
    void setBasicAuthCredential(Subject subject, String realm, String username, String password) throws CredentialException;
}
