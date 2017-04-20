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
 * Reason           Date  Origin   Description
 * ---------------  ------ -------- -------------------------------------------
 * 186967.7.6       250304 millwood Support TRM link interfaces   
 * 199938.1         280404 cwilkin  Expose linkChangeListener    
 * 230256           090904 rjnorris Add remoteME for Interbus testcases     
 * 250746           190105 gatfora  Remove unthrown exception declarations
 * ============================================================================
 */

package com.ibm.ws.sib.processor.test;

import java.util.HashMap;

import com.ibm.ws.sib.admin.JsMessagingEngine;
import com.ibm.ws.sib.trm.links.LinkChangeListener;
import com.ibm.ws.sib.trm.links.LinkManager;
import com.ibm.ws.sib.trm.links.LinkSelection;
import com.ibm.ws.sib.trm.links.ibl.InterBusLinkManager;
import com.ibm.ws.sib.trm.links.mql.MQLinkManager;
import com.ibm.ws.sib.utils.SIBUuid12;
import com.ibm.ws.sib.utils.SIBUuid8;

/**
 * @author millwood
 *
 */
public class UTLinkManager implements LinkManager
{
  private UTMQLinkManager uTMQLinkManager = new UTMQLinkManager();
  private UTInterBusLinkManager uTInterBusLinkManager = new UTInterBusLinkManager();
  private HashMap links = new HashMap();
  private HashMap remoteUuids = new HashMap();
  private JsMessagingEngine engine;
  private LinkChangeListener linkChangeListener = null;
  
  public UTLinkManager(JsMessagingEngine engine)
  {
    this.engine = engine;
    
  }
  
  /* (non-Javadoc)
   * @see com.ibm.ws.sib.trm.links.LinkManager#register(com.ibm.ws.sib.utils.SIBUuid12)
   */
  public void register(SIBUuid12 arg0)
  {
    links.put(arg0, engine.getUuid());
  }
  
  /* (non-Javadoc)
   * @see com.ibm.ws.sib.trm.links.LinkManager#deregister(com.ibm.ws.sib.utils.SIBUuid12)
   */
  public void deregister(SIBUuid12 arg0)
  {
    links.remove(arg0);
  }

  /* 
   * 
    */
   public void setRemoteUuidForLink(SIBUuid12 arg0, SIBUuid8 remoteMEuuid)
   {
     remoteUuids.put(arg0, remoteMEuuid);
   }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.trm.links.LinkManager#select(com.ibm.ws.sib.utils.SIBUuid12)
   */
  public LinkSelection select(SIBUuid12 arg0)
  {
    LinkSelection linkSelection = null;
    
    if (uTMQLinkManager.isDefined(arg0))
    {
      //Pretend this ME is the owner of the link
      linkSelection = new LinkSelection((SIBUuid8)links.get(arg0), null);
    }
    else
    {
      //Pretend this ME is the outbound gateway ME and another ME hosts the other end
      //of the link
      SIBUuid8 localUuid = (SIBUuid8)links.get(arg0);
      SIBUuid8 remoteUuid = (SIBUuid8)remoteUuids.get(arg0);
      if( localUuid != null && remoteUuid != null)
      {     
        linkSelection = new LinkSelection(localUuid, remoteUuid);
      }
      else
      {
        linkSelection = null;
      }
   }
   return linkSelection; 
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.trm.links.LinkManager#setChangeListener(com.ibm.ws.sib.trm.links.LinkChangeListener)
   */
  public void setChangeListener(LinkChangeListener lcl)
  {
    linkChangeListener = lcl;
  }
  
  public LinkChangeListener getChangeListener()
  {
    return linkChangeListener;
  }

  public MQLinkManager getMQLinkManager()
  {
    return uTMQLinkManager;
  }

  public InterBusLinkManager getInterBusLinkManager()
  {
    return uTInterBusLinkManager;
  }
}
