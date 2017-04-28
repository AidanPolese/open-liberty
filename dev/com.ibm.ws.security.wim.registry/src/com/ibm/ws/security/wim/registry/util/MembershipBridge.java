/************** Begin Copyright - Do not add comments here **************
 *
 * IBM Confidential OCO Source Material
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2012, 2013
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U. S. Copyright Office.
 *
 * Change History:
 *
 * Tag          Person   	Defect/Feature      Comments
 * ----------   ------   	--------------      --------------------------------------------------
 */

package com.ibm.ws.security.wim.registry.util;

import java.util.ArrayList;
import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.security.wim.ConfigConstants;
import com.ibm.websphere.security.wim.Service;
import com.ibm.websphere.security.wim.ras.WIMMessageHelper;
import com.ibm.websphere.security.wim.ras.WIMMessageKey;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.security.registry.EntryNotFoundException;
import com.ibm.ws.security.registry.RegistryException;
import com.ibm.ws.security.registry.SearchResult;
import com.ibm.ws.security.wim.registry.dataobject.IDAndRealm;
import com.ibm.wsspi.security.wim.SchemaConstants;
import com.ibm.wsspi.security.wim.exception.EntityNotFoundException;
import com.ibm.wsspi.security.wim.exception.InvalidIdentifierException;
import com.ibm.wsspi.security.wim.exception.InvalidUniqueNameException;
import com.ibm.wsspi.security.wim.exception.WIMException;
import com.ibm.wsspi.security.wim.model.Context;
import com.ibm.wsspi.security.wim.model.Control;
import com.ibm.wsspi.security.wim.model.Entity;
import com.ibm.wsspi.security.wim.model.ExternalNameControl;
import com.ibm.wsspi.security.wim.model.Group;
import com.ibm.wsspi.security.wim.model.GroupMemberControl;
import com.ibm.wsspi.security.wim.model.GroupMembershipControl;
import com.ibm.wsspi.security.wim.model.IdentifierType;
import com.ibm.wsspi.security.wim.model.LoginAccount;
import com.ibm.wsspi.security.wim.model.Root;
import com.ibm.wsspi.security.wim.model.SearchControl;

/**
 * Bridge class for mapping user and group membership methods.
 *
 */
public class MembershipBridge {

    private static final TraceComponent tc = Tr.register(MembershipBridge.class);

    /**
     * Property mappings.
     */
    private TypeMappings propertyMap = null;

    /**
     * Mappings utility class.
     */
    private BridgeUtils mappingUtils = null;

    /**
     * Default constructor.
     *
     * @param mappingUtil
     */
    public MembershipBridge(BridgeUtils mappingUtil) {

        this.mappingUtils = mappingUtil;
        propertyMap = new TypeMappings(mappingUtil);

    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.security.registry.UserRegistry#getGroupsForUser(java.lang.String)
     */
    @FFDCIgnore(WIMException.class)
    public List<String> getGroupsForUser(String inputUserSecurityName) throws EntryNotFoundException, RegistryException {
        // initialize the method name
        String methodName = "getGroupsForUser";

        // initialize the return value
        List<String> returnValue = new ArrayList<String>();
        // bridge the APIs
        try {
            // validate the id
            this.mappingUtils.validateId(inputUserSecurityName);
            // separate the ID and the realm
            IDAndRealm idAndRealm = this.mappingUtils.separateIDAndRealm(inputUserSecurityName);
            // create an empty root DataObject
            Root root = this.mappingUtils.getWimService().createRootObject();
            // if realm is defined
            if (idAndRealm.isRealmDefined()) {
                // set "WIM.Realm" in the Context DataGraph to the realm
                this.mappingUtils.createRealmDataObject(root, idAndRealm.getRealm());
            }

            //PK63962
            String quote = "'";
            String id = idAndRealm.getId();
            if (id.indexOf("'") != -1) {
                quote = "\"";
            }

            // get input and output values
            String inputAttrName = this.propertyMap.getInputUserSecurityName(idAndRealm.getRealm());
            inputAttrName = this.mappingUtils.getRealInputAttrName(inputAttrName, id, true);
            String outputAttrName = this.propertyMap.getOutputGroupSecurityName(idAndRealm.getRealm());

            //PM55588 Read the custom property from BridgeUtils
            boolean allowDNAsPrincipalName = this.mappingUtils.allowDNAsPrincipalName;
            if (allowDNAsPrincipalName) {
                List<Context> contexts = root.getContexts();
                if (contexts != null) {
                    Context ctx = new Context();
                    ctx.setKey(SchemaConstants.ALLOW_DN_PRINCIPALNAME_AS_LITERAL);
                    ctx.setValue(allowDNAsPrincipalName);
                    contexts.add(ctx);
                }

            }
            Root resultRoot = null;

            try {
                // get the entity if the input parameter is an identifier type
                resultRoot = this.mappingUtils.getEntityByIdentifier(root, inputAttrName,
                                                                     id, outputAttrName, this.mappingUtils);
            } catch (WIMException e) {
                if (!allowDNAsPrincipalName)
                    throw e;
            }

            if (resultRoot != null) {
                root = resultRoot;
            } else {
                if (allowDNAsPrincipalName)
                    inputAttrName = "principalName";

                // use the root DataGraph to create a SearchControl DataGraph
                List<Control> controls = root.getControls();
                SearchControl srchCtrl = new SearchControl();
                if (controls != null) {
                    controls.add(srchCtrl);
                }
                // set the "expression" string to "type=LoginAccount and MAP(userSecurityName)="user""
                /*
                 * String quote = "'";
                 * String id = idAndRealm.getId();
                 * if (id.indexOf("'") != -1) {
                 * quote = "\"";
                 * }
                 */

                // f112199
                srchCtrl.setExpression("//" + Service.DO_ENTITIES + "[@xsi:type='"
                                       + Service.DO_LOGIN_ACCOUNT + "' and "
                                       + inputAttrName
                                       + "=" + quote + id + quote + "]");

                // Set context to use userFilter if applicable
                Context context = new Context();
                context.set("key", SchemaConstants.USE_USER_FILTER_FOR_SEARCH);
                context.set("value", id);
                root.getContexts().add(context);

                // invoke ProfileService.search with the input root DataGraph
                root = this.mappingUtils.getWimService().search(root);
            }

            // set the user to the value of "uniqueName" from the output DataGraph
            List<Entity> returnList = root.getEntities();
            // the user was not found or more than one user was found
            // d125249
            if (returnList.isEmpty()) {
                // if (tc.isErrorEnabled()) {
                //     Tr.error(tc, WIMMessageKey.ENTITY_NOT_FOUND, WIMMessageHelper.generateMsgParms(inputUserSecurityName));
                // }
                throw new EntityNotFoundException(WIMMessageKey.ENTITY_NOT_FOUND, Tr.formatMessage(
                                                                                                   tc,
                                                                                                   WIMMessageKey.ENTITY_NOT_FOUND,
                                                                                                   WIMMessageHelper.generateMsgParms(inputUserSecurityName)));
            } else if (returnList.size() != 1) {
                // if (tc.isErrorEnabled()) {
                //     Tr.error(tc, WIMMessageKey.MULTIPLE_PRINCIPALS_FOUND, WIMMessageHelper.generateMsgParms(inputUserSecurityName));
                // }
                throw new EntityNotFoundException(WIMMessageKey.MULTIPLE_PRINCIPALS_FOUND, Tr.formatMessage(
                                                                                                            tc,
                                                                                                            WIMMessageKey.MULTIPLE_PRINCIPALS_FOUND,
                                                                                                            WIMMessageHelper.generateMsgParms(inputUserSecurityName)));
            }
            // the user was found
            else {
                Entity user = returnList.get(0);
                idAndRealm.setId(user.getIdentifier().getUniqueName());
            }
            // create an empty root DataObject
            root = this.mappingUtils.getWimService().createRootObject();
            // if realm is defined
            if (idAndRealm.isRealmDefined()) {
                // set "WIM.Realm" in the Context DataGraph to the realm
                this.mappingUtils.createRealmDataObject(root, idAndRealm.getRealm());
            }
            // use the root DataGraph to create a GroupMembershipControl DataGraph
            List<Control> controls = root.getControls();
            GroupMembershipControl grpMbrShipCtrl = new GroupMembershipControl();
            if (controls != null) {
                controls.add(grpMbrShipCtrl);
            }
            // add MAP(groupSecurityName) to the return list of properties
            // d115913
            if (!this.mappingUtils.isIdentifierTypeProperty(this.propertyMap.getOutputGroupSecurityName(idAndRealm.getRealm()))) {
                grpMbrShipCtrl.getProperties().add(
                                                   this.propertyMap.getOutputGroupSecurityName(idAndRealm.getRealm()));
            }
            // set the "level" property
            grpMbrShipCtrl.setLevel((this.mappingUtils.getGroupDepth()));
            // f112199
            // only request Groups
            grpMbrShipCtrl.setExpression("@xsi:type='" + Service.DO_GROUP + "'");
            // use the root DataGraph to create a LoginAccount DataObject
            List<Entity> entities = root.getEntities();
            LoginAccount loginAccount = new LoginAccount();
            if (entities != null) {
                entities.add(loginAccount);
            }
            // set "uniqueName" to user
            IdentifierType idfType = new IdentifierType();
            idfType.setUniqueName(idAndRealm.getId());
            loginAccount.setIdentifier(idfType);
            // invoke ProfileService.get with the input root DataGraph
            root = this.mappingUtils.getWimService().get(root);
            List<Entity> entityList = root.getEntities();
            if (entityList.isEmpty()) {
                // if (tc.isErrorEnabled()) {
                //     Tr.error(tc, WIMMessageKey.ENTITY_NOT_FOUND, WIMMessageHelper.generateMsgParms(inputUserSecurityName));
                // }
                throw new EntityNotFoundException(WIMMessageKey.ENTITY_NOT_FOUND, Tr.formatMessage(
                                                                                                   tc,
                                                                                                   WIMMessageKey.ENTITY_NOT_FOUND,
                                                                                                   WIMMessageHelper.generateMsgParms(inputUserSecurityName)));
            }
            Entity entity = entityList.get(0);
            List<Group> groupList = entity.getGroups();
            // if the output DataGraph contains MAP(groupSecurityName)s
            if (!groupList.isEmpty()) {
                // add the MAP(groupSecurityName)s to the List
                String grpAttrName = this.propertyMap.getOutputGroupSecurityName(idAndRealm.getRealm());
                boolean isIdentifier = this.mappingUtils.isIdentifierTypeProperty(grpAttrName);
                if (tc.isDebugEnabled()) {
                    Tr.debug(tc, methodName + " " + "grpAttrName=" + grpAttrName +
                                 ", isIdentifier=" + isIdentifier,
                             methodName);
                }
                for (int i = 0; i < groupList.size(); i++) {
                    Group member = groupList.get(i);
                    // d115913
                    Object groups;
                    if (!isIdentifier) {
                        groups = member.get(grpAttrName);
                    } else {
                        groups = member.getIdentifier().get(grpAttrName);
                    }
                    if (groups instanceof String) {
                        returnValue.add((String) groups);
                    } else if (groups instanceof List<?>) {
                        returnValue.addAll((List<String>) groups);
                    }
                }
            }
        } catch (WIMException toCatch) {
            // log the Exception
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, toCatch.getMessage(), toCatch);
            }
            // if (tc.isErrorEnabled()) {
            //     Tr.error(tc, toCatch.getMessage());
            // }
            // the user was not found
            if (toCatch instanceof EntityNotFoundException || toCatch instanceof InvalidIdentifierException) {
                throw new EntryNotFoundException(toCatch.getMessage(), toCatch);
            }
            // other cases
            else {
                throw new RegistryException(toCatch.getMessage(), toCatch);
            }
        }
        return returnValue;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.security.registry.UserRegistry#getUsersForGroup(java.lang.String, int)
     */
    @FFDCIgnore(WIMException.class)
    public SearchResult getUsersForGroup(String inputGroupSecurityName, int inputLimit) throws EntryNotFoundException, RegistryException {
        // initialize the method name
        String methodName = "getUsersForGroup";

        // initialize the return value
        SearchResult returnValue = null;//new SearchResult();
        // bridge the APIs
        try {
            // validate the id
            this.mappingUtils.validateId(inputGroupSecurityName);
            // validate the limit
            this.mappingUtils.validateLimit(inputLimit);
            // separate the ID and the realm
            IDAndRealm idAndRealm = this.mappingUtils.separateIDAndRealm(inputGroupSecurityName);
            // create an empty root DataObject
            Root root = this.mappingUtils.getWimService().createRootObject();
            // if realm is defined
            if (idAndRealm.isRealmDefined()) {
                // set "WIM.Realm" in the Context DataGraph to the realm
                this.mappingUtils.createRealmDataObject(root, idAndRealm.getRealm());
            }
            //PK63962
            String quote = "'";
            String id = idAndRealm.getId();
            if (id.indexOf("'") != -1) {
                quote = "\"";
            }
            // get input and output values
            String inputAttrName = this.propertyMap.getInputGroupSecurityName(idAndRealm.getRealm());
            inputAttrName = this.mappingUtils.getRealInputAttrName(inputAttrName, id, false);
            String outputAttrName = Service.PROP_UNIQUE_NAME;

            // get the entity if the input parameter is an identifier type
            Root resultRoot = this.mappingUtils.getEntityByIdentifier(root, inputAttrName,
                                                                      id, outputAttrName, this.mappingUtils);
            if (resultRoot != null) {
                root = resultRoot;
            } else {
                // use the root DataGraph to create a SearchControl DataGraph
                List<Control> controls = root.getControls();
                SearchControl srchCtrl = new SearchControl();
                if (controls != null) {
                    controls.add(srchCtrl);
                }
                // set the "expression" string to "type=Group and MAP(groupSecurityName)="group""
                /*
                 * String quote = "'";
                 * String id = idAndRealm.getId();
                 * if (id.indexOf("'") != -1) {
                 * quote = "\"";
                 * }
                 */

                // f112199
                srchCtrl.setExpression("//" + Service.DO_ENTITIES + "[@xsi:type='"
                                       + Service.DO_GROUP + "' and " + inputAttrName
                                       + "=" + quote + id + quote + "]");

                // Set context to use groupFilter if applicable
                Context context = new Context();
                context.set("key", SchemaConstants.USE_GROUP_FILTER_FOR_SEARCH);
                context.set("value", id);
                root.getContexts().add(context);

                // invoke ProfileService.search with the input root DataGraph
                root = this.mappingUtils.getWimService().search(root);
            }

            // set the group to the value of "uniqueName" from the output DataGraph
            List<Entity> returnList = root.getEntities();
            // the group was not found or more than one group was found
            // d125249
            if (returnList.isEmpty()) {
                // if (tc.isErrorEnabled()) {
                //     Tr.error(tc, WIMMessageKey.ENTITY_NOT_FOUND, WIMMessageHelper.generateMsgParms(inputGroupSecurityName));
                // }
                throw new EntityNotFoundException(WIMMessageKey.ENTITY_NOT_FOUND, Tr.formatMessage(
                                                                                                   tc,
                                                                                                   WIMMessageKey.ENTITY_NOT_FOUND,
                                                                                                   WIMMessageHelper.generateMsgParms(inputGroupSecurityName)));
            } else if (returnList.size() != 1) {
                // if (tc.isErrorEnabled()) {
                //     Tr.error(tc, WIMMessageKey.MULTIPLE_PRINCIPALS_FOUND, WIMMessageHelper.generateMsgParms(inputGroupSecurityName));
                // }
                throw new EntityNotFoundException(WIMMessageKey.MULTIPLE_PRINCIPALS_FOUND, Tr.formatMessage(
                                                                                                            tc,
                                                                                                            WIMMessageKey.MULTIPLE_PRINCIPALS_FOUND,
                                                                                                            WIMMessageHelper.generateMsgParms(inputGroupSecurityName)));
            }
            // the group was found
            else {
                Group group = (Group) returnList.get(0);
                idAndRealm.setId(group.getIdentifier().getUniqueName());
            }
            // create an empty root DataObject
            root = this.mappingUtils.getWimService().createRootObject();
            // if realm is defined
            if (idAndRealm.isRealmDefined()) {
                // set "WIM.Realm" in the Context DataGraph to the realm
                this.mappingUtils.createRealmDataObject(root, idAndRealm.getRealm());
            }
            // use the root DataGraph to create a GroupMemberControl DataGraph
            // f113366

            List<Control> controls = root.getControls();
            GroupMemberControl groupMemberControl = new GroupMemberControl();
            if (controls != null) {
                controls.add(groupMemberControl);
            }
            // set the "level" property
            groupMemberControl.setLevel(this.mappingUtils.getGroupDepth());
            // add MAP(userSecurityName) to the return list of properties
            // d115256
            if (!this.mappingUtils.isIdentifierTypeProperty(this.propertyMap.getOutputUserSecurityName(idAndRealm.getRealm()))) {
                groupMemberControl.getProperties().add(
                                                       this.propertyMap.getOutputUserSecurityName(idAndRealm.getRealm()));
            }
            // set the count limit to limit + 1
            if (inputLimit != 0) {
                groupMemberControl.setCountLimit(inputLimit + 1);
            } else {
                groupMemberControl.setCountLimit(inputLimit);
            }
            // f112199
            // only request LoginAccounts
            groupMemberControl.setExpression("@xsi:type='" + Service.DO_LOGIN_ACCOUNT + "'");
            // use the root DataGraph to create a Group DataObject
            List<Entity> entities = root.getEntities();
            Group group = new Group();
            if (entities != null) {
                entities.add(group);
            }
            // set "uniqueName" to group
            IdentifierType idfType = new IdentifierType();
            idfType.setUniqueName(idAndRealm.getId());
            group.setIdentifier(idfType);
            // invoke ProfileService.get with the input root DataGraph
            root = this.mappingUtils.getWimService().get(root);
            List<Entity> entityList = root.getEntities();
            if (entityList.isEmpty()) {
                // if (tc.isErrorEnabled()) {
                //     Tr.error(tc, WIMMessageKey.ENTITY_NOT_FOUND, WIMMessageHelper.generateMsgParms(inputGroupSecurityName));
                // }
                throw new EntityNotFoundException(WIMMessageKey.ENTITY_NOT_FOUND, Tr.formatMessage(
                                                                                                   tc,
                                                                                                   WIMMessageKey.ENTITY_NOT_FOUND,
                                                                                                   WIMMessageHelper.generateMsgParms(inputGroupSecurityName)));
            }
            Group entity = (Group) entityList.get(0);
            List memberList = entity.getMembers();
            // if the output DataGraph contains MAP(userSecurityName)s
            if (!memberList.isEmpty()) {
                // add the MAP(userSecurityName)s to the Result list while count < limit
                String userAttrName = this.propertyMap.getOutputUserSecurityName(idAndRealm.getRealm());
                boolean isIdentifier = this.mappingUtils.isIdentifierTypeProperty(userAttrName);
                if (tc.isDebugEnabled()) {
                    Tr.debug(tc, methodName + " " + "userAttrName=" + userAttrName +
                                 ", isIdentifier=" + isIdentifier,
                             methodName);
                }

                ArrayList users = new ArrayList();
                for (int count = 0; count < memberList.size(); count++) {
                    if ((inputLimit != 0) && (count == inputLimit)) {
                        // set the Result boolean to true
                        //returnValue.setHasMore();
                        break;
                    }
                    Entity member = (Entity) memberList.get(count);
                    // d115256
                    if (!isIdentifier) {
                        users.add(member.get(userAttrName));
                    } else {
                        users.add(member.getIdentifier().get(userAttrName));
                    }
                }
                //returnValue.setList(users);
                returnValue = new SearchResult(users, true);

            } else {
                //returnValue.setList(new ArrayList());
                returnValue = new SearchResult(new ArrayList(), false);

            }
        } catch (WIMException toCatch) {
            // log the Exception
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, toCatch.getMessage(), toCatch);
            }
            // if (tc.isErrorEnabled()) {
            //     Tr.error(tc, toCatch.getMessage());
            // }
            // the user was not found
            if (toCatch instanceof EntityNotFoundException || toCatch instanceof InvalidIdentifierException) {
                throw new EntryNotFoundException(toCatch.getMessage(), toCatch);
            }
            // other cases
            else {
                throw new RegistryException(toCatch.getMessage(), toCatch);
            }
        }
        return returnValue;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.security.registry.UserRegistry#getUniqueGroupIds(java.lang.String)
     */
    @FFDCIgnore({ WIMException.class, InvalidNameException.class })
    public List<String> getUniqueGroupIds(String inputUniqueUserId) throws EntryNotFoundException, RegistryException {
        // initialize the method name
        String methodName = "getUniqueGroupIds";

        // initialize the return value
        List<String> returnValue = new ArrayList<String>();
        // bridge the APIs
        try {
            // validate the id
            this.mappingUtils.validateId(inputUniqueUserId);
            // separate the ID and the realm
            IDAndRealm idAndRealm = this.mappingUtils.separateIDAndRealm(inputUniqueUserId);
            // if MAP(userUniqueId) is not an IdentifierType property
            // f112199
            //if (!this.mappingUtils.isIdentifierTypeProperty(this.propertyMap.getInputUniqueUserId(idAndRealm.getRealm()))) {
            // create an empty root DataObject
            Root root = this.mappingUtils.getWimService().createRootObject();
            // if realm is defined
            if (idAndRealm.isRealmDefined()) {
                // set "WIM.Realm" in the Context DataGraph to the realm
                this.mappingUtils.createRealmDataObject(root, idAndRealm.getRealm());
                //allow operation if one or more repositories are down
                List<Context> contexts = root.getContexts();
                if (contexts != null) {
                    Context ctx = new Context();
                    ctx.setKey(ConfigConstants.CONFIG_PROP_ALLOW_OPERATION_IF_REPOS_DOWN);
                    ctx.setValue(Boolean.valueOf(this.mappingUtils.getCoreConfiguration().isAllowOpIfRepoDown(idAndRealm.getRealm())));
                    contexts.add(ctx);
                }
            }

            String quote = "'";
            String id = idAndRealm.getId();
            if (id.indexOf("'") != -1) {
                quote = "\"";
            }

            // get input and output values
            String inputAttrName = this.propertyMap.getInputUniqueUserId(idAndRealm.getRealm());
            inputAttrName = this.mappingUtils.getRealInputAttrName(inputAttrName, id, true);
            String outputAttrName = this.propertyMap.getOutputUniqueGroupId(idAndRealm.getRealm());

            //PM55588 Read the custom property from BridgeUtils
            boolean allowDNAsPrincipalName = this.mappingUtils.allowDNAsPrincipalName;
            if (allowDNAsPrincipalName) {
                List<Context> contexts = root.getContexts();
                if (contexts != null) {
                    Context ctx = new Context();
                    ctx.setKey(SchemaConstants.ALLOW_DN_PRINCIPALNAME_AS_LITERAL);
                    ctx.setValue(allowDNAsPrincipalName);
                    contexts.add(ctx);
                }
            }
            Root resultRoot = null;

            try {
                // get the entity if the input parameter is an identifier type
                resultRoot = this.mappingUtils.getEntityByIdentifier(root, inputAttrName,
                                                                     id, outputAttrName, this.mappingUtils);
            } catch (WIMException e) {
                if (!allowDNAsPrincipalName)
                    throw e;
            }

            if (resultRoot != null) {
                root = resultRoot;
            } else {
                // use the root DataGraph to create a SearchControl DataGraph
                List<Control> controls = root.getControls();
                SearchControl searchControl = new SearchControl();
                if (controls != null) {
                    controls.add(searchControl);
                }

                //allow operation if one or more repositories are down
                List<Context> contexts = root.getContexts();
                if (contexts != null) {
                    Context ctx = new Context();
                    ctx.setKey(ConfigConstants.CONFIG_PROP_ALLOW_OPERATION_IF_REPOS_DOWN);
                    ctx.setValue(Boolean.valueOf(this.mappingUtils.getCoreConfiguration().isAllowOpIfRepoDown(idAndRealm.getRealm())));
                    contexts.add(ctx);
                }
                if (allowDNAsPrincipalName)
                    inputAttrName = "principalName";

                searchControl.setExpression("//" + Service.DO_ENTITIES + "[@xsi:type='"
                                            + Service.DO_LOGIN_ACCOUNT + "' and "
                                            + inputAttrName
                                            + "=" + quote + id + quote + "]");

                // Set context to use userFilter if applicable
                Context context = new Context();
                context.set("key", SchemaConstants.USE_USER_FILTER_FOR_SEARCH);
                context.set("value", id);
                root.getContexts().add(context);

                // invoke ProfileService.search with the input root DataGraph
                root = this.mappingUtils.getWimService().search(root);
                // set the user to the value of "uniqueName" from the output DataGraph
                List<Entity> returnList = root.getEntities();
                // the user was not found or more than one user was found
                // d125249
                if (returnList.isEmpty()) {
                    // if (tc.isErrorEnabled()) {
                    //     Tr.error(tc, WIMMessageKey.ENTITY_NOT_FOUND, WIMMessageHelper.generateMsgParms(inputUniqueUserId));
                    // }
                    throw new EntityNotFoundException(WIMMessageKey.ENTITY_NOT_FOUND, Tr.formatMessage(
                                                                                                       tc,
                                                                                                       WIMMessageKey.ENTITY_NOT_FOUND,
                                                                                                       WIMMessageHelper.generateMsgParms(inputUniqueUserId)));
                } else if (returnList.size() != 1) {
                    // if (tc.isErrorEnabled()) {
                    //     Tr.error(tc, WIMMessageKey.MULTIPLE_PRINCIPALS_FOUND, WIMMessageHelper.generateMsgParms(inputUniqueUserId));
                    // }
                    throw new EntityNotFoundException(WIMMessageKey.MULTIPLE_PRINCIPALS_FOUND, Tr.formatMessage(
                                                                                                                tc,
                                                                                                                WIMMessageKey.MULTIPLE_PRINCIPALS_FOUND,
                                                                                                                WIMMessageHelper.generateMsgParms(inputUniqueUserId)));
                }
                // the user was found
                else {
                    Entity user = returnList.get(0);
                    idAndRealm.setId(user.getIdentifier().getUniqueName());
                }
            }
            // create an empty root DataObject
            root = this.mappingUtils.getWimService().createRootObject();
            // if realm is defined
            if (idAndRealm.isRealmDefined()) {
                // set "WIM.Realm" in the Context DataGraph to the realm
                this.mappingUtils.createRealmDataObject(root, idAndRealm.getRealm());
                List<Context> contexts = root.getContexts();
                if (contexts != null) {
                    Context ctx = new Context();
                    ctx.setKey(ConfigConstants.CONFIG_PROP_ALLOW_OPERATION_IF_REPOS_DOWN);
                    ctx.setValue(Boolean.valueOf(this.mappingUtils.getCoreConfiguration().isAllowOpIfRepoDown(idAndRealm.getRealm())));
                    contexts.add(ctx);
                }
            }
            // use the root DataGraph to create a GroupMembershipControl DataGraph
            List<Control> controls = root.getControls();
            GroupMembershipControl groupMembershipControl = new GroupMembershipControl();
            if (controls != null) {
                controls.add(groupMembershipControl);
            }
            // if MAP(groupUniqueId) is not an IdentifierType property
            // f112199
            if (!this.mappingUtils.isIdentifierTypeProperty(this.propertyMap.getOutputUniqueGroupId(idAndRealm.getRealm()))) {
                // add MAP(groupUniqueId) to the return list of properties
                groupMembershipControl.getProperties().add(
                                                           this.propertyMap.getOutputUniqueGroupId(idAndRealm.getRealm()));
            }
            // set the "level" property
            groupMembershipControl.setLevel((this.mappingUtils.getGroupDepth()));
            // f112199
            // only request Groups
            groupMembershipControl.setExpression("@xsi:type='" + Service.DO_GROUP + "'");
            // use the root DataGraph to create a LoginAccount DataObject

            List<Entity> entities = root.getEntities();
            LoginAccount loginAccount = new LoginAccount();
            if (entities != null) {
                entities.add(loginAccount);
            }
            // if MAP(userUniqueId) is not an IdentifierType property
            // f112199
            IdentifierType actIdfType = new IdentifierType();
            if (!this.mappingUtils.isIdentifierTypeProperty(this.propertyMap.getInputUniqueUserId(idAndRealm.getRealm()))) {

                actIdfType.setUniqueName(idAndRealm.getId());
                loginAccount.setIdentifier(actIdfType);
            } else {
                actIdfType.set(this.propertyMap.getInputUniqueUserId(idAndRealm.getRealm()), idAndRealm.getId());
                loginAccount.setIdentifier(actIdfType);

                // Create an external name control if the external identifier is being used
                if (this.propertyMap.getInputUniqueUserId(idAndRealm.getRealm()).equals(Service.PROP_EXTERNAL_NAME)) {
                    List<Control> extCtrls = root.getControls();
                    if (extCtrls != null) {
                        extCtrls.add(new ExternalNameControl());
                    }
                }
            }
            // invoke ProfileService.get with the input root DataGraph
            root = this.mappingUtils.getWimService().get(root);
            List<Entity> entityList = root.getEntities();
            if (entityList.isEmpty()) {
                // if (tc.isErrorEnabled()) {
                //     Tr.error(tc, WIMMessageKey.ENTITY_NOT_FOUND, WIMMessageHelper.generateMsgParms(inputUniqueUserId));
                // }
                throw new EntityNotFoundException(WIMMessageKey.ENTITY_NOT_FOUND, Tr.formatMessage(
                                                                                                   tc,
                                                                                                   WIMMessageKey.ENTITY_NOT_FOUND,
                                                                                                   WIMMessageHelper.generateMsgParms(inputUniqueUserId)));
            }
            Entity entity = entityList.get(0);
            List<Group> groupList = entity.getGroups();
            // if the output DataGraph contains MAP(groupUniqueId)s
            if (!groupList.isEmpty()) {
                // add the MAP(groupUniqueId)s to the List
                String grpAttrName = this.propertyMap.getOutputUniqueGroupId(idAndRealm.getRealm());
                boolean isIdentifier = this.mappingUtils.isIdentifierTypeProperty(grpAttrName);
                if (tc.isDebugEnabled()) {
                    Tr.debug(tc, methodName + " " + "grpAttrName=" + grpAttrName +
                                 ", isIdentifier=" + isIdentifier,
                             methodName);
                }
                for (int i = 0; i < groupList.size(); i++) {
                    Group member = groupList.get(i);
                    String value = null;
                    if (!isIdentifier) {
                        value = (String) member.get(grpAttrName);
                    } else {
                        value = (String) member.getIdentifier().get(grpAttrName);
                    }

                    // if return attribute is uniqueName and returnValue is not in DN format, default to uniqueId (as this is potentially a customRegistry data)
                    if (SchemaConstants.PROP_UNIQUE_NAME.equalsIgnoreCase(grpAttrName)) {
                        try {
                            new LdapName(String.valueOf(value));
                        } catch (InvalidNameException e) {
                            String uid = member.getIdentifier().getUniqueId();
                            if (uid != null)
                                value = uid;
                        }
                    }

                    returnValue.add(value);
                }
            }
        } catch (WIMException toCatch) {
            // log the Exception
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, methodName + " " + toCatch.getMessage(), toCatch);
            }
            // if (tc.isErrorEnabled()) {
            //     Tr.error(tc, toCatch.getMessage());
            // }
            // the group was not found
            if (toCatch instanceof EntityNotFoundException) {
                throw new EntryNotFoundException(toCatch.getMessage(), toCatch);
            }
            // the identifier is invalid
            else if (toCatch instanceof InvalidUniqueNameException) {
                throw new EntryNotFoundException(toCatch.getMessage(), toCatch);
            }
            // the identifier is invalid
            else if (toCatch instanceof InvalidIdentifierException) {
                throw new EntryNotFoundException(toCatch.getMessage(), toCatch);
            }
            // other cases
            else {
                throw new RegistryException(toCatch.getMessage(), toCatch);
            }
        }
        return returnValue;
    }
}
