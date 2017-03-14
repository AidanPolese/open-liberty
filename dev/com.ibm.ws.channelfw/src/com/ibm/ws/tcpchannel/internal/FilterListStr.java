//* ===========================================================================
//*
//* IBM SDK, Java(tm) 2 Technology Edition, v5.0
//* (C) Copyright IBM Corp. 2005, 2006
//*
//* The source code for this program is not published or otherwise divested of
//* its trade secrets, irrespective of what has been deposited with the U.S.
//* Copyright office.
//*
//* ===========================================================================
//
// Change History:
// Date     UserId      Defect          Description
// --------------------------------------------------------------------------------
// 09/21/04 gilgen      233448          Add copyright statement and change history.

package com.ibm.ws.tcpchannel.internal;

/**
 * 
 * Interface for building an access list and for determining if an
 * address is in an access list.
 */
public interface FilterListStr {

    /**
     * Build the address tree from a string array which contains valid
     * URL addresses.
     * 
     * @param data
     *            list of URL address which are
     *            to be used to create a new address tree. An address may start with
     *            a wildcard (for example: "*.Rest.Of.Address"),
     *            otherwise wildcards may not be used.
     * @return boolean
     */
    boolean buildData(String[] data);

    /**
     * Determine if an address is in the address tree
     * 
     * @param address
     *            address to look for
     * @return true if this address is found in the address tree, false if
     *         it is not.
     */
    boolean findInList(String address);

    /**
     * Sets if the address list is now active or dormant
     * 
     * @param value
     *            true if address list is to be active, else false
     */
    void setActive(boolean value);

    /**
     * Gets if the address list is now active or dormant
     * 
     * @return boolean
     */
    boolean getActive();

}
