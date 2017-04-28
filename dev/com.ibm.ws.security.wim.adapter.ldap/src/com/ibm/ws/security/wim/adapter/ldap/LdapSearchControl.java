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

import java.util.List;

import javax.naming.directory.SearchControls;

public class LdapSearchControl {

    private String[] iBases = null;
    private String iFilter = null;
    private int iCountLimit = 0;
    private int iTimeLimit = 0;
    private List<String> iPropNames = null;
    private List<String> iEntityTypes = null;
    private int iScope = SearchControls.SUBTREE_SCOPE;

    /**
     *
     */
    public LdapSearchControl(String[] bases, List<String> entityTypes, String filter, List<String> propNames, int countLimit,
                             int timeLimit) {
        iBases = bases;
        iEntityTypes = entityTypes;
        iFilter = filter;
        iPropNames = propNames;
        iCountLimit = countLimit;
        iTimeLimit = timeLimit;
    }

    /**
     * @return Returns the iBases.
     */
    public String[] getBases() {
        return iBases;
    }

    /**
     * @param bases The iBases to set.
     */
    public void setBases(String[] bases) {
        iBases = bases;
    }

    /**
     * @return Returns the iCountLimit.
     */
    public int getCountLimit() {
        return iCountLimit;
    }

    /**
     * @param countLimit The iCountLimit to set.
     */
    public void setCountLimit(int countLimit) {
        iCountLimit = countLimit;
    }

    /**
     * @return Returns the iEntityTypes.
     */
    public List<String> getEntityTypes() {
        return iEntityTypes;
    }

    /**
     * @param entityTypes The iEntityTypes to set.
     */
    public void setEntityTypes(List<String> entityTypes) {
        iEntityTypes = entityTypes;
    }

    /**
     * @return Returns the iFilter.
     */
    public String getFilter() {
        return iFilter;
    }

    /**
     * @return Returns the search scope.
     */
    public int getScope() {
        return iScope;
    }

    /**
     * @param filter The iFilter to set.
     */
    public void setFilter(String filter) {
        iFilter = filter;
    }

    /**
     * @return Returns the iPropNames.
     */
    public List<String> getPropertyNames() {
        return iPropNames;
    }

    /**
     * @param propNmaes The iPropNmaes to set.
     */
    public void setPropertiyNmaes(List<String> propNmaes) {
        iPropNames = propNmaes;
    }

    /**
     * @return Returns the iTimeLimit.
     */
    public int getTimeLimit() {
        return iTimeLimit;
    }

    /**
     * @param timeLimit The iTimeLimit to set.
     */
    public void setTimeLimit(int timeLimit) {
        iTimeLimit = timeLimit;
    }

    public void setScope(int scope) {
        iScope = scope;
    }
}
