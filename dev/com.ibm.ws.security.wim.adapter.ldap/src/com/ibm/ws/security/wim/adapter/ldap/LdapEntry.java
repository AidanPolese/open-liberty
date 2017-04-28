/************** Begin Copyright - Do not add comments here **************
 *
 *
 * IBM Confidential OCO Source Material
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2012, 2013
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U. S. Copyright Office.
 *
 */
package com.ibm.ws.security.wim.adapter.ldap;

import javax.naming.directory.Attributes;

/**
 * The class which contains the returned information of a entity from the LDAP query.
 */
public class LdapEntry {

    private String iDN = null;
    private String iExtId = null;
    private String iUniqueName = null;
    private String iType = null;
    private Attributes iAttrs = null;
    private String iChangeType = null;

    /**
     *
     */
    public LdapEntry(String dn, String extId, String uniqueName, String type, Attributes attrs) {
        iDN = dn;
        iExtId = extId;
        iUniqueName = uniqueName;
        iType = type;
        iAttrs = attrs;
        iChangeType = null;
    }

    /**
     * @return Returns the iAttrs.
     */
    public Attributes getAttributes() {
        return iAttrs;
    }

    /**
     * @param iAttrs The iAttrs to set.
     */
    public void setAttributes(Attributes attrs) {
        this.iAttrs = attrs;
    }

    /**
     * @return Returns the iDN.
     */
    public String getDN() {
        return iDN;
    }

    /**
     * @param dn The iDN to set.
     */
    public void setDN(String dn) {
        iDN = dn;
    }

    /**
     * @return Returns the iExtId.
     */
    public String getExtId() {
        return iExtId;
    }

    /**
     * @param extId The iExtId to set.
     */
    public void setExtId(String extId) {
        iExtId = extId;
    }

    /**
     * @return Returns the iType.
     */
    public String getType() {
        return iType;
    }

    /**
     * @param type The iType to set.
     */
    public void setType(String type) {
        iType = type;
    }

    /**
     * @return Returns the iUniqueName.
     */
    public String getUniqueName() {
        return iUniqueName;
    }

    /**
     * @param uniqueName The iUniqueName to set.
     */
    public void setIUniqueName(String uniqueName) {
        iUniqueName = uniqueName;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("\nDN: ").append(iDN).append("  ");
        result.append("ExtId: ").append(iExtId).append("  ");
        result.append("UniqueName: ").append(iUniqueName).append("  ");
        result.append("Type: ").append(iType).append("\n");
        result.append("Attributes: ").append(iAttrs);
        return result.toString();
    }

    /*
     * Sets the change type for an entity.
     * Applicable during a search for changed entities.
     */
    public void setChangeType(String changeType) {
        this.iChangeType = changeType;
    }

    /*
     * Gets the change type for an entity.
     * Applicable during a search for changed entities.
     */
    public String getChangeType() {
        return this.iChangeType;
    }
}
