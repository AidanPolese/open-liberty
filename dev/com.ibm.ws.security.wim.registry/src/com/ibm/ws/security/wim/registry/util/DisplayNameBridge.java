/************** Begin Copyright - Do not add comments here **************
 *
 * IBM Confidential OCO Source Material
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2012 - 2015
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

import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.security.wim.Service;
import com.ibm.websphere.security.wim.ras.WIMMessageHelper;
import com.ibm.websphere.security.wim.ras.WIMMessageKey;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.security.registry.EntryNotFoundException;
import com.ibm.ws.security.registry.RegistryException;
import com.ibm.ws.security.wim.registry.dataobject.IDAndRealm;
import com.ibm.ws.security.wim.util.SchemaConstantsInternal;
import com.ibm.wsspi.security.wim.SchemaConstants;
import com.ibm.wsspi.security.wim.exception.EntityNotFoundException;
import com.ibm.wsspi.security.wim.exception.InvalidIdentifierException;
import com.ibm.wsspi.security.wim.exception.WIMException;
import com.ibm.wsspi.security.wim.model.Context;
import com.ibm.wsspi.security.wim.model.Control;
import com.ibm.wsspi.security.wim.model.Entity;
import com.ibm.wsspi.security.wim.model.Group;
import com.ibm.wsspi.security.wim.model.PersonAccount;
import com.ibm.wsspi.security.wim.model.Root;
import com.ibm.wsspi.security.wim.model.SearchControl;

/**
 * Bridge class for mapping user and group display name methods.
 *
 */
public class DisplayNameBridge {

    private static final TraceComponent tc = Tr.register(DisplayNameBridge.class);

    /**
     * Property mappings.
     */
    private TypeMappings propertyMap = null;

    /**
     * Mappings utility class.
     */
    private BridgeUtils mappingUtils = null;

    public DisplayNameBridge(BridgeUtils mappingUtil) {
        this.mappingUtils = mappingUtil;
        propertyMap = new TypeMappings(mappingUtil);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.security.registry.UserRegistry#getUserDisplayName(java.lang.String)
     */
    @FFDCIgnore(WIMException.class)
    public String getUserDisplayName(String inputUserSecurityName) throws EntryNotFoundException, RegistryException {
        // initialize the return value
        String returnValue = "";
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
            // New:: Change to Input/Output property
            // String inputAttrName = this.propertyMap.getInputUserSecurityName(idAndRealm.getRealm());
            String inputAttrName = this.propertyMap.getInputUniqueUserId(idAndRealm.getRealm());
            inputAttrName = this.mappingUtils.getRealInputAttrName(inputAttrName, id, true);
            String outputAttrName = this.propertyMap.getOutputUserDisplayName(idAndRealm.getRealm());

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

            // Add context for URBridge
            Context context = new Context();
            context.setKey(SchemaConstantsInternal.IS_URBRIDGE_RESULT);
            context.setValue("false");
            root.getContexts().add(context);

            // get the entity if the input parameter is an identifier type
            // if the input value is not a DN, then search on the principal name
            // this will allow the security name to be either a shortname or DN
            Root resultRoot = null;

            try {
                // New:: Change to Input/Output property
                if (outputAttrName != null && outputAttrName.equalsIgnoreCase(Service.PROP_PRINCIPAL_NAME))
                    outputAttrName = "displayBridgePrincipalName";

                // get the entity if the input parameter is an identifier type
                resultRoot = this.mappingUtils.getEntityByIdentifier(root, inputAttrName,
                                                                     id, outputAttrName, this.mappingUtils);
            } catch (WIMException e) {
                if (!allowDNAsPrincipalName)
                    throw e;
            }

            // Did you find data in URBridge
            boolean foundInURBridge = false;
            if (resultRoot != null && !resultRoot.getEntities().isEmpty()) {
                // Determine if the return object to check if the context was set.
                List<Context> contexts = resultRoot.getContexts();
                for (Context ctx : contexts) {
                    String key = ctx.getKey();

                    if (key != null && SchemaConstantsInternal.IS_URBRIDGE_RESULT.equals(key)) {
                        if ("true".equalsIgnoreCase((String) ctx.getValue()))
                            foundInURBridge = true;
                    }
                }
            }

            root.getContexts().clear();

            if (resultRoot != null && !resultRoot.getEntities().isEmpty() && (isDN(id) || foundInURBridge)) {
                root = resultRoot;
            } else {
                // use the root DataGraph to create a SearchControl DataGraph

                List<Control> controls = root.getControls();
                SearchControl srchCtrl = new SearchControl();
                if (controls != null) {
                    controls.add(srchCtrl);
                }

                // if MAP(userDisplayName) is not an IdentifierType property
                // d112199
                if (!this.mappingUtils.isIdentifierTypeProperty(this.propertyMap.getOutputUserDisplayName(idAndRealm.getRealm()))) {
                    srchCtrl.getProperties().add(this.propertyMap.getOutputUserDisplayName(idAndRealm.getRealm()));
                }
                // set the "expression" string to "type=LoginAccount and MAP(userSecurityName)="user""
                /*
                 * String quote = "'";
                 * String id = idAndRealm.getId();
                 * if (id.indexOf("'") != -1) {
                 * quote = "\"";
                 * }
                 */

                // d112199

                if (allowDNAsPrincipalName)
                    inputAttrName = "principalName";

                srchCtrl.setExpression("//" + Service.DO_ENTITIES + "[@xsi:type='"
                                       + Service.DO_LOGIN_ACCOUNT + "' and "
                                       + inputAttrName
                                       + "=" + quote + id + quote + "]");

                // Set context to use userFilter if applicable
                context = new Context();
                context.set("key", SchemaConstants.USE_USER_FILTER_FOR_SEARCH);
                context.set("value", id);
                root.getContexts().add(context);

                // invoke ProfileService.search with the input root DataGraph
                root = this.mappingUtils.getWimService().search(root);
            }
            // return the value of MAP(userDisplayName) from the output DataGraph
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
                PersonAccount loginAccount = (PersonAccount) returnList.get(0);
                // f113366
                if (!this.mappingUtils.isIdentifierTypeProperty(this.propertyMap.getOutputUserDisplayName(idAndRealm.getRealm()))) {
                    //returnValue = loginAccount.getString(this.propertyMap.getOutputUserDisplayName(idAndRealm.getRealm()));
                    String mappedProp = this.propertyMap.getOutputUserDisplayName(idAndRealm.getRealm());
                    if (mappedProp.equals("displayName")) {
                        if (loginAccount.getDisplayName().size() == 0)
                            returnValue = "";
                        else
                            returnValue = loginAccount.getDisplayName().get(0);
                    } else if (mappedProp.equals("principalName") && foundInURBridge) {
                        if (!this.mappingUtils.isIdentifierTypeProperty(this.propertyMap.getOutputUserPrincipal(idAndRealm.getRealm()))) {
                            returnValue = (String) loginAccount.get(this.propertyMap.getOutputUserPrincipal(idAndRealm.getRealm()));
                        } else {
                            returnValue = (String) loginAccount.getIdentifier().get(this.propertyMap.getOutputUserPrincipal(idAndRealm.getRealm()));
                        }
                    } else {
                        returnValue = (String) loginAccount.get(mappedProp);
                    }
                } else {
                    returnValue = (String) loginAccount.getIdentifier().get(this.propertyMap.getOutputUserDisplayName(idAndRealm.getRealm()));
                }
            }
        } catch (WIMException toCatch) {
            // log the Exception
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, toCatch.getMessage(), toCatch);
            }
            // if (tc.isErrorEnabled()) {
            //     Tr.error(tc, toCatch.getMessage());
            // }// the user was not found
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
     * @see com.ibm.ws.security.registry.UserRegistry#getGroupDisplayName(java.lang.String)
     */
    @FFDCIgnore(WIMException.class)
    public String getGroupDisplayName(String inputGroupSecurityName) throws EntryNotFoundException, RegistryException {
        // initialize the return value
        String returnValue = "";
        // bridge the APIs
        try {
            // validate the id
            this.mappingUtils.validateId(inputGroupSecurityName);
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
            String outputAttrName = this.propertyMap.getOutputGroupDisplayName(idAndRealm.getRealm());

            // New:: Change to Input/Output property
            if (outputAttrName != null && outputAttrName.equalsIgnoreCase("cn"))
                outputAttrName = "displayBridgeCN";

            // get the entity if the input parameter is an identifier type
            Root resultRoot = this.mappingUtils.getEntityByIdentifier(root, inputAttrName,
                                                                      id, outputAttrName, this.mappingUtils);
            if (resultRoot != null) {
                root = resultRoot;
            } else {
                // use the root DataGraph to create a SearchControl DataGraph
                List<Control> controls = root.getControls();
                SearchControl searchControl = new SearchControl();
                if (controls != null) {
                    controls.add(searchControl);
                }
                // if MAP(groupDisplayName) is not an IdentifierType property
                // d112199
                if (!this.mappingUtils.isIdentifierTypeProperty(this.propertyMap.getOutputGroupDisplayName(idAndRealm.getRealm()))) {
                    // add MAP(groupDisplayName) to the return list of properties
                    searchControl.getProperties().add(this.propertyMap.getOutputGroupDisplayName(idAndRealm.getRealm()));
                }
                // set the "expression" string to "type=Group and MAP(groupSecurityName)="group""
                /*
                 * String quote = "'";
                 * String id = idAndRealm.getId();
                 * if (id.indexOf("'") != -1) {
                 * quote = "\"";
                 * }
                 */

                // d115907
                searchControl.setExpression("//" + Service.DO_ENTITIES + "[@xsi:type='"
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

            // return the value of MAP(groupDisplayName) from the output DataGraph
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
                // f113366
                if (!this.mappingUtils.isIdentifierTypeProperty(this.propertyMap.getOutputGroupDisplayName(idAndRealm.getRealm()))) {
                    // get the property to return
                    Object value = group.get(this.propertyMap.getOutputGroupDisplayName(idAndRealm.getRealm()));

                    if (value instanceof String)
                        returnValue = (String) value;
                    else
                        returnValue = String.valueOf(((List<?>) value).get(0));
                } else {
                    // get the identifier to return
                    returnValue = (String) group.getIdentifier().get(this.propertyMap.getOutputGroupDisplayName(idAndRealm.getRealm()));
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
            // the group was not found
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

    @FFDCIgnore(InvalidNameException.class)
    public static boolean isDN(String uniqueName) {
        if (uniqueName == null)
            return false;

        try {
            new LdapName(uniqueName);
            return true;
        } catch (InvalidNameException e) {
            return false;
        }
    }
}
