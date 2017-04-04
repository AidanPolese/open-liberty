package com.ibm.ws.sib.msgstore;
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
 * Reason            Date    Origin      Description
 * --------------- -------- ----------  ---------------------------------------
 *                 03/02/03 van Leersum  Original
 * 189573          05/02/04  gareth      Add NLS support to transaction code
 * LIDB3706-5.241  19/01/05  gareth      Add Serialization support
 * SIB0121a.utl.1  10/04/07  djvines     Make Reasonable
 * ============================================================================
 */

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.ws.exception.WsException;
import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.websphere.sib.exception.SIException;
import com.ibm.ws.sib.utils.Reasonable;

/**
 * @author drphill
 *
 * <p>Wrapper for exceptions thrown in the object store.  Will help
 * isolate/localise problems.</p>
 */
public class MessageStoreException extends WsException implements Reasonable
{
    private static final long serialVersionUID = -959379983555487604L;

    private static TraceNLS nls = TraceNLS.getTraceNLS(MessageStoreConstants.MSG_BUNDLE);

    public MessageStoreException()
    {
        super();
    }

    /**
     * @param arg0
     */
    public MessageStoreException(String arg0)
    {
        super(nls.getString(arg0));
    }

    /**
     * @param arg0
     */
    public MessageStoreException(Throwable arg0)
    {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public MessageStoreException(String arg0, Throwable arg1)
    {
        super(nls.getString(arg0), arg1);
    }

    /**
     * Provide a key and use formatted string
     * @param arg0
     * @param args
     */
    public MessageStoreException(String arg0, Object[] args)
    {
        super(nls.getFormattedMessage(arg0, args, null));
    }

    /**
     * Provide a key and use formatted string
     * @param arg0
     * @param args
     */
    public MessageStoreException(String arg0, Object[] args, Throwable exp)
    {
        super(nls.getFormattedMessage(arg0, args, null), exp);
    }

    /**
     * @see com.ibm.ws.sib.utils.Reasonable#getExceptionReason()
     * @return a reason code that can be used if this exception causes a message
     *         to be rerouted to the exception destination
     */
    public int getExceptionReason() 
    {
        Throwable cause = getCause();
        if (cause instanceof Reasonable)
            return ((Reasonable)cause).getExceptionReason();
        else if (cause instanceof SIException)
            return ((SIException)cause).getExceptionReason();
        else if (cause instanceof SIErrorException)
            return ((SIErrorException)cause).getExceptionReason();
        else
            return Reasonable.DEFAULT_REASON;   
    }
    
    /**
     * Returns an array of "exception inserts", which are used in conjunction with
     * the exception reason to identify the reason that a message could not be 
     * delivered. For example, if getExceptionReason returns a value of 
     * SIRC0003_DESTINATION_NOT_FOUND, the array will contain, as its only element,
     * the name of the destination that could not be found. See 
     * getExceptionInserts() for further information.
     * 
     * @see #getExceptionReason 
     * 
     * @return exception inserts
     */
    public String[] getExceptionInserts() 
    {
        Throwable cause = getCause();
      
        if (cause instanceof Reasonable)
            return ((Reasonable)cause).getExceptionInserts();
        else if (cause instanceof SIException)
            return ((SIException)cause).getExceptionInserts();
        else if (cause instanceof SIErrorException)
            return ((SIErrorException)cause).getExceptionInserts();
        else
            return Reasonable.DEFAULT_INSERTS;
    }
}
