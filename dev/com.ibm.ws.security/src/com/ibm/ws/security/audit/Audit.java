/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.audit;

/**
 * This class provides an audit probe point method and constants.
 */
public class Audit {
    public static enum EventID {
        SECURITY_AUTHN_01,
        SECURITY_AUTHZ_01, // web
        SECURITY_AUTHZ_02, // jacc web
        SECURITY_AUTHZ_03, // jacc ejb
        SECURITY_AUTHZ_04, // ejb
        SECURITY_AUDIT_MGMT_01,
        SECURITY_AUDIT_MGMT_02,
        SECURITY_AUTHN_DELEGATION_01,
        SECURITY_AUTHZ_DELEGATION_01,
        SECURITY_API_AUTHN_01,
        SECURITY_API_AUTHN_TERMINATE_01,
        SECURITY_AUTHN_TERMINATE_01,
        SECURITY_AUTHN_FAILOVER_01,
        SECURITY_MEMBER_MGMT_01,
        SECURITY_JMS_AUTHN_01,
        SECURITY_JMS_AUTHZ_01,
        SECURITY_JMS_AUTHN_TERMINATE_01

    }

    /**
     * Audit probe point. This method should be called to generate a
     * security audit record. It does nothing and returns nothing - the audit
     * record is produced by a com.ibm.wsspi.probeExtension.ProbeExtension
     * implementation which will be called (via bytecode injection) when the
     * audit feature is enabled and this method is invoked.
     *
     * @param eventId -
     *            The unique ID identifying the ProbeExtension method to be
     *            called to generate the audit record. The ID should be defined
     *            in the Audit.EventID enumeration. An ID should be defined
     *            for each unique set of params to be passed to the ProbeExtension.
     * @param params -
     *            The objects needed to produce the audit record.
     */
    public static void audit(EventID eventId, Object... params) {}
}
