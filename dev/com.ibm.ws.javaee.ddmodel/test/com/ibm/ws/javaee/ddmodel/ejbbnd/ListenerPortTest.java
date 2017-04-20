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
package com.ibm.ws.javaee.ddmodel.ejbbnd;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.ws.javaee.dd.ejbbnd.EJBJarBnd;
import com.ibm.ws.javaee.dd.ejbbnd.EnterpriseBean;
import com.ibm.ws.javaee.dd.ejbbnd.MessageDriven;

public class ListenerPortTest extends EJBJarBndTestBase {

    String listenerPortXML =
                    "<message-driven name=\"MessageDrivenBean1\"> \n" +
                                    "<listener-port name=\"lpName1\"/> \n " +
                                    "</message-driven>\n";

    @Test
    public void testListenerPortAttributeName() throws Exception {
        EJBJarBnd ejbJarBnd = getEJBJarBnd(EJBJarBndTestBase.ejbJarBnd11() + listenerPortXML + "</ejb-jar-bnd>");
        List<EnterpriseBean> mdBeans = ejbJarBnd.getEnterpriseBeans();

        Assert.assertEquals("Only expected 1 message driven bean", 1, mdBeans.size());
        MessageDriven bean0 = (MessageDriven) mdBeans.get(0);
        Assert.assertEquals(bean0.getName(), "MessageDrivenBean1", bean0.getName());
        Assert.assertNotNull("Listener port should not be null", bean0.getListenerPort());
        Assert.assertEquals("Incorrect listener port name: " + bean0.getListenerPort().getName(), "lpName1", bean0.getListenerPort().getName());
    }
}
