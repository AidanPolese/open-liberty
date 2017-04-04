/*
 * @start_prolog@
 * Version: @(#) 1.9 SIB/ws/code/sib.comms.client/src/com/ibm/ws/sib/comms/DirectConnection.java, SIB.comms, WASX.SIB, aa1225.01 05/09/28 10:35:24 [7/2/12 05:59:04]
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
 * F201972.2       040727 mattheg  Core SPI Exceptions rework (not change flagged)
 * SIB0014.comm.1  050906 prestona Add getMetaData method
 * ============================================================================
 */
package com.ibm.ws.sib.comms;

import com.ibm.wsspi.sib.core.SICoreConnection;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;

/**
 * This class is used when creating a direct connection to a messaging engine.
 * It holds information such as the bus name or ME name and also provides a 
 * callback to allow the retrieved connection to be set in the code.
 * 
 * @author Gareth Matthews
 */
public interface DirectConnection
{
   /**
    * Sets the bus name.
    * 
    * @param busName
    */
   public void setBus(String busName);

   /**
    * Sets the messaging engine name.
    * 
    * @param meName
    */
   public void setName(String meName);

   /**
    * @return Returns the bus name.
    */
   public String getBus();

   /**
    * @return Returns the messaging engine name.
    */
   public String getName();

   /**
    * This method is provided so that whoever this class is passed off to
    * to obtain the connection can call this methid when they have found
    * an appropriate connection.
    * 
    * @param conn
    */
   public void setSICoreConnection(SICoreConnection conn);

   /**
    * @return Returns the connection that was retrieved. 
    *         If none was retrieved this may be null.
    * 
    * @throws SIConnectionLostException
    */
   public SICoreConnection getSICoreConnection() throws SIConnectionLostException;
   
   /**
    * @returns meta-data about the (network) connection which originated
    * the direct connection request.
    */
   ConnectionMetaData getMetaData();
}
