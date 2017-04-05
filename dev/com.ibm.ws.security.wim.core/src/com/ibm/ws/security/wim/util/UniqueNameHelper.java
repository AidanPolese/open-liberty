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
 *	         ankit_jain	92798		    Change the NLS formatting method for exception message
 * 01/12/2014    rzunzarr       109887              Create API implementation
 */
package com.ibm.ws.security.wim.util;

import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.websphere.security.wim.ras.WIMMessageHelper;
import com.ibm.websphere.security.wim.ras.WIMMessageKey;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.wsspi.security.wim.exception.InvalidPropertyValueException;
import com.ibm.wsspi.security.wim.exception.InvalidUniqueNameException;
import com.ibm.wsspi.security.wim.exception.MissingMandatoryPropertyException;
import com.ibm.wsspi.security.wim.exception.WIMException;
import com.ibm.wsspi.security.wim.model.Entity;

/**
 * @author Ankit Jain
 */
public class UniqueNameHelper
{
    /**
     * IBM Copyright string
     */
    static final String COPYRIGHT_NOTICE = com.ibm.websphere.security.wim.copyright.IBMCopyright.COPYRIGHT_NOTICE_SHORT_2012;

    private static final TraceComponent tc = Tr.register(UniqueNameHelper.class);

    /**
     * The character used to separate the components of entity UniqueName.
     */
    public final static String ENTITY_DN_SEPARATOR = ",";

    @Trivial
    public static String isDN(String uniqueName)
    {
        if (uniqueName == null) {
            return null;
        }
        return getValidDN(uniqueName);
    }

    /**
     * Formats the specified entity unique name and also check it using the LDAP DN syntax rule.
     * The formatting including remove
     * 
     * @param The unique name of the entity to be formatted.
     * 
     * @return The formatted entity unique name.
     * 
     * @exception InvalidUniqueNameException if the specified member DN does not pass the syntax check.
     */
    public static String formatUniqueName(String uniqueName) throws InvalidUniqueNameException
    {
        String validName = getValidUniqueName(uniqueName);
        if (validName == null) {
            if (tc.isErrorEnabled()) {
                Tr.error(tc, WIMMessageKey.INVALID_UNIQUE_NAME_SYNTAX, WIMMessageHelper.generateMsgParms(uniqueName));
            }
            throw new InvalidUniqueNameException(WIMMessageKey.INVALID_UNIQUE_NAME_SYNTAX, Tr.formatMessage(
                                                                                                            tc,
                                                                                                            WIMMessageKey.INVALID_UNIQUE_NAME_SYNTAX,
                                                                                                            WIMMessageHelper.generateMsgParms(uniqueName)));
        }
        else {
            return validName;
        }
    }

    /**
     * Validates and returns the fomratted unique name.
     * Extra spaces will be removed from the unique name during formatting.
     * If the specified unique name does not satisfy the LDAP DN syntax rule, null will be returned.
     * 
     * @param uniqueName The unique name to be formatted.
     * 
     * @return The valid and formatted unique name. null will be returned if the unique name is invalid.
     */
    @Trivial
    public static String getValidUniqueName(String uniqueName)
    {
        return getValidDN(uniqueName);
    }

    @FFDCIgnore(InvalidNameException.class)
    private static String getValidDN(String uniqueName) {
        LdapName name = null;
        try {
            name = new LdapName(uniqueName);
            return name.getPrefix(name.size()).toString();
        } catch (InvalidNameException e) {
            return null;
        }
    }

    /**
     * Returns the unique name based on the input value.
     * 
     * @param RDNs a list possible rdns
     * @param entity the entity DataObject which contains the entity data
     * @param parentDN the unique name of the parent
     * @param throwExc if true, exception is thrown if an uniqueName can not be constructed
     * @return the unique name, null if uniqueName can not be constructed and throwExc is false
     * @exception WIMException if throwExc is true
     */
    public static String constructUniqueName(String[] RDNs, Entity entity, String parentDN, boolean throwExc)
                    throws WIMException
    {
        boolean found = false;
        String uniqueName = null;
        String missingPropName = null;
        for (int i = 0; i < RDNs.length; i++) {
            String[] localRDNs = getRDNs(RDNs[i]);
            int size = localRDNs.length;
            String[] RDNValues = new String[size];
            boolean findValue = true;
            for (int j = 0; j < size && findValue; j++) {
                String thisRDN = localRDNs[j];
                String thisRDNValue = String.valueOf(entity.get(thisRDN));
                if (thisRDNValue == null || "null".equalsIgnoreCase(thisRDNValue)) {
                    findValue = false;
                    missingPropName = thisRDN;
                }
                else if (thisRDNValue.trim().length() == 0) {
                    String qualifiedEntityType = entity.getTypeName();
                    throw new InvalidPropertyValueException(WIMMessageKey.CAN_NOT_CONSTRUCT_UNIQUE_NAME,
                                    Tr.formatMessage(tc, WIMMessageKey.CAN_NOT_CONSTRUCT_UNIQUE_NAME, WIMMessageHelper.generateMsgParms(thisRDN, qualifiedEntityType)));
                }
                else {
                    RDNValues[j] = thisRDNValue;
                }
            }
            if (findValue) {
                if (!found) {
                    uniqueName = constructUniqueName(localRDNs, RDNValues, parentDN);
                    found = true;
                }
                else if (throwExc) {
                    String qualifiedEntityType = entity.getTypeName();
                    throw new InvalidUniqueNameException(WIMMessageKey.CAN_NOT_CONSTRUCT_UNIQUE_NAME,
                                    Tr.formatMessage(tc, WIMMessageKey.CAN_NOT_CONSTRUCT_UNIQUE_NAME, WIMMessageHelper.generateMsgParms(RDNs[i], qualifiedEntityType)));
                }
            }
        }
        if (missingPropName != null && !found && throwExc) {
            throw new MissingMandatoryPropertyException(WIMMessageKey.MISSING_MANDATORY_PROPERTY,
                            Tr.formatMessage(tc, WIMMessageKey.MISSING_MANDATORY_PROPERTY, WIMMessageHelper.generateMsgParms(missingPropName)));
        }
        return uniqueName;
    }

    /**
     * Returns the unique name based on the input value. If multiple RDNs are provided, a "+" sign will be added.
     * For example, the list of RDNs contains "uid" and "email", the string array of RDNValues contains "joedoe" and "jdoe@acom.com"
     * and the parentDN has the value of "cn=users,dc=acom,dc=com". The return unique name will be
     * "uid=joedoe+email=jdoe@acom.com,cn=users,dc=acom,dc=com"
     * 
     * @param RDNs a string array contains the property name of the rdn
     * @param RDNValues a string array contains the values of the rdn properties.
     * @param parentDN the unique name of the parent
     * @return the unqiue name
     * @throws InvalidEntityUniqueNameException
     */
    public static String constructUniqueName(String[] RDNs, String[] RDNValues, String parentDN)
                    throws InvalidUniqueNameException
    {
        int length;
        if (RDNs != null) {
            length = RDNs.length;
        }
        else {
            return null;
        }
        if (length != RDNValues.length || length == 0)
            return null;

        StringBuffer RDN = new StringBuffer();

        for (int i = 0; i < length; i++) {

            if (RDNValues[i] != null && RDNValues[i].length() != 0) {
                if (i != 0 && RDN.length() != 0) {
                    RDN.append("+");
                }
                RDN.append(RDNs[i] + "=" + escapeAttributeValue(RDNValues[i]));
            }
        }

        String DN = null;
        if (parentDN.length() == 0) {
            DN = RDN.toString();
        }
        else {
            DN = RDN.toString() + "," + parentDN;
        }

        return formatUniqueName(DN);
    }

    /**
     * Given the value of an attribute, returns a string suitable for inclusion in a DN.
     * 
     * If the value is a string, this is accomplished by using backslash (\) to escape
     * the following characters: , = + < > # ; " \
     * 
     * @param value
     * @return
     */
    private static String escapeAttributeValue(String value) {
        final String escapees = ",=+<>#;\"\\";
        char[] chars = value.toCharArray();
        StringBuffer buf = new StringBuffer(2 * value.length());

        // Find leading and trailing whitespace.
        int lead; // index of first char that is not leading whitespace
        for (lead = 0; lead < chars.length; lead++) {
            if (!isWhitespace(chars[lead])) {
                break;
            }
        }
        int trail; // index of last char that is not trailing whitespace
        for (trail = chars.length - 1; trail >= 0; trail--) {
            if (!isWhitespace(chars[trail])) {
                break;
            }
        }

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if ((i < lead) || (i > trail) || (escapees.indexOf(c) >= 0)) {
                buf.append('\\');
            }
            buf.append(c);
        }
        return new String(buf);
    }

    private static boolean isWhitespace(char c) {
        return (c == ' ' || c == '\r');
    }

    /**
     * Returns array of RDN attribute types from the given RDN string.
     * RDN string may contain multiple RDNs separated by "+".
     * For example, "uid+mail" string will return [uid, mail].
     * 
     * @param rdnStr the RDN string
     * @return the array of separated RDNs.
     */
    public static String[] getRDNs(String rdnStr)
    {
        StringTokenizer st = new StringTokenizer(rdnStr, "+");
        ArrayList<String> list = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            String rdn = st.nextToken();
            list.add(rdn);
        }
        return list.toArray(new String[0]);
    }
}
