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
package com.ibm.ws.webcontainer.security.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.security.DeclareRoles;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.container.service.annotations.FragmentAnnotations;
import com.ibm.ws.container.service.annotations.WebAnnotations;
import com.ibm.ws.container.service.config.ServletConfigurator;
import com.ibm.ws.container.service.config.ServletConfigurator.ConfigItem;
import com.ibm.ws.container.service.config.ServletConfigurator.ConfigSource;
import com.ibm.ws.container.service.config.ServletConfiguratorHelper;
import com.ibm.ws.container.service.config.WebFragmentInfo;
import com.ibm.ws.javaee.dd.common.EnvEntry;
import com.ibm.ws.javaee.dd.common.RunAs;
import com.ibm.ws.javaee.dd.common.SecurityRole;
import com.ibm.ws.javaee.dd.common.SecurityRoleRef;
import com.ibm.ws.javaee.dd.web.WebApp;
import com.ibm.ws.javaee.dd.web.WebFragment;
import com.ibm.ws.javaee.dd.web.common.AuthConstraint;
import com.ibm.ws.javaee.dd.web.common.FormLoginConfig;
import com.ibm.ws.javaee.dd.web.common.LoginConfig;
import com.ibm.ws.javaee.dd.web.common.Servlet;
import com.ibm.ws.javaee.dd.web.common.ServletMapping;
import com.ibm.ws.javaee.dd.web.common.UserDataConstraint;
import com.ibm.ws.javaee.dd.webbnd.WebBnd;
import com.ibm.ws.javaee.dd.webext.WebExt;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.anno.info.AnnotationInfo;
import com.ibm.wsspi.anno.info.AnnotationValue;
import com.ibm.wsspi.anno.info.ClassInfo;
import com.ibm.wsspi.webcontainer.metadata.WebModuleMetaData;

/**
 * This class is called by the web container in order to extract the security information
 * from the different sources: web.xml, web-fragment.xml and annotations.
 */

public class SecurityServletConfiguratorHelper implements ServletConfiguratorHelper, SecurityMetadata {
    public static final String AUTH_METHOD_KEY = "auth-method";
    public static final String FORM_LOGIN_CONFIG_KEY = "form-login-config";
    public static final String REALM_NAME_KEY = "realm-name";
    public static final String LOGIN_CONFIG_KEY = "login-config";
    public static final String RUN_AS_KEY = "run-as";
    public static final String SERVLET_NAME_KEY = "servlet-name";
    public static final String SERVLET_KEY = "servlet";
    public static final String SECURITY_CONSTRAINT_KEY = "security-constraint";
    public static final String AUTH_CONSTRAINT_KEY = "auth-constraint";
    public static final String USER_DATA_CONSTRAINT_KEY = "user-data-constraint";
    public static final String DENY_UNCOVERED_HTTP_METHODS_KEY = "deny-uncovered-http-methods";
    protected static final String SYNC_TO_OS_THREAD_ENV_ENTRY_KEY = "com.ibm.websphere.security.SyncToOSThread";

    private static final TraceComponent tc = Tr.register(SecurityServletConfiguratorHelper.class);

    private ServletConfigurator configurator;

    private static final String ALL_ROLES_MARKER = "*";
    private SecurityConstraintCollection securityConstraintCollection;
    protected LoginConfiguration loginConfiguration;;
    private List<String> allRoles = new ArrayList<String>();
    private final Map<String, Map<String, String>> securityRoleRefsByServlet = new HashMap<String, Map<String, String>>();
    private Map<String, String> urlPatternToServletName = new HashMap<String, String>();
    private final Map<String, String> servletNameToRunAsRole = new HashMap<String, String>();
    private boolean syncToOSThread = false;
    private boolean denyUncoveredHttpMethods = false;

    public SecurityServletConfiguratorHelper(ServletConfigurator configurator) {
        this.configurator = configurator;
    }

    @Override
    public void configureInit() {
        // nothing at the moment
    }

    @Override
    public void configureFromWebApp(WebApp webApp) {

        configureSecurity(webApp.getSecurityConstraints(),
                          webApp.getLoginConfig(),
                          webApp.getSecurityRoles(),
                          webApp.getServletMappings(),
                          webApp.getEnvEntries(),
                          webApp.isSetDenyUncoveredHttpMethods());
        if (webApp.isSetDenyUncoveredHttpMethods()) {
            setDenyUncoveredHttpMethods(true);
        }
        for (Servlet servlet : webApp.getServlets()) {
            processSecurityRoleRefs(servlet.getServletName(), servlet.getSecurityRoleRefs());
            processRunAs(servlet);

        }
    }

    @Override
    public void configureFromWebFragment(WebFragmentInfo webFragmentItem) {
        WebFragment webFragment = webFragmentItem.getWebFragment();
        configureSecurity(webFragment.getSecurityConstraints(),
                          webFragment.getLoginConfig(),
                          webFragment.getSecurityRoles(),
                          webFragment.getServletMappings(),
                          webFragment.getEnvEntries(),
                          false);
        for (Servlet servlet : webFragment.getServlets()) {
            processSecurityRoleRefs(servlet.getServletName(), servlet.getSecurityRoleRefs());
            processRunAs(servlet);
        }
    }

    @Override
    /*
     * Process only the @DeclareRoles annotation
     * The other security annotations are handled by the
     * web container
     */
    public void configureFromAnnotations(WebFragmentInfo webFragmentItem) throws UnableToAdaptException {
        WebAnnotations webAnnotations = configurator.getWebAnnotations();
        FragmentAnnotations fragmentAnnotations = webAnnotations.getFragmentAnnotations(webFragmentItem);
        processSecurityRoles(webAnnotations, fragmentAnnotations.selectAnnotatedClasses(DeclareRoles.class));
    }

    @Override
    public void configureDefaults() throws UnableToAdaptException {
        //set default values
        if (loginConfiguration == null) {
            loginConfiguration = new LoginConfigurationImpl(LoginConfiguration.BASIC, null, null);
        }
    }

    @Override
    public void configureWebBnd(WebBnd webBnd) throws UnableToAdaptException {
        // nothing at the moment
    }

    @Override
    public void configureWebExt(WebExt webExt) throws UnableToAdaptException {
        // nothing at the moment
    }

    @Override
    public void finish() {
        WebModuleMetaData wmmd = (WebModuleMetaData) configurator.getFromModuleCache(WebModuleMetaData.class);
        configurator = null;
        wmmd.setSecurityMetaData(this);
    }

    /**
     * Configure the security metadata from the given information.
     * 
     * @param securityConstraints a list of security constraints
     * @param loginConfig the login configuration
     * @param securityRoles a list of security roles
     * @param servletMappings a list of servlet mappings
     */
    private void configureSecurity(List<com.ibm.ws.javaee.dd.web.common.SecurityConstraint> securityConstraints,
                                   LoginConfig loginConfig,
                                   List<SecurityRole> securityRoles,
                                   List<ServletMapping> servletMappings,
                                   List<EnvEntry> envEntries,
                                   boolean denyUncoveredHttpMethods) {

        processSecurityConstraints(securityConstraints, denyUncoveredHttpMethods);
        processLoginConfig(loginConfig);
        processSecurityRoles(securityRoles);
        processURLPatterns(servletMappings);
        processEnvEntries(envEntries);
        processDenyUncoveredHttpMethods(denyUncoveredHttpMethods);
    }

    /**
     * 
     */

    private void processDenyUncoveredHttpMethods(boolean denyUncoveredHttpMethods) {
        if (denyUncoveredHttpMethods == false) {
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "deny-uncovered-http-methods element NOT found");
            }
        } else {
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "deny-uncovered-http-methods element IS found");
            }
        }
    }

    /**
     * Creates a list of zero or more security constraint objects that represent the
     * security-constraint elements in web.xml and/or web-fragment.xml files.
     * 
     * @param securityConstraints a list of security constraints
     */
    private void processSecurityConstraints(List<com.ibm.ws.javaee.dd.web.common.SecurityConstraint> archiveSecurityConstraints, boolean denyUncoveredHttpMethods) {
        List<SecurityConstraint> securityConstraints = new ArrayList<SecurityConstraint>();
        for (com.ibm.ws.javaee.dd.web.common.SecurityConstraint archiveSecurityConstraint : archiveSecurityConstraints) {
            SecurityConstraint securityConstraint = createSecurityConstraint(archiveSecurityConstraint, denyUncoveredHttpMethods);
            securityConstraints.add(securityConstraint);
        }
        if (securityConstraintCollection == null) {
            securityConstraintCollection = new SecurityConstraintCollectionImpl(securityConstraints);
        } else {
            securityConstraintCollection.addSecurityConstraints(securityConstraints);
        }
    }

    /**
     * Creates a login configuration object that represents a login-config element in web.xml and/or web-fragment.xml files.
     * Note that the following elements can be present once inside the login-config, so only their first occurrence is processed:
     * <li>auth-method</li>
     * <li>realm-name</li>
     * <li>form-login</li>
     * If multiple web fragments specify these elements with different values and they are absent from the web.xml, this will result
     * in an error that fails the application install.
     * 
     * @param loginConfig the login configuration
     */
    protected void processLoginConfig(LoginConfig loginConfig) {

        if (loginConfig != null) {
            String authenticationMethod = loginConfig.getAuthMethod();
            if (authenticationMethod != null) {
                Map<String, ConfigItem<String>> authMethodMap = configurator.getConfigItemMap(AUTH_METHOD_KEY);
                ConfigItem<String> existingAuthMethod = authMethodMap.get(LOGIN_CONFIG_KEY);
                if (existingAuthMethod == null) {
                    authMethodMap.put(LOGIN_CONFIG_KEY, configurator.createConfigItem(authenticationMethod));
                } else {
                    this.configurator.validateDuplicateConfiguration(LOGIN_CONFIG_KEY, AUTH_METHOD_KEY, authenticationMethod, existingAuthMethod);
                }
            } else {
                //Default to BASIC
                authenticationMethod = LoginConfiguration.BASIC;
            }

            String realmName = loginConfig.getRealmName();
            if (realmName != null) {
                Map<String, ConfigItem<String>> realmNameMap = configurator.getConfigItemMap(REALM_NAME_KEY);
                ConfigItem<String> existingRealmName = realmNameMap.get(LOGIN_CONFIG_KEY);
                if (existingRealmName == null) {
                    realmNameMap.put(LOGIN_CONFIG_KEY, configurator.createConfigItem(realmName));
                } else {
                    this.configurator.validateDuplicateConfiguration(LOGIN_CONFIG_KEY, REALM_NAME_KEY, realmName, existingRealmName);
                }
            }

            FormLoginConfig formLoginConfig = loginConfig.getFormLoginConfig();
            FormLoginConfiguration formLoginConfiguration = null;
            if (formLoginConfig != null) {
                Map<String, ConfigItem<FormLoginConfig>> formLoginConfigMap = configurator.getConfigItemMap(FORM_LOGIN_CONFIG_KEY);
                ConfigItem<FormLoginConfig> existingFormLoginConfig = formLoginConfigMap.get(LOGIN_CONFIG_KEY);
                if (existingFormLoginConfig == null) {
                    formLoginConfigMap.put(LOGIN_CONFIG_KEY, configurator.createConfigItem(loginConfig.getFormLoginConfig()));

                    formLoginConfiguration = createFormLoginConfiguration(loginConfig);
                } else {
                    this.configurator.validateDuplicateConfiguration(LOGIN_CONFIG_KEY, FORM_LOGIN_CONFIG_KEY, formLoginConfig,
                                                                     existingFormLoginConfig);
                }
            }
            loginConfiguration = new LoginConfigurationImpl(authenticationMethod, realmName, formLoginConfiguration);
        }
    }

    /**
     * Create a list of roles that represent the security-role elements in the web.xml and/or web-fragment.xml
     * 
     * @param securityRoles a list of security roles
     */
    private void processSecurityRoles(List<SecurityRole> securityRoles) {
        for (SecurityRole securityRole : securityRoles) {
            if (!allRoles.contains(securityRole.getRoleName())) {
                allRoles.add(securityRole.getRoleName());
            }
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "allRoles: " + allRoles);
        }
    }

    /**
     * Create a list of roles that represent the @DeclareRoles
     * 
     * @param webAnnotations the main link for web module annotation related services
     * @param securityRoles a list of classes containing the @DeclareRole annotation
     */
    private void processSecurityRoles(WebAnnotations webAnnotations, Set<String> classesWithSecurityRoles) throws UnableToAdaptException {
        for (String classWithSecurityRole : classesWithSecurityRoles) {
            ClassInfo classInfo = webAnnotations.getClassInfo(classWithSecurityRole);
            final String fullyQualifiedClassName = classWithSecurityRole;

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "@DeclareRoles found on class ", fullyQualifiedClassName);
            }

            AnnotationInfo declareRolesAnnotation = classInfo.getAnnotation(DeclareRoles.class);
            if (declareRolesAnnotation != null) {
                AnnotationValue value = declareRolesAnnotation.getValue("value");
                final List<? extends AnnotationValue> roleValues = value.getArrayValue();
                for (AnnotationValue roleValue : roleValues) {
                    String role = roleValue.getStringValue();
                    if (!allRoles.contains(role)) {
                        allRoles.add(role);
                    }
                }
            }
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "allRoles: " + allRoles);
        }
    }

    /**
     * Creates a map of url patterns to serlvet names that represents the url-pattern elements inside the servlet-mapping elements
     * in the web.xml and/or web-fragment.xml
     * 
     * @param servletMappings the servlet mappings
     */
    private void processURLPatterns(List<ServletMapping> servletMappings) {
        for (ServletMapping servletMapping : servletMappings) {
            String servletName = servletMapping.getServletName();
            List<String> urlPatterns = servletMapping.getURLPatterns();
            if (urlPatterns != null) {
                for (String pattern : urlPatterns) {
                    urlPatternToServletName.put(pattern, servletName);
                }
            }
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "urlPatternToServletName: " + urlPatternToServletName);
        }
    }

    /**
     * Process the env-entry's from the application's deployment descriptor.
     */
    private void processEnvEntries(List<EnvEntry> envEntries) {
        if (envEntries != null) {
            for (EnvEntry envEntry : envEntries) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "processing envEntry", envEntry.getName(), envEntry.getValue());
                }
                if (SYNC_TO_OS_THREAD_ENV_ENTRY_KEY.equals(envEntry.getName())) {
                    syncToOSThread = Boolean.parseBoolean(envEntry.getValue());
                }
            }
        }
    }

    /**
     * Creates a map of security-role-ref elements to servlet name.
     * 
     * @param servletName the name of the servlet
     * @param servletSecurityRoleRefs a list of security-role-ref elements in the given servlet
     */
    private void processSecurityRoleRefs(String servletName, List<SecurityRoleRef> servletSecurityRoleRefs) {
        Map<String, String> securityRoleRefs = new HashMap<String, String>();
        securityRoleRefsByServlet.put(servletName, securityRoleRefs);

        for (SecurityRoleRef secRoleRef : servletSecurityRoleRefs) {
            if (secRoleRef.getLink() == null) {
                Tr.warning(tc, "MISSING_SEC_ROLE_REF_ROLE_LINK", new Object[] { servletName, secRoleRef.getName() });
            } else if (allRoles.contains(secRoleRef.getLink())) {
                securityRoleRefs.put(secRoleRef.getName(), secRoleRef.getLink());
            } else {
                Tr.warning(tc, "INVALID_SEC_ROLE_REF_ROLE_LINK", new Object[] { servletName, secRoleRef.getLink(), secRoleRef.getName() });
            }
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "securityRoleRefsByServlet: " + securityRoleRefsByServlet);
        }
    }

    /**
     * Creates the servlet to run-as mapping from the run-as elements in web.xml and/or web-fragment.xml files.
     * Note that only one run-as element can be present per servlet. Only the first occurrence is processed.
     * If multiple web fragments specify this element with different values and it's absent from the web.xml, this will result
     * in an error that fails the application install.
     * 
     * @param servlet the servlet
     */
    private void processRunAs(Servlet servlet) {
        String servletName = servlet.getServletName();
        Map<String, ConfigItem<String>> runAsMap = this.configurator.getConfigItemMap(RUN_AS_KEY);
        ConfigItem<String> existingRunAs = runAsMap.get(servletName);

        RunAs runAs = servlet.getRunAs();
        String roleName = (runAs != null) ? runAs.getRoleName() : null;
        if (runAs != null) {
            if (existingRunAs == null) {
                runAsMap.put(servletName, this.configurator.createConfigItem(roleName));
                if (roleName != null)
                    this.servletNameToRunAsRole.put(servletName, roleName);
            } else {
                this.configurator.validateDuplicateKeyValueConfiguration(SERVLET_KEY, SERVLET_NAME_KEY, servletName, RUN_AS_KEY, roleName, existingRunAs);
            }
        }
        if ((TraceComponent.isAnyTracingEnabled()) && (SecurityServletConfiguratorHelper.tc.isDebugEnabled()))
            Tr.debug(SecurityServletConfiguratorHelper.tc, "servletNameToRunAsRole: " + this.servletNameToRunAsRole, new Object[0]);
    }

    /**
     * Creates a SecurityConstraint object that represents a security-constraint element in web.xml.
     * 
     * @param archiveConstraint the security-constraint
     * @return the security code's representation of a security constraint
     */
    private SecurityConstraint createSecurityConstraint(com.ibm.ws.javaee.dd.web.common.SecurityConstraint archiveConstraint, boolean denyUncoveredHttpMethods) {
        List<WebResourceCollection> webResourceCollections = createWebResourceCollections(archiveConstraint, denyUncoveredHttpMethods);
        List<String> roles = createRoles(archiveConstraint);
        boolean sslRequired = isSSLRequired(archiveConstraint);
        boolean accessPrecluded = isAccessPrecluded(archiveConstraint);
        boolean fromHttpConstraint = false;
        boolean accessUncovered = false;
        return new SecurityConstraint(webResourceCollections, roles, sslRequired, accessPrecluded, fromHttpConstraint, accessUncovered);
    }

    /**
     * Gets a list of zero or more web resource collection objects that represent the
     * web-resource-collection elements in web.xml and/or web-fragment.xml files.
     * 
     * @param archiveConstraint the security-constraint
     * @return a list of web resource collections
     */
    private List<WebResourceCollection> createWebResourceCollections(com.ibm.ws.javaee.dd.web.common.SecurityConstraint archiveConstraint, boolean denyUncoveredHttpMethods) {
        List<WebResourceCollection> webResourceCollections = new ArrayList<WebResourceCollection>();
        List<com.ibm.ws.javaee.dd.web.common.WebResourceCollection> archiveWebResourceCollections = archiveConstraint.getWebResourceCollections();
        for (com.ibm.ws.javaee.dd.web.common.WebResourceCollection archiveWebResourceCollection : archiveWebResourceCollections) {
            List<String> urlPatterns = archiveWebResourceCollection.getURLPatterns();
            List<String> methods = archiveWebResourceCollection.getHTTPMethods();
            List<String> omissionMethods = archiveWebResourceCollection.getHTTPMethodOmissions();
            webResourceCollections.add(new WebResourceCollection(urlPatterns, methods, omissionMethods, denyUncoveredHttpMethods));
        }
        return webResourceCollections;
    }

    /**
     * Gets a list of roles from the auth-constraint element in web.xml and/or web-fragment.xml files.
     * Note that only one auth-constraint element can be present per security-constraint. Only the first occurrence is processed.
     * If multiple web fragments specify this element with different values and it's absent from the web.xml, this will result
     * in an error that fails the application install.
     * 
     * @param archiveConstraint the security-constraint
     * @return a list of role names defined in the given security constraint
     */
    private List<String> createRoles(com.ibm.ws.javaee.dd.web.common.SecurityConstraint archiveConstraint) {
        List<String> roles = new ArrayList<String>();
        AuthConstraint authConstraint = archiveConstraint.getAuthConstraint();
        if (authConstraint != null) {
            Map<String, ConfigItem<List<String>>> authConstraintMap = configurator.getConfigItemMap(AUTH_CONSTRAINT_KEY);
            String webResourceName = archiveConstraint.getWebResourceCollections().get(0).getWebResourceName();
            ConfigItem<List<String>> existingAuthConstraint = authConstraintMap.get(webResourceName);
            roles = authConstraint.getRoleNames();
            if (roles.contains(ALL_ROLES_MARKER)) {
                roles = allRoles;
            }
            if (existingAuthConstraint == null) {
                authConstraintMap.put(webResourceName, this.configurator.createConfigItem(roles));
            } else {
                this.configurator.validateDuplicateConfiguration(SECURITY_CONSTRAINT_KEY, AUTH_CONSTRAINT_KEY, roles, existingAuthConstraint);
                //ignore auth-constraint specified in web-fragments, since it's already specified in web.xml
                if (ConfigSource.WEB_FRAGMENT == this.configurator.getConfigSource() && ConfigSource.WEB_XML == existingAuthConstraint.getSource()) {
                    return new ArrayList<String>();
                }
            }
        }
        return roles;
    }

    /**
     * Determines if SSL is required. SSL is required if the transport guarantee is other than NONE.
     * Note that only one user-data-constraint element can be present per security-constraint. Only the first occurrence is processed.
     * If multiple web fragments specify this element with different values and it's absent from the web.xml, this will result
     * in an error that fails the application install.
     * 
     * @param archiveConstraint the security-constraint
     * @return false when transport-guarantee is NONE, otherwise true
     */
    private boolean isSSLRequired(com.ibm.ws.javaee.dd.web.common.SecurityConstraint archiveConstraint) {
        boolean sslRequired = false;

        UserDataConstraint dataConstraint = archiveConstraint.getUserDataConstraint();
        if (dataConstraint != null) {
            int transportGuarantee = dataConstraint.getTransportGuarantee();
            String webResourceName = archiveConstraint.getWebResourceCollections().get(0).getWebResourceName();
            Map<String, ConfigItem<String>> userDataConstraintMap = configurator.getConfigItemMap(USER_DATA_CONSTRAINT_KEY);
            ConfigItem<String> existingUserDataConstraint = userDataConstraintMap.get(webResourceName);
            if (existingUserDataConstraint == null) {
                userDataConstraintMap.put(webResourceName, this.configurator.createConfigItem(String.valueOf(transportGuarantee)));
                if (transportGuarantee != UserDataConstraint.TRANSPORT_GUARANTEE_NONE) {
                    sslRequired = true;
                }
            } else {
                this.configurator.validateDuplicateConfiguration(SECURITY_CONSTRAINT_KEY, USER_DATA_CONSTRAINT_KEY, String.valueOf(transportGuarantee),
                                                                 existingUserDataConstraint);
                //ignore user-data-constraint specified in web-fragments, since it's already specified in web.xml
                if (ConfigSource.WEB_FRAGMENT == this.configurator.getConfigSource() && ConfigSource.WEB_XML == existingUserDataConstraint.getSource()) {
                    return false;
                }
            }

        }
        return sslRequired;
    }

    /**
     * Determines if access is precluded. Access is precluded if there is an auth-constraint element,
     * but there are no roles.
     * 
     * @param archiveConstraint the security-constraint
     * @return true when access is precluded, otherwise false
     */
    private boolean isAccessPrecluded(com.ibm.ws.javaee.dd.web.common.SecurityConstraint archiveConstraint) {
        boolean accessPrecluded = false;
        AuthConstraint authConstraint = archiveConstraint.getAuthConstraint();
        if (authConstraint != null) {
            List<String> roles = authConstraint.getRoleNames();
            if (roles == null || roles.isEmpty()) {
                accessPrecluded = true;
            }
        }
        return accessPrecluded;
    }

    /**
     * Creates a form login configuration object that represents a form-login-config element in web.xml and/or web-fragment.xml files.
     * 
     * @param loginConfig the login-config element
     * @return the security code's representation of a login configuration
     */
    private FormLoginConfiguration createFormLoginConfiguration(LoginConfig loginConfig) {
        FormLoginConfiguration formLoginConfiguration = null;
        FormLoginConfig formLoginConfig = loginConfig.getFormLoginConfig();
        if (formLoginConfig != null) {
            String loginPage = formLoginConfig.getFormLoginPage();
            String errorPage = formLoginConfig.getFormErrorPage();
            formLoginConfiguration = new FormLoginConfigurationImpl(loginPage, errorPage);
        }
        return formLoginConfiguration;
    }

    @Override
    public SecurityConstraintCollection getSecurityConstraintCollection() {
        return securityConstraintCollection;
    }

    @Override
    public LoginConfiguration getLoginConfiguration() {
        return loginConfiguration;
    }

    @Override
    public String getSecurityRoleReferenced(String servletName, String roleName) {
        Map<String, String> secRoleRefs = securityRoleRefsByServlet.get(servletName);
        if (secRoleRefs == null) {
            // We do not have secRoleRefs, or we can't match the URI, fall back to the
            // global roles.
            if (allRoles.contains(roleName)) {
                return roleName;
            }
        } else {
            String roleLink = secRoleRefs.get(roleName);
            if (roleLink == null) {
                if (allRoles.contains(roleName)) {
                    return roleName;
                }
            } else {
                return roleLink;
            }
        }
        return null;
    }

    @Override
    public Map<String, String> getRoleRefs(String servletName) {
        Map<String, String> secRoleRefs = securityRoleRefsByServlet.get(servletName);
        return secRoleRefs;
    }

    @Override
    public String getRunAsRoleForServlet(String servletName) {
        return servletNameToRunAsRole.get(servletName);
    }

    @Override
    public Map<String, String> getRunAsMap() {
        return servletNameToRunAsRole;
    }

    @Override
    public List<String> getRoles() {
        return allRoles;
    }

    /** {@inheritDoc} */
    @Override
    public void setSecurityConstraintCollection(SecurityConstraintCollection constraintCollection) {
        this.securityConstraintCollection = constraintCollection;
    }

    /** {@inheritDoc} */
    @Override
    public void setRoles(List<String> roles) {
        allRoles = roles;
    }

    /** {@inheritDoc} */
    @Override
    public void setLoginConfiguration(LoginConfiguration loginConfiguration) {
        this.loginConfiguration = loginConfiguration;
    }

    /** {@inheritDoc} */
    @Override
    public void setUrlPatternToServletNameMap(Map<String, String> urlPatternToServletName) {
        this.urlPatternToServletName = urlPatternToServletName;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSyncToOSThreadRequested() {
        return syncToOSThread;
    }

    /** {@inheritDoc} */
    @Override
    public void setDenyUncoveredHttpMethods(boolean denyUncoveredHttpMethodsValue) {
        denyUncoveredHttpMethods = denyUncoveredHttpMethodsValue;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDenyUncoveredHttpMethods() {
        return denyUncoveredHttpMethods;
    }
}
