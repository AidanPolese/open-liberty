/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1999
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container.activator;

import com.ibm.ejs.container.BeanId;

/**
 * A <code>MasterKey</code> is a specialized <code>TransactionKey</code>
 * used to lookup the master instance of a BeanO in the cache. The
 * master instance of a BeanO is identified by its <code>BeanId</code>
 * and a null <code>ContainerTx</code>.
 * <p>
 * 
 */

class MasterKey extends TransactionKey
{

    /**
     * Create new <code>MasterKey</code> instance associated with
     * given bean id.
     */

    MasterKey(BeanId id) {

        super(null, id);

    } // MasterKey

    public String toString() {

        return "MasterKey(" + id + ")";

    } // toString

} // MasterKey

