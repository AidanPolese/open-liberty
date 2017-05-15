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
package com.ibm.ws.webcontainer.security.internal;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 *
 */
public class OAuthChallengeReply extends WebReply {
    private static final TraceComponent tc = Tr.register(OAuthChallengeReply.class);
    private final String reason;
    public static final String AUTHENTICATE_HDR = ChallengeReply.AUTHENTICATE_HDR;

    public OAuthChallengeReply(String reason) {
        super(HttpServletResponse.SC_UNAUTHORIZED, reason);
        this.reason = reason; //
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.webcontainer.security.internal.WebReply#writeResponse(javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void writeResponse(HttpServletResponse rsp) throws IOException {
        if (rsp.isCommitted())
            return; // if oidc, oauth... etc had already handle the response

        rsp.setStatus(responseCode);
        // in case, make sure we set the WWW-Authenticate
        String wwwAuthenticate = rsp.getHeader(AUTHENTICATE_HDR);
        if (wwwAuthenticate == null || wwwAuthenticate.isEmpty()) {
            wwwAuthenticate = "Bearer realm=\"oauth\"";;
            rsp.setHeader(AUTHENTICATE_HDR, wwwAuthenticate);
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "WWW-Authenticate:'" + wwwAuthenticate + "' code:" + responseCode + " reason:" + reason);
        }
    }

}
