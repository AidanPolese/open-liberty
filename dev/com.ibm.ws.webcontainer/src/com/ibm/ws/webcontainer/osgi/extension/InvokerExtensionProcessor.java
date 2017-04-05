//IBM Confidential OCO Source Material
//5639-D57 (C) COPYRIGHT International Business Machines Corp. 1997-2003
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.

//Code added as part of LIDB 2283-4
package com.ibm.ws.webcontainer.osgi.extension;

import java.util.HashMap;

import com.ibm.ws.webcontainer.osgi.servlet.ServletWrapper;
import com.ibm.ws.webcontainer.webapp.WebApp;
import com.ibm.wsspi.webcontainer.servlet.IServletConfig;
import com.ibm.wsspi.webcontainer.servlet.IServletWrapper;

/**
 * @author asisin
 * 
 */
public class InvokerExtensionProcessor extends com.ibm.ws.webcontainer.extension.InvokerExtensionProcessor
{

  private static String showCfg = "com.ibm.websphere.examples.ServletEngineConfigDumper";

  /**
   * @param webApp
   */
  @SuppressWarnings("unchecked")
  public InvokerExtensionProcessor(WebApp webApp, HashMap params)
  {
    super(webApp, params);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ibm.ws.webcontainer.extension.WebExtensionProcessor#createServletWrapper
   * (com.ibm.wsspi.webcontainer.servlet.IServletConfig)
   */
  public IServletWrapper createServletWrapper(IServletConfig config) throws Exception
  {
    ServletWrapper wrapper = new com.ibm.ws.webcontainer.osgi.servlet.ServletWrapper(extensionContext);
    try
    {
      wrapper.initialize(config);
    }
    catch (Throwable e)
    {
      // Might be more serious....so first log
      e.printStackTrace(System.out);
    }

    return wrapper;
  }

}
