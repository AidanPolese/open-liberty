/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1997,2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.j2c;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnectionFactory;
import javax.security.auth.Subject;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.j2c.SecurityHelper;
import com.ibm.ws.jca.adapter.WSConnectionRequestInfo;

/**
 * 
 * <P> The DefaultSecurityHelper is used in the case where the given
 * platform doesn't need to any special security processing with
 * respect to the current Subject. The DefaultSecurityHelper
 * implements each method as a NO-OP.
 * 
 * <P>Scope : EJB server and Web server
 * 
 * <P>Object model : 1 instance per ManagedConnectionFactory
 * 
 */

public class DefaultSecurityHelper implements SecurityHelper {

    private static final long serialVersionUID = -4731664268509657999L; 

    private static final TraceComponent tc =
                    Tr.register(DefaultSecurityHelper.class,
                                J2CConstants.traceSpec,
                                J2CConstants.messageFile); 

    /**
     * The finalizeSubject method is used to set what the final Subject
     * will be for processing.
     * 
     * The primary intent of this method is to allow the Subject to be
     * defaulted.
     * 
     * @param Subject subject
     * @param ConnectionRequestInfo reqInfo
     * @param cmConfigData
     * @return Subject
     * @exception ResourceException
     */
    @Override
    public Subject finalizeSubject(Subject subject,
                                   ConnectionRequestInfo reqInfo,
                                   CMConfigData cmConfigData) 
    throws ResourceException {

        final boolean isTracingEnabled = TraceComponent.isAnyTracingEnabled(); 

        if (isTracingEnabled && tc.isEntryEnabled()) { 
            Tr.entry(this, tc, "finalizeSubject");
        }

        if (isTracingEnabled && tc.isEntryEnabled()) { 
            Tr.exit(this, tc, "finalizeSubject");
        }

        return subject; // Pass back unchanged Subject

    }

    /**
     * The beforeCreateManageConnection() method is used to allow
     * special security processing to be performed prior to calling
     * a resource adapter to get a connection.
     * 
     * @param Subject subject
     * @param ConnectionRequestInfo reqInfo
     * @return Object if non-null, the user identity defined by the
     *         Subject was pushed to thread. The Object in
     *         this case needs to be passed as input to
     *         afterGettingConnection method processing and
     *         will be used to restore the thread identity
     *         back to what it was.
     * @exception ResourceException
     */
    @Override
    public Object beforeGettingConnection(Subject subject,
                                          ConnectionRequestInfo reqInfo)
                    throws ResourceException {

        final boolean isTracingEnabled = TraceComponent.isAnyTracingEnabled(); 

        if (isTracingEnabled && tc.isEntryEnabled()) { 
            Tr.entry(this, tc, "beforeGettingConnection");
        }

        Object retObject = null;

        if (isTracingEnabled && tc.isEntryEnabled()) { 
            Tr.exit(this, tc, "beforeGettingConnection");
        }

        return retObject;

    }

    /**
     * The afterGettingConnection() method is used to allow
     * special security processing to be performed after calling
     * a resource adapter to get a connection.
     * 
     * @param Subject subject
     * @param ConnectionRequestInfo reqInfo
     * @param Object credentialToken
     * @return void
     * @exception ResourceException
     */
    @Override
    public void afterGettingConnection(Subject subject,
                                       ConnectionRequestInfo reqInfo,
                                       Object credentialToken)
                    throws ResourceException {

        final boolean isTracingEnabled = TraceComponent.isAnyTracingEnabled(); 

        if (isTracingEnabled && tc.isEntryEnabled()) { 
            Tr.entry(this, tc, "afterGettingConnection");
        }

        // Since the beforeGettingConnection never pushes the Subject
        // to the thread, ignore the input credToken Object and
        // simply return without doing anything.

        if (isTracingEnabled && tc.isEntryEnabled()) { 
            Tr.exit(this, tc, "afterGettingConnection");
        }

    }

    /**
     * Call the RRA's finalizeCriForRRA method to set data in the CRI
     * for use in matching connections.
     * Note that the ThreadIdentitySecurityHelper does not make this
     * call, since ThreadIdentity take precedence over trusted context.
     * 
     * @param subject
     * @param reqInfo
     * @param mcf
     * @return
     * @throws ResourceException
     */
    @Override
    public void finalizeCriForRRA(Subject subject, ConnectionRequestInfo reqInfo,
                                  ManagedConnectionFactory mcf) throws ResourceException {

        final boolean isTracingEnabled = TraceComponent.isAnyTracingEnabled(); 

        if (isTracingEnabled && tc.isEntryEnabled()) { 
            Tr.entry(this, tc, "finalizeCriForRRA");
        }

        if (subject != null) { // only call if subject is not null     
            // blindly cast to WSManagedConnectionFactory since we know that its RRA
            ((WSConnectionRequestInfo) reqInfo).populateWithIdentity(subject);
        }

        if (isTracingEnabled && tc.isEntryEnabled()) { 
            Tr.exit(this, tc, "finalizeCriForRRA");
        }

    }
}