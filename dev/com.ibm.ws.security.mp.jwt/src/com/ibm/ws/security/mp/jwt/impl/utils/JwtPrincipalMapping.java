/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.security.mp.jwt.impl.utils;

import java.util.ArrayList;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.security.common.jwk.utils.JsonUtils;
import com.ibm.ws.security.mp.jwt.TraceConstants;
import com.ibm.ws.security.mp.jwt.impl.MicroProfileJwtConfigImpl;

/**
 *
 */
public class JwtPrincipalMapping {

    private static TraceComponent tc = Tr.register(JwtPrincipalMapping.class, TraceConstants.TRACE_GROUP, TraceConstants.MESSAGE_BUNDLE);
    //String realm = null;
    //String uniqueSecurityName = null;
    String userName = null;
    ArrayList<String> groupIds = null;

    public JwtPrincipalMapping(String jsonstr, String userAttr, String groupAttr, boolean mapToUr) {
        userName = getUserName(userAttr, jsonstr);
        if (userName == null) {
            return;
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "user name = ", userName);
        }
        if (!mapToUr) {
            populateGroupIds(jsonstr, groupAttr);
        }
    }

    public boolean isUserNameNull() {
        return (userName == null || userName.isEmpty());
    }

    public String getMappedUser() {
        return userName;
    }

    public ArrayList<String> getMappedGroups() {
        return groupIds;
    }

    private String getUserName(String userNameAttr, String jsonstr) {
        if (jsonstr != null && userNameAttr != null && !userNameAttr.isEmpty()) {
            Object user = getClaim(jsonstr, userNameAttr);
            setUserName(userNameAttr, user);
        }
        if (userName == null) {
            Tr.error(tc, "PRINCIPAL_MAPPING_MISSING_ATTR", new Object[] { userNameAttr, MicroProfileJwtConfigImpl.KEY_userNameAttribute });
        }
        return userName;
    }

    void setUserName(String userNameAttr, Object user) {
        if (user == null) {
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "Provided user name object is null. Current user name [" + userName + "] will not be changed");
            }
            return;
        }
        if (user instanceof String) {
            userName = (String) user;
        } else {
            Tr.error(tc, "PRINCIPAL_MAPPING_INCORRECT_CLAIM_TYPE", new Object[] { userNameAttr, MicroProfileJwtConfigImpl.KEY_userNameAttribute });
        }
    }

    void populateGroupIds(String jsonstr, String groupAttr) {
        Object groupClaim = null;
        if (groupAttr != null) {
            groupClaim = getClaim(jsonstr, groupAttr);
        }
        populateGroupIdsFromGroupClaim(groupClaim);
    }

    Object getClaim(String jsonstr, String claimAttr) {
        try {
            return JsonUtils.claimFromJsonObject(jsonstr, claimAttr);
        } catch (Exception e) {
            Tr.error(tc, "CANNOT_GET_CLAIM_FROM_JSON", new Object[] { claimAttr, e.getLocalizedMessage() });
        }
        return null;
    }

    void populateGroupIdsFromGroupClaim(Object groupClaim) {
        if (groupClaim == null) {
            return;
        }
        if (groupClaim instanceof ArrayList<?>) {
            setGroupIdArrayList(groupClaim);
        } else {
            setGroupClaimAsOnlyGroupId(groupClaim);
        }
        if (groupIds != null && TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "groups size = ", groupIds.size());
        }
    }

    @SuppressWarnings("unchecked")
    void setGroupIdArrayList(Object groupClaim) {
        groupIds = (ArrayList<String>) groupClaim;
    }

    void setGroupClaimAsOnlyGroupId(Object groupClaim) {
        try {
            String groupName = (String) groupClaim;
            groupIds = new ArrayList<String>();
            groupIds.add(groupName);
        } catch (ClassCastException cce) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "cannot get meaningful group due to CCE: " + cce.getMessage());
            }
        }
    }

}
