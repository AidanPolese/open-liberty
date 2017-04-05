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

package com.ibm.ws.sib.processor.impl.store.filters;

// Import required classes.
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.msgstore.AbstractItem;
import com.ibm.ws.sib.msgstore.Filter;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.processor.impl.ConsumerDispatcher;
import com.ibm.ws.sib.processor.impl.store.itemstreams.SIMPReferenceStream;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * SubscriptionFilter.
 * <p> This class is a filter for searching for subscriptions
 * on any destinations itemstream. It is used to remove detatched subscription
 * referenceStream items from from a destination itemstream.
 * 
 * <p>A filter is needed to find a particular item on an itemstream. We do a remove
 * and supply a filter. A matching item is returned.
 */

public final class SubscriptionFilter implements Filter
{
  private static TraceComponent tc =
    SibTr.register(
      SubscriptionFilter.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);

  /**
   * consumerDispatcher - This is the consumer dispatcher to compare the incoming
   * items to. If they are equal then there is a match
   */
  
  ConsumerDispatcher consumerDispatcher;

  /**
   * Constructor for SubscriptionFilter.
   * @param consumerDispatcher - The consumerDispatcher to match
   */
  public SubscriptionFilter(ConsumerDispatcher consumerDispatcher)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.entry(tc, "SubscriptionFilter", consumerDispatcher);
    this.consumerDispatcher = consumerDispatcher;
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.exit(tc, "SubscriptionFilter", this);
  }

  /**
   * Filter method. Checks whether the given item is a consumerDispatcher and matches the
   * one associated with this filter
   * @param item - The item on the itemstream
   * @return boolean - True if a match, false otherwise.
   */
  
  public boolean filterMatches(AbstractItem item)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "filterMatches", item);

    /* Cast the incoming item to a PersistentStoreReferenceStream object. if it is not, an
     * exception will be thrown and the match will fail */

    SIMPReferenceStream rstream;

    if (item instanceof SIMPReferenceStream) {

      rstream = (SIMPReferenceStream) item;
    
      // Check for matching consumer dispatchers
      if (rstream == consumerDispatcher.getReferenceStream())
      {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
          SibTr.exit(tc, "filterMatches", Boolean.TRUE);        
        return true;
      }      
    }

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "filterMatches", Boolean.FALSE);
    return false;

  }

}
