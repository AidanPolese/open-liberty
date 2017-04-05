/************** Begin Copyright - Do not add comments here **************
 *
 *  
 * IBM Confidential OCO Source Material
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2012, 2013
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U. S. Copyright Office.
 * 
 * Change History:
 * 
 * Tag           Person        Defect/Feature      Comments
 * -------       ------        --------------      --------------------------------------------------
 *		 ankit_jain    92798		   Change the NLS formatting method for exception message
 * 04/24/2104    ankit_jain    128429              Cleanup findbugs.exclude file
 * 04/21/2016    speddapa      203909              USER LOGIN FAILURE WHEN UNIQUEUSERIDMAPPING INPUTPROPERTY SET TO NON DEFAULT VALUES.
 */
package com.ibm.ws.security.wim.xpath.ldap.util;

import java.util.Set;
import java.util.Stack;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.security.wim.ras.WIMMessageHelper;
import com.ibm.websphere.security.wim.ras.WIMMessageKey;
import com.ibm.ws.security.wim.adapter.ldap.LdapConfigManager;
import com.ibm.ws.security.wim.adapter.ldap.LdapEntity;
import com.ibm.ws.security.wim.xpath.ParenthesisNode;
import com.ibm.ws.security.wim.xpath.mapping.datatype.LogicalNode;
import com.ibm.ws.security.wim.xpath.mapping.datatype.PropertyNode;
import com.ibm.ws.security.wim.xpath.mapping.datatype.XPathLogicalNode;
import com.ibm.ws.security.wim.xpath.mapping.datatype.XPathNode;
import com.ibm.ws.security.wim.xpath.util.XPathTranslateHelper;
import com.ibm.wsspi.security.wim.exception.PropertyNotDefinedException;
import com.ibm.wsspi.security.wim.exception.WIMException;
import com.ibm.wsspi.security.wim.model.Entity;
import com.ibm.wsspi.security.wim.model.Group;
import com.ibm.wsspi.security.wim.model.Person;
import com.ibm.wsspi.security.wim.model.PersonAccount;

public class LdapXPathTranslateHelper implements XPathTranslateHelper {
    /**
     * IBM Copyright string
     */
    static final String COPYRIGHT_NOTICE = com.ibm.websphere.security.wim.copyright.IBMCopyright.COPYRIGHT_NOTICE_SHORT_2012;

    private LdapConfigManager ldapConfigMgr = null;
    private Set<String> entityTypes = null;
    private Stack<String> logOps = null;
    private static final TraceComponent tc = Tr.register(LdapXPathTranslateHelper.class);

    public LdapXPathTranslateHelper(Set<String> entityTypes, LdapConfigManager ldapCfgMgr) {
        logOps = new Stack<String>();
        this.entityTypes = entityTypes;
        this.ldapConfigMgr = ldapCfgMgr;
    }

    @Override
    public void genSearchString(StringBuffer searchExpBuffer, XPathNode node) throws WIMException {
        switch (node.getNodeType()) {
            case XPathNode.NODE_PROPERTY:
                genSearchString(searchExpBuffer, (PropertyNode) node);
                break;
            case XPathNode.NODE_PARENTHESIS:
                genSearchString(searchExpBuffer, (ParenthesisNode) node);
                break;
            case XPathNode.NODE_LOGICAL:
                genSearchString(searchExpBuffer, (LogicalNode) node);
                break;
            default:
                break;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.wim.xpath.util.XPathTranslateHelper#genSearchString(java.lang.StringBuffer, com.ibm.ws.wim.xpath.mapping.datatype.PropertyNode)
     */
    private void genSearchString(StringBuffer searchExpBuffer, PropertyNode propNode) throws WIMException {
        String propName = propNode.getName();
        String ldapAttrName = null;
        String dataType = null;
        Set<String> attrs = ldapConfigMgr.getAttributeNames(entityTypes, propName);
        // attrs known to be non-null as getAttributesNames creates a new set
        if (attrs.size() == 1) {
            ldapAttrName = (String) attrs.toArray()[0];

            Object value = null;

            for (String entityType : entityTypes) {
                Entity entity = null;
                if (entityType != null) {
                    if (entityType.equalsIgnoreCase("PersonAccount") || entityType.equalsIgnoreCase("LoginAccount"))
                        entity = new PersonAccount();
                    else if (entityType.equalsIgnoreCase("Person"))
                        entity = new Person();
                    else if (entityType.equalsIgnoreCase("Group"))
                        entity = new Group();
                    else
                        entity = new Entity();

                    dataType = entity.getDataType(propName);
                }

                if (dataType != null) {
                    value = ldapConfigMgr.getLdapValue(propNode.getValue(), dataType, ldapAttrName);
                } else {
                    throw new PropertyNotDefinedException(WIMMessageKey.PROPERTY_NOT_DEFINED,
                                    Tr.formatMessage(tc, WIMMessageKey.PROPERTY_NOT_DEFINED, WIMMessageHelper.generateMsgParms(propName)));
                }
            }

            short operator = ldapConfigMgr.getOperator(propNode.getOperator());
            value = ldapConfigMgr.escapeSpecialCharacters((String) value);
            Object[] arguments = { ldapAttrName, value };
            String searchCondtion = ldapConfigMgr.CONDITION_FORMATS[operator].format(arguments);
            searchExpBuffer.append(searchCondtion);

        } else if (attrs.size() > 1) {
            searchExpBuffer.append("|");
            for (String entityType : entityTypes) {
                LdapEntity ldapEntity = ldapConfigMgr.getLdapEntity(entityType);
                ldapAttrName = ldapConfigMgr.getAttributeName(ldapEntity, propName);
                /*
                 * if (ldapAttrName == null) {
                 * //search property is not defined
                 * throw new PropertyNotDefinedException(WIMMessageKey.PROPERTY_NOT_DEFINED, Tr.formatMessage(
                 * tc,
                 * WIMMessageKey.PROPERTY_NOT_DEFINED,
                 * WIMMessageHelper.generateMsgParms(propName)));
                 * }
                 */
                Object value = null;

                Entity entity = null;
                if (entityType != null) {
                    if (entityType.equalsIgnoreCase("PersonAccount"))
                        entity = new PersonAccount();
                    else if (entityType.equalsIgnoreCase("Person"))
                        entity = new Person();
                    else if (entityType.equalsIgnoreCase("Group"))
                        entity = new Group();
                    else
                        entity = new Entity();

                    dataType = entity.getDataType(propName);
                }

                value = ldapConfigMgr.getLdapValue(propNode.getValue(), dataType, ldapConfigMgr.getSyntax(ldapAttrName));
                short operator = ldapConfigMgr.getOperator(propNode.getOperator());
                value = ldapConfigMgr.escapeSpecialCharacters((String) value);
                Object[] arguments = { ldapAttrName, value };
                String searchCondtion = ldapConfigMgr.CONDITION_FORMATS[operator].format(arguments);
                searchExpBuffer.append("(");
                searchExpBuffer.append(searchCondtion);
                searchExpBuffer.append(")");
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.wim.xpath.util.XPathTranslateHelper#genSearchString(java.lang.StringBuffer, com.ibm.ws.wim.xpath.mapping.datatype.LogicalNode)
     */
    private void genSearchString(StringBuffer searchExpBuffer, LogicalNode logicalNode) throws WIMException {
        boolean write = false;

        if (logOps.isEmpty()) {
            write = true;
            logOps.push(logicalNode.getOperator());
        } else if ((logOps.peek()) != logicalNode.getOperator()) {
            write = true;
            logOps.push(logicalNode.getOperator());
        }

        if (write) {
            if (logicalNode.getOperator().equals(XPathLogicalNode.OP_AND)) {

                searchExpBuffer.append("(&");
            } else {
                searchExpBuffer.append("(|");
            }
        }

        genStringChild(searchExpBuffer, (XPathNode) logicalNode.getLeftChild());
        genStringChild(searchExpBuffer, (XPathNode) logicalNode.getRightChild());

        if (write) {
            searchExpBuffer.append(')');
            logOps.pop();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.wim.xpath.util.XPathTranslateHelper#genSearchString(java.lang.StringBuffer, com.ibm.ws.wim.xpath.mapping.datatype.ParenthesisNode)
     */
    private void genSearchString(StringBuffer searchExpBuffer, ParenthesisNode parenNode) throws WIMException {
        XPathNode child = (XPathNode) parenNode.getChild();
        genStringChild(searchExpBuffer, child);
    }

    private void genStringChild(StringBuffer searchExpBuffer, XPathNode child) throws WIMException {
        switch (child.getNodeType()) {
            case XPathNode.NODE_PROPERTY:
                searchExpBuffer.append('(');
                genSearchString(searchExpBuffer, (PropertyNode) child);
                searchExpBuffer.append(')');
                break;
            case XPathNode.NODE_PARENTHESIS:
                genSearchString(searchExpBuffer, (ParenthesisNode) child);
                break;
            case XPathNode.NODE_LOGICAL:
                genSearchString(searchExpBuffer, (LogicalNode) child);
                break;
            default:
                break;
        }
    }
}
