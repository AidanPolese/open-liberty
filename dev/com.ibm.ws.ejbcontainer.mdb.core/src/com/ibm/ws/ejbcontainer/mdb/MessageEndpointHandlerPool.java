/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.mdb;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;
import com.ibm.ws.ejbcontainer.util.PoolDiscardStrategy;

/**
 * This class implements the PoolDiscardStrategy interface to handle
 */
public class MessageEndpointHandlerPool implements PoolDiscardStrategy
{
    private BaseMessageEndpointFactory ivMessageEnpointHandlerFactory = null;

    private static final TraceComponent tc =
                    Tr.register(MessageEndpointHandlerPool.class,
                                "EJBContainer",
                                "com.ibm.ejs.container.container");

    /**
     * Default Constructor for a MessageEndpointHandlerPool object.
     * The initialize method must be called to initialize the message
     * endpoint handler factory object to handle discard.
     */
    public MessageEndpointHandlerPool(BaseMessageEndpointFactory messageEnpointHandlerFactory)
    {
        ivMessageEnpointHandlerFactory = messageEnpointHandlerFactory;
    }

    /**
     * If the PoolManager for ivInvocationHandlerPool discards
     * an instance from free pool since it has not been used for
     * some period of time, then this method will be called to
     * indicate an instance was discarded. This method is called
     * for each instance that is discarded.
     */
    //LI2110.56 - added entire method.
    @Override
    public void discard(Object o)
    {
        final boolean isTraceOn = TraceComponent.isAnyTracingEnabled();
        if (isTraceOn && tc.isEntryEnabled())
            Tr.entry(tc, "MEF.discard");

        ivMessageEnpointHandlerFactory.discard();

        MessageEndpointBase meh = (MessageEndpointBase) o;
        MessageEndpointBase.discard(meh);

        if (isTraceOn && tc.isEntryEnabled())
            Tr.exit(tc, "MEF.discard");

    }

}
