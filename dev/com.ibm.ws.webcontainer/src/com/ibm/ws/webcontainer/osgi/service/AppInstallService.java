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

import com.ibm.ws.webcontainer.osgi.webapp.AppInstaller;

/**
 * @author rbackhouse
 */
public interface AppInstallService
{
  public boolean installAppForUri(String uri, AppInstaller installer);
}
