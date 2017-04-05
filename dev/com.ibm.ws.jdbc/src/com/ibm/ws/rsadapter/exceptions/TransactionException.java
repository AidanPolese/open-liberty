/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2001,2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.rsadapter.exceptions;

import javax.resource.spi.ResourceAdapterInternalException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.rsadapter.AdapterUtil;

/**
 * This class is used for Transaction Exceptions.
 */
public class TransactionException extends ResourceAdapterInternalException {
    private static final long serialVersionUID = 1025950887360722410L; 
    private static final TraceComponent tc = Tr.register(TransactionException.class, AdapterUtil.TRACE_GROUP, AdapterUtil.NLS_FILE);

    /**
     * Constructor that takes the current and intended value of the state object
     * NLS String is in IBMDataStoreAdapterNLS.properties
     * 
     * @param curState int current state
     * @param destState int destination state
     */
    public TransactionException(String action, String transaction, boolean internalError) {
        super(internalError
              ? Tr.formatMessage(tc, "DSA_INTERNAL_ERROR", "Action = ", action, " is not allowed for the current transaction state = " + transaction)
              : Tr.formatMessage(tc, "WS_ERROR", "Action = " + action + " is not allowed for the current transaction state = " + transaction));
    }

    public TransactionException(String action, String transaction) {
        this(action, transaction, true);
    }
}
