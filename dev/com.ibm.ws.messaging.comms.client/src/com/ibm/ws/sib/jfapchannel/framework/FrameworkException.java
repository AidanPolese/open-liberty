/*
 * @start_prolog@
 * Version: @(#) 1.2 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/framework/FrameworkException.java, SIB.comms, WASX.SIB, uu1215.01 08/02/13 06:30:11 [4/12/12 22:14:18]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 Copyright IBM Corp. 2003, 2008 
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
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 * 494863          080213 mleming  Prevent NPE if TCP/IP connection goes while establishing connection
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel.framework;

/**
 * This class is used to throw generic framework problems. Typically this will involve wrapping a
 * channel framework exception.
 * 
 * @author Gareth Matthews
 */
public class FrameworkException extends Exception
{
   /** Serial version UId */
   private static final long serialVersionUID = 6261785028467855112L;
   
   /**
    * Constructor.
    * @param t
    */
   public FrameworkException(Throwable t)
   {
      super(t);
   }
   
   /**
    * Create a FrameworkException specifying an error message.
    * 
    * @param message
    */
   public FrameworkException(String message)
   {
      super(message);
   }
}
