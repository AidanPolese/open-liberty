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
package com.ibm.ws.webcontainer.osgi.srt.internal;

import org.osgi.service.component.annotations.Component;

import com.ibm.ws.webcontainer.osgi.srt.SRTConnectionContext;
import com.ibm.ws.webcontainer.osgi.srt.SRTConnectionContextPool;

/**
 * A simple pool for SRTConnectionContext objects.
 */
@Component(property = { "service.vendor=IBM", "service.ranking:Integer=30", "servlet.version=3.0" })
public class SRTConnectionContextPoolImpl implements SRTConnectionContextPool
{
  private ThreadLocal<SRTConnectionContext> head = new ThreadLocal<SRTConnectionContext>();

  public final SRTConnectionContext get()
  {
    SRTConnectionContext context = null;
    SRTConnectionContext headContext = head.get();
    if (headContext != null)
    {
      context = headContext;
      head.set(context.nextContext);
    }

    if (context == null)
    {
      context = new SRTConnectionContext();
    }

    context.nextContext = null;

    return context;
  }

  public final void put(SRTConnectionContext context)
  {
    context.nextContext = head.get();
    head.set(context);
  }
}
