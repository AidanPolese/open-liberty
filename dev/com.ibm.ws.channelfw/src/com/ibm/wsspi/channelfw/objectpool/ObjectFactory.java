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

package com.ibm.wsspi.channelfw.objectpool;

/**
 * ObjectFactory interface for creating objects that can be pooled.
 * 
 */
public interface ObjectFactory {

    /**
     * Create an entry for the pool
     * 
     * @return Object
     */
    Object create();

}
