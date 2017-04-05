// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// 1.1 SERV1/ws/code/utils/src/com/ibm/ws/util/dopriv/GetThreadContextAccessorPrivileged.java, WAS.ejbcontainer, WASX.SERV1 9/6/07 14:24:46
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2007
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  GetThreadContextAccessorPrivileged.java
//
// Source File Description:
//
//     Returns a ThreadContextAccessor while in privileged mode.
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// d464232   EJB3      20070906 tkb      : Initial version
// --------- --------- -------- --------- -----------------------------------------
package com.ibm.ws.util.dopriv;

import java.security.PrivilegedAction;

import com.ibm.ws.util.ThreadContextAccessor;

/**
 * This class gets the ThreadContextAccessor while in privileged mode. Its purpose
 * is to eliminate the need to use an anonymous inner class in multiple modules
 * throughout the product, when the only privileged action required is to
 * get the ThreadContextAccessor. This reduces product footprint.
 */
public class GetThreadContextAccessorPrivileged implements PrivilegedAction {
    /**
     * Returns a ThreadContextAccessor implementation.
     * 
     * @return <code>oldClassLoader</code>
     */
    public Object run() {
        return ThreadContextAccessor.getThreadContextAccessor();
    }

}
