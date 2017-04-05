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
 * ---------------  ------ -------- --------------------------------------------
 * 180483.3         201003 sbhola   initial implementation
 * 184035.1         110204 tevans   New MP Alarm Manager interface
 * 185691           190204 tevans   Redesign/Rework LinkedMap and LockedMessageEnumeration
 * 199655           210404 sbhola   improved handling for 0 timeout, i.e., for NO_WAIT
 * 180483.14        010604 sbhola   now extends Entry to reduce object creation
 * 209977           080704 gatfora  Removal of AnycastConstansts file.
 * 248145           201204 gatfora  Remove code that is not used
 * SIB0113a.mp.3    010807 vaughton Class rename
 * 458890           130807 sibcopyr Automatic update of trace guards 
 * SIB0113a.mp.9    261007 cwilkin  Remote Gathering
 * 499849           170308 cwilkin  Fix trace
 * 532349           260608 cwilkin  Forward requests to DME correctly
 * ============================================================================
 */

package com.ibm.ws.sib.processor.impl;

// Import required classes.
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ejs.ras.TraceNLS;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.ws.sib.processor.utils.UserTrace;
import com.ibm.ws.sib.processor.utils.am.MPAlarmManager;
import com.ibm.ws.sib.processor.utils.linkedlist.Entry;
import com.ibm.ws.sib.processor.impl.interfaces.SIMPMessage;
import com.ibm.ejs.util.am.Alarm;
import com.ibm.ejs.util.am.AlarmListener;
/**
 * A Requested Tick waiting for an assignment. Associated with an JSRemoteConsumerPoint.
 * The tick starts of in the state (!satisfied and !expired). There are 2 possible transitions
 * from this state into two ending states: (!satisfied and expired), (satisfied and !expired).
 * The expired transition will result in the AOStream containing this tick to transition this tick to
 * the completed state. The satisfied transition will result in the AOStream containing this tick
 * to transition this tick to the value state.
 *
 * SYNCHRONIZATION: No method in this class is synchronized. Since two different threads can compete
 * with making these 2 transitions concurrently, using the expire() and satisfy() methods respectively,
 * the parent (JSRemoteConsumerPoint) is responsible for synchronization.
 */
public final class AORequestedTick extends Entry implements AlarmListener
{
  
  // NLS for component
  private static final TraceNLS nls_mt =
    TraceNLS.getTraceNLS(SIMPConstants.TRACE_MESSAGE_RESOURCE_BUNDLE);
  private static TraceComponent tc =
  SibTr.register(
    AORequestedTick.class,
    SIMPConstants.MP_TRACE_GROUP,
    SIMPConstants.RESOURCE_BUNDLE);


  private final JSRemoteConsumerPoint parent;
  public final long tick; // the tick in the stream
  public final Long objTick; // the tick as a Long object
  public final long timeout; // the time taken for this request to expire
  public final long requestTime; // the time that the tick was requested
  private Alarm expiryHandle; // the expiry handle for the alarm

  private boolean satisfied;
  private SIMPMessage msg; // satisfied implies msg!=null

  private boolean expired;

  /**
   * Constructor
   * @param parent The containing JSRemoteConsumerPoint
   * @param tick The tick position in the stream
   * @param expiryTimeout The time period in milliseconds, after which this tick should expire
   * @param listEntry The entry of this in the parent's linked list
   */
  public AORequestedTick(JSRemoteConsumerPoint parent, long tick, Long objTick,
      long expiryTimeout, MPAlarmManager am)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "AORequestedTick",
        new Object[]{parent, Long.valueOf(tick), objTick, Long.valueOf(expiryTimeout), am});

    this.parent = parent;
    this.tick = tick;
    this.objTick = objTick;
    this.timeout = expiryTimeout;
    this.requestTime = System.currentTimeMillis();
    if ((expiryTimeout != SIMPConstants.INFINITE_TIMEOUT) && (expiryTimeout > 0L))
      this.expiryHandle = am.create(expiryTimeout, this);
    else
    {
      // for INFINITE_TIMEOUT we don't do any expiry processing. For 0 timeout, the JSRemoteConsumerPoint does the expiry
      // without using the MPAlarmManager.
      this.expiryHandle = null;
    }

    this.satisfied = false;
    this.expired = false;
    this.msg = null;

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "AORequestedTick", this);

  }

  /**
   * The get request has expired. Will tell the parent to expire this request (i.e. change the tick in the stream
   * to completed) only if the transition to (!satisfied and expired) is successful
   * @param thandle
   */
  public void alarm(Object thandle)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "alarm", thandle);

    parent.expiryAlarm(this);

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "alarm");
  }

  /**
   * Try to transition to (!satisfied and expired) state
   * @param cancelTimer Set to true if this method should also cancel the timer
   * @return true if the transition to (!satisfied and expired) was successful
   */
  public boolean expire(boolean cancelTimer)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "expire", new Boolean(cancelTimer));

    if ((expiryHandle != null) && cancelTimer)
    {
      expiryHandle.cancel();
    }
    expiryHandle = null;
    if (!satisfied)
    {
      expired = true;
      if (TraceComponent.isAnyTracingEnabled() && UserTrace.tc_mt.isDebugEnabled())    
        SibTr.debug(UserTrace.tc_mt,       
           nls_mt.getFormattedMessage(
           "REMOTE_REQUEST_EXPIRED_CWSJU0033",
           new Object[] {
             tick},
           null));
    }

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "expire", new Boolean(expired));

    return expired;
  }

  /**
   * Try to transition to (satisfied and !expired) state
   * @return true if the transition to (satisfied and !expired) was successful
   */
  public boolean satisfy(SIMPMessage msg)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "satisfy");

    if (expiryHandle != null)
      expiryHandle.cancel();
    if (!expired)
    {
      satisfied = true;
      this.msg = msg;
      
      if (TraceComponent.isAnyTracingEnabled() && UserTrace.tc_mt.isDebugEnabled())    
        SibTr.debug(UserTrace.tc_mt,       
           nls_mt.getFormattedMessage(
           "REMOTE_REQUEST_SATISFIED_CWSJU0032",
           new Object[] {
             Long.valueOf(tick),
             msg},
           null));
    }

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "satisfy", new Boolean(satisfied));

    return satisfied;
  }

  public final SIMPMessage getMessage()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
    {
      SibTr.entry(tc, "getMessage");
      SibTr.exit(tc, "getMessage",msg);
    }
    return msg;
  }
}
