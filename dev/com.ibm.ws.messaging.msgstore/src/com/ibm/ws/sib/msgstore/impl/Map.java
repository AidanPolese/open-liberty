/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason          Date    Origin       Description
 * --------------- ------  --------     --------------------------------------------
 * 223743          130804  van Leersum  Original
 * ============================================================================
 */
package com.ibm.ws.sib.msgstore.impl;

import java.io.IOException;

import com.ibm.ws.sib.msgstore.cache.links.AbstractItemLink;
import com.ibm.ws.sib.utils.ras.FormattedWriter;

/**
 * This interface created so we can easily swap between the various implementations
 * of map in MessageStoreImpl.
 * This interface can be discarded when we decide which implementation to use.
 * @author DrPhill
 */
interface Map {
    /**
     * used in MessageStoreImpl.findById
     * @param key
     */
    public abstract AbstractItemLink get(final long key);
    /**
     * used in MessageStoreImpl.register
     * @param key
     * @param value
     */
    public abstract void put(final long key, final AbstractItemLink value);
    /**
     * used in MessageStoreImpl.unregister
     * @param key
     */
    public abstract AbstractItemLink remove(final long key);
    public abstract void clear();
    
    /**
     * @param writer
     */
    public abstract void xmlWriteOn(FormattedWriter writer) throws IOException ;
}
