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
import javax.ejb.RemoveException;

/**
 * The base class for wrapper proxies of local home views. This class must be
 * a subclass EJSLocalWrapper because internals expect that the home wrapper is
 * an EJBLocalObject, presumably for implementation convenience. Therefore,
 * this class uses the same rationale to extend EJSLocalWrapperProxy.
 * 
 * @see WrapperProxy
 */
public class EJSLocalHomeWrapperProxy
                extends EJSLocalWrapperProxy
                implements EJBLocalHome
{
    public EJSLocalHomeWrapperProxy(WrapperProxyState state)
    {
        super(state);
    }

    public void remove(Object primaryKey)
                    throws RemoveException
    {
        ((EJBLocalHome) EJSContainer.resolveWrapperProxy(this)).remove(primaryKey);
    }
}
