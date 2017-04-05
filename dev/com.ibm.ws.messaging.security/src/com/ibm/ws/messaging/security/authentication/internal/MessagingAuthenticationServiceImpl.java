/*
 * IBM Confidential OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 * 
 * Change activity:
 * ---------------  --------  --------  ------------------------------------------
 * Reason           Date      Origin    Description
 * 114580          06/11/13   balgirid Fixing message threshold not shown in messages.log
 * ---------------  --------  --------  ------------------------------------------
 */

package com.ibm.ws.messaging.security.authentication.internal;

import java.security.AccessController;
import java.security.cert.Certificate;

import javax.security.auth.Subject;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.messaging.security.MSTraceConstants;
import com.ibm.ws.messaging.security.MessagingSecurityConstants;
import com.ibm.ws.messaging.security.MessagingSecurityException;
import com.ibm.ws.messaging.security.authentication.MessagingAuthenticationException;
import com.ibm.ws.messaging.security.authentication.MessagingAuthenticationService;
import com.ibm.ws.messaging.security.authentication.actions.MessagingLoginAction;
import com.ibm.ws.messaging.security.internal.MessagingSecurityServiceImpl;
import com.ibm.ws.messaging.security.utility.MessagingSecurityUtility;
import com.ibm.ws.security.authentication.AuthenticationData;
import com.ibm.ws.security.authentication.WSAuthenticationData;
import com.ibm.ws.sib.utils.ras.SibTr;

/*
 * Assumption:
 * 1. These methods are called only when the security is enabled for Messaging
 * and hence we are not doing any explicit checks inside this code
 * 2. Security Auditing feature does not exist for Liberty profile
 * 3. Check if the user passed has any of the access defined in Messaging, then only proceed for authentication
 * Reference: Configuration Differences between the Full profile and the Liberty profile: Security
 */
/**
 * Implementation class for Messaging Authentication Service
 * 
 * @author Sharath Chandra B
 */
public class MessagingAuthenticationServiceImpl implements
                MessagingAuthenticationService, MessagingSecurityConstants {

    // Trace component for the MessagingAuthenticationService Implementation class
    private static TraceComponent tc = SibTr.register(MessagingAuthenticationServiceImpl.class,
                                                      MSTraceConstants.MESSAGING_SECURITY_TRACE_GROUP,
                                                      MSTraceConstants.MESSAGING_SECURITY_RESOURCE_BUNDLE);

    // Absolute class name along with the package name, used for tracing
    private static final String CLASS_NAME = "com.ibm.ws.messaging.security.authentication.internal.MessagingAuthenticationServiceImpl";

    private MessagingSecurityServiceImpl _messagingSecurityService = null;

    private final AuthenticationData authenticationDataForSubject = new WSAuthenticationData();

    /**
     * Constructor
     * 
     * @param messagingSecurityService
     */
    public MessagingAuthenticationServiceImpl(
                                              MessagingSecurityServiceImpl messagingSecurityService) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            SibTr.entry(tc, CLASS_NAME + "constructor", messagingSecurityService);
        }
        this._messagingSecurityService = messagingSecurityService;
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            SibTr.exit(tc, CLASS_NAME + "constructor");
        }
    }

    @Override
    public Subject login(Subject subj) throws MessagingAuthenticationException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            SibTr.entry(tc, CLASS_NAME + "login", subj);
        }
        Subject result = null;
        result = AccessController.doPrivileged(new MessagingLoginAction(
                        authenticationDataForSubject, MessagingSecurityConstants.SUBJECT, _messagingSecurityService.getSecurityService(), subj));
        if (result == null) {
            //114580
            String userName = null;
            try {
                userName = _messagingSecurityService.getUniqueUserName(subj);
            } catch (MessagingSecurityException e) {
                //No FFDC Code Needed
            }
            throwAuthenticationException(userName);//114580
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            SibTr.exit(tc, CLASS_NAME + "login", result);
        }
        return result;
    }

    @Override
    public Subject login(String userName, String password) throws MessagingAuthenticationException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            SibTr.entry(tc, CLASS_NAME + "login", new Object[] { userName, "Password Not Traced" });
        }
        Subject result = null;
        AuthenticationData authData = MessagingSecurityUtility
                        .createAuthenticationData(userName, password);
        result = AccessController.doPrivileged(new MessagingLoginAction(
                        authData, MessagingSecurityConstants.USERID, _messagingSecurityService.getSecurityService()));
        if (result == null) {
            throwAuthenticationException(userName);//114580
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            SibTr.exit(tc, CLASS_NAME + "login", result);
        }
        return result;
    }

    @Override
    public Subject login(byte[] securityToken,
                         String securityTokenType) throws MessagingAuthenticationException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            SibTr.entry(tc, CLASS_NAME + "login", new Object[] { securityToken, securityTokenType });
        }
        Subject result = null;
        boolean doLogin = SUPPORTED_TOKEN_TYPE.equals(securityTokenType);
        if (doLogin) {
            AuthenticationData authData = MessagingSecurityUtility
                            .createAuthenticationData(securityToken);
            result = AccessController.doPrivileged(new MessagingLoginAction(
                            authData, MessagingSecurityConstants.LTPA, _messagingSecurityService.getSecurityService()));
            if (result == null) {
                //114580
                String userName = null;
                try {
                    userName = _messagingSecurityService.getUniqueUserName(result);
                } catch (MessagingSecurityException e) {
                    //No FFDC Code Needed
                }
                throwAuthenticationException(userName);//114580
            }
        } else {
            SibTr.error(tc, "SECURITY_TOKEN_TYPE_NOT_SUPPORTED_MSE1002", securityTokenType);
            result = null;
            throw new MessagingAuthenticationException(Tr.formatMessage(tc, "SECURITY_TOKEN_TYPE_NOT_SUPPORTED_MSE1002"));
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            SibTr.exit(tc, CLASS_NAME + "login", result);
        }
        return result;
    }

    @Override
    public Subject login(String userName) throws MessagingAuthenticationException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            SibTr.entry(tc, CLASS_NAME + "login", userName);
        }
        Subject result = null;
        AuthenticationData authData = MessagingSecurityUtility
                        .createAuthenticationData(userName, _messagingSecurityService.getUserRegistry());
        result = AccessController
                        .doPrivileged(new MessagingLoginAction(authData,
                                        MessagingSecurityConstants.IDASSERTION, _messagingSecurityService.getSecurityService()));
        if (result == null) {
            //114580
            throwAuthenticationException(userName);//114580
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            SibTr.exit(tc, CLASS_NAME + "login", result);
        }
        return result;
    }

    @Override
    public Subject login(Certificate[] certificates) throws MessagingAuthenticationException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            SibTr.entry(tc, CLASS_NAME + "login", certificates);
        }
        Subject result = null;
        if (certificates == null) {
            result = null;
        } else {
            AuthenticationData authData = MessagingSecurityUtility
                            .createAuthenticationData(certificates, _messagingSecurityService.getUserRegistry());
            result = AccessController.doPrivileged(new MessagingLoginAction(
                            authData, MessagingSecurityConstants.CLIENTSSL, _messagingSecurityService.getSecurityService()));
            if (result == null) {
                //114580
                String userName = null;
                try {
                    userName = _messagingSecurityService.getUniqueUserName(result);
                } catch (MessagingSecurityException e) {
                    //No FFDC Code Needed
                }
                throwAuthenticationException(userName);//114580
            }
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            SibTr.exit(tc, CLASS_NAME + "login", result);
        }
        return result;
    }

    @Override
    public void logout(Subject subj) {
        /*
         * What should we do when we logout? In tWAS it is just executing some
         * Auditing features which are not supported in Liberty
         */
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            SibTr.entry(tc, CLASS_NAME + "logout", subj);
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            SibTr.exit(tc, CLASS_NAME + "logout");
        }
    }

    private void throwAuthenticationException(String userName) throws MessagingAuthenticationException {
        throw new MessagingAuthenticationException(Tr.formatMessage(tc, "USER_NOT_AUTHENTICATED_MSE1009", userName));//114580
    }

}
