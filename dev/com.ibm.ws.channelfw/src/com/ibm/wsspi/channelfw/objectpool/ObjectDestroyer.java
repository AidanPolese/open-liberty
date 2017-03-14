//* ===========================================================================
//*
//* IBM SDK, Java(tm) 2 Technology Edition, v5.0
//* (C) Copyright IBM Corp. 2007
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
// 12/12/06 bgower      PK36998         Support native storage manager for ByteBuffer allocations

package com.ibm.wsspi.channelfw.objectpool;

/**
 * ObjectDestroyer interface for cleaning up objects that can be pooled.
 * 
 */
public interface ObjectDestroyer {

    /**
     * Destroy the input object.
     * 
     * @param obj
     */
    void destroy(Object obj);

}
