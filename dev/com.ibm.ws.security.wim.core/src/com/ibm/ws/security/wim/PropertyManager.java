/************** Begin Copyright - Do not add comments here **************
 *
 *  
 * IBM Confidential OCO Source Material
 * Virtual Member Manager (C) COPYRIGHT International Business Machines Corp. 2012
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * 
 * Change History:
 * 
 * Tag          Person   Defect/Feature      Comments
 * ----------   ------   --------------      --------------------------------------------------
 */
package com.ibm.ws.security.wim;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ibm.wsspi.security.wim.exception.WIMException;

public class PropertyManager {
    /**
     * IBM Copyright string.
     */
    static final String COPYRIGHT_NOTICE = com.ibm.websphere.security.wim.copyright.IBMCopyright.COPYRIGHT_NOTICE_LONG_2012;

    public final static String CLASSNAME = PropertyManager.class.getName();

    HashMap<String, RepositoryPropertyMap> propCache = null;
    RepositoryPropertyMap lookAsidePropNames = null;
    RepositoryPropertyMap referenceTypePropNames = null;

    public synchronized static PropertyManager singleton() throws WIMException {
        return new PropertyManager();
    }

    public PropertyManager() {
        propCache = new HashMap<String, RepositoryPropertyMap>();
        lookAsidePropNames = new RepositoryPropertyMap();
        referenceTypePropNames = new RepositoryPropertyMap();
    }

    public void setPropertyMapByRepository(String reposId, RepositoryPropertyMap propMap) {
        if (reposId != null && propMap != null) {
            propCache.put(reposId, propMap);
        }
    }

    public RepositoryPropertyMap getPropertyMapByRepositoryId(String reposId) {
        if (reposId != null) {
            return propCache.get(reposId);
        } else {
            return null;
        }
    }

    public Set<String> getRepositoryPropertyNameSet(String reposId, String entityType) {
        if (reposId != null && entityType != null) {
            RepositoryPropertyMap propNames = propCache.get(reposId);
            if (propNames != null) {
                return propNames.getRepositoryPropertySetByEntityType(entityType);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public Set<String> getRepositoryPropertyNameSet(String reposId, List<String> entTypes) {
        if (reposId != null) {
            RepositoryPropertyMap propNames = propCache.get(reposId);
            return getPropertyNameSet(propNames, entTypes);
        } else {
            return null;
        }
    }

    public RepositoryPropertyMap getLookAsidePropertyNameMap() {
        return lookAsidePropNames;
    }

    public RepositoryPropertyMap getReferenceTypePropertyNameMap() {
        return referenceTypePropNames;
    }

    public void setReferenceTypePropertyNameMap(RepositoryPropertyMap propMap) {
        referenceTypePropNames = propMap;
    }

    public void setLookAsidePropertyNameMap(RepositoryPropertyMap propMap) {
        lookAsidePropNames = propMap;
    }

    public Set<String> getLookAsidePropertyNameSet(List<String> entTypes) {
        return getPropertyNameSet(lookAsidePropNames, entTypes);
    }

    public Set<String> getReferenceTypePropertyNameSet(List<String> entTypes) {
        return getPropertyNameSet(referenceTypePropNames, entTypes);
    }

    private Set<String> getPropertyNameSet(RepositoryPropertyMap propNameMap, List<String> entTypes) {
        HashSet<String> propNameSet = null;
        if (propNameMap != null) {
            Set<String> entityTypes = null;
            if (entTypes == null) {
                entityTypes = propNameMap.getEntityTypes();
            } else {
                entityTypes = new HashSet<String>(entTypes);
            }
            if (entityTypes.size() > 0) {
                propNameSet = new HashSet<String>();
                for (String entityType : entityTypes) {
                    Set<String> propNames = propNameMap.getRepositoryPropertySetByEntityType(entityType);
                    if (propNames != null) {
                        propNameSet.addAll(propNames);
                    }
                }
            }
        }
        return propNameSet;
    }

    public Set<String> getLookAsidePropertyNameSet(String entityType) {
        Set<String> laPropNameSet = null;
        if (lookAsidePropNames != null && entityType != null) {
            laPropNameSet = lookAsidePropNames.getRepositoryPropertySetByEntityType(entityType);
        }

        return laPropNameSet;
    }

    public Set<String> getReferencePropertyNameSet(String entityType) {
        Set<String> refPropNameSet = null;
        if (referenceTypePropNames != null && entityType != null) {
            refPropNameSet = referenceTypePropNames.getRepositoryPropertySetByEntityType(entityType);
        }
        return refPropNameSet;
    }
}
