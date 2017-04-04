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

import com.ibm.ws.session.HttpSessionFacade;
import com.ibm.ws.webcontainer.session.IHttpSession;

public class HttpSessionFacadeImpl extends HttpSessionFacade implements IHttpSession
{

  public HttpSessionFacadeImpl(HttpSessionImpl data)
  {
    super(data);
  }

  // Webcontainer's IHttpSession Interface methods cmd 196151
  public Object getSecurityInfo()
  {
    return ((HttpSessionImpl) _session).getSecurityInfo();
  }

  public void putSecurityInfo(Object pValue)
  {
    ((HttpSessionImpl) _session).putSecurityInfo(pValue);
  }
}
