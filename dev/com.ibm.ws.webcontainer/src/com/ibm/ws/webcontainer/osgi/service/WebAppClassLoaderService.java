/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.ws.webcontainer.osgi.service;

public interface WebAppClassLoaderService
{
  ClassLoader getWebAppClassLoader(String contextRoot);

  void registerWebAppClassLoader(ClassLoader classloader, String contextRoot);
}
