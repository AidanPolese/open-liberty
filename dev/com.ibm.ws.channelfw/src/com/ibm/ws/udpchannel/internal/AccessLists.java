//-------------------------------------------------------------------------------
//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//Change History:
//
//Change ID     Author    Abstract
//---------     --------  -------------------------------------------------------
//d306341		mjohn256  Add RAS logging support to UDP Channel.
//-------------------------------------------------------------------------------
package com.ibm.ws.udpchannel.internal;

import java.net.Inet6Address;
import java.net.InetAddress;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

public class AccessLists {
    protected UDPFilterList excludeAccess = null;
    protected UDPFilterList includeAccess = null;

    private static final TraceComponent tc = Tr.register(AccessLists.class, UDPMessages.TR_GROUP, UDPMessages.TR_MSGS);

    /**
     * Constructor.
     * 
     * @param _excludeAccess
     * @param _includeAccess
     */
    public AccessLists(UDPFilterList _excludeAccess, UDPFilterList _includeAccess) {
        excludeAccess = _excludeAccess;
        includeAccess = _includeAccess;
    }

    protected static AccessLists getInstance(UDPChannelConfiguration config) {
        AccessLists retVal = null;
        boolean haveAList = false;
        UDPFilterList excludeAccess = null;
        UDPFilterList includeAccess = null;

        String sExclude[] = config.getAddressExcludeList();
        excludeAccess = new UDPFilterList();
        if (sExclude != null) {
            excludeAccess.buildData(sExclude, false);
            excludeAccess.setActive(true);
            haveAList = true;
        }

        String[] sInclude = config.getAddressIncludeList();
        includeAccess = new UDPFilterList();
        if (sInclude != null) {
            includeAccess.buildData(sInclude, false);
            includeAccess.setActive(true);
            haveAList = true;
        }

        if (haveAList) {
            retVal = new AccessLists(excludeAccess, includeAccess);
        }

        return retVal;
    }

    public boolean accessDenied(InetAddress remoteAddr) {

        if (includeAccess.getActive()) {
            boolean allOk = false;

            if (remoteAddr instanceof Inet6Address) {
                if (includeAccess.findInList6(remoteAddr.getAddress())) {
                    allOk = true;
                }
            } else {
                if (includeAccess.findInList(remoteAddr.getAddress())) {
                    allOk = true;
                }
            }

            if (allOk == false) {
                if (tc.isEventEnabled())
                    Tr.event(tc, "Address and host name not in include list, address: " + remoteAddr.getHostAddress() + " host name: " + remoteAddr.getHostName());
                return true;
            }
        }

        if (excludeAccess.getActive()) {

            if (remoteAddr instanceof Inet6Address) {
                if (excludeAccess.findInList6(remoteAddr.getAddress())) {
                    // close the excluded socket connection
                    if (tc.isEventEnabled())
                        Tr.event(tc, "Address (IPv6) in exclude list, address: " + remoteAddr.getHostAddress());
                    return true;
                }
            } else {
                if (excludeAccess.findInList(remoteAddr.getAddress())) {
                    // close the excluded socket connection
                    if (tc.isEventEnabled())
                        Tr.event(tc, "Address in exclude list, address: " + remoteAddr.getHostAddress());
                    return true;
                }
            }

        } // end if(there are excludes to check)
        return false;
    }

}
