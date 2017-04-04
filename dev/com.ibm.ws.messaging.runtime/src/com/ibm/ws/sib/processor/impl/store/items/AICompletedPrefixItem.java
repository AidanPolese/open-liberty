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
 * 180483.3         111403 isilval  Initial implementation
 * 185688           161203 gatfora  Package restructure for items and itemstreams
 * 180483.4         080104 isilval  More functionally complete Remote Get
 * 190631.2         200204 caseyj   Implement persistent version no for SIMPItem
 * 189332.3         240304 gatfora  Correct Trace entry/exit
 * 225785           230804 gatfora  Persistent version checking
 * 234985           290904 gatfora  getPersistentData should throw SIErrorException
 * 248145           201204 gatfora  Remove code that is not used
 * 272109           290405 gatfora  Removal of the PROBE class 
 * 492029           260608 dware    Improve toString and MsgStore XML text
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.impl.store.items;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import com.ibm.websphere.ras.TraceComponent;

import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.msgstore.AbstractItem;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.utils.ras.FormattedWriter;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 *
 */
public class AICompletedPrefixItem extends SIMPItem
{
  private long _tick;

  // Standard debug/trace
  private static final TraceComponent tc =
        SibTr.register(
          AICompletedPrefixItem.class,
          SIMPConstants.MP_TRACE_GROUP,
          SIMPConstants.RESOURCE_BUNDLE);

  /**
   * Persistent data version number.
   */
  private static final int PERSISTENT_VERSION = 1;
  
  public AICompletedPrefixItem(long tick)
  {
    super();

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "AICompletedPrefixItem", Long.valueOf(tick));

    this._tick = tick;
      
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "AICompletedPrefixItem", this);
  }

  public AICompletedPrefixItem()
  {
    super();
  }

  public int getStorageStrategy()
  {
    return AbstractItem.STORE_ALWAYS;
  }

  public long getMaximumTimeInStore()
  {
    return AbstractItem.NEVER_EXPIRES;
  }
  
  /*
   * (non-Javadoc)
   * @see com.ibm.ws.sib.processor.impl.store.itemstreams.SIMPReferenceStream#getVersion()
   */
  public int getPersistentVersion()
  {
    return PERSISTENT_VERSION;
  } 

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.impl.store.items.SIMPItem#getPersistentData(java.io.ObjectOutputStream)
   */
  public void getPersistentData(ObjectOutputStream oos)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "getPersistentData", oos);

    try
    { 
      HashMap hm = new HashMap();
      hm.put("tick", Long.valueOf(_tick));
      
      oos.writeObject(hm);
    }
    catch (IOException e)
    {
      FFDCFilter.processException(
        e,
        "com.ibm.ws.sib.processor.impl.store.items.AICompletedPrefixItem.getPersistentData",
        "1:129:1.18",
        this);

      SIErrorException e2 = new SIErrorException(e);
      SibTr.exception(tc, e2);

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
        SibTr.exit(tc, "getPersistentData");

      throw e2;
    }
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "getPersistentData");
  }

  public void restore(ObjectInputStream ois, int dataVersion) 
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(
        tc, "restore", new Object[] { ois, Integer.valueOf(dataVersion) });
    
    checkPersistentVersionId(dataVersion);
    
    try
    {
      HashMap hm = (HashMap)ois.readObject();
      
      _tick = ((Long)hm.get("tick")).longValue();
    }
    catch (Exception e)
    {
      // FFDC
      FFDCFilter.processException(
        e,
        "com.ibm.ws.sib.processor.impl.store.items.AICompletedPrefixItem.restore",
        "1:165:1.18",
        this);

      SIErrorException e2 = new SIErrorException(e);
      SibTr.exception(tc, e2);

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
        SibTr.exit(tc, "restore", e2);

      throw e2;
    }

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "restore");
  }
  
  public long getTick()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "getTick");
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "getTick", Long.valueOf(_tick));

    return _tick;
  }

  public void setTick(long tick)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "setTick", Long.valueOf(tick));
    
    this._tick = tick;

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "setTick");
  }

  /**
   * Prints the Message Details to the xml output.
   */
  public void xmlWriteOn(FormattedWriter writer) throws IOException
  {
    writer.newLine();
    writer.taggedValue("completedPrefix", _tick);
  }

  public String toString()
  {
    return super.toString() + "[" + _tick + "]"; 
  }
}
