package com.ibm.ws.sib.msgstore;
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
 * Reason          Date      Origin        Description
 * --------------- --------  -----------   ----------------------------------------
 *                 16102003  van Leersum   Original
 * 290610          09082005  schofiel      Remove deprecated and unused methods
 * ============================================================================
 */

/**
 * This class provides an interface to query the current counts for a stream. 
 */
public interface Statistics {
    /**
     * @return number of messages currently in the process of being added.
     */
    public long getAddingItemCount();

    /**
     * @return number of messages currently available.
     */
    public long getAvailableItemCount();

    /**
     * @return number of messages currently in the process of being expired.
     */
    public long getExpiringItemCount();

    /**
     * @return number of messages currently locked.
     */
    public long getLockedItemCount();

    /**
     * @return number of messages currently in the process of being removed.
     */
    public long getRemovingItemCount();


    /**
     * @return long the size of the stream in total number of items.
     * This is the value that is used to determine watermark breaches.
     * Only items directly contained within the stream will contribute to the count,
     * not contained reference streams, items streams, or items contained within
     * contained streams.
     * The size will be the maximal calculated for all counted items - ie those in
     * all states which imply containment within the stream.
     * 
     */
    public long getTotalItemCount();

    /**
     * @return number of messages currently not available.
     */
    public long getUnavailableItemCount();

    /**
     * @return number of messages currently in the process of being updated.
     */
    public long getUpdatingItemCount();


}
