//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//
//CHANGE HISTORY
//Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//      SECPYX          01/30/06        mmolden             security changes/collaborator refactoring  

package com.ibm.wsspi.webcontainer.collaborator;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.wsspi.webcontainer.extension.ExtensionProcessor;
import com.ibm.wsspi.webcontainer.security.SecurityViolationException;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;

public interface IWebAppSecurityCollaborator {


	    public Object preInvoke(HttpServletRequest req, HttpServletResponse resp, String servletName, boolean enforceSecurity)
	    throws SecurityViolationException, IOException;
	    
	    public boolean authenticate(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException;
	    
	    public void login(HttpServletRequest req, HttpServletResponse resp,String username, String password)
        throws ServletException;
	    
	    public void logout(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException;
	    
	    public Object preInvoke(String servletName)
	    throws SecurityViolationException, IOException;
	    
	    public Object preInvoke() throws SecurityViolationException;

	    public void postInvoke(Object secObject) throws ServletException;

	    public void handleException(HttpServletRequest req, HttpServletResponse rsp,
	                                Throwable wse) throws ServletException, IOException;

	    public java.security.Principal getUserPrincipal();

	    public boolean isUserInRole(String role, HttpServletRequest req);
	    
	    public ExtensionProcessor getFormLoginExtensionProcessor(IServletContext webapp);
		public ExtensionProcessor getFormLogoutExtensionProcessor(IServletContext webapp);
		
		public List<String> getURIsInSecurityConstraints(String appName, String contextRoot, String vHost, List<String> URIs);
		
		

}
