/*
 * @start_prolog@
 * Version: @(#) 1.9 SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/common/DirectConnectionImpl.java, SIB.comms, WASX.SIB, uu1215.01 05/09/28 10:35:45 [4/12/12 22:14:09]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-I63, 5724-H88, 5655-N02, 5733-W70  (C) Copyright IBM Corp. 2004, 2005
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * Creation        031128 mattheg  Original
 * f184933         031208 mattheg  Removed subnet property
 * SIB0014.comm.1  050906 prestona Add getMetaData method
 * ============================================================================
 */
package com.ibm.ws.sib.comms.common;

import com.ibm.ws.sib.comms.ConnectionMetaData;
import com.ibm.ws.sib.comms.DirectConnection;
import com.ibm.wsspi.sib.core.SICoreConnection;

/**
 * 
 * 
 * @author Gareth Matthews
 */
public class DirectConnectionImpl implements DirectConnection
{

   private String busName = "";
   private String meName = "";
   private SICoreConnection conn = null;
   private final ConnectionMetaData metaData;
   
   public DirectConnectionImpl(ConnectionMetaData metaData)
   {
   	this.metaData = metaData;
   }
   
   /**
    * Sets the bus name.
    * 
    * @param busName
    */
   public void setBus(String busName)
   {
      this.busName = busName;
   }

   /**
    * Sets the messaging engine name.
    * 
    * @param meName
    */
   public void setName(String meName)
   {
      this.meName = meName;
   }

   /**
    * @return Returns the bus name.
    */
   public String getBus()
   {
      return busName;
   }

   /**
    * @return Returns the messaging engine name.
    */
   public String getName()
   {
      return meName;
   }

   /**
    * This method is provided so that whoever this class is passed off to
    * to obtain the connection can call this methid when they have found
    * an appropriate connection.
    * 
    * @param conn
    */
   public void setSICoreConnection(SICoreConnection conn)
   {
      this.conn = conn;
   }

   /**
    * @return Returns the connection that was retrieved. 
    *         If none was retrieved this may be null.
    * 
    * @throws SICommsException
    */
   public SICoreConnection getSICoreConnection()
   {
      return conn;
   }
   
   /** @see DirectConnection#getMetaData() */
   public ConnectionMetaData getMetaData()
   {
   	final ConnectionMetaData result = metaData;
      return result;
   }
}
