/*
 * IBM Confidential OCO Source Material
 * 5639-D57, 5630-A36, 5630-A37, 5724-D18 (C) COPYRIGHT International Business Machines Corp. 2003
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.ibm.ws.webcontainer.security.internal;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.wsspi.webcontainer.servlet.IExtendedResponse;

public class TAIChallengeReply extends WebReply {
    private static final TraceComponent tc = Tr.register(TAIChallengeReply.class);

    public TAIChallengeReply(int code) {
        // the response code returned from NegotiateTrustAssociationInterceptor.negotiateAndValidateEstablishedTrust()
        super(code, null);
    }

    /**
     * @see com.ibm.ws.security.web.WebReply#writeResponse(HttpServletResponse)
     */
    @Override
    public void writeResponse(HttpServletResponse rsp) throws IOException {
        if (rsp.isCommitted())
            return;

        if (rsp instanceof IExtendedResponse) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "IExtendedResponse type - status code is " + ((IExtendedResponse) rsp).getStatusCode());
            }
            if (((IExtendedResponse) rsp).getStatusCode() == HttpServletResponse.SC_OK) {
                rsp.setStatus(responseCode);
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "Response code set is " + responseCode);
                }
            }
        } else {
            rsp.setStatus(responseCode);
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Response code set is " + responseCode);
            }
        }
    }
}
