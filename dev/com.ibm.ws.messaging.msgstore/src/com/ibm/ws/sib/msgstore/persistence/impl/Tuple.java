package com.ibm.ws.sib.msgstore.persistence.impl;
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
 *  Reason         Date     Origin   Description
 * --------------- -------- -------- ------------------------------------------
 *                 13/07/04 pradine  Original
 * 229942          08/09/04 pradine  Remove obsolete code
 * 247513          14/01/05 schofiel Improve spilling performance
 * 496154          22/04/08 gareth   Improve spilling performance
 * 515543.2        08/07/08 gareth   Change runtime exceptions to caught exception
 * ============================================================================
 */

import com.ibm.ws.sib.msgstore.SevereMessageStoreException;
import com.ibm.ws.sib.msgstore.persistence.Persistable;

/**
 * The methods of this interface are only of importance to this particular
 * implementation of a persistent message store. Therefore they are brought
 * together here instead of on the {@link Persistable} interface.
 * 
 * @see Persistable
 */
public interface Tuple extends Persistable
{
    /**
     * Indicates that the persistence code has accepted a request to write
     * the persistent representation of the object. Since there may be
     * several such requests outstanding, care must be taken to ensure that
     * the current persistent representation matches the object's current data.
     */
    public void persistableOperationBegun() throws SevereMessageStoreException;

    /**
     * Indicates that the persistence code has completed a request to write
     * the persistent representation of the object. Since there may be
     * several such requests outstanding, care must be taken to ensure that
     * the current persistent representation matches the object's current data.
     * <p>When {@link #persistableOperationBegun}
     * has been called at least once more than the number of calls to
     * {@link #persistableOperationCancelled()} and the number of calls to
     * {@link #persistableOperationBegun()} matches the sum of the number of calls to 
     * {@link #persistableOperationCompleted()} and
     * {@link #persistableOperationCancelled()}, the current persistent representation
     * of the object is consistent with the object's current data.
     */
    public void persistableOperationCompleted() throws SevereMessageStoreException;

    /**
     * Indicates that the persistence code has cancelled a request to write
     * the persistent representation of the object. Since there may be
     * several such requests outstanding, care must be taken to ensure that
     * the current persistent representation matches the object's current data.
     * <p>When {@link #persistableOperationBegun}
     * has been called at least once more than the number of calls to
     * {@link #persistableOperationCancelled()} and the number of calls to
     * {@link #persistableOperationBegun()} matches the sum of the number of calls to 
     * {@link #persistableOperationCompleted()} and
     * {@link #persistableOperationCancelled()}, the current persistent representation
     * of the object is consistent with the object's current data.
     */
    public void persistableOperationCancelled() throws SevereMessageStoreException;

    /**
     * Queries whether a persistent representation of the object was created.
     * This can be used to determine whether there's a persistent representation
     * to be deleted.
     * <p>This method returns true when {@link #persistableOperationCompleted()}
     * has been called at least once.
     */
    public boolean persistableRepresentationWasCreated();

    // Defect 496154
    /**
     * Returns the number of outstanding operations currently being
     * carried out on this persistable instance. This can be used by
     * the spill diapatcher to determine when it has found all 
     * cancellable tasks related to this persistable.
     * 
     * @return The difference between the number of operations begun and the number completed/cancelled
     */
    public int persistableOperationsOutstanding();

    /**
     * Sets the table id for the table in which a stream stores its
     * {@link com.ibm.ws.sib.msgstore.AbstractItem#STORE_ALWAYS} and
     * {@link com.ibm.ws.sib.msgstore.AbstractItem#STORE_EVENTUALLY}
     * tuples.
     * 
     * @param permanentTableId
     */
    public void setPermanentTableId(int permanentTableId);

    /**
     * Returns the table id for the table in which a stream stores its
     * {@link com.ibm.ws.sib.msgstore.AbstractItem#STORE_ALWAYS} and
     * {@link com.ibm.ws.sib.msgstore.AbstractItem#STORE_EVENTUALLY}
     * tuples.
     * 
     * @return Item Table id
     */
    public int getPermanentTableId();

    /**
     * Sets the table id for the table in which a stream stores its
     * {@link com.ibm.ws.sib.msgstore.AbstractItem#STORE_MAYBE}
     * tuples.
     * 
     * @param temporaryTableId the table id
     */
    public void setTemporaryTableId(int temporaryTableId);

    /**
     * Returns the table id for the table in which a stream stores its
     * {@link com.ibm.ws.sib.msgstore.AbstractItem#STORE_MAYBE}
     * tuples.
     * 
     * @return the table id
     */
    public int getTemporaryTableId();

    /**
     * Sets the unique id of the java class name of the message object.
     * 
     * @param itemClassId the class name
     * @see com.ibm.ws.sib.msgstore.persistence.impl.ClassmapTable
     */
    public void setItemClassId(int itemClassId);

    /**
     * Returns the unique id of the java class name of the message object.
     * 
     * @return the unique id
     * @see com.ibm.ws.sib.msgstore.persistence.impl.ClassmapTable
     */
    public int getItemClassId();
}
