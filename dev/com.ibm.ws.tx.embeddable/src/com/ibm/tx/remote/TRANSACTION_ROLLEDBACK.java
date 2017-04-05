/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.tx.remote;


/**
 *
 */
@SuppressWarnings("serial")
public class TRANSACTION_ROLLEDBACK extends RuntimeException {

    /**
     * @param string
     */
    public TRANSACTION_ROLLEDBACK(String string) {
        super(string);
    }

    /**
     * @param i
     * @param completedYes
     */
    public TRANSACTION_ROLLEDBACK(int i, Boolean completionStatus) {
        // TODO Auto-generated constructor stub
    }

}
