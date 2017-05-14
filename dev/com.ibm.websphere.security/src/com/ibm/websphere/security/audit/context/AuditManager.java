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
package com.ibm.websphere.security.audit.context;

import java.util.ArrayList;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 *
 */
public class AuditManager {

    private static ThreadLocal<AuditThreadContext> threadLocal = new AuditThreadLocal();

    /**
     * Sets the HttpServletRequest on the thread
     */
    public void setHttpServletRequest(Object req) {
        AuditThreadContext auditThreadContext = getAuditThreadContext();
        auditThreadContext.setHttpServletRequest(req);

    }

    /**
     * Gets the HttpServletRequest from the thread
     */
    public Object getHttpServletRequest() {
        AuditThreadContext auditThreadContext = getAuditThreadContext();
        return auditThreadContext.getHttpServletRequest();
    }

    /**
     * Sets the WebRequest on the thread
     */
    public void setWebRequest(Object webreq) {
        AuditThreadContext auditThreadContext = getAuditThreadContext();
        auditThreadContext.setWebRequest(webreq);

    }

    /**
     * Gets the WebRequest from the thread
     */
    public Object getWebRequest() {
        AuditThreadContext auditThreadContext = getAuditThreadContext();
        return auditThreadContext.getWebRequest();
    }

    /**
     * Sets the realm on the thread
     */
    public void setRealm(String realm) {
        AuditThreadContext auditThreadContext = getAuditThreadContext();
        auditThreadContext.setRealm(realm);
    }

    /**
     * Gets the realm on the thread
     */
    public String getRealm() {
        AuditThreadContext auditThreadContext = getAuditThreadContext();
        return auditThreadContext.getRealm();
    }

    /**
     * Sets the list of users from the initial caller through the last caller in a runAs delegation call
     */
    public void setDelegatedUsers(ArrayList<String> delegatedUsers) {
        AuditThreadContext auditThreadContext = getAuditThreadContext();
        auditThreadContext.setDelegatedUsers(delegatedUsers);
    }

    /**
     * Gets the list of users from the initial through the last in a runAs delegation call
     */
    public ArrayList<String> getDelegatedUsers() {
        AuditThreadContext auditThreadContext = getAuditThreadContext();
        return auditThreadContext.getDelegatedUsers();
    }

    /**
     * Gets the audit thread context that is unique per thread.
     * If/when a common thread storage framework is supplied, then this method
     * implementation may need to be updated to take it into consideration.
     *
     * @return the subject thread context.
     */
    @Trivial
    protected AuditThreadContext getAuditThreadContext() {
        ThreadLocal<AuditThreadContext> currentThreadLocal = getThreadLocal();
        AuditThreadContext auditThreadContext = currentThreadLocal.get();
        if (auditThreadContext == null) {
            auditThreadContext = new AuditThreadContext();
            currentThreadLocal.set(auditThreadContext);
        }
        return auditThreadContext;
    }

    /**
     * Gets the thread local object.
     * If/when a common thread storage framework is supplied, then this method
     * implementation may need to be updated to take it into consideration.
     *
     * @return the thread local object.
     */
    @Trivial
    private ThreadLocal<AuditThreadContext> getThreadLocal() {
        return threadLocal;
    }

    private static final class AuditThreadLocal extends ThreadLocal<AuditThreadContext> {
        @Override
        protected AuditThreadContext initialValue() {
            return new AuditThreadContext();
        }
    }

}
