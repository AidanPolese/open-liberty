package com.ibm.ws.collective.security;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import com.ibm.ws.collective.utils.RepositoryPathUtility;

/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */

/**
 * A Collective DN takes the form of:<br>
 * cn=serverName,l=urlEncodedUserDir,l=hostName,ou=role[controller|member],o=collectiveUUID,dc=com.ibm.ws.collective
 * <p>
 * The RDN element types were chosen based on standard RDNs: http://www.ietf.org/rfc/rfc2253.txt
 */
public class CollectiveDNUtil {
    public static final String COLLECTIVE_REALM = "collective";

    /**
     * Top-level (0th) DN element. Must equal {@value #COLLECTIVE_FLAG}.
     */
    public static final String RDN_COLLECTIVE_FLAG = "dc";

    /**
     * 1st DN element. Must equal the collective's UUID.
     */
    public static final String RDN_COLLECTIVE_UUID = "o";

    /**
     * 2nd DN element. Must equal either {@value #COLLECTIVE_ROLE_CONTROLLER} or {@value #COLLECTIVE_ROLE_MEMBER}.
     */
    public static final String RDN_COLLECTIVE_ROLE = "ou";

    /**
     * 3rd DN element. Indicates the server's host. The host must be valid within the collective.
     */
    public static final String RDN_HOST_NAME = "l";

    /**
     * 4th DN element. Indicates the server's URL encoded user directory. The user directory must be valid for the host.
     */
    public static final String RDN_USER_DIR = "l";

    /**
     * 5th DN element. Indicates the server's name. The server must be valid within the collective.
     */
    public static final String RDN_SERVER_NAME = "cn";

    /**
     * The Collective DN flag. A DN missing this flag is not a Collective DN.
     */
    public static final String COLLECTIVE_FLAG = "com.ibm.ws.collective";

    /**
     * The Collective role flag indicating the server is a Collective Controller.
     */
    public static final String COLLECTIVE_ROLE_CONTROLLER = "controller";

    /**
     * The Collective role flag indicating the server is a Collective member.
     */
    public static final String COLLECTIVE_ROLE_MEMBER = "member";

    /**
     * The Collective role flag indicating the certificate is the Collective Root certificate.
     */
    public static final String CONTROLLER_ROLE_COLLECTIVE_ROOT_CERT = "controllerRoot";

    /**
     * The Collective role flag indicating the certificate is the Member Root certificate.
     */
    public static final String COLLECTIVE_ROLE_MEMBER_ROOT_CERT = "memberRoot";

    /**
     * Common method to build a root certificate DN.
     * 
     * @param root
     * @param collectiveUUID
     * @return
     */
    private static String buildCommonRootDN(String root, String collectiveUUID) {
        if (collectiveUUID == null) {
            throw new IllegalArgumentException("buildCommonRootDN: collectiveUUID is null");
        }
        if (collectiveUUID.isEmpty()) {
            throw new IllegalArgumentException("buildCommonRootDN: collectiveUUID is empty");
        }
        StringBuilder sb = new StringBuilder(RDN_COLLECTIVE_ROLE);
        sb.append("=");
        sb.append(root);
        sb.append(",");
        sb.append(RDN_COLLECTIVE_UUID);
        sb.append("=");
        sb.append(collectiveUUID);
        sb.append(",");
        sb.append(RDN_COLLECTIVE_FLAG);
        sb.append("=");
        sb.append(COLLECTIVE_FLAG);
        return sb.toString();
    }

    /**
     * Creates the Collective Root certificate DN which represents the
     * specified collective UUID.
     * 
     * @param UUID
     * @return
     */
    public static String buildControllerRootCertificateDN(String collectiveUUID) {
        return buildCommonRootDN(CONTROLLER_ROLE_COLLECTIVE_ROOT_CERT, collectiveUUID);
    }

    /**
     * Creates the Member Root certificate DN which represents the
     * specified collective UUID.
     * 
     * @param UUID
     * @return
     */
    public static String buildMemberRootCertificateDN(String collectiveUUID) {
        return buildCommonRootDN(COLLECTIVE_ROLE_MEMBER_ROOT_CERT, collectiveUUID);
    }

    /**
     * Common method to build a member DN.
     * 
     * @param serverName
     * @param userDir
     * @param String
     * @return
     */
    private static String buildCommonMemberDN(String serverName, String userDir, String hostName, String role, String collectiveUUID) {
        if (serverName == null) {
            throw new IllegalArgumentException("buildCommonMemberDN: serverName is null");
        }
        if (serverName.isEmpty()) {
            throw new IllegalArgumentException("buildCommonMemberDN: serverName is empty");
        }
        if (userDir == null) {
            throw new IllegalArgumentException("buildCommonMemberDN: userDir is null");
        }
        if (userDir.isEmpty()) {
            throw new IllegalArgumentException("buildCommonMemberDN: userDir is empty");
        }
        if (hostName == null) {
            throw new IllegalArgumentException("buildCommonMemberDN: hostName is null");
        }
        if (hostName.isEmpty()) {
            throw new IllegalArgumentException("buildCommonMemberDN: hostName is empty");
        }
        if (collectiveUUID == null) {
            throw new IllegalArgumentException("buildCommonMemberDN: collectiveUUID is null");
        }
        if (collectiveUUID.isEmpty()) {
            throw new IllegalArgumentException("buildCommonMemberDN: collectiveUUID is empty");
        }

        StringBuilder sb = new StringBuilder(RDN_SERVER_NAME);
        sb.append("=");
        sb.append(serverName);
        sb.append(",");
        sb.append(RDN_USER_DIR);
        // The user dir entry has to be in quotes so that a space (encoded as +)
        // is not interpreted as part of the DN, but rather a part of the RDN value
        sb.append("=\"");
        sb.append(RepositoryPathUtility.getURLEncodedPath(userDir));
        sb.append("\",");
        sb.append(RDN_HOST_NAME);
        sb.append("=");
        // The host name must always be in lower case in the DN
        sb.append(hostName.toLowerCase());
        sb.append(",");
        sb.append(RDN_COLLECTIVE_ROLE);
        sb.append("=");
        sb.append(role);
        sb.append(",");
        sb.append(RDN_COLLECTIVE_UUID);
        sb.append("=");
        sb.append(collectiveUUID);
        sb.append(",");
        sb.append(RDN_COLLECTIVE_FLAG);
        sb.append("=");
        sb.append(COLLECTIVE_FLAG);
        return sb.toString();
    }

    /**
     * Creates the Collective Controller certificate DN which represents the
     * specified collective UUID.
     * 
     * @param UUID
     * @return
     */
    public static String buildControllerDN(String serverName, String userDir, String hostName, String collectiveUUID) {
        return buildCommonMemberDN(serverName, userDir, hostName, COLLECTIVE_ROLE_CONTROLLER, collectiveUUID);
    }

    /**
     * Creates the Collective Member certificate DN which represents the
     * specified collective UUID.
     * 
     * @param UUID
     * @return
     */
    public static String buildMemberDN(String serverName, String userDir, String hostName, String collectiveUUID) {
        return buildCommonMemberDN(serverName, userDir, hostName, COLLECTIVE_ROLE_MEMBER, collectiveUUID);
    }

    /**
     * Validates the DN is an Collective DN. If the DN is not a recognized
     * Collective DN, then an InvalidNameException will be thrown. Note that
     * this method only validates the syntax of the DN, it does not perform
     * any validation on the contents which may vary based the subject's true
     * identity.
     * 
     * @param dn
     * @throws InvalidNameException
     */
    public static void validateCollectiveRootDNSyntax(String dn) throws InvalidNameException {
        LdapName name = new LdapName(dn);

        if (name.size() != 3) {
            throw new InvalidNameException("Validation of the Collective Root DN failed. Incorrect number of elements. Size was: " + name.size() + ". DN: " + dn);
        }

        Rdn shouldBeDC = name.getRdn(0); // dc
        if (!RDN_COLLECTIVE_FLAG.equalsIgnoreCase(shouldBeDC.getType())) {
            throw new InvalidNameException("Validation of the Collective Root DN failed. 0th element type was not " + RDN_COLLECTIVE_FLAG + ". DN: " + dn);
        }
        if (!COLLECTIVE_FLAG.equals(shouldBeDC.getValue())) {
            throw new InvalidNameException("Validation of the Collective Root DN failed. DC element value was not " + COLLECTIVE_FLAG + ". Value is " + shouldBeDC.getValue()
                                           + ". DN: " + dn);
        }

        Rdn shouldBeO = name.getRdn(1); // o
        if (!RDN_COLLECTIVE_UUID.equalsIgnoreCase(shouldBeO.getType())) {
            throw new InvalidNameException("Validation of the Collective Root DN failed. 1st element type was not " + RDN_COLLECTIVE_UUID + ". DN: " + dn);
        }

        Rdn shouldBeOU = name.getRdn(2); // ou
        if (!RDN_COLLECTIVE_ROLE.equalsIgnoreCase(shouldBeOU.getType())) {
            throw new InvalidNameException("Validation of the Collective Root DN failed. 2nd element type was not " + RDN_COLLECTIVE_ROLE + ". DN: " + dn);
        }
        if (!CONTROLLER_ROLE_COLLECTIVE_ROOT_CERT.equals(shouldBeOU.getValue()) &&
            !COLLECTIVE_ROLE_MEMBER_ROOT_CERT.equals(shouldBeOU.getValue())) {
            throw new InvalidNameException("Validation of the Collective Root DN failed. OU element value was not recognized. Value is " + shouldBeOU.getValue() + ". DN: " + dn);
        }
    }

    /**
     * Validates the DN is an Collective DN. If the DN is not a recognized
     * Collective DN, then an InvalidNameException will be thrown. Note that
     * this method only validates the syntax of the DN, it does not perform
     * any validation on the contents which may vary based the subject's true
     * identity.
     * 
     * @param dn
     * @throws InvalidNameException
     */
    public static void validateCollectiveDNSyntax(String dn) throws InvalidNameException {
        LdapName name = new LdapName(dn);

        if (name.size() != 6) {
            throw new InvalidNameException("Validation of the Collective DN failed. Incorrect number of elements. Size was: " + name.size() + ". DN: " + dn);
        }

        Rdn shouldBeDC = name.getRdn(0); // dc
        if (!RDN_COLLECTIVE_FLAG.equalsIgnoreCase(shouldBeDC.getType())) {
            throw new InvalidNameException("Validation of the Collective DN failed. 0th element type was not " + RDN_COLLECTIVE_FLAG + ". DN: " + dn);
        }
        if (!COLLECTIVE_FLAG.equals(shouldBeDC.getValue())) {
            throw new InvalidNameException("Validation of the Collective DN failed. DC element value was not " + COLLECTIVE_FLAG + ". Value is " + shouldBeDC.getValue() + ". DN: "
                                           + dn);
        }

        Rdn shouldBeO = name.getRdn(1); // o
        if (!RDN_COLLECTIVE_UUID.equalsIgnoreCase(shouldBeO.getType())) {
            throw new InvalidNameException("Validation of the Collective DN failed. 1st element type was not " + RDN_COLLECTIVE_UUID + ". DN: " + dn);
        }

        Rdn shouldBeOU = name.getRdn(2); // ou
        if (!RDN_COLLECTIVE_ROLE.equalsIgnoreCase(shouldBeOU.getType())) {
            throw new InvalidNameException("Validation of the Collective DN failed. 2nd element type was not " + RDN_COLLECTIVE_ROLE + ". DN: " + dn);
        }
        if (!COLLECTIVE_ROLE_CONTROLLER.equals(shouldBeOU.getValue()) &&
            !COLLECTIVE_ROLE_MEMBER.equals(shouldBeOU.getValue())) {
            throw new InvalidNameException("Validation of the Collective DN failed. OU element value was not recognized. Value is " + shouldBeOU.getValue() + ". DN: " + dn);
        }

        Rdn shouldBeL = name.getRdn(3); // l
        if (!RDN_HOST_NAME.equalsIgnoreCase(shouldBeL.getType())) {
            throw new InvalidNameException("Validation of the Collective DN failed. 3rd element type was not " + RDN_HOST_NAME + ". DN: " + dn);
        }

        shouldBeL = name.getRdn(4); // l
        if (!RDN_USER_DIR.equalsIgnoreCase(shouldBeL.getType())) {
            throw new InvalidNameException("Validation of the Collective DN failed. 4th element type was not " + RDN_USER_DIR + ". DN: " + dn);
        }

        Rdn shouldBeCN = name.getRdn(5); // cn
        if (!RDN_SERVER_NAME.equalsIgnoreCase(shouldBeCN.getType())) {
            throw new InvalidNameException("Validation of the Collective DN failed. 5th element type was not " + RDN_SERVER_NAME + ". DN: " + dn);
        }
    }

    /**
     * Gets the server name from the Collective DN.
     * 
     * @param dn
     * @return
     * @throws InvalidNameException
     */
    public static String getServerName(String dn) throws InvalidNameException {
        validateCollectiveDNSyntax(dn);

        LdapName name = new LdapName(dn);
        return (String) name.getRdn(5).getValue();
    }

    /**
     * Gets the URL encoded user dir from the Collective DN.
     * <p>
     * The LDAP classes will automatically strip any quotes, so this
     * is handled automatically for us.
     * 
     * @param dn
     * @return
     * @throws InvalidNameException
     */
    public static String getURLEncodedUserDir(String dn) throws InvalidNameException {
        validateCollectiveDNSyntax(dn);

        LdapName name = new LdapName(dn);
        return (String) name.getRdn(4).getValue();
    }

    /**
     * Gets the host name from the Collective DN.
     * <p>
     * No manipulation is done to the host name value from the DN.
     * 
     * @param dn
     * @return
     * @throws InvalidNameException
     */
    public static String getHostName(String dn) throws InvalidNameException {
        validateCollectiveDNSyntax(dn);

        LdapName name = new LdapName(dn);
        return (String) name.getRdn(3).getValue();
    }

    /**
     * Gets the collective role from the Collective DN.
     * 
     * @param dn
     * @return
     * @throws InvalidNameException
     */
    public static String getCollectiveRole(String dn) throws InvalidNameException {
        validateCollectiveDNSyntax(dn);

        LdapName name = new LdapName(dn);
        return (String) name.getRdn(2).getValue();
    }

    /**
     * Gets the collective UUID from the Collective DN.
     * 
     * @param dn
     * @return
     * @throws InvalidNameException
     */
    public static String getCollectiveUUID(String dn) throws InvalidNameException {
        validateCollectiveDNSyntax(dn);

        LdapName name = new LdapName(dn);
        return (String) name.getRdn(1).getValue();
    }

}
