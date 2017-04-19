/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.remote.client.server.internal;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

import javax.ejb.EJBException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.ibm.websphere.ejbcontainer.EJBFactory;
import com.ibm.ws.clientcontainer.remote.common.ClientEJBFactory;
import com.ibm.ws.container.service.naming.RemoteObjectInstance;
import com.ibm.ws.container.service.naming.RemoteObjectInstanceFactory;
import com.ibm.ws.ejbcontainer.osgi.EJBStubClassGenerator;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;

@Component(service = ClientEJBFactory.class)
public class ClientEJBFactoryImpl implements ClientEJBFactory {

    private EJBFactory ejbFactory;
    private EJBStubClassGenerator stubGenerator;
    private RemoteObjectInstanceFactory roif;

    @Reference
    protected void setEJBFactory(EJBFactory ejbFactory) {
        this.ejbFactory = ejbFactory;
    }

    protected void unsetEJBFactory(EJBFactory ejbFactory) {
        this.ejbFactory = null;
    }

    @Reference
    protected void setEJBStubClassGenerator(EJBStubClassGenerator ejbStubClassGenerator) {
        this.stubGenerator = ejbStubClassGenerator;
    }

    protected void unsetEJBStubClassGenerator(EJBStubClassGenerator ejbStubClassGenerator) {
        this.stubGenerator = null;
    }

    @Reference
    protected void setRemoteObjectInstanceFactory(RemoteObjectInstanceFactory roif) {
        this.roif = roif;
    }

    protected void unsetRemoteObjectInstanceFactory(RemoteObjectInstanceFactory roif) {
        this.roif = null;
    }

    @Override
    public Set<String> getRmicCompatibleClasses(String appName) throws RemoteException {
        return stubGenerator.getRMICCompatibleClasses(appName);
    }

    @Override
    @FFDCIgnore({ RemoteException.class, EJBException.class })
    public RemoteObjectInstance create(String application, String module, String beanName, String interfaceName) throws RemoteException {
        try {
            Remote stub = (Remote) ejbFactory.create(application, module, beanName, interfaceName);
            return roif.create(stub, interfaceName);
        } catch (RemoteException rex) {
            throw rex;
        } catch (EJBException ejbex) {
            throw new RemoteException(ejbex.getClass().getName() + " : " + ejbex.getMessage());
        } catch (Throwable ex) {
            throw new RemoteException(ex.getClass().getName() + " : " + ex.getMessage(), ex);
        }
    }

    @Override
    @FFDCIgnore({ RemoteException.class, EJBException.class })
    public RemoteObjectInstance create(String application, String beanName, String interfaceName) throws RemoteException {
        try {
            Remote stub = (Remote) ejbFactory.create(application, beanName, interfaceName);
            return roif.create(stub, interfaceName);
        } catch (RemoteException rex) {
            throw rex;
        } catch (EJBException ejbex) {
            throw new RemoteException(ejbex.getClass().getName() + " : " + ejbex.getMessage());
        } catch (Throwable ex) {
            throw new RemoteException(ex.getClass().getName() + " : " + ex.getMessage(), ex);
        }
    }

    @Override
    @FFDCIgnore({ RemoteException.class, EJBException.class })
    public RemoteObjectInstance findByBeanName(String application, String beanName, String interfaceName) throws RemoteException {
        try {
            Remote stub = (Remote) ejbFactory.findByBeanName(application, beanName, interfaceName);
            return roif.create(stub, interfaceName);
        } catch (RemoteException rex) {
            throw rex;
        } catch (EJBException ejbex) {
            throw new RemoteException(ejbex.getClass().getName() + " : " + ejbex.getMessage());
        } catch (Throwable ex) {
            throw new RemoteException(ex.getClass().getName() + " : " + ex.getMessage(), ex);
        }
    }

    @Override
    @FFDCIgnore({ RemoteException.class, EJBException.class })
    public RemoteObjectInstance findByInterface(String application, String interfaceName) throws RemoteException {
        try {
            Remote stub = (Remote) ejbFactory.findByInterface(application, interfaceName);
            return roif.create(stub, interfaceName);
        } catch (RemoteException rex) {
            throw rex;
        } catch (EJBException ejbex) {
            throw new RemoteException(ejbex.getClass().getName() + " : " + ejbex.getMessage());
        } catch (Throwable ex) {
            throw new RemoteException(ex.getClass().getName() + " : " + ex.getMessage(), ex);
        }
    }
}
