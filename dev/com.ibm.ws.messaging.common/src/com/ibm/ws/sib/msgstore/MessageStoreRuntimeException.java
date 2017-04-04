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
 * Reason            Date     Origin      Description
 * --------------- --------  ----------  --------------------------------------
 *                 26/06/03  van Leersum  Original
 * 189573          05/02/04   gareth      Add NLS support to transaction code
 * LIDB3706-5.241  19/01/05   gareth      Add Serialization support
 * ============================================================================
 */

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.ws.exception.WsRuntimeException;

public class MessageStoreRuntimeException extends WsRuntimeException
{
    private static final long serialVersionUID = -3790027338845641878L;

    private static TraceNLS nls = TraceNLS.getTraceNLS(MessageStoreConstants.MSG_BUNDLE);

    public MessageStoreRuntimeException()
    {
        super();
    }

    /**
     * @param arg0
     */
    public MessageStoreRuntimeException(String arg0)
    {
        super(nls.getString(arg0));
    }

    /**
     * @param arg0
     */
    public MessageStoreRuntimeException(Throwable arg0)
    {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public MessageStoreRuntimeException(String arg0, Throwable arg1)
    {
        super(nls.getString(arg0), arg1);
    }

    /**
     * Provide a key and use formatted string
     * @param arg0
     * @param args
     */
    public MessageStoreRuntimeException(String arg0, Object[] args)
    {
        super(nls.getFormattedMessage(arg0, args, null));
    }

    /**
     * Provide a key and use formatted string
     * @param arg0
     * @param args
     */
    public MessageStoreRuntimeException(String arg0, Object[] args, Throwable exp)
    {
        super(nls.getFormattedMessage(arg0, args, null), exp);
    }
}

