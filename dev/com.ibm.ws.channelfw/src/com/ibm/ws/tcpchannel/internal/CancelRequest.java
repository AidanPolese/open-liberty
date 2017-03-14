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

import java.nio.channels.SelectionKey;

/**
 * Wrapper class for handling an attempt to cancel an outstanding
 * IO request.
 */
public class CancelRequest {
    static final int Reset = 0;
    static final int Ready_To_Cancel = 1;
    static final int Ready_To_Signal_Done = 2;

    /** Target key of the cancel attempt */
    protected SelectionKey key = null;
    /** Current state of the cancel attempt */
    protected int state = Reset;

    /**
     * Cosntructor.
     * 
     * @param targetKey
     */
    protected CancelRequest(SelectionKey targetKey) {
        if (null == targetKey) {
            throw new IllegalArgumentException("Null key");
        }
        this.key = targetKey;
        this.state = Ready_To_Cancel;
    }

}
