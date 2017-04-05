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
package com.ibm.websphere.collective.controller;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;

/*
 * TODO: Extension
 *  * <tr>
 * <td>{@value #WLP_INSTALLS}</td>
 * <td>A map of WLP install locations and their versions.
 * Option 1: String verison,path e.g. 8.5.5.2,C:\my,path\wlp
 * Option 2: List<String> version=8.5.5.2,path=C:\my,path\wlp (here version could be optional)
 * Option 3: String path,version e.g. C:\my,path\wlp,8552 (here version could be optional)
 * </td>
 * <td>String (If specified, must not be empty)</td>
 * <td>optional</td>
 * </tr>
 */
/**
 * CollectiveRegistrationMBean defines the interface for registering and
 * unregistering servers and hosts with the collective.
 * <p>
 * The ObjectName for this MBean is {@value #OBJECT_NAME}.
 * <p>
 * The registration operations must provide sufficient data (the host
 * authentication information) for a remote client to be able to start
 * the server. The unregistration operation removes all data associated
 * with the host or server from the collective.
 * <p>
 * The values specified to the host authentication information should
 * match the values set in the &lthostAuthInfo&gt configuration element
 * for the registered server. Host authentication information should define
 * sufficient information to authenticate the operating system user,
 * using either the user's password or an SSH private key. Use of keys is
 * encouraged; use of passwords is discouraged.
 * <p>
 * <h1>Supported Properties</h1>
 * <h2>Host Authentication Information</h2>
 * The host authentication information map containing properties that would be needed by a remote client to start the server.
 * Must not be {@code null} during host registration. May be {@code null} during host updates, if there is no data to be changed.
 * When specified, the hostAuthInfo map must be complete. Partial updates of the information are not supported.
 * <p>
 * For example, if the {@value #RPC_USER_PASSWORD} password has changed, the entire set of required properties must be defined to the host update operation:
 * the {@value #RPC_USER_PASSWORD}, the {@value #RPC_USER_PASSWORD}, and any other information.
 * </p>
 * Recognized properties are summarized in the following table. Most property values are non-empty Strings, with types and further constraints list by key.
 * Complete details for each property are documented with each property constant.
 * <table border="1">
 * <tr>
 * <th>Property Name</th>
 * <th>Description</th>
 * <th>Data Type / Format</th>
 * <th>Required/Optional</th>
 * </tr>
 * <tr>
 * <td>{@value #USE_HOST_CREDENTIALS}</td>
 * <td>Indicates whether to inherit the host level credentials for member server RPC</td>
 * <td>Boolean</td>
 * <td>optional; default is false. If this option is set to true, all other RPC credentials specified in hostAuthInfo config element are ignored.</td>
 * </tr>
 * <tr>
 * <td>{@value #RPC_HOST}</td>
 * <td>The fully qualified host name or IP address</td>
 * <td>String</td>
 * <td>optional; defaults to the hostName parameter specified</td>
 * </tr>
 * <tr>
 * <td>{@value #RPC_PORT}</td>
 * <td>The SSH or RPC port number</td>
 * <td>Integer</td>
 * <td>optional; defaults to SSH port (22)</td>
 * </tr>
 * <tr>
 * <td>{@value #RPC_USER}</td>
 * <td>The user ID for the remote connection</td>
 * <td>String</td>
 * <td>required</td>
 * </tr>
 * <tr>
 * <td>{@value #RPC_USER_PASSWORD}</td>
 * <td>The password for the user ID</td>
 * <td>String (either clear text or encoded)</td>
 * <td>required if the {@value #SSH_PRIVATE_KEY} property is not specified; otherwise optional</td>
 * </tr>
 * <tr>
 * <td>{@value #SSH_PRIVATE_KEY}</td>
 * <td>The SSH private key</td>
 * <td>String (either clear text or encoded)</td>
 * <td>required if the {@value #RPC_USER_PASSWORD} property is not specified; otherwise optional</td>
 * </tr>
 * <tr>
 * <td>{@value #SSH_PRIVATE_KEY_PASSWORD}</td>
 * <td>The password for the SSH private key</td>
 * <td>String (either clear text or encoded)</td>
 * <td>required if the SSH private key is password protected; otherwise optional</td>
 * </tr>
 * <tr>
 * <td>{@value #USE_SUDO}</td>
 * <td>Indicates whether using sudo should be used</td>
 * <td>Boolean</td>
 * <td>optional</td>
 * </tr>
 * <tr>
 * <td>{@value #SUDO_USER}</td>
 * <td>The sudo user ID</td>
 * <td>String</td>
 * <td>optional</td>
 * </tr>
 * <tr>
 * <td>{@value #SUDO_USER_PASSWORD}</td>
 * <td>The password for the sudo user</td>
 * <td>String (either clear text or encoded)</td>
 * <td>required if the {@value #SUDO_USER} property specified and a password is required to authenticate as the sudo user; otherwise optional</td>
 * </tr>
 * <tr>
 * <td>{@value #HOST_READ_LIST}</td>
 * <td>The list of locations on the host with allowed read-access.</td>
 * <td>List&lt;String&gt; (The list is allowed to be empty, and string inside it is also allowed to be empty)</td>
 * <td>optional<br>
 * <b>N.B.</b> This value should be specified in the host paths map. If the host paths map is provided, the value specified in the hostAuthInfo map will be ignored.</td>
 * </tr>
 * <tr>
 * <td>{@value #HOST_WRITE_LIST}</td>
 * <td>The list of locations on the host with allowed write-access.</td>
 * <td>List&lt;String&gt; (The list is allowed to be empty, and the string inside it is also allowed to be empty)</td>
 * <td>optional<br>
 * <b>N.B.</b> This value should be specified in the host paths map. If the host paths map is provided, the value specified in the hostAuthInfo map will be ignored.</td>
 * </tr>
 * <tr>
 * <td>{@value #HOST_JAVA_HOME}</td>
 * <td>The location of the java installation to use for this host.</td>
 * <td>String (If specified, must not be empty)</td>
 * <td>optional<br>
 * <b>N.B.</b> This value should be specified in the host paths map. If the host paths map is provided, the value specified in the hostAuthInfo map will be ignored.</td>
 * </tr>
 * </table>
 * 
 * 
 * <h2>Host Paths</h2>
 * The host paths map contains the various paths that the collective controller should be aware of on a given host.
 * The host paths map may be {@code null}. If host paths map is provided, all path values used are from this map.
 * If host paths map is not provided, some path values from hostAuthInfo map will be used (if defined in hostAuthInfo map).
 * See <q>Host Authentication Information</q> for the set of path values which hostAuthInfo map can define.
 * <p>
 * Partial updates of the host paths map is supported, individual keys are replaced with new values. For example,
 * a host update operation can change only the {@value #HOST_READ_LIST} by specifying a new value in the host paths map.
 * In this case, the other values in the host paths map are not changed, but the previous value of {@value #HOST_READ_LIST} is replaced with the new value specified in the host
 * paths map.
 * </p>
 * Recognized properties are summarized in the following table. All properties are optional and are documented by key.
 * Complete details for each property are documented with each property constant.
 * <table border="1">
 * <tr>
 * <th>Property Name</th>
 * <th>Description</th>
 * <th>Data Type / Format</th>
 * <th>Required/Optional</th>
 * </tr>
 * <tr>
 * <td>{@value #HOST_READ_LIST}</td>
 * <td>The list of locations on the host with allowed read-access.</td>
 * <td>List&lt;String&gt; (The list is allowed to be empty, and string inside it is also allowed to be empty)</td>
 * <td>optional</td>
 * </tr>
 * <tr>
 * <td>{@value #HOST_WRITE_LIST}</td>
 * <td>The list of locations on the host with allowed write-access.</td>
 * <td>List&lt;String&gt; (The list is allowed to be empty, and string inside it is also allowed to be empty)</td>
 * <td>optional</td>
 * </tr>
 * <tr>
 * <td>{@value #HOST_JAVA_HOME}</td>
 * <td>The location of the java installation to use for this host.</td>
 * <td>String (If specified, must not be empty)</td>
 * <td>optional</td>
 * </tr>
 * </table>
 * 
 * 
 * <h2>Certificate Creation Properties</h2>
 * Properties Additional properties to control the certificate creation.
 * May be {@code null} or an empty Map.
 * If the Map is {@code null} or empty, all default values will be taken.
 * Recognized properties are summarized in the following table. All property values are non-empty Strings, with further constraints by key.
 * Complete details for each property are documented with each property constant.
 * <table border="1">
 * <tr>
 * <th>Property Name</th>
 * <th>Description</th>
 * <th>Data Type / Format</th>
 * <th>Required/Optional</th>
 * </tr>
 * <tr>
 * <td>{@value #SERVER_IDENTITY_KEYSTORE_PASSWORD}</td>
 * <td>The password for the serverIdentity.jks keystore</td>
 * <td>String (either clear text or encoded)</td>
 * <td>optional; defaults to the specified keystorePassword parameter</td>
 * </tr>
 * <tr>
 * <td>{@value #SERVER_IDENTITY_CERTIFICATE_VALIDITY}</td>
 * <td>The validity period in number of days for the server identity certificate is valid for</td>
 * <td>Integer, unit is days</td>
 * <td>optional; defaults to 5 years or 1825 days</td>
 * </tr>
 * <tr>
 * <td>{@value #COLLECTIVE_TRUST_KEYSTORE_PASSWORD}</td>
 * <td>The password for the collectiveTrust.jks keystore</td>
 * <td>String (either clear text or encoded)</td>
 * <td>optional; defaults to the specified keystorePassword parameter</td>
 * </tr>
 * <tr>
 * <td>{@value #HTTPS_KEYSTORE_PASSWORD}</td>
 * <td>The password for the key.jks keystore</td>
 * <td>String (either clear text or encoded)</td>
 * <td>optional; defaults to the specified keystorePassword parameter</td>
 * </tr>
 * <tr>
 * <td>{@value #HTTPS_CERTIFICATE_SUBJECT}</td>
 * <td>The DN to use as the HTTPS certificate subject</td>
 * <td>String</td>
 * <td>optional; defaults to CN=hostname,OU=serverName,O=ibm,C=us</td>
 * </tr>
 * <tr>
 * <td>{@value #HTTPS_CERTIFICATE_VALIDITY}</td>
 * <td>The validity period in number of days for the HTTPS certificate is valid for</td>
 * <td>Integer, unit is days</td>
 * <td>optional; defaults to 5 years or 1825 days</td>
 * </tr>
 * <tr>
 * <td>{@value #HTTPS_TRUSTSTORE_PASSWORD}</td>
 * <td>The password for the trust.jks keystore</td>
 * <td>String (either clear text or encoded)</td>
 * <td>optional; defaults to the specified keystorePassword parameter</td>
 * </tr>
 * </table>
 * 
 * @ibm-api
 */
public interface CollectiveRegistrationMBean {

    /**
     * A String representing the {@link javax.management.ObjectName} that this MBean maps to.
     */
    String OBJECT_NAME = "WebSphere:feature=collectiveController,type=CollectiveRegistration,name=CollectiveRegistration";

    /**
     * Whether to inherit the host level credentials for member server RPC.
     * <p>
     * If this property is set to true, any RPC operations will use host
     * level credentials.
     * <p>
     * This property is optional. If it is not specified, the default is false. If this option is set to true, all other RPC credentials
     * specified in hostAuthInfo config element are ignored.
     */
    String USE_HOST_CREDENTIALS = "useHostCredentials";

    /**
     * Host name host authentication information map.
     * <p>
     * The host can take on the form of a fully qualified domain name, or
     * an IP address. The host name must be unique within the network and
     * must be the host name on which the remote connection protocol is
     * listening (SSH, or OS specific RPC). The host name should match the
     * defaultHostName or configured value for &lthostAuthInfo&gt in the
     * server.xml.
     * <p>
     * This property is optional. If it is not specified, the hostName
     * specified will be used.
     */
    String RPC_HOST = "rpcHost";

    /**
     * Port for host authentication information map.
     * <p>
     * The port on which the remote connection protocol is listening
     * (SSH, or other supported RPC mechanism). See product documentation for
     * supported RPC mechanisms.
     * <p>
     * This property is optional. Type is Integer. If the property is not
     * specified, the SSH port (22) is assumed.
     */
    String RPC_PORT = "rpcPort";

    /**
     * User ID for host authentication information map.
     * <p>
     * The operating system user ID to use to connect to the host.
     * <p>
     * This property is required.
     */
    String RPC_USER = "rpcUser";

    /**
     * User password for host authentication information map.
     * <p>
     * The password for the operating system user.
     * <p>
     * Either {@value #RPC_USER_PASSWORD} or {@value #SSH_PRIVATE_KEY} should be specified, but not both.
     * If both are specified, an IllegalArgumentException will be thrown.
     * <p>
     * This property is optional.
     */
    String RPC_USER_PASSWORD = "rpcUserPassword";

    /**
     * Collective member type for host authentication information map.
     * <p>
     * The type of member that is joining the collective.
     * <p>
     * This property is optional.
     */
    String COLLECTIVE_MEMBER_TYPE = "collectiveMemberType";

    /**
     * SSH private key for host authentication information map.
     * <p>
     * The SSH private key to use for authenticating the specified
     * operating system user. The SSH private key value is expected
     * to be in the PEM format; a path to a key file is not supported.
     * <p>
     * <h3>Example PEM format</h3>
     * <code>
     * -----BEGIN RSA PRIVATE KEY-----<br>
     * ....<br>
     * -----END RSA PRIVATE KEY-----<br>
     * </code>
     * Any key algorithm supported by the target sshd server is valid.
     * <p>
     * Either {@value #RPC_USER_PASSWORD} or {@value #SSH_PRIVATE_KEY} should be specified, but not both.
     * If both are specified, an IllegalArgumentException will be thrown.
     * <p>
     * This property is optional.
     */
    String SSH_PRIVATE_KEY = "sshPrivateKey";

    /**
     * SSH private key password for host authentication information map.
     * <p>
     * The password for the SSH private key.
     * <p>
     * If this property is set but no {@value #SSH_PRIVATE_KEY} has been set,
     * an IllegalArgumentException will be thrown.
     * <p>
     * This property is optional.
     */
    String SSH_PRIVATE_KEY_PASSWORD = "sshPrivateKeyPassword";

    /**
     * Use sudo key for host authentication information map.
     * <p>
     * If this property is set to true, then sudo will be used to invoke commands.
     * The user to sudo as can be controlled by setting {@value #SUDO_USER}.
     * If {@value #SUDO_USER} is not set, then the user to sudo as will be
     * the configured default sudo user for the target host.
     * <p>
     * If this property is not set, and either {@value #SUDO_USER} or {@value #SUDO_USER_PASSWORD} are set, then {@value #USE_SUDO} is assumed
     * to be true.
     * <p>
     * If this property is set to false, and either {@value #SUDO_USER} or {@value #SUDO_USER_PASSWORD} are set, then an IllegalArgumentException
     * will be thrown.
     * <p>
     * This property is optional. Type is Boolean.
     * Defaults to false if no sudo options are set.
     */
    String USE_SUDO = "useSudo";

    /**
     * sudo user for host authentication information map.
     * <p>
     * Causes sudo to run the as specified user.
     * <p>
     * This property must not be set when {@value #USE_SUDO} is set to false.
     * <p>
     * This property is optional.
     */
    String SUDO_USER = "sudoUser";

    /**
     * sudo user password for host authentication information map.
     * <p>
     * Set this property if the sudo user (explicit or implied) requires
     * a password.
     * <p>
     * This property must not be set when {@value #USE_SUDO} is set to false.
     * <p>
     * This property is optional.
     */
    String SUDO_USER_PASSWORD = "sudoUserPassword";

    /**
     * A list of locations on the host with allowed read-access.
     * Key for host paths map. Also supported in the host authentication information map.
     * <p>
     * Set this property if file access is needed outside of the server
     * instance level. An example scenario is routing (host level context)
     * a file transfer operation through the collective controller into
     * a registered member.
     * <p>
     * This property is optional. Type is List&lt;String&gt;.
     * This property is only valid for {@link registerHost} and {@link updateHost}.
     */
    String HOST_READ_LIST = "hostReadList";

    /**
     * A list of locations on the host with allowed write-access.
     * Key for host paths map. Also supported in the host authentication information map.
     * <p>
     * Set this property if file access is needed outside of the server
     * instance level. An example scenario is routing (host level context)
     * a file transfer operation through the collective controller into a
     * registered member.
     * <p>
     * This property is optional. Type is List&lt;String&gt;.
     * This property is only valid for {@link registerHost} and {@link updateHost}.
     */
    String HOST_WRITE_LIST = "hostWriteList";

    /**
     * The location of the java installation that the collective controller should use for this host.
     * Key for host paths map. Also supported in the host authentication information map.
     * <p>
     * Set this property if a different java instance is to be used by the
     * collective controller when invoking archive expansion on the host
     * during routing file transfer uploads.
     * <p>
     * This property is optional. If specified, must not be empty.
     * This property is only valid for {@link registerHost} and {@link updateHost}.
     */
    String HOST_JAVA_HOME = "hostJavaHome";

    /**
     * The serverIdentity.jks keystore password for the certificate properties.
     * <p>
     * This property is optional.
     */
    String SERVER_IDENTITY_KEYSTORE_PASSWORD = "serverIdentityKeystorePassword";

    /**
     * The validity in days of the serverIdentity certificate for the certificate properties.
     * <p>
     * This property is optional. Type is Integer, unit is days.
     * Defaults to 5 years or 1825 days.
     */
    String SERVER_IDENTITY_CERTIFICATE_VALIDITY = "serverIdentityCertificateValidity";

    /**
     * The collectiveTrust.jks keystore password for the certificate properties.
     * <p>
     * This property is optional.
     */
    String COLLECTIVE_TRUST_KEYSTORE_PASSWORD = "collectiveTrustKeystorePassword";

    /**
     * The key.jks keystore password for the certificate properties.
     * <p>
     * This property is optional.
     */
    String HTTPS_KEYSTORE_PASSWORD = "httpsKeystorePassword";

    /**
     * The subject of the HTTPS certificate for the certificate properties.
     * <p>
     * This property is optional.
     */
    String HTTPS_CERTIFICATE_SUBJECT = "httpsCertificateSubject";

    /**
     * The validity in days of the HTTPS certificate for the certificate properties.
     * <p>
     * This property is optional. Type is Integer, unit is days.
     * Defaults to 5 years or 1825 days.
     */
    String HTTPS_CERTIFICATE_VALIDITY = "httpsCertificateValidity";

    /**
     * The trust.jks keystore password for the certificate properties.
     * <p>
     * This property is optional.
     */
    String HTTPS_TRUSTSTORE_PASSWORD = "httpsTruststorePassword";

    /**
     * Key for the serverIdentity.jks entry in the map returned by join and replicate.
     * <p>
     * This entry should be written to:
     * ${server.config.dir}/resources/collective/serverIdentity.jks
     */
    String KEYSTORE_SERVER_IDENTITY_JKS = "serverIdentity.jks";

    /**
     * Key for the serverIdentity.pfx entry in the map returned by join and replicate.
     * <p>
     * This entry should be written to:
     * ${server.config.dir}/resources/collective/serverIdentity.pfx
     */
    String KEYSTORE_SERVER_IDENTITY_PFX = "serverIdentity.pfx";

    /**
     */
    String X509_CERTIFICATE = "x509.cert";

    /**
     * Key for the collectiveTrust.jks entry in the map returned by join and replicate.
     * <p>
     * This entry should be written to:
     * ${server.config.dir}/resources/collective/collectiveTrust.jks
     */
    String KEYSTORE_COLLECTIVE_TRUST_JKS = "collectiveTrust.jks";

    /**
     * Key for the collectiveTrust.pfx entry in the map returned by join and replicate.
     * <p>
     * This entry should be written to:
     * ${server.config.dir}/resources/collective/collectiveTrust.pfx
     */
    String KEYSTORE_COLLECTIVE_TRUST_PFX = "collectiveTrust.pfx";

    /**
     * Key for the key.jks entry in the map returned by join and replicate.
     * <p>
     * This entry should be written to:
     * ${server.config.dir}/resources/security/key.jks
     */
    String KEYSTORE_KEY_JKS = "key.jks";

    /**
     * Key for the key.pfx entry in the map returned by join and replicate.
     * <p>
     * This entry should be written to:
     * ${server.config.dir}/resources/security/key.pfx
     */
    String KEYSTORE_KEY_PFX = "key.pfx";

    /**
     * Key for the trust.jks entry in the map returned by join and replicate.
     * <p>
     * This entry should be written to:
     * ${server.config.dir}/resources/security/trust.jks
     */
    String KEYSTORE_TRUST_JKS = "trust.jks";

    /**
     * Key for the trust.pfx entry in the map returned by join and replicate.
     * <p>
     * This entry should be written to:
     * ${server.config.dir}/resources/security/trust.pfx
     */
    String KEYSTORE_TRUST_PFX = "trust.pfx";

    /**
     * Key for the rootKeys.jks entry in the map returned by replicate.
     * <p>
     * This entry should be written to:
     * ${server.config.dir}/resources/collective/rootKeys.jks
     */
    String KEYSTORE_ROOT_KEYS_JKS = "rootKeys.jks";

    /**
     * Key for the collective.uuid entry in the map returned by replicate.
     * <p>
     * This entry should be written to:
     * ${server.config.dir}/resources/collective/collective.uuid
     */
    String FILE_COLLECTIVE_UUID = "collective.uuid";

    /**
     * Key for the collective.name entry in the map returned by replicate.
     * <p>
     * This entry should be written to:
     * ${server.config.dir}/resources/collective/collective.name
     */
    String FILE_COLLECTIVE_NAME = "collective.name";

    /**
     * The subject of the certificate for the genKey certificate.
     * <p>
     * This property is optional.
     */
    String CERTIFICATE_SUBJECT = "certificateSubject";

    /**
     * The validity in days of the certificate for the genKey certificate.
     * <p>
     * This property is optional. Type is Integer, unit is days.
     * Defaults to 5 years or 1825 days.
     */
    String CERTIFICATE_VALIDITY = "certificateValidity";

    /**
     * Registers a host with the collective. The host name provided is converted to
     * lowercase when it is registered.
     * <p>
     * The host authentication information requires either the user password
     * or the SSH private key.
     * 
     * @param hostName The case insensitive host name. Must not be {@code null} or an empty string.
     * @param hostAuthInfo See the <Q>Host Authentication Information</Q> {@link CollectiveRegistrationMBean}.
     *            Must not be {@code null}.
     * @param hostPaths See the <Q>Host Paths</Q> {@link CollectiveRegistrationMBean}.
     *            May be {@code null}.
     * 
     * @throws IOException If there was any problem completing the operation
     * @throws IllegalArgumentException If any of the parameters are not valid or
     *             if any of the keys in the properties map are unrecognized
     * @throws IllegalStateException If the host was already registered
     */
    void registerHost(String hostName, Map<String, Object> hostAuthInfo, Map<String, Object> hostPaths)
                    throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * Same as {@link #registerHost(String, Map, Map)}, except hostPaths is null. Some host paths are
     * may be specified in hostAuthInfo.
     */
    void registerHost(String hostName, Map<String, Object> hostAuthInfo)
                    throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * Updates the authentication information for a known host with the collective.
     * <p>
     * The host authentication information requires either the user password
     * or the SSH private key.
     * 
     * @param hostName The case insensitive host name. Must not be {@code null} or an empty string.
     * @param hostAuthInfo See the <Q>Host Authentication Information</Q> {@link CollectiveRegistrationMBean}.
     *            May be {@code null} if there is no authentication information to change.
     * @param hostPaths See the <Q>Host Paths</Q> {@link CollectiveRegistrationMBean}.
     *            May be {@code null} if there is no path information to change.
     * 
     * @throws IOException If there was any problem completing the operation
     * @throws IllegalArgumentException If any of the parameters are not valid or
     *             if any of the keys in the properties map are unrecognized
     * @throws IllegalStateException If the host was not registered
     */
    void updateHost(String hostName, Map<String, Object> hostAuthInfo, Map<String, Object> hostPaths)
                    throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * Same as {@link #registerHost(String, Map, Map)}, except hostPaths is null. Some host paths are
     * may be specified in hostAuthInfo.
     */
    void updateHost(String hostName, Map<String, Object> hostAuthInfo)
                    throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * Unregisters a host from the collective. Any servers on this
     * host will be automatically removed from any clusters for which they are a member.
     * 
     * @param hostName The case insensitive host name. Must not be {@code null} or an empty string.
     * 
     * @throws IOException If there was any problem completing the operation
     * @throws IllegalArgumentException If any of the parameters are not valid
     * @throws IllegalStateException If the host was not registered
     */
    void unregisterHost(String hostName)
                    throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * Join the specified server to the collective as a member.
     * <p>
     * This will register the server and generate the security credentials
     * required by the server to communicate with the collective.
     * <p>
     * A server is uniquely identified by its name, the host on which it
     * resides, and the wlpUserDir within which it resides. The wlpUserDir is
     * used in the repository path to differentiate between servers of the same
     * name on the same host.
     * <p>
     * The host authentication information requires either the user password
     * or the SSH private key.
     * 
     * @param hostName The host name. Must not be {@code null} or an empty string. The
     *            host name set here will directly control where the server's
     *            information is stored within the repository. This host name should
     *            match the host name set to the defaultHostName variable for the
     *            server's server.xml
     *            Must not be {@code null} or an empty string.
     * @param wlpUserDir The canonical path for the user directory of server.
     *            This should match the WLP_USER_DIR environment variable for the server.
     *            Must not be {@code null} or an empty string. Must not have a trailing slash.
     *            Must not be encoded.
     * @param serverName The server name. Must not be {@code null} or an empty string.
     * @param wlpInstallDir The Liberty install directory for this server.
     *            Must not be {@code null} or an empty string.
     * @param keystorePassword The password to protect the created keystores.
     *            Must not be {@code null}. Each keystore's password can be
     *            overridden individually by specifying additional certProperties.
     * @param certProperties See the <Q>Certificate Creation Properties</Q> {@link CollectiveRegistrationMBean}
     * @param hostAuthInfo See the <Q>Host Authentication Information</Q> {@link CollectiveRegistrationMBean}
     * @return A Map of byte[] mapped to a keystore name. Each element in the map
     *         represents the bytes of a keystore file that should be laid down on disk.
     * @throws IOException If there was any problem completing the operation
     * @throws IllegalArgumentException If any of the parameters are not valid or if any of the keys in the properties maps are unrecognized
     * @throws IllegalStateException If the server was already registered
     * @throws CertificateException If there is a problem creating the certificates
     * @throws KeyStoreException If there is a problem creating the keystore
     */
    Map<String, byte[]> join(String hostName, String wlpUserDir,
                             String serverName, String wlpInstallDir,
                             String keystorePassword,
                             Map<String, Object> certProperties,
                             Map<String, Object> hostAuthInfo)
                    throws IOException, IllegalArgumentException, IllegalStateException,
                    CertificateException, KeyStoreException;

    /**
     * Generate a collective controller client keystore.
     * <p>
     * This will register the server and generate the security credentials
     * required by the server to communicate with the collective.
     * <p>
     * A server is uniquely identified by its name, the host on which it
     * resides, and the wlpUserDir within which it resides. The wlpUserDir is
     * used in the repository path to differentiate between servers of the same
     * name on the same host.
     * <p>
     * The host authentication information requires either the user password
     * or the SSH private key.
     * 
     * @param keystorePassword The password to protect the created keystores.
     *            Must not be {@code null}. Each keystore's password can be
     *            overridden individually by specifying additional certProperties.
     * @param certProperties See the <Q>Certificate Creation Properties</Q> {@link CollectiveRegistrationMBean}
     * @param addMemberRootSigner if true, add member root signer to the generated keystore
     * @return A Map of byte[] mapped to a keystore name. The return element in the map
     *         represents the bytes of a keystore file that should be laid down on disk.
     * @throws IOException If there was any problem completing the operation
     * @throws IllegalArgumentException If any of the parameters are not valid or if any of the keys in the properties maps are unrecognized
     * @throws IllegalStateException If the server was already registered
     * @throws CertificateException If there is a problem creating the certificates
     * @throws KeyStoreException If there is a problem creating the keystore
     */
    Map<String, byte[]> genKey(String keystorePassword,
                               Map<String, Object> certProperties, Boolean addMemberRootSigner)
                    throws IOException, IllegalArgumentException, IllegalStateException,
                    CertificateException, KeyStoreException;

    /**
     * Replicates the collective controller configuration it order to allow
     * the specified server to act as a collective controller.
     * <p>
     * This will register the server and generate the security credentials
     * required by the server to communicate with the collective.
     * <p>
     * A server is uniquely identified by its name, the host on which it
     * resides, and the wlpUserDir within which it resides. The wlpUserDir is
     * used in the repository path to differentiate between servers of the same
     * name on the same host.
     * <p>
     * The host authentication information requires either the user password
     * or the SSH private key.
     * 
     * @param hostName The host name. Must not be {@code null} or an empty string. The
     *            host name set here will directly control where the server's
     *            information is stored within the repository. This host name should
     *            match the host name set to the defaultHostName variable for the
     *            server's server.xml
     * @param wlpUserDir The canonical path for the user directory of server.
     *            This should match the WLP_USER_DIR environment variable for the server.
     *            Must not be {@code null} or an empty string. Must not have a trailing slash.
     *            Must not be encoded.
     * @param serverName The server name. Must not be {@code null} or an empty string.
     * @param wlpInstallDir The Liberty install directory for this server.
     *            Must not be {@code null} or an empty string.
     * @param keystorePassword The password to protect the created keystores.
     *            Must not be {@code null}. Each keystore's password can be
     *            overridden individually by specifying additional certProperties.
     * @param certProperties See the <Q>Certificate Creation Properties</Q> {@link CollectiveRegistrationMBean}
     * @param hostAuthInfo See the <Q>Host Authentication Information</Q> {@link CollectiveRegistrationMBean}
     * @return A Map of byte[] mapped to a keystore name. Each element in the map
     *         represents the bytes of a keystore file that should be laid down on disk.
     * @throws IOException If there was any problem completing the operation
     * @throws IllegalArgumentException If any of the parameters are not valid or if any of the keys in the properties maps are unrecognized
     * @throws IllegalStateException If the server was already registered
     * @throws CertificateException If there is a problem creating the certificates
     * @throws KeyStoreException If there is a problem creating the keystore
     */
    Map<String, byte[]> replicate(String hostName, String wlpUserDir,
                                  String serverName, String wlpInstallDir,
                                  String keystorePassword,
                                  Map<String, Object> certProperties,
                                  Map<String, Object> hostAuthInfo)
                    throws IOException, IllegalArgumentException, IllegalStateException,
                    CertificateException, KeyStoreException;

    /**
     * Removes the server from the collective. The server will be
     * automatically removed from any clusters for which it is a member.
     * <p>
     * A server is uniquely identified by its name, the host on which it
     * resides, and the wlpUserDir within which it resides. The wlpUserDir is
     * used in the repository path to differentiate between servers of the same
     * name on the same host.
     * 
     * @param hostName The host name. Must not be {@code null} or an empty string.
     *            This host name should match the host name set to the defaultHostName
     *            variable for the server's server.xml
     * @param wlpUserDir The canonical path for the user directory of server.
     *            This should match the WLP_USER_DIR environment variable for the server.
     *            Must not be {@code null} or an empty string. Must not have a trailing slash.
     *            Must not be encoded.
     * @param serverName The server name. Must not be {@code null} or an empty string.
     * 
     * @throws IOException If there was any problem completing the operation
     * @throws IllegalArgumentException If any of the parameters are not valid
     * @throws IllegalStateException If the server was not registered
     */
    void remove(String hostName, String wlpUserDir, String serverName)
                    throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * Avow the server to the collective. The server will be allowed to
     * authenticate to the collective as long as it has the correct credentials.
     * <p>
     * A server is uniquely identified by its name, the host on which it
     * resides, and the wlpUserDir within which it resides. The wlpUserDir is
     * used in the repository path to differentiate between servers of the same
     * name on the same host.
     * 
     * @param hostName The host name. Must not be {@code null} or an empty string.
     *            This host name should match the host name set to the defaultHostName
     *            variable for the server's server.xml
     * @param wlpUserDir The canonical path for the user directory of server.
     *            This should match the WLP_USER_DIR environment variable for the server.
     *            Must not be {@code null} or an empty string. Must not have a trailing slash.
     *            Must not be encoded.
     * @param serverName The server name. Must not be {@code null} or an empty string.
     * 
     * @throws IOException If there was any problem completing the operation
     * @throws IllegalArgumentException If any of the parameters are not valid
     * @throws IllegalStateException If the server was not registered
     */
    void avow(String hostName, String wlpUserDir, String serverName)
                    throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * Disavow the server from the collective. The server will be prevented
     * from authenticating to the collective controllers.
     * <p>
     * A server is uniquely identified by its name, the host on which it
     * resides, and the wlpUserDir within which it resides. The wlpUserDir is
     * used in the repository path to differentiate between servers of the same
     * name on the same host.
     * 
     * @param hostName The host name. Must not be {@code null} or an empty string.
     *            This host name should match the host name set to the defaultHostName
     *            variable for the server's server.xml
     * @param wlpUserDir The canonical path for the user directory of server.
     *            This should match the WLP_USER_DIR environment variable for the server.
     *            Must not be {@code null} or an empty string. Must not have a trailing slash.
     *            Must not be encoded.
     * @param serverName The server name. Must not be {@code null} or an empty string.
     * 
     * @throws IOException If there was any problem completing the operation
     * @throws IllegalArgumentException If any of the parameters are not valid
     * @throws IllegalStateException If the server was not registered
     */
    void disavow(String hostName, String wlpUserDir, String serverName)
                    throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * Registers the liberty installed directory for the host to the collective repository.
     * It will add a new branch per host to collective repository
     * /sys.was.collectives/local/hosts/installdirs/<type>/<install-dir> and
     * <install-dir> will be URL encoded.
     * Example of an (unencoded) install-dir value: /opt/wlp
     * 
     * @param hostName The case insensitive host name. Must not be {@code null} or an empty string.
     * @param type The installable type, valid types are wlp, jre and other. Default is other.
     * @param wlpInstallDir The canonical path for the install directory.
     *            Must not be {@code null} or an empty string. Must not have a trailing slash.
     *            Must not be encoded.
     * 
     * @throws IOException If there was any problem completing the operation.
     * @throws IllegalArgumentException If any of the parameters are not valid.
     * @throws IllegalStateException If the host was unregistered or
     *             the install directory was registered already.
     */
    void registerInstallDir(String hostName, String type, String wlpInstallDir) throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * Unregisters the liberty installed directory for the host from the collective repository.
     * <p>
     * 
     * @param hostName The case insensitive host name. Must not be {@code null} or an empty string.
     * @param type The installable type, valid types including wlp, jre and other. Default is other.
     * @param wlpInstallDir The canonical path for the install directory.
     *            Must not be {@code null} or an empty string. Must not have a trailing slash.
     *            Must not be encoded.
     * 
     * @throws IOException If there was any problem completing the operation.
     * @throws IllegalArgumentException If any of the parameters are not valid.
     * @throws IllegalStateException If the host or install directory was unregistered.
     */
    void unregisterInstallDir(String hostName, String type, String wlpInstallDir) throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * List all liberty installed directories under the specified host.
     * <p>
     * 
     * @param hostName The case insensitive host name. Must not be {@code null} or an empty string.
     * @param type The installable type, valid types including wlp, jre and other. Default is other.
     * 
     * @throws IOException If there was any problem completing the operation.
     * @throws IllegalArgumentException If the parameter is not valid.
     * @throws IllegalStateException If the host was unregistered.
     */
    List<String> listInstallDirs(String hostName, String type) throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * List all hosts configured in the collective repository .
     * <p>
     * 
     * @throws IOException If there was any problem completing the operation.
     */
    List<String> listHosts() throws IOException, IllegalStateException;

    /**
     * List all user directories of server for the specified host.
     * <p>
     * 
     * @param hostName The case insensitive host name. Must not be {@code null} or an empty string.
     * 
     * @throws IOException If there was any problem completing the operation
     * @throws IllegalArgumentException If any of the parameters are not valid
     * @throws IllegalStateException If the host was unregistered.
     */
    List<String> listUserDirs(String hostName) throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * List all liberty servers created under the specified host and user directory.
     * <p>
     * 
     * @param hostName The case insensitive host name. Must not be {@code null} or an empty string.
     * @param wlpUserDir The canonical path for the user directory of server.
     *            Must not be {@code null} or an empty string. Must not have a trailing slash.
     *            Must not be encoded.
     * 
     * @throws IOException If there was any problem completing the operation
     * @throws IllegalArgumentException If any of the parameters are not valid.
     * @throws IllegalStateException If the host was unregistered
     */
    List<String> listServers(String hostName, String wlpUserDir) throws IOException, IllegalArgumentException, IllegalStateException;

}
