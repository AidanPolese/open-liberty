/************** Begin Copyright - Do not add comments here **************

 * IBM Confidential OCO Source Material
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2012, 2013
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U. S. Copyright Office.
 *
 * Change History:
 *
 * Tag           Person           Defect/Feature      Comments
 * ----------    ------           --------------      --------------------------------------------------
 */
package com.ibm.ws.security.wim.registry;

import java.rmi.RemoteException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.ws.bnd.metatype.annotation.Ext;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.security.registry.CertificateMapFailedException;
import com.ibm.ws.security.registry.CertificateMapNotSupportedException;
import com.ibm.ws.security.registry.CustomRegistryException;
import com.ibm.ws.security.registry.EntryNotFoundException;
import com.ibm.ws.security.registry.FederationRegistry;
import com.ibm.ws.security.registry.NotImplementedException;
import com.ibm.ws.security.registry.RegistryException;
import com.ibm.ws.security.registry.SearchResult;
import com.ibm.ws.security.registry.UserRegistry;
import com.ibm.ws.security.wim.ConfigManager;
import com.ibm.ws.security.wim.VMMService;
import com.ibm.ws.security.wim.registry.util.BridgeUtils;
import com.ibm.ws.security.wim.registry.util.DisplayNameBridge;
import com.ibm.ws.security.wim.registry.util.LoginBridge;
import com.ibm.ws.security.wim.registry.util.MembershipBridge;
import com.ibm.ws.security.wim.registry.util.SearchBridge;
import com.ibm.ws.security.wim.registry.util.SecurityNameBridge;
import com.ibm.ws.security.wim.registry.util.UniqueIdBridge;
import com.ibm.ws.security.wim.registry.util.ValidBridge;
import com.ibm.ws.security.wim.util.SchemaConstantsInternal;

@ObjectClassDefinition(pid = "com.ibm.ws.security.wim.registry.WIMUserRegistry", name = Ext.INTERNAL, description = Ext.INTERNAL_DESC, localization = Ext.LOCALIZATION)
@Ext.ObjectClassClass(FederationRegistry.class)
interface WIMUserRegistryConfig {}

/*
 *
 * This component shares configuration with the ConfigManager, which is in another bundle.
 * I'd think the registry adapter should go in core and be one component.
 */
//TODO policy REQUIRE when we count this....
@Component(configurationPolicy = ConfigurationPolicy.IGNORE, property = { "service.vendor=IBM", "com.ibm.ws.security.registry.type=WIM" })
public class WIMUserRegistry implements FederationRegistry, UserRegistry {

    private static final TraceComponent tc = Tr.register(WIMUserRegistry.class);

    public static final String CFG_KEY_REALM = "realm";
    protected static final String DEFAULT_REALM_NAME = "WIMRegistry";
    private String realm = DEFAULT_REALM_NAME;

    @Reference
    ConfigManager configManager;

    @Reference
    VMMService vmmService;

    /**
     * WIM Delimiter to seperate token
     */
    private final static String TOKEN_DELIMETER = "::";

    /**
     * Mapping utility class.
     */
    private BridgeUtils mappingUtils;

    /**
     * Bridge classes for the WIM APIs.
     */
    private LoginBridge loginBridge;

    private DisplayNameBridge displayBridge;

    private SecurityNameBridge securityBridge;

    private UniqueIdBridge uniqueBridge;

    private ValidBridge validBridge;

    private SearchBridge searchBridge;

    private MembershipBridge membershipBridge;

    @Activate
    protected void activate() {

        Map<String, Object> props = configManager.getConfigurationProperties();
        processConfig(props);
        initializeUtils(props);
    }

    private void processConfig(Map<String, Object> urProps) {
        if (urProps == null) {
            throw new NullPointerException("initialize does not support null Properties");
        }

        if (urProps.containsKey(CFG_KEY_REALM))
            realm = ((String[]) urProps.get(CFG_KEY_REALM))[0];
/*
 * realm = (String) urProps.get(CFG_KEY_REALM);
 * if (getRealm() != null) {
 * realm = getRealm();
 * }
 */
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.security.registry.UserRegistry#getType()
     */
    @Override
    public String getType() {
        return "WIM";
    }

    private void initializeUtils(Map<String, Object> configProps) {
        mappingUtils = new BridgeUtils(vmmService, configManager);
        loginBridge = new LoginBridge(mappingUtils);
        displayBridge = new DisplayNameBridge(mappingUtils);
        securityBridge = new SecurityNameBridge(mappingUtils);
        uniqueBridge = new UniqueIdBridge(mappingUtils);
        validBridge = new ValidBridge(mappingUtils);
        searchBridge = new SearchBridge(mappingUtils);
        membershipBridge = new MembershipBridge(mappingUtils);

        mappingUtils.initialize(configProps);
    }

    @Deactivate
    protected void deinitializeUtils() {
        mappingUtils = null;
        loginBridge = null;
        displayBridge = null;
        securityBridge = null;
        uniqueBridge = null;
        validBridge = null;
        searchBridge = null;
        membershipBridge = null;
    }

    /** {@inheritDoc} */
    @Override
    @FFDCIgnore(Exception.class)
    public String checkPassword(final String inputUser, @Sensitive final String inputPassword) throws RegistryException {
        if (loginBridge == null) {
            return null;
        }

        try {
            String returnValue = loginBridge.checkPassword(inputUser, inputPassword);
            return returnValue;
        } catch (Exception excp) {
            if (excp instanceof RegistryException) {
                // New:: Change in Input/Output mapping
                // throw (RegistryException) excp;
                return null;
            } else
                throw new RegistryException(excp.getMessage(), excp);
        }

    }

    /** {@inheritDoc} */
    @Override
    @FFDCIgnore(Exception.class)
    public String mapCertificate(X509Certificate inputCertificate) throws CertificateMapNotSupportedException, CertificateMapFailedException, RegistryException {
        try {
            String returnValue = loginBridge.mapCertificate(inputCertificate);
            return returnValue;
        } catch (Exception excp) {
            if (excp instanceof CertificateMapFailedException)
                throw (CertificateMapFailedException) excp.getCause();

            else if (excp instanceof RegistryException)
                throw (RegistryException) excp;

            else
                throw new RegistryException(excp.getMessage(), excp);
        }
    }

    /** {@inheritDoc} */
    @Override
    @FFDCIgnore(Exception.class)
    public String getRealm() {
        String returnValue = getCoreConfiguration().getDefaultRealmName();

        // New:: Get the primary realm or realm defined directly in the repository.
        if (returnValue == null && loginBridge != null)
            try {
                returnValue = loginBridge.getRealmName();
            } catch (Exception e) {
            }
        if (returnValue == null)
            returnValue = realm;

        return returnValue;
    }

    private ConfigManager getCoreConfiguration() {
        return configManager;
    }

    /** {@inheritDoc} */
    @Override
    @FFDCIgnore(Exception.class)
    public SearchResult getUsers(final String inputPattern, final int inputLimit) throws RegistryException {
        try {

            SearchResult returnValue = searchBridge.getUsers(inputPattern, inputLimit);
            return returnValue;
        } catch (Exception excp) {
            if (excp instanceof RegistryException)
                throw (RegistryException) excp;
            else
                throw new RegistryException(excp.getMessage(), excp);
        }
    }

    /** {@inheritDoc} */
    @Override
    @FFDCIgnore(Exception.class)
    public String getUserDisplayName(final String inputUserSecurityName) throws EntryNotFoundException, RegistryException {
        try {
            // bridge the APIs
            String returnValue = displayBridge.getUserDisplayName(inputUserSecurityName);
            return returnValue;
        } catch (Exception excp) {
            if (excp instanceof EntryNotFoundException)
                throw (EntryNotFoundException) excp;
            else
                throw new RegistryException(excp.getMessage(), excp);
        }
    }

    /**
     * In case of SAF registry securityName returned will be of format <userId>::<token>.
     *
     * @return the userId
     */
    protected String parseUserId(String securityName) {
        int idx = securityName.indexOf(TOKEN_DELIMETER); // Don't use String.split() - way too expensive.
        if (idx > 0) {
            return securityName.substring(0, idx);
        } else {
            return securityName;
        }
    }

    /** {@inheritDoc} */
    @Override
    @FFDCIgnore(Exception.class)
    public String getUniqueUserId(final String inputUserSecurityName) throws EntryNotFoundException, RegistryException {
        HashMap<String, String> result = null;

        try {

            result = uniqueBridge.getUniqueUserId(parseUserId(inputUserSecurityName));

            return result.get("RESULT");

        } catch (Exception excp) {
            if (excp instanceof EntryNotFoundException)
                throw (EntryNotFoundException) excp;
            else
                throw new RegistryException(excp.getMessage(), excp);
        }
    }

    /** {@inheritDoc} */
    @Override
    @FFDCIgnore(Exception.class)
    public String getUserSecurityName(final String inputUniqueUserId) throws EntryNotFoundException, RegistryException {
        HashMap<String, String> result = null;

        // New:: Change to Input/Output property
        try {
            result = uniqueBridge.getUniqueUserId(parseUserId(inputUniqueUserId));

            boolean isURBridgeResult = Boolean.parseBoolean(result.get(SchemaConstantsInternal.IS_URBRIDGE_RESULT));
            if (!isURBridgeResult) {
                String returnValue = result.get("RESULT");
                if (!inputUniqueUserId.equalsIgnoreCase(returnValue))
                    return returnValue;
            }
        } catch (Exception excp) {
            if (excp instanceof EntryNotFoundException)
                throw (EntryNotFoundException) excp;
            else
                throw new RegistryException(excp.getMessage(), excp);
        }

        try {
            // bridge the APIs
            String id = inputUniqueUserId;
            if (id.startsWith("user:") || id.startsWith("group:"))
                // New method added as an alternative of getUserFromUniqueId method of WSSecurityPropagationHelper
                id = getUserFromUniqueID(id);
            String returnValue = securityBridge.getUserSecurityName(id);
            return returnValue;
        } catch (Exception excp) {
            if (excp instanceof EntryNotFoundException)
                throw (EntryNotFoundException) excp;
            else
                throw new RegistryException(excp.getMessage(), excp);
        }
    }

    /** {@inheritDoc} */
    @Override
    @FFDCIgnore(Exception.class)
    public boolean isValidUser(final String inputUserSecurityName) throws RegistryException {
        try {
            boolean returnValue = validBridge.isValidUser(inputUserSecurityName);
            return Boolean.valueOf(returnValue);
        } catch (Exception excp) {
            if (excp instanceof RegistryException)
                throw (RegistryException) excp;

            else
                throw new RegistryException(excp.getMessage(), excp);
        }
    }

    /** {@inheritDoc} */
    @Override
    @FFDCIgnore(Exception.class)
    public SearchResult getGroups(final String inputPattern, final int inputLimit) throws RegistryException {
        try {
            SearchResult returnValue = searchBridge.getGroups(inputPattern, inputLimit);
            return returnValue;
        } catch (Exception excp) {
            if (excp instanceof RegistryException)
                throw (RegistryException) excp;

            else
                throw new RegistryException(excp.getMessage(), excp);
        }
    }

    /** {@inheritDoc} */
    @Override
    @FFDCIgnore(Exception.class)
    public String getGroupDisplayName(final String inputGroupSecurityName) throws EntryNotFoundException, RegistryException {
        try {
            String returnValue = displayBridge.getGroupDisplayName(inputGroupSecurityName);
            return returnValue;
        } catch (Exception excp) {
            if (excp instanceof EntryNotFoundException)
                throw (EntryNotFoundException) excp;

            else if (excp instanceof RegistryException)
                throw (RegistryException) excp;

            else
                throw new RegistryException(excp.getMessage(), excp);
        }
    }

    /** {@inheritDoc} */
    @Override
    @FFDCIgnore(Exception.class)
    public String getUniqueGroupId(final String inputGroupSecurityName) throws EntryNotFoundException, RegistryException {

        try {
            String returnValue = uniqueBridge.getUniqueGroupId(inputGroupSecurityName);
            return returnValue;
        } catch (Exception excp) {
            if (excp instanceof EntryNotFoundException)
                throw (EntryNotFoundException) excp;

            else if (excp instanceof RegistryException)
                throw (RegistryException) excp;
            else
                throw new RegistryException(excp.getMessage(), excp);
        }
    }

    /** {@inheritDoc} */
    @Override
    @FFDCIgnore(Exception.class)
    public List<String> getUniqueGroupIdsForUser(final String inputUniqueUserId) throws EntryNotFoundException, RegistryException {

        try {
            // bridge the APIs
            String id = inputUniqueUserId;
            if (id.startsWith("user:") || id.startsWith("group:"))
                //New method added as an alternative of getUserFromUniqueId method of WSSecurityPropagationHelper
                id = getUserFromUniqueID(id);

            List<String> returnValue = membershipBridge.getUniqueGroupIds(id);
            return returnValue;
        } catch (Exception excp) {
            if (excp instanceof EntryNotFoundException)
                throw (EntryNotFoundException) excp;

            else if (excp instanceof RegistryException)
                throw (RegistryException) excp;
            else
                throw new RegistryException(excp.getMessage(), excp);
        }
    }

    /** {@inheritDoc} */
    @Override
    @FFDCIgnore(Exception.class)
    public String getGroupSecurityName(final String inputUniqueGroupId) throws EntryNotFoundException, RegistryException {
        try {
            // bridge the APIs
            String returnValue = securityBridge.getGroupSecurityName(inputUniqueGroupId);
            return returnValue;
        } catch (Exception excp) {
            if (excp instanceof EntryNotFoundException)
                throw (EntryNotFoundException) excp;

            else if (excp instanceof RegistryException)
                throw (RegistryException) excp;
            else
                throw new RegistryException(excp.getMessage(), excp);
        }
    }

    /** {@inheritDoc} */
    @Override
    @FFDCIgnore(Exception.class)
    public boolean isValidGroup(final String inputGroupSecurityName) throws RegistryException {
        try {
            // bridge the APIs
            boolean returnValue = validBridge.isValidGroup(inputGroupSecurityName);
            return Boolean.valueOf(returnValue);
        } catch (Exception excp) {
            if (excp instanceof RegistryException)
                throw (RegistryException) excp;

            else
                throw new RegistryException(excp.getMessage(), excp);
        }
    }

    /** {@inheritDoc} */
    @Override
    @FFDCIgnore(Exception.class)
    public List<String> getGroupsForUser(final String inputUserSecurityName) throws EntryNotFoundException, RegistryException {
        try {
            // bridge the APIs
            List<String> returnValue = membershipBridge.getGroupsForUser(inputUserSecurityName);
            return returnValue;
        } catch (Exception excp) {
            if (excp instanceof EntryNotFoundException)
                throw (EntryNotFoundException) excp;

            else if (excp instanceof RegistryException)
                throw (RegistryException) excp;

            else
                throw new RegistryException(excp.getMessage(), excp);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.security.registry.UserRegistry#getUsersForGroup(java.lang.String, int)
     */
    @Override
    @FFDCIgnore(Exception.class)
    public SearchResult getUsersForGroup(String groupSecurityName,
                                         int limit) throws NotImplementedException, EntryNotFoundException, CustomRegistryException, RemoteException, RegistryException {
        try {
            // bridge the APIs
            SearchResult returnValue = membershipBridge.getUsersForGroup(groupSecurityName, limit);
            return returnValue;
        } catch (Exception excp) {
            if (excp instanceof EntryNotFoundException)
                throw (EntryNotFoundException) excp;

            else if (excp instanceof RegistryException)
                throw (RegistryException) excp;

            else
                throw new RegistryException(excp.getMessage(), excp);
        }
    }

    //New method added as an alternative of getUserFromUniqueId method of WSSecurityPropagationHelper
    private String getUserFromUniqueID(String id) {
        if (id == null) {
            return "";
        }
        id = id.trim();
        int realmDelimiterIndex = id.indexOf("/");
        if (realmDelimiterIndex < 0) {
            return "";
        } else {
            return id.substring(realmDelimiterIndex + 1);
        }
    }

    @Override
    public void addFederationRegistries(List<UserRegistry> registries) {
        mappingUtils.addFederationRegistries(registries);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.security.registry.FederationRegistry#removeAllFederatedRegistries()
     */
    @Override
    public void removeAllFederatedRegistries() {
        mappingUtils.removeAllFederatedRegistries();
    }
}
