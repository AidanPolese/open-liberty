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
import com.ibm.ws.sib.utils.ras.SibTr;

public class LinkTypeFilter extends AbstractDestinationTypeFilter
{
  public Boolean MQLINK = null;
  public Boolean LOCAL = null;
  public Boolean REMOTE = null;
  
  /** 
   * Trace for the component
   */
  private static final TraceComponent tc =
    SibTr.register(
      LinkTypeFilter.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);
    
                  
  public boolean matches(Index.Type type)
  {
    if (tc.isEntryEnabled()) 
       SibTr.entry(tc, "matches", type);

    if(type == null) return false;
    if(type instanceof LinkIndex.Type)
    {
      LinkIndex.Type destType = (LinkIndex.Type) type;      
      if (tc.isDebugEnabled())
        SibTr.debug(tc, "LinkIndex with destType: " + destType);
      if(super.matches(destType) &&
        (MQLINK == null || MQLINK.equals(destType.mqLink)) &&
        (LOCAL == null || LOCAL.equals(destType.local)) &&
        (REMOTE == null || REMOTE.equals(destType.remote)))
      {
        if (tc.isEntryEnabled()) 
          SibTr.exit(tc, "matches", new Boolean(true)); 

        return true;
      }
    }      
    
    if (tc.isEntryEnabled()) 
      SibTr.exit(tc, "matches", new Boolean(false)); 
    return false;  
  }     
}
