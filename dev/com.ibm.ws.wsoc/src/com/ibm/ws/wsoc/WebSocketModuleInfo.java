/** IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.wsoc;

import java.io.Serializable;

/**
 *
 */
public class WebSocketModuleInfo implements Serializable {

    private static final long serialVersionUID = -8116043459266953308L;
    private String contextRoot;

    public WebSocketModuleInfo() {

    }

    public String getContextRoot() {
        return contextRoot;
    }

    public void setContextRoot(String contextRoot) {
        this.contextRoot = contextRoot;
    }

}
