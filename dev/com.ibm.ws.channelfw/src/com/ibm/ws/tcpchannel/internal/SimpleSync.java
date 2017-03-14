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
 * Simple sync block object.
 */
public class SimpleSync {

    boolean notifyOn = false;

    protected void simpleWait() {
        synchronized (this) {
            if (notifyOn) {
                // return right away if notify is outstanding
                notifyOn = false;
                return;
            }
            // else wait
            try {
                this.wait();
                notifyOn = false;
            } catch (InterruptedException x) {
                // do nothing
            }
        } // end-sync
    }

    protected void simpleNotify() {
        synchronized (this) {
            notifyOn = true;
            this.notify();
        }
    }

}
