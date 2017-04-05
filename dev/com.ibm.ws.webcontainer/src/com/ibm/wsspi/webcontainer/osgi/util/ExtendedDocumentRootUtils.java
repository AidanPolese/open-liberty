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
package com.ibm.wsspi.webcontainer.osgi.util;

import javax.servlet.ServletContext;

public class ExtendedDocumentRootUtils extends com.ibm.ws.webcontainer.util.ExtendedDocumentRootUtils
{
  public ExtendedDocumentRootUtils(ServletContext ctxt, String extendedDocumentRoot)
  {
    super(ctxt, extendedDocumentRoot);
  }

  public ExtendedDocumentRootUtils(String baseDir, String extendedDocumentRoot)
  {
    super(baseDir, extendedDocumentRoot);
  }
}
