/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.cdi.impl.weld;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jboss.weld.probe.ProbeExtension;

import com.ibm.websphere.csi.J2EEName;
import com.ibm.ws.cdi.CDIException;
import com.ibm.ws.cdi.impl.ResourceInjectionBag;
import com.ibm.ws.cdi.interfaces.AbstractCDIArchive;
import com.ibm.ws.cdi.interfaces.Application;
import com.ibm.ws.cdi.interfaces.ArchiveType;
import com.ibm.ws.cdi.interfaces.CDIArchive;
import com.ibm.ws.cdi.interfaces.CDIRuntime;
import com.ibm.ws.cdi.interfaces.ExtensionArchive;
import com.ibm.ws.cdi.interfaces.Resource;
import com.ibm.ws.runtime.metadata.MetaData;

/**
 * This is the probe extension.
 */
public class ProbeExtensionArchive extends AbstractCDIArchive implements ExtensionArchive {

    private final Class<?> probeClass;
    private final Application application;

    public ProbeExtensionArchive(CDIRuntime cdiRuntime, Application application) {

        super(ProbeExtension.class.getName(),
              cdiRuntime);
        this.application = application;
        this.probeClass = ProbeExtension.class;
    }

    /** {@inheritDoc} */
    @Override
    public J2EEName getJ2EEName() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public ArchiveType getType() {
        return ArchiveType.RUNTIME_EXTENSION;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws CDIException
     */
    @Override
    public ClassLoader getClassLoader() throws CDIException {
        ClassLoader classLoader = this.probeClass.getClassLoader();
        if (classLoader == null) {
            classLoader = application.getClassLoader();
        }
        return classLoader;
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> getClassNames() {
        return Collections.singleton(probeClass.getName());
    }

    /** {@inheritDoc} */
    @Override
    public boolean isModule() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public Application getApplication() {
        return application;
    }

    /** {@inheritDoc} */
    @Override
    public String getClientModuleMainClass() throws CDIException {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getInjectionClassList() throws CDIException {
        return Collections.emptyList();
    }

    /** {@inheritDoc} */
    @Override
    public MetaData getMetaData() throws CDIException {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public ResourceInjectionBag getAllBindings() throws CDIException {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getClientAppCallbackHandlerName() throws CDIException {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> getExtensionClasses() {
        return Collections.singleton(this.probeClass.getName());
    }

    /** {@inheritDoc} */
    @Override
    public String getPath() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Set<CDIArchive> getModuleLibraryArchives() throws CDIException {
        return Collections.emptySet();
    }

    /** {@inheritDoc} */
    @Override
    public Resource getResource(String path) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> getBeanDefiningAnnotations() throws CDIException {
        return Collections.emptySet();
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> getAnnotatedClasses(Set<String> annotations) throws CDIException {
        return Collections.emptySet();
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> getExtraClasses() {
    	return Collections.emptySet();
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> getExtraBeanDefiningAnnotations() {
    	return Collections.emptySet();
    }

    /** {@inheritDoc} */
    @Override
    public boolean applicationBDAsVisible() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExtClassesOnly() {
        return true;
    }

}
