/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2001, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.mdb;

import com.ibm.ejs.container.BeanO;
import com.ibm.ejs.container.BeanOFactory;
import com.ibm.ejs.container.EJSContainer;
import com.ibm.ejs.container.EJSHome;

/**
 * BeanOFactory that creates MessageDriven <code>BeanOs</code>. <p>
 */
public class CMMessageDrivenBeanOFactory
                extends BeanOFactory
{
    @Override
    protected BeanO newInstance(EJSContainer c, EJSHome h)
    {
        return new CMMessageDrivenBeanO(c, h);
    }
}
