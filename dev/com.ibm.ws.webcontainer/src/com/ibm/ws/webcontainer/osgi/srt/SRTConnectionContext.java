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
package com.ibm.ws.webcontainer.osgi.srt;

import com.ibm.ws.webcontainer.osgi.webapp.WebAppDispatcherContext;

/**
 * @author asisin
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SRTConnectionContext extends com.ibm.ws.webcontainer.srt.SRTConnectionContext
{

  /**
   * Used for pooling the SRTConnectionContext objects.
   */
  public SRTConnectionContext nextContext;

  /**
	 * 
	 */
  public SRTConnectionContext()
  {
    super();
    this._dispatchContext = new WebAppDispatcherContext(_request);
    _request.setWebAppDispatcherContext(_dispatchContext);
  }

}
