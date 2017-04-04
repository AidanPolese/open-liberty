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
 * Reason          Date     Origin   Description
 * --------------- -------- -------- ------------------------------------------
 * 175099          18/07/03 corrigk  Original 
 * 175362          29/08/03 corrigk  Expirable implementation
 * 181731          04/11/03 corrigk  Cache Expirer extensions for M5
 * 183455          20/11/03 corrigk  Non-Cache Expirer implementation 
 * 179365.3        01/12/03 corrigk  Expiry callback needs transaction
 * SIB0002.ms.1    28/07/05 schofiel Changes for remote MQ subordinate resources
 * 515543.2        08/07/08 gareth   Change runtime exceptions to caught exception
 * ============================================================================
 */ 

import com.ibm.ws.sib.msgstore.SevereMessageStoreException;
import com.ibm.ws.sib.msgstore.transactions.impl.PersistentTransaction;


/** 
 * Defines the behaviour of items which may expire from the
 * message store. That is, they are defined to have an expiry time, after
 * which, they may be removed by the Expirer (the Expiry Daemon).
 */
public interface Expirable
{
    /**
     * Return the time at which the expirable should expire.
     * @return the expiry time.
     */
    public long expirableGetExpiryTime(); 

    /**
     * Return the unique ID of the expirable object.
     * @return the object ID.
     */
    public long expirableGetID();

    /**
     * Return true if the Expirable is in the message store. This method will be called
     * by the expirer to decide whether to add the expirable into the expiry index. Expirables
     * which have already been deleted are then ignored.
     * @return true if the Expirable is in the store.
     */ 
    public boolean expirableIsInStore();

    /**
     * Invoked by the Expirer during expiry processing to instruct the associated object
     * to delete itself. If successful (the method returns true), then the Expirer will 
     * remove the expirable reference from the expiry index. If the method returns
     * false, then the Expirer will keep the expirable reference in its index and process
     * it again on the next cycle. 
     * @param tran the transaction under which the item is being
     *               expired
     * @return true if the object has deleted itself.
     */
    public boolean expirableExpire(PersistentTransaction tran) throws SevereMessageStoreException;      // 179365.3
}
