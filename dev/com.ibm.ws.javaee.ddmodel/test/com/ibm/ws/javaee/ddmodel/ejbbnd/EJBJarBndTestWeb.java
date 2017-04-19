/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmodel.ejbbnd;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.ws.javaee.dd.ejbbnd.EJBJarBnd;

/**
 * Repeat a few tests for a ejb binding in a web module.
 */
public class EJBJarBndTestWeb extends EJBJarBndTestBaseWeb {

    @Test
    public void testGetVersionID() throws Exception {
        Assert.assertEquals("1.0", parse(ejbJarBnd10() + "</ejb-jar-bnd>").getVersion());
        Assert.assertEquals("1.1", parse(ejbJarBnd11() + "</ejb-jar-bnd>").getVersion());

    }

    @Test
    public void testEmptyEnterpriseBeans() throws Exception {
        Assert.assertNotNull("Enterprise bean list should not be null.", parse(ejbJarBnd10() + "</ejb-jar-bnd>").getEnterpriseBeans());
        Assert.assertEquals("Enterprise bean list should be empty.", 0, parse(ejbJarBnd10() + "</ejb-jar-bnd>").getEnterpriseBeans().size());
    }

    @Test
    public void testEmptyMessageDestinations() throws Exception {
        Assert.assertNotNull("MessageDestinations list should not be null.", parse(ejbJarBnd10() + "</ejb-jar-bnd>").getMessageDestinations());
        Assert.assertEquals("MessageDestinations list should be empty.", 0, parse(ejbJarBnd10() + "</ejb-jar-bnd>").getMessageDestinations().size());
    }

    @Test
    public void testEmptyInterceptorsList() throws Exception {
        Assert.assertNotNull("Interceptor list should not be null.", parse(ejbJarBnd10() + "</ejb-jar-bnd>").getInterceptors());
        Assert.assertEquals("Interceptor list should be empty.", 0, parse(ejbJarBnd10() + "</ejb-jar-bnd>").getInterceptors().size());
    }

    @Test
    public void testEmptyXMI() throws Exception {
        EJBJarBnd ejbJarBnd = parseEJBJarBinding(ejbJarBinding("") + "</ejbbnd:EJBJarBinding>",
                                                 parseEJBJar(ejbJar21() + "</ejb-jar>"));
        Assert.assertNotNull("Enterprise bean list should not be null.", ejbJarBnd.getEnterpriseBeans());
        Assert.assertEquals("Enterprise bean list should be empty.", 0, ejbJarBnd.getEnterpriseBeans().size());
        Assert.assertNotNull("MessageDestinations list should not be null.", ejbJarBnd.getMessageDestinations());
        Assert.assertEquals("MessageDestinations list should be empty.", 0, ejbJarBnd.getMessageDestinations().size());
        Assert.assertNotNull("Interceptor list should not be null.", ejbJarBnd.getInterceptors());
        Assert.assertEquals("Interceptor list should be empty.", 0, ejbJarBnd.getInterceptors().size());
    }
}
