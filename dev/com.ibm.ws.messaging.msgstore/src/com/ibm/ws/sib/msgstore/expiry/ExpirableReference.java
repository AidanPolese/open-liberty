package com.ibm.ws.sib.msgstore.expiry;
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
 *                 18/07/03 corrigk  Original
 * F 175362        02/09/03 corrigk  Expirable implementation
 * 306998.20       09/01/06 gareth   Add new guard condition to trace statements
 * 426133          26/03/07 gareth   Replace WeakReference with SoftReference
 * 515543.2        08/07/08 gareth   Change runtime exceptions to caught exception
 * ============================================================================
 */ 

import java.lang.ref.SoftReference;
import com.ibm.ws.sib.msgstore.AbstractItem;
import com.ibm.ws.sib.msgstore.MessageStoreConstants; 
import com.ibm.ws.sib.msgstore.SevereMessageStoreException;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * Defines the ExpirableReference which is a SoftReference to an
 * Item which contains an expiry time. ExpirableReferences are
 * used to populate the ExpiryIndex.
 */
public class ExpirableReference extends SoftReference
{
    private static TraceComponent tc = SibTr.register(ExpirableReference.class,
                                                      MessageStoreConstants.MSG_GROUP,
                                                      MessageStoreConstants.MSG_BUNDLE);
    private long expiryTime = 0;
    private long objectID = 0;

    /**
     * Constructor to create the ExpiryReference for the Item. Sets the expiry time to zero.
     * @param expirable The Item for which the ExpiryReference is required.
     * @throws MessageStoreRuntimeException 
     */ 
    public ExpirableReference(Expirable expirable) throws SevereMessageStoreException
    {
        super(expirable);

        if (expirable != null)
        {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>", "id="+expirable.expirableGetID());
    
            expiryTime = 0;
            objectID   = expirable.expirableGetID();
        }
        else
        {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>", "null");
        }

        if (objectID == AbstractItem.NO_ID)
        {
            // This item is not a member of a stream and therefore the ID is not unique.
        	if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "<init>");
            throw new SevereMessageStoreException("DUPLICATE_EXPIRABLE_SIMS2000");
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "<init>");
    }  

    /**
     * Return the expiry time.
     * @return the expiry time in milliseconds.
     */   
    public long getExpiryTime()
    {
        return expiryTime;
    }

    /**
     * Return the object ID.
     * @return the ID of the object.
     */
    public long getID()
    {
        return objectID;
    }

    /**
     * Set the expiry time.
     * @param expiryTime the expiry time in milliseconds.
     */
    public void setExpiryTime(long expiryTime)
    {
        this.expiryTime = expiryTime;
    }
}
