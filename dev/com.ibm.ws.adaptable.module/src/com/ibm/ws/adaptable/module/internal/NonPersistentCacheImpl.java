/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.adaptable.module.internal;

import com.ibm.wsspi.adaptable.module.NonPersistentCache;
import com.ibm.wsspi.artifact.overlay.OverlayContainer;

class NonPersistentCacheImpl implements NonPersistentCache {
    private final OverlayContainer rootOverlay;
    private final String path;

    public NonPersistentCacheImpl(OverlayContainer rootOverlay, String path) {
        this.rootOverlay = rootOverlay;
        this.path = path;
    }

    @Override
    public void addToCache(Class<?> owner, Object data) {
        rootOverlay.addToNonPersistentCache(path, owner, data);
    }

    @Override
    public void removeFromCache(Class<?> owner) {
        rootOverlay.removeFromNonPersistentCache(path, owner);
    }

    @Override
    public Object getFromCache(Class<?> owner) {
        return rootOverlay.getFromNonPersistentCache(path, owner);
    }
}