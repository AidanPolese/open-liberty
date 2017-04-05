/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.sib.admin.internal;


import com.ibm.ws.sib.admin.SIBus;

/**
 *This class Implements the SIBus interface
 */
public class SIBusImpl implements SIBus {

    String uuid = null;
    String name = JsAdminConstants.DEFAULTBUS;

    /** {@inheritDoc} */
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return name;
    }

    /** Nothing is set as we dont want user to change the name of the bus */
    @Override
    public void setName(String value) {
    // TODO Auto-generated method stub

    }

    /** {@inheritDoc} */
    @Override
    public void setUuid(String value) {
    // TODO Auto-generated method stub

    }

    /** {@inheritDoc} */
    @Override
    public String getUuid() {
        // TODO Auto-generated method stub
        return uuid;
    }

    /**
     * All the other mock methods of JsEObject
     */

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void setDescription(String value) {
    // TODO Auto-generated method stub

    }

  
}
