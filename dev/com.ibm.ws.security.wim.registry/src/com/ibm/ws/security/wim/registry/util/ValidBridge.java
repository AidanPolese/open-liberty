/************** Begin Copyright - Do not add comments here **************
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

package com.ibm.ws.security.wim.registry.util;

import java.util.List;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.security.wim.Service;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.security.registry.RegistryException;
import com.ibm.ws.security.wim.registry.dataobject.IDAndRealm;
import com.ibm.wsspi.security.wim.SchemaConstants;
import com.ibm.wsspi.security.wim.exception.WIMException;
import com.ibm.wsspi.security.wim.model.Context;
import com.ibm.wsspi.security.wim.model.Control;
import com.ibm.wsspi.security.wim.model.Root;
import com.ibm.wsspi.security.wim.model.SearchControl;

/**
 * Bridge class for mapping user and group validity methods.
 *
 */
public class ValidBridge {

    private static final TraceComponent tc = Tr.register(ValidBridge.class);

    /**
     * Property mappings.
     */
    private TypeMappings propertyMap = null;

    /**
     * Mappings utility class.
     */
    private BridgeUtils mappingUtils = null;

    public ValidBridge(BridgeUtils mappingUtil) {
        this.mappingUtils = mappingUtil;
        propertyMap = new TypeMappings(mappingUtil);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.security.registry.UserRegistry#isValidUser(java.lang.String)
     */
    @FFDCIgnore(WIMException.class)
    public boolean isValidUser(String inputUserSecurityName) throws RegistryException {
        String methodName = "isValidUser";
        // initialize the return value
        boolean returnValue = false;
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
                // create the realm DataObject
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
            //String inputAttrValue = inputUserSecurityName;
            String outputAttrName = this.propertyMap.getOutputUniqueUserId(idAndRealm.getRealm());

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
                // add MAP(userSecurityName) to the return list of properties
                // d115256
                if (!this.mappingUtils.isIdentifierTypeProperty(this.propertyMap.getOutputUserSecurityName(idAndRealm.getRealm()))) {
                    searchControl.getProperties().add(
                                                      this.propertyMap.getOutputUserSecurityName(idAndRealm.getRealm()));
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
                searchControl.setExpression("//" + Service.DO_ENTITIES + "[@xsi:type='"
                                            + Service.DO_LOGIN_ACCOUNT + "' and "
                                            + this.propertyMap.getInputUserSecurityName(idAndRealm.getRealm())
                                            + "=" + quote + id + quote + "]");

                // Set context to use userFilter if applicable
                Context context = new Context();
                context.set("key", SchemaConstants.USE_USER_FILTER_FOR_SEARCH);
                context.set("value", id);
                root.getContexts().add(context);

                // invoke ProfileService.search with the input root DataGraph
                root = this.mappingUtils.getWimService().search(root);
            }

            // if the output DataGraph contains MAP(userSecurityName)
            List returnList = root.getEntities();
            // f113366
            if (returnList.size() == 1) {
                // set the boolean to true
                returnValue = true;
            }
        }
        // other cases
        catch (WIMException toCatch) {
            // log the Exception
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, methodName + " " + toCatch.getMessage(), toCatch);
            }
            throw new RegistryException(toCatch.getMessage(), toCatch);
        }
        return returnValue;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.security.registry.UserRegistry#isValidGroup(java.lang.String)
     */
    @FFDCIgnore(WIMException.class)
    public boolean isValidGroup(String inputGroupSecurityName) throws RegistryException {
        String methodName = "isValidGroup";
        // initialize the return value
        boolean returnValue = false;
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
                // create the realm DataObject
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
            //String inputAttrValue = inputGroupSecurityName;
            String outputAttrName = this.propertyMap.getOutputUniqueGroupId(idAndRealm.getRealm());

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
                // add MAP(groupSecurityName) to the return list of properties
                // d115913
                if (!this.mappingUtils.isIdentifierTypeProperty(this.propertyMap.getOutputGroupSecurityName(idAndRealm.getRealm()))) {
                    searchControl.getProperties().add(
                                                      this.propertyMap.getOutputGroupSecurityName(idAndRealm.getRealm()));
                }
                // set the "expression" string to "type=Group and MAP(groupSecurityName)="group""
                /*
                 * String quote = "'";
                 * String id = idAndRealm.getId();
                 * if (id.indexOf("'") != -1) {
                 * quote = "\"";
                 * }
                 */

                // d115913
                searchControl.setExpression("//" + Service.DO_ENTITIES + "[@xsi:type='"
                                            + Service.DO_GROUP + "' and " + this.propertyMap.getInputGroupSecurityName(idAndRealm.getRealm())
                                            + "=" + quote + id + quote + "]");

                // Set context to use groupFilter if applicable
                Context context = new Context();
                context.set("key", SchemaConstants.USE_GROUP_FILTER_FOR_SEARCH);
                context.set("value", id);
                root.getContexts().add(context);

                // invoke ProfileService.search with the input root DataGraph
                root = this.mappingUtils.getWimService().search(root);
            }

            // if the output DataGraph contains MAP(groupSecurityName)
            List returnList = root.getEntities();
            // f113366
            if (returnList.size() == 1) {
                // set the boolean to true
                returnValue = true;
            }
        }
        // other cases
        catch (WIMException toCatch) {
            // log the Exception
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, methodName + " " + toCatch.getMessage(), toCatch);
            }
            throw new RegistryException(toCatch.getMessage(), toCatch);
        }
        return returnValue;
    }
}
