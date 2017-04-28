package com.ibm.ws.security.wim;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class RepositoryPropertyMap {

    HashMap<String, HashSet<String>> reposPropCache = null;
    Set<String> entityTypes = null;

    /**
     *
     */
    public RepositoryPropertyMap() {
        reposPropCache = new HashMap<String, HashSet<String>>();
        entityTypes = new HashSet<String>();
    }

    public Set<String> getRepositoryPropertySetByEntityType(String entityType) {
        HashSet<String> propNameSet = null;
        if (entityType != null && reposPropCache != null) {
            propNameSet = (HashSet<String>) reposPropCache.get(entityType);
        }
        return propNameSet;
    }

    public void setRepositoryPropertySetByEntityType(String entityType, HashSet<String> propSet) {
        if (entityType != null && propSet != null) {
            reposPropCache.put(entityType, propSet);
            entityTypes.add(entityType);
        }
    }

    public Set<String> getEntityTypes() {
        return entityTypes;
    }
}
