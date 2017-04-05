/*
 * @start_prolog@
 * Version: @(#) 1.4 SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/client/OptimizedTransaction.java, SIB.comms, WASX.SIB, uu1215.01 05/11/09 09:34:34 [4/12/12 22:14:06]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) Copyright IBM Corp. 2005 
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
 * D307265         050922 prestona Support for optimized transactions
 * D313337.1       051027 prestona overload createUncoordinatedTransaction method
 * D321471         051109 prestona Optimized transaction related problems
 * ============================================================================
 */
package com.ibm.ws.sib.comms.client;

import javax.transaction.xa.Xid;

/**
 * Interface implemented by "optimized" transaction implementations.
 * An optimized transaction does not require an extra network flow
 * to start the transaction (the information is bundled with the
 * first transacted operation).  In the case of XA transactions,
 * an optimized transaction does not send XA_START or XA_END
 * flows over the network.  Instead this functionality is
 * achieved using client side state checking and a server side
 * implementation which implicitly moves the XA Resource into
 * the correct state.
 */
public interface OptimizedTransaction
{
   /**
    * @return true if the transaction, represented by this proxy, has been
    * created on the application server. 
    */
	boolean isServerTransactionCreated();
   
   /**
    * Marks the transaction as having been created on the server side.
    */
   void setServerTransactionCreated();

	/**
	 * @return the XID for the current unit of work, or null if there is not
    * one.  This is not valid for uncoordinated transactions - which may 
    * throw a runtime exception.
	 */
	Xid getXidForCurrentUow();

	/**
	 * @return true if the server-side resource associated with this proxy
    * must have XA_END invoked upon it before continuing.  Typically this
    * would be invoked before sending each transacted flow to see if that
    * transacted flow needs a "end server side resource" flag setting or
    * not.
	 */
	boolean isEndRequired();

	/**
	 * Clears the flag used to determine if the server-side resource needs
    * ending.  Typicically this is invoked after sending a transmission
    * with an "end server side resource" flag set.
	 */
	void setEndNotRequired();

	/**
	 * @return the flags that should be used when ending the server side resource.
    * Typically this is called after a call to isEndRequired returns true to
    * determine the flags that should be sent.
	 */
	int getEndFlags();
	
	/**
	 * 
	 * @return
	 */
	boolean areSubordinatesAllowed();
   
   int getCreatingConnectionId();
   
   int getCreatingConversationId();
}
