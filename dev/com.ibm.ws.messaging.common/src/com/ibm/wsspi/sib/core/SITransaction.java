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
 * ---------------  ------ -------- -------------------------------------------------
 * 159093           070303 jroots   Original
 * 162915           080403 tevans   Make the Core API code look like the model
 * 166828           060603 tevans   Core MP rewrite
 * 169897.0         240603 jroots   Updates for Milestone 3 Core API
 * 181796.0         041103 jroots   Move to com.ibm.wsspi
 * 199140           180804 gatfora  Cleanup javadoc      
 * 276259           130505 dware    Improve security related javadoc
 * ===========================================================================
 */

package com.ibm.wsspi.sib.core;

/**
 SITransaction is the parent interface to all Core API transaction objects. 
 SITransaction objects are passed to send and receive calls to group the 
 operations into a single unit of work. 
 <p>
 Objects with type SITransaction include:
 <ul>
 <li> Objects of type SIUncoordinatedTransaction, on which the application calls 
      commit and rollback directly. </li>
 <li> Objects of type SIXAResource, which are used to enlist send and receive 
      calls into externally coordinated transactions. 
      </li>
 </ul>
 <p>
 This class has no security implications.
*/
public interface SITransaction {
}

