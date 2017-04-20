/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.jitdeploy;

import javax.rmi.CORBA.Tie;

import org.omg.PortableServer.Servant;

import com.ibm.ejs.container.EJBConfigurationException;

public abstract class AbstractJIT_TieTest extends AbstractTieTestBase {
    @Override
    protected int[] getRMICCompatible() {
        return JITDEPLOY_RMIC_COMPATIBLE;
    }

    protected abstract boolean isPortableServer();

    @Override
    protected Class<?> defineTieClass(Class<?> targetClass, Class<?> remoteInterface, int rmicCompatible, TestClassLoader loader) {
        try {
            return JITDeploy.generate_Tie(loader, targetClass.getName(), remoteInterface, null, new ClassDefiner(), rmicCompatible, isPortableServer());
        } catch (EJBConfigurationException ex) {
            throw new IllegalStateException(ex);
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    protected String[] ids(Tie tie) {
        return isPortableServer() ? ((Servant) tie)._all_interfaces(null, null) : super.ids(tie);
    }
}
