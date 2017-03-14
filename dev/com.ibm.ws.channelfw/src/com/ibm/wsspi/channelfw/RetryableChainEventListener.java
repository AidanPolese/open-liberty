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
// @(#) @(#) 1.4 CF/ws/code/channelfw/src/com/ibm/wsspi/channel/framework/RetryableChainEventListener.java, WAS.channelfw, CCX.CF 9/20/04 15:54:22 [5/11/05 12:46:41]
//
package com.ibm.wsspi.channelfw;

import com.ibm.websphere.channelfw.ChainData;

/**
 * This listener adds an additional feature beyond the basic ChainEventListener
 * to allow those that register to be notified about failed attempts to start
 * chains.
 */
public interface RetryableChainEventListener extends ChainEventListener {

    /**
     * This method is called when an attempted to start a chain fails.
     * 
     * @param chainData
     *            chain which failed to restart
     * @param attemptsMade
     *            number of attempts made so far to start the chain
     * @param attemptsLeft
     *            number of attempts remaining to start the chain before giving up.
     *            Attempts left may be -1, indicating an unlimited number of overall
     *            attempts.
     */
    void chainStartFailed(ChainData chainData, int attemptsMade, int attemptsLeft);
}
