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
package com.ibm.wsspi.adaptable.module;

/**
 *
 */
public interface MaskedPathEntry {
    /**
     * Hides the adapted entry.<p>
     * Applies to both overlaid, and original Entries. <br>
     * Applies even if Entry is added via overlay after mask invocation.
     */
    public void mask();

    /**
     * UnHides the adapted entry previously hidden via 'mask'.
     * <br>
     * Has no effect if path is not masked.
     */
    public void unMask();

    /**
     * Query if the adapted entry is currently masked via 'mask'.
     * 
     * @return true if masked, false otherwise.
     */
    public boolean isMasked();
}
