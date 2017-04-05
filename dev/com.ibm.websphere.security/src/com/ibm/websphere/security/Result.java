//  @(#) 1.6 SERV1/ws/code/security.sas/src/com/ibm/websphere/security/Result.java, WAS.security.sas, WASX.SERV1, nn1148.03 2/14/05 10:16:04 [12/4/11 15:43:06]
//  5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
//  All Rights Reserved * Licensed Materials - Property of IBM
//
//  DESCRIPTION:
//
//    This module is used by User Registries in WebSphere when calling the
//    getUsers and getGroups method. The user registries should use this
//    to set the list of users/groups and to indicate if there are more 
//    users/groups in the registry than requested
//
package com.ibm.websphere.security;

import java.util.List;

/**
 * This module is used by User Registries in WebSphere when calling the
 * getUsers and getGroups method. The user registries should use this
 * to set the list of users/groups and to indicate if there are more
 * users/groups in the registry than requested.
 * 
 * @ibm-spi
 */
public class Result implements java.io.Serializable {
    /**
     * Default constructor
     */
    public Result() {}

    /**
     * Returns the list of users/groups
     * 
     * @return the list of users/groups
     */
    public List getList() {
        return list;
    }

    /**
     * indicates if there are more users/groups in the registry
     */
    public boolean hasMore() {
        return more;
    }

    /**
     * Set the flag to indicate that there are more users/groups in the registry to true
     */
    public void setHasMore() {
        more = true;
    }

    /*
     * Set the list of user/groups
     * 
     * @param list list of users/groups
     */
    public void setList(List list) {
        this.list = list;
    }

    private boolean more = false;
    private List list;
    private static final long serialVersionUID = -9026260195868247308L; //@vj1: Take versioning into account if incompatible changes are made to this class

}
