package com.ibm.ws.sib.msgstore.deliverydelay;

/*
 * 
 * 
 * ============================================================================
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 * ============================================================================
 */

import java.lang.ref.SoftReference;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.msgstore.AbstractItem;
import com.ibm.ws.sib.msgstore.MessageStoreConstants;
import com.ibm.ws.sib.msgstore.MessageStoreRuntimeException;
import com.ibm.ws.sib.msgstore.SevereMessageStoreException;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * Defines the DeliveryDelayableReference which is a SoftReference to an
 * Item which contains an deliveryDelay time. DeliveryDelayableReference are
 * used to populate the DeliveryDelayIndex.
 */
public class DeliveryDelayableReference extends SoftReference
{
    private static TraceComponent tc = SibTr.register(DeliveryDelayableReference.class,
                                                      MessageStoreConstants.MSG_GROUP,
                                                      MessageStoreConstants.MSG_BUNDLE);
    private long deliveryDelayTime = 0;
    private long objectID = 0;

    /**
     * Constructor to create the DeliveryDelayableReference for the Item. Sets the deliveryDelay time to zero.
     * 
     * @param deliveryDelayable The Item for which the DeliveryDelayableReference is required.
     * @throws MessageStoreRuntimeException
     */
    public DeliveryDelayableReference(DeliveryDelayable deliveryDelayable) throws SevereMessageStoreException
    {
        super(deliveryDelayable);

        if (deliveryDelayable != null)
        {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                SibTr.entry(this, tc, "<init>", "id=" + deliveryDelayable.deliveryDelayableGetID());

            deliveryDelayTime = 0;
            objectID = deliveryDelayable.deliveryDelayableGetID();
        }
        else
        {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                SibTr.entry(this, tc, "<init>", "null");
        }

        if (objectID == AbstractItem.NO_ID)
        {
            // This item is not a member of a stream and therefore the ID is not unique.
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                SibTr.exit(this, tc, "<init>");
            throw new SevereMessageStoreException("DUPLICATE_DELIVERYDELAYABLE_SIMS2010");
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(this, tc, "<init>");
    }

    /**
     * Return the delivery delay time.
     * 
     * @return the delivery delay time in milliseconds.
     */
    public long getDeliveryDelayTime()
    {
        return deliveryDelayTime;
    }

    /**
     * Return the object ID.
     * 
     * @return the ID of the object.
     */
    public long getID()
    {
        return objectID;
    }

    /**
     * Set the delivery delay time.
     * 
     * @param deliveryDelayTime the DeliveryDelay time in milliseconds.
     */
    public void setDeliveryDelayTime(long deliveryDelayTime)
    {
        this.deliveryDelayTime = deliveryDelayTime;
    }
}
