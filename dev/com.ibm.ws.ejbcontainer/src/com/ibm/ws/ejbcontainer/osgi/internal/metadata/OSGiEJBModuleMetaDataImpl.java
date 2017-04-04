/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.osgi.internal.metadata;

import java.util.Collection;

import org.osgi.framework.ServiceRegistration;

import com.ibm.ejs.csi.EJBApplicationMetaData;
import com.ibm.ejs.csi.EJBModuleMetaDataImpl;
import com.ibm.ws.ejbcontainer.runtime.NameSpaceBinder;

public class OSGiEJBModuleMetaDataImpl extends EJBModuleMetaDataImpl {
    private final boolean systemModule;
    public ClassLoader ivContextClassLoader;
    public ServiceRegistration<?> mbeanServiceReg;
    public NameSpaceBinder<?> systemModuleNameSpaceBinder;

    public OSGiEJBModuleMetaDataImpl(int slotCnt, EJBApplicationMetaData ejbAMD, boolean systemModule) {
        super(slotCnt, ejbAMD);
        this.systemModule = systemModule;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Collection<OSGiBeanMetaData> getOSGiBeanMetaDatas() {
        return (Collection) ivBeanMetaDatas.values();
    }

    @Override
    public boolean isEJBDeployed() {
        // Modules are never ejbdeploy'ed on Liberty.
        return false;
    }

    @Override
    public int getRMICCompatible() {
        // If the modules are ejbdeploy'ed on tWAS, then use maximum RMIC
        // compatibility since ejbdeploy will use rmic.  Similarly, system
        // modules are internally EJB 3 modules, but they are used to implement
        // modules that are ejbdeploy'ed on tWAS, so we also need to use maximum
        // RMIC compatibility for them. Use the default RMIC compatibility for
        // all other modules.
        return super.isEJBDeployed() || systemModule ? -1 : super.getRMICCompatible();
    }

    public boolean isSystemModule() {
        return systemModule;
    }
}
