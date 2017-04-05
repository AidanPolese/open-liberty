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

public class DestinationTypeFilter extends AbstractDestinationTypeFilter
{
  public Boolean ALIAS = null;
  public Boolean FOREIGN_DESTINATION = null;
  public Boolean LOCAL = null;
  public Boolean REMOTE = null;
  public Boolean QUEUE = null;
  
  /** 
   * Trace for the component
   */
  private static final TraceComponent tc =
    SibTr.register(
      DestinationTypeFilter.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);
            
  public boolean matches(Index.Type type)
  {
    if(type == null) return false;
    if(type instanceof DestinationIndex.Type)
    {
      DestinationIndex.Type destType = (DestinationIndex.Type) type;      
      if(super.matches(destType) &&
        (ALIAS == null || ALIAS.equals(destType.alias)) &&
        (FOREIGN_DESTINATION == null || FOREIGN_DESTINATION.equals(destType.foreignDestination)) &&
        (LOCAL == null || LOCAL.equals(destType.local)) &&
        (REMOTE == null || REMOTE.equals(destType.remote)) &&
        (QUEUE == null || QUEUE.equals(destType.queue)))
      {
        return true;
      }
    }      
    return false;  
  }     
}
