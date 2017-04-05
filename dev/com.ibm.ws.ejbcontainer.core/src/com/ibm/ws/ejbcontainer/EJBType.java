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

public enum EJBType
{
    SINGLETON_SESSION(InternalConstants.TYPE_SINGLETON_SESSION),
    STATEFUL_SESSION(InternalConstants.TYPE_STATEFUL_SESSION),
    STATELESS_SESSION(InternalConstants.TYPE_STATELESS_SESSION),

    BEAN_MANAGED_ENTITY(InternalConstants.TYPE_BEAN_MANAGED_ENTITY),
    CONTAINER_MANAGED_ENTITY(InternalConstants.TYPE_CONTAINER_MANAGED_ENTITY),

    MESSAGE_DRIVEN(InternalConstants.TYPE_MESSAGE_DRIVEN);

    private static final EJBType[] FOR_VALUE;

    static
    {
        EJBType[] values = values();

        int maxValue = 0;
        for (EJBType type : values)
        {
            maxValue = Math.max(maxValue, type.value());
        }

        FOR_VALUE = new EJBType[maxValue + 1];

        for (EJBType type : values)
        {
            FOR_VALUE[type.value()] = type;
        }
    }

    public static EJBType forValue(int value)
    {
        return FOR_VALUE[value];
    }

    private final int ivValue;

    EJBType(int value)
    {
        ivValue = value;
    }

    public int value()
    {
        return ivValue;
    }

    public boolean isSession()
    {
        return this == SINGLETON_SESSION ||
               this == STATEFUL_SESSION ||
               this == STATELESS_SESSION;
    }

    public boolean isEntity()
    {
        return this == BEAN_MANAGED_ENTITY ||
               this == CONTAINER_MANAGED_ENTITY;
    }

    public boolean isMessageDriven()
    {
        return this == MESSAGE_DRIVEN;
    }
}
