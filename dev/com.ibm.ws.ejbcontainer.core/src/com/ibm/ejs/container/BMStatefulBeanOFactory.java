/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1998, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container;

/**
 * BeanOFactory that creates BM stateful session <code>BeanOs</code>. <p>
 */
public class BMStatefulBeanOFactory
                extends BeanOFactory
{
    @Override
    protected BeanO newInstance(EJSContainer c, EJSHome h)
    {
        return new BMStatefulBeanO(c, h);
    }
}
