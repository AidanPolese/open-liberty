/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.mock;

import javax.naming.NamingException;

import com.ibm.websphere.csi.J2EEName;
import com.ibm.websphere.csi.J2EENameFactory;
import com.ibm.ws.container.service.metadata.internal.J2EENameFactoryImpl;
import com.ibm.ws.container.service.naming.NamingConstants.JavaColonNamespace;
import com.ibm.ws.ejbcontainer.osgi.internal.naming.EJBBinding;
import com.ibm.ws.ejbcontainer.osgi.internal.naming.EJBJavaColonNamingHelper;
import com.ibm.ws.runtime.metadata.ApplicationMetaData;
import com.ibm.ws.runtime.metadata.ComponentMetaData;
import com.ibm.ws.runtime.metadata.MetaDataSlotImpl;
import com.ibm.ws.runtime.metadata.ModuleMetaData;

public class MockEJBJavaColonNamingHelper extends EJBJavaColonNamingHelper {

    private MockComponentMetaData cmd;
    private ModuleMetaData mmd;
    private ApplicationMetaData amd;

    private J2EEName targetJ2eeName;

    private final boolean createCMD;
    private boolean testingException;

    public MockEJBJavaColonNamingHelper(boolean cmd) {
        super();
        this.createCMD = cmd;
        this.testingException = false;
        this.mmdSlot = new MetaDataSlotImpl(0, ModuleMetaData.class, null, null);
        this.amdSlot = new MetaDataSlotImpl(0, ApplicationMetaData.class, null, null);
    }

    public MockEJBJavaColonNamingHelper(boolean cmd, boolean exceptionTest) {
        super();
        this.createCMD = cmd;
        this.testingException = exceptionTest;

    }

    public void setTestingException(boolean testIt) {
        this.testingException = testIt;
    }

    @Override
    protected Object processJavaColon(EJBBinding bindings, JavaColonNamespace jndiType, String jndiName) throws NamingException {
        try {
            Object o = super.processJavaColon(bindings, jndiType, jndiName);
            return o;
        } catch (NamingException ne) {
            if (testingException) {
                throw ne; // rethrow any NamingException created
            }
        } catch (Exception e) {
            // catch exception since cannot instanciate in test
        }
        return "object";
    }

    @Override
    protected ComponentMetaData getComponentMetaData(JavaColonNamespace namespace, String lookupName) throws NamingException {

        if (createCMD) {
            return getCMD();
        }
        return super.getComponentMetaData(namespace, lookupName);
    }

    @Override
    protected J2EEName getJ2EEName(EJBBinding binding) {
        return getTargetEJBJ2EEName();
    }

    public ComponentMetaData getCMD() {
        if (cmd == null) {
            J2EENameFactory nameFactory = new J2EENameFactoryImpl();
            J2EEName name = nameFactory.create("app", "mod", "ejb");
            amd = new MockApplicationMetaData(name);
            mmd = new MockModuleMetaData(name, amd);
            cmd = new MockComponentMetaData(name, mmd);
        }
        return cmd;
    }

    public ModuleMetaData getMMD() {
        if (mmd == null) {
            getCMD();
        }
        return mmd;
    }

    public J2EEName getTargetEJBJ2EEName() {
        if (targetJ2eeName == null) {
            J2EENameFactory nameFactory = new J2EENameFactoryImpl();
            targetJ2eeName = nameFactory.create("ejb_app", "ejb_mod", "ejb_comp");
        }
        return targetJ2eeName;
    }
}
