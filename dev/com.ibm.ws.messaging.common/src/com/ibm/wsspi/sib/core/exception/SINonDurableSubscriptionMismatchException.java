/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2014
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 */

package com.ibm.wsspi.sib.core.exception;

import com.ibm.websphere.sib.exception.SINotPossibleInCurrentStateException;

/**
 * This exception is thrown by the createSubscriptionConsumerDispatcher
 * method if the parameters to the call do not match a subscription that exists
 * with the name supplied. It should not contain a linked exception. The recovery
 * action in this case is to delete the existing subscription and create a new
 * one.
 * <p>
 * This class has no security implications.
 */
public class SINonDurableSubscriptionMismatchException
                extends SINotPossibleInCurrentStateException
{
    public SINonDurableSubscriptionMismatchException(String msg)
    {
        super(msg);
    }

}
