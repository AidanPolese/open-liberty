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
package com.ibm.ws.webcontainer.osgi.osgi;

import com.ibm.websphere.event.Topic;

/**
 * Constants used in the webcontainer component.
 */
public class WebContainerConstants
{
  /** Trace NLS message location */
  public static final String NLS_PROPS = "com.ibm.ws.webcontainer.resources.LShimMessages";
  /** Trace group id value */
  public static final String TR_GROUP = "webcontainer";
  static String PARAMETER_BREAK = "&";
  static String PARAMETER_ASSIGNMENT = "=";

  public static final Topic STARTED_EVENT = new Topic("com/ibm/ws/app/container/webcontainer/ContainerEvent/CONTAINER_STARTED");
  public static final Topic STOPPED_EVENT = new Topic("com/ibm/ws/app/container/webcontainer/ContainerEvent/CONTAINER_STOPPED");

  /** Event fired by the HTTP dispatcher. */
  public static final String HTTP_REQUEST_ARRIVED_EVENT = "com/ibm/websphere/http/request/ARRIVED";
  /** Event fired and caught by the WebContainer. */
  public static final String WEBCONTAINER_HANDLE_REQUEST_HTTP = "com/ibm/websphere/webcontainer/handle/request/HTTP";

}
