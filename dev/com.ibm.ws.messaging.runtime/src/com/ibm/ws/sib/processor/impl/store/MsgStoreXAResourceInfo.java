/*
 * 
 * 
 * ===========================================================================
 * IBM Confidential OCO Source Material
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ===========================================================================
 * 
 *
 * Change activity:
 *
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- -------------------------------------------
 * SIB0002.mp.16    180805 tpm      hasRMQResources flag
 * 309940           031005 gatfora  Missing trace statements
 * 318614.1         071105 tevans   Support remote transaction recovery from PEV
 * 381317           271106 cwilkin  Correct RMName in MsgStoreXAResourceInfo
 * 516346           290408 djvines  Implement hashCode
 * 520288           130508 sibcopyr Automatic update of trace guards 
 * =================================================================================
 */
package com.ibm.ws.sib.processor.impl.store;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.transaction.XAResourceInfo;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * Information needed to recover a MessageStore XA resource
 */
public class MsgStoreXAResourceInfo implements XAResourceInfo
{

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = -3135180725448875737L;

  /**
   * Trace for the component
   */
  private static final TraceComponent tc =
    SibTr.register(
      MsgStoreXAResourceInfo.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);

  private final String meUuid;
  private final String rmName;
  private final String meBus;
  private final String meName;

  public MsgStoreXAResourceInfo(String meUuid, String meName, String meBus)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "MsgStoreXAResourceInfo", new String[]{meUuid, meName, meBus});

    this.meUuid = meUuid;
    this.meBus = meBus;
    this.meName = meName;
    rmName = SIMPConstants.PRODUCT_NAME+meName;

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "MsgStoreXAResourceInfo", this);
  }

  public String getMEUuid()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
    {
      SibTr.entry(tc, "getMEUuid");
      SibTr.exit(tc, "getMEUuid", meUuid);
    }
    return meUuid;
  }

  public String getMEBus()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
    {
      SibTr.entry(tc, "getMEBus");
      SibTr.exit(tc, "getMEBus", meBus);
    }
    return meBus;
  }

  public String getMEName()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
    {
      SibTr.entry(tc, "getMEName");
      SibTr.exit(tc, "getMEName", meName);
    }
    return meName;
  }

  /*
   *  (non-Javadoc)
   * @see com.ibm.ws.Transaction.XAResourceInfo#getRMName()
   */
  public String getRMName()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
    {
      SibTr.entry(tc, "getRMName");
      SibTr.exit(tc, "getRMName", rmName);
    }
    return rmName;
  }

  /*
   *  (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object o)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "equals", o);
    boolean returnValue = false;
    if(o != null && o instanceof MsgStoreXAResourceInfo)
    {
      MsgStoreXAResourceInfo info = (MsgStoreXAResourceInfo)o;
      if(info.getMEUuid().equals(getMEUuid()) && info.getRMName().equals(getRMName()))
      {
        returnValue = true;
      }
    }
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "equals", new Boolean(returnValue));
    return returnValue;
  }

  /*
   *  (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  public int hashCode()
  {
    return meUuid.hashCode();
  }

  /*
   *  (non-Javadoc)
   * @see com.ibm.ws.Transaction.XAResourceInfo#commitInLastPhase()
   */
  public boolean commitInLastPhase()
  {
    return false;
  }



}
