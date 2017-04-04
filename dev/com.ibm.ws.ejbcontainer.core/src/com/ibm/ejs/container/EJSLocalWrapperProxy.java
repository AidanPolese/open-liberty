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

import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.RemoveException;

import com.ibm.ejs.util.Util;

/**
 * The base class for wrapper proxies of local component views. This class
 * must be a subclass of EJSLocalWrapper due to the signature of {@link EJSHome#createWrapper_Local}.
 * 
 * @see WrapperProxy
 */
public class EJSLocalWrapperProxy
                extends EJSLocalWrapper
                implements WrapperProxy
{
    volatile WrapperProxyState ivState;

    public EJSLocalWrapperProxy(WrapperProxyState state)
    {
        ivState = state;
    }

    @Override
    public String toString()
    {
        return Util.identity(this) + '(' + ivState + ')';
    }

    @Override
    public int hashCode()
    {
        return ivState.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof EJSLocalWrapperProxy &&
               ivState.equals(((EJSLocalWrapperProxy) o).ivState);
    }

    @Override
    public EJBLocalHome getEJBLocalHome()
    {
        return ((EJBLocalObject) EJSContainer.resolveWrapperProxy(this)).getEJBLocalHome();
    }

    @Override
    public Object getPrimaryKey()
    {
        return ((EJBLocalObject) EJSContainer.resolveWrapperProxy(this)).getPrimaryKey();
    }

    @Override
    public void remove()
                    throws RemoveException
    {
        ((EJBLocalObject) EJSContainer.resolveWrapperProxy(this)).remove();
    }

    @Override
    public boolean isIdentical(EJBLocalObject o)
    {
        return equals(o);
    }
}
