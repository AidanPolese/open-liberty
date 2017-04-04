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
package com.ibm.ws.sib.processor.impl.indexes;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.processor.utils.index.Index;
import com.ibm.ws.sib.processor.utils.index.IndexFilter;
import com.ibm.ws.sib.utils.ras.SibTr;

public class SubscriptionTypeFilter implements IndexFilter
{
  public Boolean LOCAL = null;
  public Boolean DURABLE = null;

  /**
   * Trace for the component
   */
  private static final TraceComponent tc =
    SibTr.register(
      SubscriptionTypeFilter.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);

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
  public boolean matches(Index.Type type)
  {
    if(type == null) return false;
    if(type instanceof SubscriptionIndex.SubscriptionType)
    {
      SubscriptionIndex.SubscriptionType subType = (SubscriptionIndex.SubscriptionType) type;
      if((LOCAL == null || LOCAL.booleanValue() == subType.local) &&
         (DURABLE == null || DURABLE.booleanValue() == subType.durable))
      {
        return true;
      }
    }
    return false;
  }
}
