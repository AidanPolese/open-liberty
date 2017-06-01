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

import java.io.Serializable;

import javax.transaction.UserTransaction;

import com.ibm.ejs.container.EJSContainer;
import com.ibm.ejs.container.EJSHome;
import com.ibm.ejs.container.MessageDrivenBeanO;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * CMMessageDrivenBeanO manages the lifecycle of a
 * single MessageDrivenBean instance for the case of a MessageDriven
 * defined to ContainerManaged transactions, it also provides the
 * MessageDrivenConext implementation for the enterprise bean. <p>
 **/
public class CMMessageDrivenBeanO
                extends MessageDrivenBeanO
                implements Serializable
{
    private static final long serialVersionUID = -5840982304444508479L;

    private static final TraceComponent tc = Tr.register(CMMessageDrivenBeanO.class,
                                                         "EJBContainer",
                                                         "com.ibm.ejs.container.container");

    /**
     * Create new <code>CMMessageDrivenBeanO</code>. <p>
     */
    public CMMessageDrivenBeanO(EJSContainer c, EJSHome h)
    {
        super(c, h);
    }

    /**
     * getUserTransaction - This method is unavailable to message-driven beans
     * with container-managed transaction demarcation.
     */
    @Override
    public synchronized UserTransaction getUserTransaction() //d116376
    {
        Tr.error(tc, "METHOD_NOT_ALLOWED_CNTR0047E",
                 "CMMessageDrivenBeanO.getUserTransaction()");
        throw new IllegalStateException("Method Not Allowed Exception: See Message-drive Bean Component " +
                                        "Contract section of the applicable EJB Specification.");
    }

}
