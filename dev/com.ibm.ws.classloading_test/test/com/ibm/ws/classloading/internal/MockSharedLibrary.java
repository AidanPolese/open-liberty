/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.classloading.internal;

import static com.ibm.wsspi.classloading.ApiType.API;
import static com.ibm.wsspi.classloading.ApiType.SPEC;

import java.io.File;
import java.util.Collection;
import java.util.EnumSet;

import com.ibm.wsspi.artifact.ArtifactContainer;
import com.ibm.wsspi.classloading.ApiType;
import com.ibm.wsspi.config.Fileset;
import com.ibm.wsspi.library.Library;

class MockSharedLibrary implements Library {

    private final String id;
    private final ClassLoader loader;

    public MockSharedLibrary(String id, ClassLoader loader) {
        this.id = id;
        this.loader = loader;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Collection<Fileset> getFilesets() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClassLoader getClassLoader() {
        return loader;
    }

    @Override
    public EnumSet<ApiType> getApiTypeVisibility() {
        return EnumSet.of(API, SPEC);
    }

    @Override
    public Collection<File> getFiles() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<File> getFolders() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<ArtifactContainer> getContainers() {
        throw new UnsupportedOperationException();
    }
}