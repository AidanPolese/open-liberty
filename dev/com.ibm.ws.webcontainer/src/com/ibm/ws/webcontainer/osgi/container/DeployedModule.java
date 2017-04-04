/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2009
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.ws.webcontainer.osgi.container;

import java.util.concurrent.Future;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.webcontainer.VirtualHost;
import com.ibm.ws.webcontainer.osgi.osgi.WebContainerConstants;
import com.ibm.ws.webcontainer.osgi.webapp.WebApp;
import com.ibm.ws.webcontainer.osgi.webapp.WebAppConfiguration;
import com.ibm.ws.webcontainer.webapp.WebGroup;
import com.ibm.ws.webcontainer.webapp.WebGroupConfiguration;
import com.ibm.wsspi.adaptable.module.Container;

/**
 * Class encapsulating a single WAR application image.
 */
public class DeployedModule extends com.ibm.ws.container.DeployedModule
{
  @SuppressWarnings("unused")
  private static final TraceComponent tc = Tr.register(DeployedModule.class, WebContainerConstants.TR_GROUP, WebContainerConstants.NLS_PROPS);
  /**
   * A ClassLoader or LazyClassLoader to use as the thread context class loader
   * while components in this module are running.  This should either be
   * {@link #moduleClassLoader} or delegate to it.
   */
  private ClassLoader loader;
  /** Webcontainer app created for this module */
  private WebApp webApp;
  private WebAppConfiguration webAppConfig;
  private Future<Boolean> contextRootAdded;
  private String properContextRoot;
  private String mappingContextRoot;
  
  public DeployedModule(Container webAppContainer,
                        WebAppConfiguration webAppConfig,
                        ClassLoader loader)
  {
    this.loader = loader;
    this.webAppConfig = webAppConfig;
    this.webApp = webAppConfig.getWebApp();
  }

  /*
   * @see com.ibm.ws.container.DeployedModule#getWebAppConfig()
   */
  public WebAppConfiguration getWebAppConfig()
  {
    return this.webAppConfig;
  }

  /*
   * @see com.ibm.ws.container.DeployedModule#getWebApp()
   */
  public WebApp getWebApp()
  {
    return this.webApp;
  }

  /**
   * The class loader to use for the thread context class loader. This could be
   * the same as the actual module class loader, but this implementation returns
   * the result of passing that class loader to 
   * ClassLoadingService.createThreadContextClassLoader().
   *
   * @see com.ibm.ws.container.DeployedModule#getClassLoader()
   */
  public ClassLoader getClassLoader()
  {
    return loader;
  }

  /*
   * @see com.ibm.ws.container.DeployedModule#getContextRoot()
   */
  public String getContextRoot()
  {
    return this.getWebAppConfig().getContextRoot();
  }

  public void setContextRootAdded(Future<Boolean> contextRootAdded)
  {
    this.contextRootAdded = contextRootAdded;
  }

  public Future<Boolean> getContextRootAdded()
  {
    return this.contextRootAdded;
  }

  public String getProperContextRoot()
  {
    String properContextRoot = this.properContextRoot;
    if (properContextRoot == null)
    {
      properContextRoot = VirtualHost.makeProperContextRoot(getContextRoot());
      this.properContextRoot = properContextRoot;
    }
    return properContextRoot;
  }

  public String getMappingContextRoot()
  {
    String mappingContextRoot = this.mappingContextRoot;
    if (mappingContextRoot == null)
    {
      mappingContextRoot = VirtualHost.makeMappingContextRoot(getProperContextRoot());
      this.mappingContextRoot = mappingContextRoot;
    }
    return mappingContextRoot;
  }

  /*
   * @see com.ibm.ws.container.DeployedModule#getDisplayName()
   */
  public String getDisplayName()
  {
    return this.getWebAppConfig().getDisplayName();
  }

  /*
   * @see com.ibm.ws.container.DeployedModule#getName()
   */
  public String getName()
  {
    return this.webApp.getName();
  }

  /*
   * @see com.ibm.ws.container.DeployedModule#getWebGroup()
   */
  public WebGroup getWebGroup()
  {
    return null;
  }

  /*
   * @see com.ibm.ws.container.DeployedModule#getWebGroupConfig()
   */
  public WebGroupConfiguration getWebGroupConfig()
  {
    return null;
  }

  /*
   * @see com.ibm.ws.container.DeployedModule#getVirtualHostName()
   */
  public String getVirtualHostName()
  {
    return getWebAppConfig().getVirtualHostName();
  }

  /*
   * @see com.ibm.ws.container.DeployedModule#getVirtualHosts()
   */
  @SuppressWarnings("deprecation")
  public com.ibm.ws.http.VirtualHost[] getVirtualHosts()
  {
    return null;
  }
}
