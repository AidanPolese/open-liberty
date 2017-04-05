/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container;

/**
 * The base class for wrapper proxies of local business interface.
 * 
 * @see WrapperProxy
 */
public class BusinessLocalWrapperProxy
                implements WrapperProxy
{
    volatile WrapperProxyState ivState;

    public BusinessLocalWrapperProxy(WrapperProxyState state)
    {
        ivState = state;
    }

    @Override
    public String toString()
    {
        return super.toString() + '(' + ivState + ')';
    }

    @Override
    public int hashCode()
    {
        return ivState.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof BusinessLocalWrapperProxy &&
               ivState.equals(((BusinessLocalWrapperProxy) o).ivState);
    }
}
