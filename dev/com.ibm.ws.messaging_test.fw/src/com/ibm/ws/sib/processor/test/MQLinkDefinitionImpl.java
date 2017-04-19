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
 * ---------------  ------ -------- -------------------------------------------
 * SIB0211.mp.2     080607 nyoung   Support for dynamic PSB configuration
 * ============================================================================
 */
package com.ibm.ws.sib.processor.test;

import com.ibm.ws.sib.admin.MQLinkDefinition;
import com.ibm.ws.sib.admin.MQLinkReceiverChannelDefinition;
import com.ibm.ws.sib.admin.MQLinkSenderChannelDefinition;

import com.ibm.ws.sib.utils.SIBUuid8;

public class MQLinkDefinitionImpl implements MQLinkDefinition
{
  SIBUuid8 mqlinkuuid;
  
  public MQLinkDefinitionImpl(SIBUuid8 mqlinkuuid)
  {
    this.mqlinkuuid = mqlinkuuid;
  }
  
  public boolean getAdoptable()
  {
    return false;
  }

  public int getBatchSize()
  {
    return 0;
  }

  public String getConfigId()
  {
    return null;
  }

  public String getDescription()
  {
     return null;
  }

  public int getHeartBeat()
  {
    return 0;
  }

  public String getInitialState()
  {
    return null;
  }

  public int getMaxMsgSize()
  {
    return 0;
  }

  public String getName()
  {
    return null;
  }

  public String getNpmSpeed()
  {
    return null;
  }

  public String getQmName()
  {
    return null;
  }

  public MQLinkReceiverChannelDefinition getReceiverChannel()
  {
    return null;
  }

  public MQLinkSenderChannelDefinition getSenderChannel()
  {
    return null;
  }

  public long getSequenceWrap()
  {
    return 0;
  }

  public String getTargetUuid()
  {
    return null;
  }

  public SIBUuid8 getUuid()
  {
    return mqlinkuuid;
  }

}
