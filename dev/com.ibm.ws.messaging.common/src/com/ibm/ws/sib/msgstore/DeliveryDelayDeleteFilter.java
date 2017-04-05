/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.sib.msgstore;

/**
 * Prior to delivery delay feature when the destination is deleted,
 * the AsyncDeletionThread internally passes null as the filter and
 * used to delete only the available messages which are not expired.
 * 
 * But with the introduction of delivery delay feature items
 * locked for delivery delay also must be deleted
 * or reallocated to exception destination.
 * 
 * This filter is just used as a marker filter
 */
public class DeliveryDelayDeleteFilter implements Filter {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.msgstore.Filter#filterMatches(com.ibm.ws.sib.msgstore.AbstractItem)
     */
    @Override
    public boolean filterMatches(AbstractItem abstractItem) throws MessageStoreException {
        // TODO Auto-generated method stub
        return true;
    }

}
