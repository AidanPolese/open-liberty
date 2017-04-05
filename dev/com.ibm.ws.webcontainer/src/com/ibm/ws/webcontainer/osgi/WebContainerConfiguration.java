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
package com.ibm.ws.webcontainer.osgi;

import java.util.Map;

import com.ibm.wsspi.webcontainer.WCCustomProperties;

/**
 * @author asisin
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WebContainerConfiguration extends com.ibm.ws.webcontainer.WebContainerConfiguration
{
  //private String port = null;
  private Map<String, Object> properties;

  // Configuration property key strings
  private static final String CFG_KEY_ENFORCE_SEC = "enforce.security";
  
  // unused
  // public final static String CFG_KEY_LOGIN_CONFIG = "login.config";
  // public final static String CFG_KEY_TOKEN_TYPE = "token.type";
  // public final static String CFG_KEY_COOKIE_NAME = "cookie.name";

  public WebContainerConfiguration(String port)
  {
    super();
    //this.port = port;
  }

  @SuppressWarnings("unchecked")
  public void setConfiguration(Map<String, Object> properties)
  {
    this.properties = properties;
//    if (this.properties != null && "true".equalsIgnoreCase((String) this.properties.get(CFG_KEY_ENFORCE_SEC)))
//    {
//      SecurityContext.setSecurityEnabled(true);
//    }
    WCCustomProperties.setCustomProperties(properties);
    
  }

  @SuppressWarnings("unchecked")
  public Map<String, Object> getConfiguration()
  {
    return properties;
  }
  
}
