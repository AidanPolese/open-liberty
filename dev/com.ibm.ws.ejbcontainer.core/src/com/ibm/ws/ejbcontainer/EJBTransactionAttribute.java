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
package com.ibm.ws.ejbcontainer;

public enum EJBTransactionAttribute
{
    BEAN_MANAGED(InternalConstants.TX_BEAN_MANAGED),

    NOT_SUPPORTED(InternalConstants.TX_NOT_SUPPORTED),
    SUPPORTS(InternalConstants.TX_SUPPORTS),
    REQUIRED(InternalConstants.TX_REQUIRED),
    REQUIRES_NEW(InternalConstants.TX_REQUIRES_NEW),
    MANDATORY(InternalConstants.TX_MANDATORY),
    NEVER(InternalConstants.TX_NEVER);

    private static final EJBTransactionAttribute[] FOR_VALUE;

    static
    {
        EJBTransactionAttribute[] values = EJBTransactionAttribute.values();

        int maxValue = 0;
        for (EJBTransactionAttribute type : values)
        {
            maxValue = Math.max(maxValue, type.value());
        }

        FOR_VALUE = new EJBTransactionAttribute[maxValue + 1];

        for (EJBTransactionAttribute type : values)
        {
            FOR_VALUE[type.value()] = type;
        }
    }

    public static EJBTransactionAttribute forValue(int value)
    {
        return FOR_VALUE[value];
    }

    private final int ivValue;

    EJBTransactionAttribute(int value)
    {
        ivValue = value;
    }

    public int value()
    {
        return ivValue;
    }
}
