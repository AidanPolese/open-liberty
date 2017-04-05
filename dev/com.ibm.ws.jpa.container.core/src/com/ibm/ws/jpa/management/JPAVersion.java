/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jpa.management;

public enum JPAVersion {
    JPA20(6, "2.0"),
    JPA21(7, "2.1");

    private final int jeeSpecLevel;
    private final String versionStr;

    private JPAVersion(int jeeSpecLevel, String versionStr) {
        this.jeeSpecLevel = jeeSpecLevel;
        this.versionStr = versionStr;
    }

    public int getJeeSpecLevel() {
        return jeeSpecLevel;
    }

    public String getVersionStr() {
        return versionStr;
    }

    public boolean greaterThan(JPAVersion jpaVersionObj) {
        if (jpaVersionObj == null) {
            return false;
        }

        return jeeSpecLevel > jpaVersionObj.getJeeSpecLevel();
    }

    public boolean greaterThanOrEquals(JPAVersion jpaVersionObj) {
        if (jpaVersionObj == null) {
            return false;
        }

        return jeeSpecLevel >= jpaVersionObj.getJeeSpecLevel();
    }

    public boolean lesserThan(JPAVersion jpaVersionObj) {
        if (jpaVersionObj == null) {
            return false;
        }

        return jeeSpecLevel < jpaVersionObj.getJeeSpecLevel();
    }

    public boolean lesserThanOrEquals(JPAVersion jpaVersionObj) {
        if (jpaVersionObj == null) {
            return false;
        }

        return jeeSpecLevel <= jpaVersionObj.getJeeSpecLevel();
    }

}