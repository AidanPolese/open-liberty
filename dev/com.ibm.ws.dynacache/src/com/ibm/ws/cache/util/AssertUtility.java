// 1.2, 9/30/04
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.util;

public class AssertUtility {

    //---------------------------------------------------------------------
    // DynaCache Component assert checking - only call from within CTORs
    //---------------------------------------------------------------------
    static public boolean assertCheck(boolean assertRanOnce, Object clazz) {
        if ( !assertRanOnce ) {
            assert assertRanOnce = true;
            if ( assertRanOnce ) {
                System.out.println("A S S E R T S  A R E  A C T I V E  IN: "+clazz.getClass()+"@@"+clazz.hashCode() );
            }
        }
        return true;
    }


}


