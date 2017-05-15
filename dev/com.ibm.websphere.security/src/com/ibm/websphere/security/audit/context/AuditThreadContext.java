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

/**
 * <p>This interface encapsulates the artifacts pertaining to an auditable transaction.</p>
 *
 * <p>Implementations of this interface are not guaranteed to be thread safe</p>
 *
 * @ibm-spi
 */
public class AuditThreadContext {

    private Object auditReq;
    private Object auditWebReq;
    private String auditRealm;
    private ArrayList<String> delegatedUsers;

    public void setHttpServletRequest(Object req) {
        auditReq = req;
    }

    public Object getHttpServletRequest() {
        return auditReq;
    }

    public void setWebRequest(Object webreq) {
        auditWebReq = webreq;
    }

    public Object getWebRequest() {
        return auditWebReq;
    }

    public void setRealm(String realm) {
        auditRealm = realm;
    }

    public String getRealm() {
        return auditRealm;
    }

    public void setDelegatedUsers(ArrayList<String> delUsers) {
        if (delUsers != null) {
            delegatedUsers = new ArrayList<String>(delUsers);
        }
    }

    public ArrayList<String> getDelegatedUsers() {
        return delegatedUsers;
    }
}
