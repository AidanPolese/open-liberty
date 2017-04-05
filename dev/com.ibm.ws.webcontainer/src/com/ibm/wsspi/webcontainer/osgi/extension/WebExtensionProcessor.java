/*
 * IBM Confidential
 * 
 * OCO Source Materials
 * 
 * Copyright IBM Corp. 2004, 2010
 * 
 * The source code for this program is not published or other- wise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */
/*
 * Created on Jan 1, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.ibm.wsspi.webcontainer.osgi.extension;

import com.ibm.ws.webcontainer.servlet.ServletWrapper;
import com.ibm.wsspi.webcontainer.servlet.IServletConfig;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;
import com.ibm.wsspi.webcontainer.servlet.IServletWrapper;

/**
 * LIBERTY: This class is needed to create com.ibm.ws.webcontainer.osgi.servlet.ServletWrapper
 * instead of com.ibm.ws.webcontainer.servlet.ServletWrapperImpl
 */
public abstract class WebExtensionProcessor extends com.ibm.ws.webcontainer.extension.WebExtensionProcessor
{
  public WebExtensionProcessor(IServletContext webApp)
  {
    super(webApp);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ibm.ws.webcontainer.extension.WebExtensionProcessor#createServletWrapper
   * (com.ibm.ws.webcontainer.servlet.ServletConfig)
   */
  public IServletWrapper createServletWrapper(IServletConfig config) throws Exception
  {
    ServletWrapper wrapper = new com.ibm.ws.webcontainer.osgi.servlet.ServletWrapper(extensionContext);

    if (config == null)
      return wrapper;
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
