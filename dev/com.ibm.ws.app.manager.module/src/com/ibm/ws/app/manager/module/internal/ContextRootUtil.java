/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.app.manager.module.internal;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.javaee.dd.webext.WebExt;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;

/**
 *
 */
public class ContextRootUtil {
    private static final TraceComponent tc = Tr.register(ContextRootUtil.class);

    public static String getContextRoot(String root) {
        if (root != null && !root.isEmpty()) {
            if (!root.startsWith("/")) {
                root = "/" + root;
            }
            return root;
        }
        return null;
    }

    public static String getContextRoot(Container webContainer) {
        if (webContainer != null) {
            try {
                WebExt webExt = webContainer.adapt(WebExt.class);
                if (webExt != null) {
                    String contextRoot = webExt.getContextRoot();
                    return getContextRoot(contextRoot);
                }
            } catch (UnableToAdaptException e) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                	Tr.debug(tc, "getContextRoot: Unable to parse the WebExt file", e);
                }
            }
        }
        return null;
    }
}
