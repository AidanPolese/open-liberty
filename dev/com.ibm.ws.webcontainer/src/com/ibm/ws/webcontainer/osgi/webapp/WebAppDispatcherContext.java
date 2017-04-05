/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2004, 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
/*
 * Created on Jan 1, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.ibm.ws.webcontainer.osgi.webapp;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import com.ibm.wsspi.webcontainer.servlet.IExtendedRequest;

/**
 * @author asisin
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WebAppDispatcherContext extends com.ibm.ws.webcontainer.webapp.WebAppDispatcherContext
{
  private Principal principal;

  public WebAppDispatcherContext()
  {
    this._webapp = null;
  }

  public WebAppDispatcherContext(WebApp webapp)
  {
    this._webapp = webapp;
  }

  public WebAppDispatcherContext(IExtendedRequest req)
  {
    this._request = req;
    this.initForNextDispatch(req);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ibm.ws.webcontainer.webapp.WebAppDispatcherContext#getUserPrincipal()
   */
  public Principal getUserPrincipal()
  {
      //System.err.println("WebAppDispatch getUserPrincipal");
      // TODO: do we need to add any logic for getUserPrincipal in this class?
      // TODO: confirm w/ Bobby?
      return principal;
  }

  public void setUserPrincipal(Principal principal)
  {
    this.principal = principal;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ibm.ws.webcontainer.webapp.WebAppDispatcherContext#isUserInRole(java
   * .lang.String, javax.servlet.http.HttpServletRequest)
   */
  public boolean isUserInRole(String role, HttpServletRequest req)
  {
    // TODO Auto-generated method stub
    return false;
  }

}
