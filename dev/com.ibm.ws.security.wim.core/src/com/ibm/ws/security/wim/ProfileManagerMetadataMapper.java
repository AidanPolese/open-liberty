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

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ibm.ws.security.wim.xpath.util.MetadataMapper;
import com.ibm.wsspi.security.wim.exception.WIMException;
import com.ibm.wsspi.security.wim.model.Entity;

public class ProfileManagerMetadataMapper implements MetadataMapper {
    static final String COPYRIGHT_NOTICE = com.ibm.websphere.security.wim.copyright.IBMCopyright.COPYRIGHT_NOTICE_LONG_2012;

    private List<String> entityTypes = null;
    RepositoryPropertyMap laPropertyNames = null;
    RepositoryPropertyMap reposPropertyNames = null;

    public ProfileManagerMetadataMapper(String reposId, List<String> entityTypes) throws WIMException
    {
        this.entityTypes = entityTypes;

        PropertyManager propMgr = PropertyManager.singleton();
        laPropertyNames = propMgr.getLookAsidePropertyNameMap();
        reposPropertyNames = propMgr.getPropertyMapByRepositoryId(reposId);
    }

    public boolean isPropertyInLookAside(String propertyName, String entityType)
    {
        boolean inRepository = false;
        if (laPropertyNames != null && propertyName != null) {
            Set<String> propertyNames = laPropertyNames.getRepositoryPropertySetByEntityType(entityType);
            if (propertyNames != null) {
                inRepository = propertyNames.contains(propertyName);
            }
            else {
                try {
                    Set<String> subEntTypes = Entity.getSubEntityTypes(entityType);
                    if (subEntTypes != null) {
                        Iterator<String> iter = subEntTypes.iterator();
                        while (iter.hasNext() && !inRepository) {
                            propertyNames = laPropertyNames.getRepositoryPropertySetByEntityType(iter.next());
                            if (propertyNames != null) {
                                inRepository = propertyNames.contains(propertyName);
                            }
                        }
                    }
                }
                catch (Exception e) {
                }
            }
        }
        return inRepository;
    }

    public boolean isPropertyInRepository(String propertyName, String entityType)
    {
        boolean inRepository = false;
        if (reposPropertyNames != null && propertyName != null) {
            Set<String> propertyNames = reposPropertyNames.getRepositoryPropertySetByEntityType(entityType);
            if (propertyNames != null) {
                inRepository = propertyNames.contains(propertyName);
            }
            else {
                try {
                    Set<String> subEntTypes = Entity.getSubEntityTypes(entityType);
                    if (subEntTypes != null) {
                        Iterator<String> iter = subEntTypes.iterator();
                        while (iter.hasNext() && !inRepository) {
                            propertyNames = reposPropertyNames.getRepositoryPropertySetByEntityType(iter.next());
                            if (propertyNames != null) {
                                inRepository = propertyNames.contains(propertyName);
                            }
                        }
                    }
                }
                catch (Exception e) {
                }
            }
        }
        return inRepository;
    }

    public boolean isValidEntityType(String entityType)
    {
        boolean valid = false;
        if (entityTypes != null && entityType != null && entityType.length() != 0) {
            valid = entityTypes.contains(entityType);
        }
        return valid;
    }

  
}
