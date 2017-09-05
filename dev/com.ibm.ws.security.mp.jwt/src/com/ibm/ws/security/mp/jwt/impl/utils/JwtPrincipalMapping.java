/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.mp.jwt.impl.utils;

import java.util.ArrayList;

import org.jose4j.lang.JoseException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.security.common.jwk.utils.JsonUtils;
import com.ibm.ws.security.mp.jwt.TraceConstants;

/**
 *
 */
public class JwtPrincipalMapping {

    /**
     *
     */

    private static TraceComponent tc = Tr.register(JwtPrincipalMapping.class, TraceConstants.TRACE_GROUP, TraceConstants.MESSAGE_BUNDLE);
    //String realm = null;
    //String uniqueSecurityName = null;
    String userName = null;
    ArrayList<String> groupIds = null;

    @SuppressWarnings("unchecked")
    public JwtPrincipalMapping(String jsonstr, String userAttr, String groupAttr, boolean mapToUr) {

        userName = getTheUserName(userAttr, jsonstr);
        if (userName != null) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "user name = ", userName);
            }
            //customCacheKey = userName + tokenString.hashCode();

            if (!mapToUr) {
                Object group = null;
                if (groupAttr != null) {
                    try {
                        group = JsonUtils.claimFromJsonObject(jsonstr, groupAttr);
                    } catch (JoseException e) {

                    }
                }
                if (group != null) {
                    if (group instanceof ArrayList<?>) {
                        groupIds = (ArrayList<String>) group;
                    } else { // try if there is a single string identified as group
                        try {
                            String groupName = (String) group;
                            groupIds = new ArrayList<String>();
                            groupIds.add(groupName);
                        } catch (ClassCastException cce) {
                            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                                Tr.debug(tc, "cannot get meaningful group due to CCE.");
                            }
                        }
                    }
                }
                if (groupIds != null && TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "groups size = ", groupIds.size());
                }
            }
        }
    }

    public boolean checkUserNameForNull() {
        if (userName == null || userName.isEmpty()) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "There is no principal");
            }
            return true;
        } else {
            return false;
        }
    }

    public String getMappedUser() {
        return userName;
    }

    public ArrayList<String> getMappedGroups() {
        return groupIds;
    }

    private String getTheUserName(String userNameAttr, String jsonstr) {
        if (jsonstr != null) {
            if (userNameAttr != null && !userNameAttr.isEmpty()) {
                Object user = null;
                try {
                    user = JsonUtils.claimFromJsonObject(jsonstr, userNameAttr);
                } catch (JoseException e) {

                }
                if (user != null) {
                    if (user instanceof String) {
                        userName = (String) user;
                    } else {
                        Tr.error(tc, "SUBJECT_MAPPING_INCORRECT_CLAIM_TYPE", new Object[] { userNameAttr, "userNameAttribute" });
                    }
                }
            }
        }
        if (userName == null) {
            Tr.error(tc, "SUBJECT_MAPPING_MISSING_ATTR", new Object[] { userNameAttr, "userNameAttribute" });
        }
        return userName;
    }
}
