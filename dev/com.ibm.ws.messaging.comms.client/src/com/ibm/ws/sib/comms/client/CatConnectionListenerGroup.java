/*
 * @start_prolog@
 * Version: @(#) 1.11 SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/client/CatConnectionListenerGroup.java, SIB.comms, WASX.SIB, uu1215.01 06/08/15 03:54:39 [4/12/12 22:15:07]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61  (C) Copyright IBM Corp. 2004, 2006 
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 * 
 * Change activity:
 * 
 * CORE API 0.6 Implementation
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * F172297         030507 schmittm Original
 * LIDB3706-5.195  050211 prestona serialization compatibility for sib.comms.impl
 * D274182         050518 mattheg  Modify serialVersionUID to correct value
 * D378229         060808 prestona Avoid synchronizing on ME-ME send()
 * ============================================================================
 */

package com.ibm.ws.sib.comms.client;

import java.util.Vector;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.wsspi.sib.core.SICoreConnectionListener;
import com.ibm.ws.sib.utils.ras.SibTr;

public class CatConnectionListenerGroup extends Vector<SICoreConnectionListener>
{
   private static final long serialVersionUID = -2679732549257884229L;  // LIDB3706-5.195, D274182
   
   /**
    * Register Class with Trace Component
    */
   private static final TraceComponent tc =
      SibTr.register(
         CatConnectionListenerGroup.class,
         CommsConstants.MSG_GROUP,
         CommsConstants.MSG_BUNDLE);

   /**
    * Log Source code level on static load of class
    */
   static {
      if (tc.isDebugEnabled())
         SibTr.debug(tc, "Source info: @(#)SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/client/CatConnectionListenerGroup.java, SIB.comms, WASX.SIB, uu1215.01 1.11");
   }
   
   public void addConnectionListener(SICoreConnectionListener listener)
   {
      this.add(listener);
   }
   
   public void removeConnectionListener(SICoreConnectionListener listener)
   {
      this.remove(listener);
   }   
   
   public SICoreConnectionListener[] getConnectionListeners()
   {
      SICoreConnectionListener[] retVal = new SICoreConnectionListener[this.size()];
      for(int i=0; i < this.size(); ++i){
         retVal[i] = (SICoreConnectionListener)this.get(i);   
      }     
      return retVal;
   }
}
