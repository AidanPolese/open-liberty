/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.runtime.metadata;

/**
 * Internal. This class is not intended to be used by clients of the metadata infrastructure.
 */
public class MetaDataSlotImpl implements MetaDataSlot {
    /**
     * The id for this slot, which is used to index an array on {@link MetaDataImpl}.
     */
    final int id;

    /**
     * True if the slot has been destroyed because the bundle that reserved the slot has been uninstalled.
     */
    volatile boolean destroyed;

    /**
     * The slot interface.
     */
    final Class<? extends MetaData> metadataIntf;

    /**
     * The owning MetaDataManager.
     */
    private final Object manager;

    /**
     * The owning bundle, for diagnostic purposes.
     */
    private final Object bundle;

    public MetaDataSlotImpl(int id, Class<? extends MetaData> metadataIntf, Object manager, Object bundle) {
        this.id = id;
        this.metadataIntf = metadataIntf;
        this.manager = manager;
        this.bundle = bundle;
    }

    @Override
    public String toString() {
        return super.toString() + "[" + metadataIntf.getSimpleName() + ":" + id + (destroyed ? " (destroyed)" : "") + ":" + bundle + ']';
    }

    public int getID() {
        return id;
    }

    public Object getManager() {
        return manager;
    }

    public synchronized void destroy() {
        destroyed = true;
    }

    public void destroy(MetaDataImpl metaData) {
        metaData.setMetaData(this, null, true);
    }
}
