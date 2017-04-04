/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.cdi.liberty;

import java.util.Set;

import com.ibm.ws.cdi.CDIException;
import com.ibm.ws.cdi.interfaces.ArchiveType;
import com.ibm.ws.cdi.interfaces.ExtensionArchive;

public class ExtensionArchiveImpl extends CDIArchiveImpl implements ExtensionArchive {

    private final ExtensionContainerInfo extensionContainerInfo;

    public ExtensionArchiveImpl(ExtensionContainerInfo extensionContainerInfo,
                                       RuntimeFactory factory) throws CDIException {
        super(null, extensionContainerInfo, ArchiveType.RUNTIME_EXTENSION, extensionContainerInfo.getClassLoader(), factory);
        this.extensionContainerInfo = extensionContainerInfo;
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> getExtraClasses() {
        return extensionContainerInfo.getExtraClasses();
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> getExtraBeanDefiningAnnotations() {
        return extensionContainerInfo.getExtraBeanDefiningAnnotations();
    }

    /** {@inheritDoc} */
    @Override
    public boolean applicationBDAsVisible() {
        return extensionContainerInfo.applicationBDAsVisible();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExtClassesOnly() {
        return extensionContainerInfo.isExtClassesOnly();
    }
}
