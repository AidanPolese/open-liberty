/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- -------------------------------------------------
 * 216685           180704 ajw      cleanup anycast runtime controllable impl
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime.anycast;

import java.util.Iterator;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.processor.impl.AOBrowserSession;
import com.ibm.ws.sib.processor.runtime.SIMPIterator;
import com.ibm.ws.sib.processor.runtime.impl.RemoteBrowserReceiver;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * Iterator over the remote browser sessions
 */
public class RemoteBrowserIterator implements SIMPIterator
{
  private static TraceComponent tc =
    SibTr.register(
      RemoteBrowserIterator.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);

  // The actual iterator containing the browser sessions
  private Iterator browserIterator;
  
  public RemoteBrowserIterator(Iterator itr)
  {
    if (tc.isEntryEnabled()) 
      SibTr.entry(tc, "RemoteBrowserIterator", itr);
      
    browserIterator = itr;
    
    if (tc.isEntryEnabled()) 
      SibTr.exit(tc, "RemoteBrowserIterator", this);
  }
  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.SIMPIterator#finished()
   */
  public void finished()
  {
    if (tc.isEntryEnabled()) 
      SibTr.entry(tc, "finished");
      
    browserIterator = null;

    if (tc.isEntryEnabled()) 
      SibTr.exit(tc, "finished");
  }

  /* (non-Javadoc)
   * @see java.util.Iterator#hasNext()
   */
  public boolean hasNext()
  {
    if (tc.isEntryEnabled()) 
      SibTr.entry(tc, "hasNext");
      
    if (tc.isEntryEnabled()) 
      SibTr.exit(tc, "hasNext", Boolean.valueOf(browserIterator.hasNext()));
      
    return browserIterator.hasNext();
  }

  /* (non-Javadoc)
   * @see java.util.Iterator#next()
   */
  public Object next()
  {
    if (tc.isEntryEnabled()) 
      SibTr.entry(tc, "next");
      
    AOBrowserSession aoBrowserSession;  
    RemoteBrowserReceiver remoteBrowserReceiver;
    if (browserIterator.hasNext())
    {
      aoBrowserSession =  (AOBrowserSession) browserIterator.next();
      
      remoteBrowserReceiver = new RemoteBrowserReceiver(aoBrowserSession);
    }
    else
    {
      remoteBrowserReceiver = null;
    }
    
    if (tc.isEntryEnabled()) 
      SibTr.exit(tc, "next", remoteBrowserReceiver);
    return remoteBrowserReceiver;
  }

  /* (non-Javadoc)
   * @see java.util.Iterator#remove()
   */
  public void remove()
  {
    if (tc.isEntryEnabled()) 
      SibTr.entry(tc, "remove");
    
    browserIterator.remove();
    
    if (tc.isEntryEnabled()) 
      SibTr.exit(tc, "remove");
  }

}
