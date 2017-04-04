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
 * Reason          Date     Origin   Description
 * --------------- -------- -------- ------------------------------------------
 *                 03/02/03 drphill  Original
 * 321391          07/11/05 schofiel Remove STRICT_ENFORCED_IN_MESSAGESTORE
 * 306998.19       09/01/06 gareth   Add new guard condition to trace statements
 * 515543.2        08/07/08 gareth   Change runtime exceptions to caught exception
 * ============================================================================
 */

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 *  
 * This class implements reference counting.  The reference count indicates
 * how many references to this item are currently committed on item streams.
 * The reference count is incremented when a reference to the item is added 
 * to an {@link ItemStream}, and decremented when a reference to the item is
 * removed from an {@link ItemStream}. Adjustments are only made to the reference
 * count when the transactions controlling the addition or removal are 
 * committed.
 */
public class Item extends AbstractItem
{
    private static TraceComponent tc = SibTr.register(Item.class, 
                                                      MessageStoreConstants.MSG_GROUP, 
                                                      MessageStoreConstants.MSG_BUNDLE); 

    /**
     * A public no-argument constructor so that items may be dynamically
     * created.  All subclasses should provide such a subclass.
     */
    public Item() {}

    /**
     * @return The {@link ItemStream} in which the receiver is stored, or null
     * if none.
     * @throws SevereMessageStoreException 
     */
    public final ItemStream getItemStream() throws SevereMessageStoreException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
        {
            SibTr.entry(this, tc, "getItemStream");
        }
        ItemStream itemStream = null;
        ItemMembership m = ((ItemMembership)_getMembership());
        if (null == m)
        {
            throw new NotInMessageStore();
        }
        if (null != m)
        {
            itemStream = m.getOwningItemStream();
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
        {
            SibTr.exit(this, tc, "getItemStream", itemStream);
        }
        return itemStream;
    }

    /**
     * @return reference count if the item is in the store, negative value
     * if item is not in the store.
     * @throws NotInMessageStore 
     */
    public final int getReferenceCount() throws NotInMessageStore
    {
        int refCount = -1;
        ItemMembership m = ((ItemMembership)_getMembership());
        if (null == m)
        {
            throw new NotInMessageStore();
        }
        if (null != m)
        {
            refCount = m.getReferenceCount();
        }
        return refCount;
    }

    /**
     * @return true if the receiver is an instance of 
     * {@link Item},  false otherwise. Default
     * implementation returns false.
     * Overridden here to return true.
     */
    public final boolean isItem()
    {
        return true;
    }

    /**
     * A trigger method, called to indicate that there are 
     * now no references to the item.  The default implementation does nothing, 
     * and subclasses should overide the method in order to make use of the trigger.
     * This method should only be called by message store code.
     * <p>Note that this method is not guaranteed to be called after an outage,
     * but is provided as an opportunity to remove the item no longer referenced
     * if it is appropriate so to do.</p>
     */
    public void itemReferencesDroppedToZero()
    {
    }

}
