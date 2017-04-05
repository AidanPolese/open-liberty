/*
 * IBM Confidential
 * 
 * OCO Source Materials
 * 
 * Copyright IBM Corp. 2010
 * 
 * The source code for this program is not published or other- wise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */
package com.ibm.ws.webcontainer.session.impl;

import javax.servlet.ServletContext;

import com.ibm.ws.session.SessionContext;
import com.ibm.ws.session.SessionData;
import com.ibm.ws.webcontainer.facade.IFacade;
import com.ibm.ws.webcontainer.session.IHttpSession;
import com.ibm.wsspi.session.ISession;

public class HttpSessionImpl extends SessionData implements IHttpSession, IFacade
{

  public HttpSessionImpl(ISession session, SessionContext sessCtx, ServletContext servCtx)
  {
    super(session, sessCtx, servCtx);
  }

  /*
   * For security to store away special hidden value in the session
   * 
   * @see
   * com.ibm.ws.webcontainer.session.IHttpSession#putSecurityInfo(java.lang.
   * Object)
   */
  public void putSecurityInfo(Object value)
  {
    putSessionValue(SECURITY_PROP_NAME, value, true);
    _hasSecurityInfo = true;
  }

  /*
   * For security to retrieve special hidden value in the session
   * 
   * @see com.ibm.ws.webcontainer.session.IHttpSession#getSecurityInfo()
   */
  public Object getSecurityInfo()
  {
    return getSessionValue(SECURITY_PROP_NAME, true);
  }

  /*
   * To get the facade given out to the application
   * 
   * @see com.ibm.ws.webcontainer.facade.IFacade#getFacade()
   */
  public Object getFacade()
  {
    return (Object) _httpSessionFacade;
  }

}
