/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.metadata.ejb;

import com.ibm.tx.jta.embeddable.GlobalTransactionSettings;
import com.ibm.tx.jta.embeddable.LocalTransactionSettings;
import com.ibm.ws.resource.ResourceRefConfigList;

public class TestWCCMMetaData extends WCCMMetaData {
    @Override
    public ResourceRefConfigList createResRefList() {
        return null;
    }

    @Override
    public LocalTransactionSettings createLocalTransactionSettings() {
        return null;
    }

    @Override
    public GlobalTransactionSettings createGlobalTransactionSettings() {
        return null;
    }
}
