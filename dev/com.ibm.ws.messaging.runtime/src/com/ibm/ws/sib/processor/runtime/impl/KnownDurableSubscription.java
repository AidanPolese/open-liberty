/*
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
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 *                                 Version X copied from CMVC
 * ============================================================================
 */
package com.ibm.ws.sib.processor.runtime.impl;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.admin.RuntimeEvent;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.processor.exceptions.SIMPControllableNotFoundException;
import com.ibm.ws.sib.processor.runtime.SIMPKnownDurableSubscriptionControllable;
import com.ibm.ws.sib.processor.runtime.SIMPLocalSubscriptionControllable;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * 
 */
public class KnownDurableSubscription extends AbstractControlAdapter implements SIMPKnownDurableSubscriptionControllable
{
  private static TraceComponent tc =
    SibTr.register(
      KnownDurableSubscription.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);

  
  LocalSubscriptionControl subscriptionControl;
  
  public KnownDurableSubscription(LocalSubscriptionControl control)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc,"KnownDurableSubscription", control);
      
    this.subscriptionControl = control;
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc,"KnownDurableSubscription", this);
  }
  
  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.SIMPKnownDurableSubscriptionControllable#getDurableHome()
   */
  public String getDurableHome()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc,"getDurableHome");
    String home = subscriptionControl.getDurableHome();
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc,"getDurableHome", home);
    return home;
  }
  
  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.SIMPKnownDurableSubscriptionControllable#getLocalSubscriptionControl()
   */
  public SIMPLocalSubscriptionControllable getLocalSubscriptionControl()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
    {
      SibTr.entry(tc,"getLocalSubscriptionControl");
      SibTr.exit(tc,"getLocalSubscriptionControl", subscriptionControl);
    }
    return subscriptionControl;
  }
  
  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.SIMPControllable#getId()
   */
  public String getId()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc,"getId");
    String id = subscriptionControl.getId();
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc,"getId", id);
    return id;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.SIMPControllable#getName()
   */
  public String getName()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc,"getName");
    String name = subscriptionControl.getName();
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc,"getName", name);
    return name;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.AbstractControllable#checkValidControllable()
   */
  public void assertValidControllable() throws SIMPControllableNotFoundException
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
    {
      SibTr.entry(tc,"assertValidControllable");
      SibTr.exit(tc,"assertValidControllable");
    }
    subscriptionControl.assertValidControllable();    
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.AbstractControllable#dereferenceControllable()
   */
  public void dereferenceControllable()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "dereferenceControllable");
    
    subscriptionControl = null;
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "dereferenceControllable");    
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.ControlAdapter#registerControlAdapterAsMBean()
   */
  public void registerControlAdapterAsMBean()
  {
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.ControlAdapter#deregisterControlAdapterMBean()
   */
  public void deregisterControlAdapterMBean()
  {
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.ControlAdapter#runtimeEventOccurred(com.ibm.ws.sib.admin.RuntimeEvent)
   */
  public void runtimeEventOccurred(RuntimeEvent event)
  {
  }

}
