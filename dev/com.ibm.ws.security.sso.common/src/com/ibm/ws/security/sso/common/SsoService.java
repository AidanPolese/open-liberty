/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.sso.common;

import java.util.Map;

public interface SsoService {
    public static final String KEY_TYPE = "type";
    public static final String TYPE_WSS_SAML = "wssSaml";
    public static final String TYPE_WSSECURITY = "wssecurity";
    public static final String TYPE_SAML20 = "saml20";

    // Defined in wssecurity
    public static final String WSSEC_SAML_ASSERTION = "wssecurity-samlassertion";
    public static final String SAML_SSO_TOKEN = "samlssotoken";

    public Map<String, Object> handleRequest(String requestName,
                                             Map<String, Object> requestContext) throws Exception;

}
