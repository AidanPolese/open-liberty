/************** Begin Copyright - Do not add comments here **************
 *
 *  
 * IBM Confidential OCO Source Material
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2012, 2013, 2014
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U. S. Copyright Office.
 * 
 * Change History:
 * 
 * Tag          Person              Defect/Feature      Comments
 * -------      ------              --------------      --------------------------------------------------
 * 10/15/2015   rzunzarr           192087               Removed stripRDN code.
 */
package com.ibm.ws.security.wim.adapter.urbridge.utils;

import java.util.List;
import java.util.Map;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.security.wim.copyright.IBMCopyright;
import com.ibm.websphere.security.wim.ras.WIMMessageHelper;
import com.ibm.websphere.security.wim.ras.WIMMessageKey;
import com.ibm.ws.security.wim.adapter.urbridge.URBridge;
import com.ibm.wsspi.security.wim.exception.WIMApplicationException;
import com.ibm.wsspi.security.wim.exception.WIMException;
import com.ibm.wsspi.security.wim.model.Entity;
import com.ibm.wsspi.security.wim.model.Group;
import com.ibm.wsspi.security.wim.model.IdentifierType;
import com.ibm.wsspi.security.wim.model.PersonAccount;
import com.ibm.wsspi.security.wim.model.Root;

public class URBridgeGroup extends URBridgeEntity {

    /**
     * Copyright
     */
    static final String COPYRIGHT_NOTICE = IBMCopyright.COPYRIGHT_NOTICE_SHORT_2014;

    /**
     * Register the class to trace service.
     */
    private final static TraceComponent tc = Tr.register(URBridgeGroup.class);

    /**
     * Create an URBridgeEntityGroup. Extracts and set Property mapping
     * from the inAttrMap.
     * 
     * @param inEntity The entity to be wrapped by the URBridgeEntityObject. This
     *            entity's type determines which type of URBridgeEntityObject
     *            is created.
     * @param urBridge The UserRegisty associated with the adapter.
     * @param inAttrMap An attribute map containing the configuration information
     *            on how underlying UR attributes map to WIM attributes.
     * @param inBaseEntry The name of the baseEntry.
     */
    public URBridgeGroup(Entity inEntity, URBridge urBridge, Map<String, String> inAttrMap, String inNodeName,
                         Map<String, String> inEntityConfigMap)
    {
        super(inEntity, urBridge, inAttrMap, inNodeName, inEntityConfigMap);

        // Get the names of the properties that map to attrs in the underlying UR.
        securityNameProp = inAttrMap.get(URBridgeConstants.GROUP_SECURITY_NAME_PROP);
        uniqueIdProp = inAttrMap.get(URBridgeConstants.UNIQUE_GROUP_ID_PROP);
        displayNameProp = inAttrMap.get(URBridgeConstants.GROUP_DISPLAY_NAME_PROP);
        rdnProp = inEntityConfigMap.get(entity.getTypeName());
    }

    /**
     * Request to retrieve the uniqueGroupId from the registry.
     * 
     * @param securityName the groupSecurityName of the user in the
     *            underlying registry.
     */
    @Override
    public String getUniqueIdForEntity(String securityName) throws Exception
    {
        return urBridge.getUniqueGroupId(securityName);
    }

    /**
     * Request to retrieve the groupSecurityName from the registry.
     * 
     * @param uniqueId the uniqueGroupId of the user in the
     *            underlying registry.
     */
    @Override
    public String getSecurityNameForEntity(String uniqueId) throws Exception
    {
        return urBridge.getGroupSecurityName(uniqueId);
    }

    /**
     * Request to retrive the groupDisplayName from the registry.
     * 
     * @param securityName the groupSecurityName of the user in the
     *            underlying registry.
     */
    @Override
    public String getDisplayNameForEntity(String securityName) throws Exception
    {
        return urBridge.getGroupDisplayName(securityName);
    }

    /**
     * Get the groups for the user and add the specified attributes
     * to each of the groups.
     * 
     * @param grpMbrAttrs the attributes to be added to the groups.
     * @param countLimit restricts the size of the number of groups returned
     *            for the user.
     * @Exception an error occurs in the underlying registry or while
     *            attempting to add the attributes to the group.
     */
    @Override
    public void getUsersForGroup(List<String> grpMbrAttrs, int countLimit) throws WIMException
    {
        String securityName = null;

        try {
            securityName = getSecurityName(false);
            List<String> returnNames = urBridge.getUsersForGroup(securityName, countLimit).getList();

            for (int j = 0; j < returnNames.size(); j++) {
                Root fakeRoot = new Root();
                PersonAccount memberDO = new PersonAccount();
                fakeRoot.getEntities().add(memberDO);
                IdentifierType identifier = new IdentifierType();
                memberDO.setIdentifier(identifier);

                URBridgeEntityFactory osFactory = new URBridgeEntityFactory();
                URBridgeEntity osEntity = osFactory.createObject(memberDO, urBridge, attrMap, baseEntryName, entityConfigMap);
                osEntity.setSecurityNameProp(returnNames.get(j));
                osEntity.populateEntity(grpMbrAttrs);
                osEntity.setRDNPropValue(returnNames.get(j));
                ((Group) entity).getMembers().add(memberDO);
            }
        } catch (Exception e) {
            throw new WIMApplicationException(WIMMessageKey.ENTITY_GET_FAILED,
                            Tr.formatMessage(tc, WIMMessageKey.ENTITY_GET_FAILED,
                                             WIMMessageHelper.generateMsgParms(securityName, e.toString())));
        }
    }
}
