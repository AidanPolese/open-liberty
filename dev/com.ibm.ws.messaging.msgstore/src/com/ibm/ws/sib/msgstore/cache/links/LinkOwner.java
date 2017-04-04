package com.ibm.ws.sib.msgstore.cache.links;
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
 * Reason          Date     Origin   Description
 * --------------- -------- -------- ------------------------------------------
 *                 11/02/04 drphill  Original
 * 245531          17/02/05 drphill  Cached MessageStoreRef as performance enh.
 * SIB0112d.ms.2   28/06/07 gareth   MemMgmt: SpillDispatcher improvements - datastore
 * 463642          04/09/07 gareth   Revert to using spill limits
 * 484799          28/01/08 gareth   Take byte size into account in spill limits
 * 515543.2        08/07/08 gareth   Change runtime exceptions to caught exception
 * ============================================================================
 */

import com.ibm.ws.sib.msgstore.AbstractItem;
import com.ibm.ws.sib.msgstore.OutOfCacheSpace;
import com.ibm.ws.sib.msgstore.SevereMessageStoreException;
import com.ibm.ws.sib.msgstore.cache.statemodel.ListStatistics;
import com.ibm.ws.sib.msgstore.impl.MessageStoreImpl;
import com.ibm.ws.sib.msgstore.persistence.Persistable;

/**
 * Defines the responsibility of a link owning other links (ie a parent node 
 * in a tree of links).
 * Allows us to flatten the inheritance hierarchy.
 * @author DrPhill
 *
 */
public abstract class LinkOwner extends AbstractItemLink
{
    private MessageStoreImpl _messageStoreImpl = null;

    /**
     * @param item
     * @param owningStreamLink
     * @param persistable
     * @throws OutOfCacheSpace
     */
    public LinkOwner(AbstractItem item, LinkOwner owningStreamLink, Persistable persistable) throws OutOfCacheSpace 
    {
        super(item, owningStreamLink, persistable);
    }

    /**
     * @param owningStreamLink
     * @param persistable
     */
    public LinkOwner(LinkOwner owningStreamLink, Persistable persistable)
    {
        super(owningStreamLink, persistable);
    }

    /**
     * @param persistable
     * @param isRootLink
     */
    public LinkOwner(Persistable persistable, boolean isRootLink)
    {
        super(persistable, isRootLink);
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.cache.links.AbstractItemLink#getMessageStoreImpl()
     * 
     * overridden here to cache the value at each stream, saving a lot
     * of navigation up the tree to get it.
     */
    public MessageStoreImpl getMessageStoreImpl()
    {
        if (null == _messageStoreImpl)
        {
            // lazy initialise the reference. 
            _messageStoreImpl = super.getMessageStoreImpl();
        }
        return _messageStoreImpl;
    }

    public abstract void append(final AbstractItemLink link) throws SevereMessageStoreException;
    public abstract void checkSpillLimits();    // Defect 484799
    public abstract void eventWatermarkBreached() throws SevereMessageStoreException;
    public abstract ListStatistics getListStatistics();
    public abstract void linkAvailable(AbstractItemLink link) throws SevereMessageStoreException;

    /**
     * request the receiver to reload any owned items.
     * 
     * @return true if the items were reloaded in response to
     *         this call, false otherwise.
     */
    public abstract boolean loadOwnedLinks() throws SevereMessageStoreException;
    public abstract long nextSequence();
}
