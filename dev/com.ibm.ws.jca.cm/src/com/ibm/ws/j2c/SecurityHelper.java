/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2003, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.j2c;

import java.io.Serializable;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnectionFactory;
import javax.security.auth.Subject;

import com.ibm.ejs.j2c.CMConfigData;

/**
 * Interface name : SecurityHelper
 * <p>
 * Scope : EJB server, WEB server
 * <p>
 * Object model : 1 concrete SecurityHelper per configured ManagedConnectionFactory
 * <p>
 * This interface is used by the PoolManager, FreePool, and
 * the ConnectionManager to allow the opportunity for special
 * J2C security processing to be performed with respect to the Subject
 * without awareness of the underlying platform implementation.
 * 
 */

public interface SecurityHelper extends Serializable {

    /**
     * The finalizeSubject method is used to set what the final Subject
     * will be for processing.
     * 
     * The primary intent of this method is to allow the Subject to be
     * defaulted when the passed Subject contains no credentials.
     * 
     * @param subject
     * @param reqInfo
     * @param cmConfigData - for TISH to determine whether to call getLocalOSInvocationSubject
     * @return Subject
     * @exception ResourceException
     */
    Subject finalizeSubject(Subject subject, ConnectionRequestInfo reqInfo, CMConfigData cmConfigData) throws ResourceException;

    /**
     * The beforeGettingConnection() method is used to allow
     * special security processing to be performed prior to calling
     * a resource adapter to get a connection.
     * 
     * @param subject
     * @param reqInfo
     * @return Object if non-null, the user identity defined by the
     *         Subject was pushed to thread. The Object in
     *         this case needs to be passed as input to
     *         afterGettingConnection method processing and
     *         will be used to restore the thread identity
     *         back to what it was.
     * @exception ResourceException
     */
    Object beforeGettingConnection(Subject subject, ConnectionRequestInfo reqInfo) throws ResourceException;

    /**
     * The afterGettingConnection() method is used to allow
     * special security processing to be performed after calling
     * a resource adapter to get a connection.
     * 
     * @param subject
     * @param reqInfo
     * @param credentialToken
     * @return void
     * @exception ResourceException
     */
    void afterGettingConnection(Subject subject, ConnectionRequestInfo reqInfo, Object credentialToken) throws ResourceException;

    /**
     * 
     * @param subject
     * @param reqInfo
     * @param mcf
     * @return void
     * @throws ResourceException
     */
    public void finalizeCriForRRA(Subject subject, ConnectionRequestInfo reqInfo, ManagedConnectionFactory mcf) throws ResourceException;

}
