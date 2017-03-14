/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.timer;


/**
 * Static utility for getting quick approx time
 */
public class QuickApproxTime {
    /**
     * Get the time which is set according to the time interval.
     * 
     * @return time
     */
    public static long getApproxTime() {

        // Testing has shown that currentTimeMillis is now not such a performance drag, as it was years ago.
        // For 2Q 2015 we will go straight to currentTimeMillis.
        // In 3Q 2015, assuming no issues with this fix, we can get rid of the Approx Timer service altogether
        //QuickApproxTimeImpl impl = QuickApproxTimeImpl.instance.get();

        //if (impl == null) {
        return System.currentTimeMillis();
        //}

        //return impl.getApproxTime();

    }
}
