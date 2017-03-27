/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2016
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.security.thread.zos.hooks.internal;

import org.eclipse.osgi.internal.hookregistry.BundleFileWrapperFactoryHook;
import org.eclipse.osgi.internal.hookregistry.HookConfigurator;
import org.eclipse.osgi.internal.hookregistry.HookRegistry;
import org.eclipse.osgi.storage.BundleInfo.Generation;
import org.eclipse.osgi.storage.bundlefile.BundleFile;
import org.eclipse.osgi.storage.bundlefile.BundleFileWrapper;

public class ThreadIdentityBundleFileWrapperFactoryHook implements BundleFileWrapperFactoryHook, HookConfigurator {
    @Override
    public void addHooks(HookRegistry hookRegistry) {
        hookRegistry.addBundleFileWrapperFactoryHook(this);
    }

    @Override
    public BundleFileWrapper wrapBundleFile(final BundleFile bundleFile, final Generation generation, final boolean base) {
        return new ThreadIdentityBundleFileWrapper(bundleFile);
    }
}