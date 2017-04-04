/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.authorization.jacc;

public class RoleInfo {
    private String roleName;
    private boolean isDenyAll;
    private boolean isPermitAll;

    public static RoleInfo DENY_ALL = new RoleInfo(null, true, false);
    public static RoleInfo PERMIT_ALL = new RoleInfo(null, false, true);

    private RoleInfo(String roleName, boolean isDenyAll, boolean isPermitAll) {
        this.roleName = roleName;
        this.isDenyAll = isDenyAll;
        this.isPermitAll = isPermitAll;
    }

    public RoleInfo(String roleName) {
        this.roleName = roleName;
        this.isDenyAll = false;
        this.isPermitAll = false;
    }

    public RoleInfo() {
        roleName = null;
        isDenyAll = false;
        isPermitAll = false;
    }
    
    public void setDenyAll() {
        roleName = null;
        isDenyAll = true;
        isPermitAll = false;
    }
    public void setPermitAll() {
        roleName = null;
        isDenyAll = false;
        isPermitAll = true;
    }

    public String getRoleName() {
        return roleName;
    }

    public boolean isDenyAll() {
        return isDenyAll;
    }

    public boolean isPermitAll() {
        return isPermitAll;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("role : " ).append(roleName).append(" DenyAll : ").append(isDenyAll).append(" PermitAll : ").append(isPermitAll);
        return buf.toString();
    }

}
