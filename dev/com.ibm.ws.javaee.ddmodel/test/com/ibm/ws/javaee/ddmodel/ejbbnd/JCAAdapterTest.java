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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.ws.javaee.dd.ejbbnd.EJBJarBnd;
import com.ibm.ws.javaee.dd.ejbbnd.EnterpriseBean;
import com.ibm.ws.javaee.dd.ejbbnd.MessageDriven;

public class JCAAdapterTest extends EJBJarBndTestBase {

    String jcaAdapterXML1 = "<message-driven name=\"MessageDrivenBean1\"> \n" +
                            "<jca-adapter activation-spec-binding-name=\"activationSpecBindingName1\"/> \n " +
                            "</message-driven>\n";

    @Test
    public void testJCAAdapterAttributeActivationSpecBindingName() throws Exception {
        EJBJarBnd ejbJarBnd = getEJBJarBnd(EJBJarBndTestBase.ejbJarBnd11() + jcaAdapterXML1 + "</ejb-jar-bnd>");
        List<EnterpriseBean> mdBeans = ejbJarBnd.getEnterpriseBeans();
        Assert.assertEquals("Only expected 1 message driven bean", 1, mdBeans.size());
        MessageDriven bean0 = (MessageDriven) mdBeans.get(0);
        Assert.assertEquals("MessageDrivenBean1", bean0.getName());
        Assert.assertNull(bean0.getListenerPort());
        Assert.assertNotNull(bean0.getJCAAdapter());
        Assert.assertNotNull(bean0.getJCAAdapter().getActivationSpecBindingName());
        Assert.assertEquals("activationSpecBindingName1", bean0.getJCAAdapter().getActivationSpecBindingName());
    }

    @Test
    public void testJCAAdapterAttributeActivationSpecBindingNameXMI() throws Exception {
        EJBJarBnd ejbJarBnd = parseEJBJarBinding(ejbJarBinding("") +
                                                 "<ejbBindings xmi:type=\"ejbbnd:MessageDrivenBeanBinding\" activationSpecJndiName=\"activationSpecBindingName1\">" +
                                                 "  <enterpriseBean xmi:type=\"ejb:MessageDriven\" href=\"" + getEJBJarPath() + "#md0\"/>" +
                                                 "</ejbBindings>" +
                                                 "</ejbbnd:EJBJarBinding>",
                                                 parseEJBJar(ejbJar21() +
                                                             "  <enterprise-beans>" +
                                                             "    <message-driven id=\"md0\">" +
                                                             "      <ejb-name>MessageDrivenBean1</ejb-name>" +
                                                             "    </message-driven>" +
                                                             "  </enterprise-beans>" +
                                                             "</ejb-jar>"));
        List<EnterpriseBean> mdBeans = ejbJarBnd.getEnterpriseBeans();
        Assert.assertEquals("Only expected 1 message driven bean", 1, mdBeans.size());
        MessageDriven bean0 = (MessageDriven) mdBeans.get(0);
        Assert.assertEquals("MessageDrivenBean1", bean0.getName());
        Assert.assertNull(bean0.getListenerPort());
        Assert.assertNotNull(bean0.getJCAAdapter());
        Assert.assertNotNull(bean0.getJCAAdapter().getActivationSpecBindingName());
        Assert.assertEquals("activationSpecBindingName1", bean0.getJCAAdapter().getActivationSpecBindingName());
    }

    String jcaAdapterXML2 = "<message-driven name=\"MessageDrivenBean2\"> \n" +
                            "<jca-adapter activation-spec-binding-name=\"activationSpecBindingName1\" \n " +
                            "activation-spec-auth-alias=\"authAlias2\"/> \n " +
                            "</message-driven>\n";

    @Test
    public void testJCAAdapterAttributeActivationSpecAuthAlias() throws Exception {
        EJBJarBnd ejbJarBnd = getEJBJarBnd(EJBJarBndTestBase.ejbJarBnd10() + jcaAdapterXML2 + "</ejb-jar-bnd>");
        List<EnterpriseBean> mdBeans = ejbJarBnd.getEnterpriseBeans();

        Assert.assertEquals(1, mdBeans.size());
        MessageDriven bean0 = (MessageDriven) mdBeans.get(0);
        Assert.assertEquals("MessageDrivenBean2", bean0.getName());
        Assert.assertNull(bean0.getListenerPort());
        Assert.assertNotNull(bean0.getJCAAdapter());
        Assert.assertEquals("authAlias2", bean0.getJCAAdapter().getActivationSpecAuthAlias());
    }

    @Test
    public void testJCAAdapterAttributeActivationSpecAuthAliasXMI() throws Exception {
        EJBJarBnd ejbJarBnd = parseEJBJarBinding(ejbJarBinding("") +
                                                 "<ejbBindings xmi:type=\"ejbbnd:MessageDrivenBeanBinding\" activationSpecAuthAlias=\"authAlias2\">" +
                                                 "  <enterpriseBean xmi:type=\"ejb:MessageDriven\" href=\"" + getEJBJarPath() + "#md0\"/>" +
                                                 "</ejbBindings>" +
                                                 "</ejbbnd:EJBJarBinding>",
                                                 parseEJBJar(ejbJar21() +
                                                             "  <enterprise-beans>" +
                                                             "    <message-driven id=\"md0\">" +
                                                             "      <ejb-name>MessageDrivenBean2</ejb-name>" +
                                                             "    </message-driven>" +
                                                             "  </enterprise-beans>" +
                                                             "</ejb-jar>"));
        List<EnterpriseBean> mdBeans = ejbJarBnd.getEnterpriseBeans();
        Assert.assertEquals(1, mdBeans.size());
        MessageDriven bean0 = (MessageDriven) mdBeans.get(0);
        Assert.assertEquals("MessageDrivenBean2", bean0.getName());
        Assert.assertNull(bean0.getListenerPort());
        Assert.assertNotNull(bean0.getJCAAdapter());
        Assert.assertEquals("authAlias2", bean0.getJCAAdapter().getActivationSpecAuthAlias());
    }

    String jcaAdapterXML3 = "<message-driven name=\"MessageDrivenBean3\"> \n" +
                            "<jca-adapter activation-spec-binding-name=\"activationSpecBindingName3\" destination-binding-name=\"destinationBindingName\"/> \n " +
                            "</message-driven>\n";

    @Test
    public void testJCAAdapterAttributeDestinationBindingName() throws Exception {
        EJBJarBnd ejbJarBnd = getEJBJarBnd(EJBJarBndTestBase.ejbJarBnd10() + jcaAdapterXML3 + "</ejb-jar-bnd>");
        List<EnterpriseBean> mdBeans = ejbJarBnd.getEnterpriseBeans();
        Assert.assertEquals("Only expected 1 message driven bean", 1, mdBeans.size());
        MessageDriven bean0 = (MessageDriven) mdBeans.get(0);
        Assert.assertEquals("MessageDrivenBean3", bean0.getName());
        Assert.assertNull(bean0.getListenerPort());
        Assert.assertNotNull(bean0.getJCAAdapter());
        Assert.assertNotNull(bean0.getJCAAdapter().getActivationSpecBindingName());
        Assert.assertEquals("destinationBindingName", bean0.getJCAAdapter().getDestinationBindingName());
    }

    @Test
    public void testJCAAdapterAttributeDestinationBindingNameXMI() throws Exception {
        EJBJarBnd ejbJarBnd = parseEJBJarBinding(ejbJarBinding("") +
                                                 "<ejbBindings xmi:type=\"ejbbnd:MessageDrivenBeanBinding\" " +
                                                 "    activationSpecJndiName=\"activationSpecBindingName3\" " +
                                                 "    destinationJndiName=\"destinationBindingName\">" +
                                                 "  <enterpriseBean xmi:type=\"ejb:MessageDriven\" href=\"" + getEJBJarPath() + "#md0\"/>" +
                                                 "</ejbBindings>" +
                                                 "</ejbbnd:EJBJarBinding>",
                                                 parseEJBJar(ejbJar21() +
                                                             "  <enterprise-beans>" +
                                                             "    <message-driven id=\"md0\">" +
                                                             "      <ejb-name>MessageDrivenBean3</ejb-name>" +
                                                             "    </message-driven>" +
                                                             "  </enterprise-beans>" +
                                                             "</ejb-jar>"));
        List<EnterpriseBean> mdBeans = ejbJarBnd.getEnterpriseBeans();
        Assert.assertEquals("Only expected 1 message driven bean", 1, mdBeans.size());
        MessageDriven bean0 = (MessageDriven) mdBeans.get(0);
        Assert.assertEquals("MessageDrivenBean3", bean0.getName());
        Assert.assertNull(bean0.getListenerPort());
        Assert.assertNotNull(bean0.getJCAAdapter());
        Assert.assertNotNull(bean0.getJCAAdapter().getActivationSpecBindingName());
        Assert.assertEquals("destinationBindingName", bean0.getJCAAdapter().getDestinationBindingName());
    }
}
