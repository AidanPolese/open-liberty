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

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * The class which contains the information of a LDAP attribute.
 */
public class LdapAttribute {

    private String iAttrName = null;
    private String iSyntax = LdapConstants.LDAP_ATTR_SYNTAX_STRING;
    private Map<String, String> iDefaultValueMap = null;
    private Map<String, String> iDefaultAttrMap = null;
    private boolean iWIMGenerate = false;
    private Set<String> iEntityTypes = null;

    /**
     *
     */
    public LdapAttribute(String attrName) {
        iAttrName = attrName;
        iEntityTypes = new HashSet<String>();
    }

    public String getName() {
        return iAttrName;
    }

    public void setSyntax(String syntax) {
        iSyntax = syntax;
    }

    public String getSyntax() {
        return iSyntax;
    }

    public void setDefaultValue(String entityType, String value) {
        if (iDefaultValueMap == null) {
            iDefaultValueMap = new Hashtable<String, String>();
        }
        iDefaultValueMap.put(entityType, value);
    }

    public Object getDefaultValue(String entityType) {
        if ((iDefaultValueMap != null) && (iDefaultValueMap.size() > 0))
            return iDefaultValueMap.get(entityType);
        else
            return null;
    }

    public void setDefaultAttribute(String entityType, String attr) {
        if (iDefaultAttrMap == null) {
            iDefaultAttrMap = new Hashtable<String, String>();
        }
        iDefaultAttrMap.put(entityType, attr);
    }

    public String getDefaultAttribute(String entityType) {
        if ((iDefaultAttrMap != null) && (iDefaultAttrMap.size() > 0))
            return iDefaultAttrMap.get(entityType);
        else
            return null;
    }

    public void setWIMGenerate(boolean wimGen) {
        iWIMGenerate = wimGen;
    }

    public boolean isWIMGenerate() {
        return iWIMGenerate;
    }

    public Set<String> getEntityTypes() {
        return iEntityTypes;
    }

    public void addEntityType(String entityType) {
        iEntityTypes.add(entityType);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.getClass().getName()).append(":{");
        sb.append("iAttrName=").append(iAttrName);
        sb.append(", iDefaultAttrMap=").append(iDefaultAttrMap);
        sb.append(", iEntityTypes=").append(iEntityTypes);
        sb.append(", iSyntax=").append(iSyntax);
        sb.append(", iWIMGenerate=").append(iWIMGenerate);
        sb.append("}");
        return sb.toString();
    }
}
