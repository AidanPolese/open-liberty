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
package com.ibm.ws.webcontainer.osgi.filter;

import com.ibm.ws.webcontainer.osgi.webapp.WebApp;
import com.ibm.ws.webcontainer.webapp.WebAppConfiguration;

public class WebAppFilterManagerImpl extends com.ibm.ws.webcontainer.filter.WebAppFilterManager
{
  public WebAppFilterManagerImpl(WebAppConfiguration config, WebApp webApp)
  {
    super(config, webApp);
  }

  
}
