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
 * Created on May 27, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.ibm.ws.webcontainer.osgi.extension;

import java.io.File;
import java.util.HashMap;

import com.ibm.ws.webcontainer.osgi.servlet.EntryServletWrapper;
import com.ibm.ws.webcontainer.servlet.StaticFileServletWrapper;
import com.ibm.ws.webcontainer.servlet.ZipFileServletWrapper;
import com.ibm.ws.webcontainer.util.ZipFileResource;
import com.ibm.wsspi.adaptable.module.Entry;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;

/**
 * @author asisin
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DefaultExtensionProcessor extends com.ibm.ws.webcontainer.extension.DefaultExtensionProcessor
{

  @SuppressWarnings("unchecked")
  public DefaultExtensionProcessor(IServletContext webapp, HashMap params)
  {
    super(webapp, params);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.ws.webcontainer.extension.DefaultExtensionProcessor#
   * getStaticFileWrapper(com.ibm.wsspi.webcontainer.servlet.IServletContext,
   * com.ibm.ws.webcontainer.extension.DefaultExtensionProcessor, java.io.File)
   */
  protected com.ibm.ws.webcontainer.servlet.FileServletWrapper getStaticFileWrapper(IServletContext _webapp, com.ibm.ws.webcontainer.extension.DefaultExtensionProcessor processor,
                                                                                    File file)
  {
    // TODO Auto-generated method stub
    return new StaticFileServletWrapper(_webapp, processor, file);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ibm.ws.webcontainer.extension.DefaultExtensionProcessor#getZipFileWrapper
   * (com.ibm.wsspi.webcontainer.servlet.IServletContext,
   * com.ibm.ws.webcontainer.extension.DefaultExtensionProcessor,
   * java.util.zip.ZipFile, java.util.zip.ZipEntry)
   */
  protected com.ibm.ws.webcontainer.servlet.ZipFileServletWrapper getZipFileWrapper(IServletContext _webapp, com.ibm.ws.webcontainer.extension.DefaultExtensionProcessor processor,
                                                                                    ZipFileResource zipFileResources)
  {
    // TODO Auto-generated method stub
    return new ZipFileServletWrapper(_webapp, processor, zipFileResources);
  }
  
  protected com.ibm.ws.webcontainer.servlet.FileServletWrapper getEntryWrapper(IServletContext _webapp, com.ibm.ws.webcontainer.extension.DefaultExtensionProcessor processor,
                                                                                    Entry entry)
  {
    // TODO Auto-generated method stub
    return new EntryServletWrapper(_webapp, processor, entry);
  }

}
