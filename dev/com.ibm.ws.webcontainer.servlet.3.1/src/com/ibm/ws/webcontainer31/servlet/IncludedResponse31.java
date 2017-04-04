// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2014
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//  CHANGE HISTORY
//Defect        Date        Modified By         Description
//--------------------------------------------------------------------------------------
//557339        10/15/08    mmolden             FP7001FVT: Server timeout FFDC after 5 mins, reply intermittent
package com.ibm.ws.webcontainer31.servlet;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.webcontainer.osgi.osgi.WebContainerConstants;

/**
 * This class provides restriction functionality for included responses.
 */
public class IncludedResponse31 extends com.ibm.ws.webcontainer.servlet.IncludedResponse {
    private static final TraceNLS nls = TraceNLS.getTraceNLS(IncludedResponse31.class, "com.ibm.ws.webcontainer.resources.Messages");
    private static final TraceComponent tc = Tr.register(IncludedResponse31.class, WebContainerConstants.TR_GROUP);


    /* (non-Javadoc)
     * @see javax.servlet.ServletResponse#setContentLengthLong(long)
     */
    @Override
    public void setContentLengthLong(long arg0) {
        if (tc.isDebugEnabled()) {
            Tr.debug(tc, "setContentLengthLong " + nls.getString("Illegal.from.included.servlet", "Illegal from included servlet"));
        } 
    }
}
