/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2011
*
* The source code for this program is not published or otherwise divested 
* of its trade secrets, irrespective of what has been deposited with the 
* U.S. Copyright Office.
*/
package com.ibm.ws.webcontainer.internal.security;

import com.ibm.ws.security.core.SecurityContext;
import com.ibm.ws.webcontainer.osgi.collaborator.CollaboratorHelperImpl;

public class WebContainerSecurityContext extends SecurityContext {
    
    // Determined by the presence of a registered ('real') security collaborator for the currently active web app
    public static boolean isSecurityEnabled() {
        return CollaboratorHelperImpl.getCurrentSecurityEnabled();
    }
}
