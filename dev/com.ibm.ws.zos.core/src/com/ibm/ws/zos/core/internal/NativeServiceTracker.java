/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.zos.core.internal;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.kernel.boot.internal.BootstrapConstants;
import com.ibm.ws.kernel.zos.NativeMethodManager;
import com.ibm.ws.zos.core.Angel;
import com.ibm.ws.zos.core.NativeClientService;
import com.ibm.ws.zos.core.NativeService;

/**
 * Component that is responsible for registering with the Angel and
 * populating the service registry with information about the authorized
 * native services.
 */
public class NativeServiceTracker implements BundleActivator {

    /**
     * Trace component used to issue messages.
     */
    private static final TraceComponent tc = Tr.register(NativeServiceTracker.class);

    /**
     * The maximum length of an angel name.
     */
    private static final int ANGEL_NAME_MAX_LENGTH = 54;

    /**
     * The location of the install root's {@code lib} directory. Native code
     * and bundles should be resolved relative to this directory.
     */
    static String WAS_LIB_DIR;

    /**
     * The location of the authorized function module relative to the install
     * root's {@code lib} directory.
     */
    final static String AUTHORIZED_FUNCTION_MODULE = "native/zos/s390x/bbgzsafm";

    /**
     * The location of the unauthorized function module relative to the install
     * root's {@code lib} directory.
     */
    final static String UNAUTHORIZED_FUNCTION_MODULE = "native/zos/s390x/bbgzsufm";

    /**
     * Used to look up the com.ibm.ws.zos.core.angelRequired=true|false property
     */
    private static final String ANGEL_REQUIRED_KEY = "com.ibm.ws.zos.core.angelRequired";

    /**
     * The native method manager to use for bootstrapping native code.
     */
    final NativeMethodManager nativeMethodManager;

    /**
     * The bundle context of the host bundle.
     */
    BundleContext bundleContext = null;

    /**
     * Indication of whether or not we successfully registered with the
     * angel.
     */
    boolean registeredWithAngel = false;

    /**
     * The set of server service registrations performed by this component. This field
     * must only be accessed by synchronized methods.
     */
    Set<ServiceRegistration<NativeService>> registrations = new HashSet<ServiceRegistration<NativeService>>();

    /**
     * The set of client service registrations performed by this component. This field
     * must only be accessed by synchronized methods.
     */
    Set<ServiceRegistration<NativeClientService>> clientRegistrations = new HashSet<ServiceRegistration<NativeClientService>>();

    /**
     * The Angel service that we've registered with OSGi. This field must only
     * be accessed by synchronized methods.
     */
    ServiceRegistration<Angel> angelRegistration = null;

    /**
     * Pattern used to determine if an angel name is valid.
     * The first "group" is a reluctant match against any characters.
     * The second "group" is a greedy match against any supported characters.
     * The third "group" is a reluctant match against any characters.
     * This should match anything, and if the first and third groups are empty, we have a valid angel name.
     */
    private final Pattern angelNamePattern = Pattern.compile("(.*?)([A-Z0-9\\!\\#\\$\\+\\-\\/\\:\\<\\>\\=\\?\\@\\[\\]\\^\\_\\`\\{\\}\\|\\~]*)(.*?)");

    /**
     * Helper class to hold return code data from native services.
     */
    final static class ServiceResults {
        final int returnValue;
        final int returnCode;
        final int reasonCode;

        ServiceResults(int returnValue, int returnCode, int reasonCode) {
            this.returnValue = returnValue;
            this.returnCode = returnCode;
            this.reasonCode = reasonCode;
        }
    }

    /**
     * Create a native service tracker.
     */
    public NativeServiceTracker(NativeMethodManager nativeMethodManager) {
        this.nativeMethodManager = nativeMethodManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(BundleContext bundleContext) throws BundleException {
        this.bundleContext = bundleContext;

        WAS_LIB_DIR = CoreBundleActivator.firstNotNull(bundleContext.getProperty(BootstrapConstants.LOC_INTERNAL_LIB_DIR), "");

        // Register our own native code
        nativeMethodManager.registerNatives(NativeServiceTracker.class);

        // Load the unauthorized code to access the registration stub
        ServiceResults loadServiceResult = loadUnauthorized();
        if (loadServiceResult.returnValue != 0) {
            Tr.error(tc,
                     "UNABLE_TO_LOAD_UNAUTHORIZED_BPX4LOD",
                     UNAUTHORIZED_FUNCTION_MODULE,
                     loadServiceResult.returnValue,
                     Integer.toHexString(loadServiceResult.returnCode),
                     Integer.toHexString(loadServiceResult.reasonCode));
            throw new BundleException("Unable to load the z/OS unauthorized native library " + UNAUTHORIZED_FUNCTION_MODULE);
        }

        // Read angel name out of bootstrap property.  Not set means use the
        // default angel (null).
        String angelName = AccessController.doPrivileged(new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty("com.ibm.ws.zos.core.angelName");
            }
        });

        // Validate the angel name.
        boolean angelNameValid = true;
        if (angelName != null) {
            if (angelName.length() > ANGEL_NAME_MAX_LENGTH) {
                angelNameValid = false;
                Tr.error(tc, "ANGEL_NAME_TOO_LONG");
            } else {
                Matcher m = angelNamePattern.matcher(angelName);
                if (m.matches()) {
                    // Go see if the first or last part of the matcher are empty.  If they are empty,
                    // we're good.  If they are not empty, we found some invalid characters that we
                    // need to report.
                    int badCharOffset = -1;
                    char badChar = ' ';
                    String badGroup = m.group(1); // Get the beginning non-matching part
                    if ((badGroup != null) && (badGroup.length() > 0)) {
                        badCharOffset = m.end(1) - 1;
                    } else {
                        badGroup = m.group(3); // Get the end non-matching part
                        if ((badGroup != null) && (badGroup.length() > 0)) {
                            badCharOffset = m.start(3);
                        }
                    }

                    if (badCharOffset >= 0) {
                        angelNameValid = false;
                        badChar = angelName.charAt(badCharOffset);
                        Tr.error(tc, "ANGEL_NAME_UNSUPPORTED_CHARACTER", new Object[] { badChar, badCharOffset });
                    }
                } else {
                    // The matcher is written to handle just about anything.  If we couldn't parse
                    // it, throw and spit the whole name out in the exception.
                    throw new IllegalArgumentException("Could not parse angel name " + angelName);
                }
            }
        }

        if (angelNameValid == true) {
            // Attempt to register with the angel
            int registerReturnCode = registerServer(angelName);
            if (registerReturnCode == 0) {
                registeredWithAngel = true;
            } else if (registerReturnCode == NativeReturnCodes.ANGEL_REGISTER_DRM_NOT_AUTHORIZED) {
                if (null == angelName) {
                    Tr.info(tc, "SERVER_NOT_AUTHORIZED_TO_CONNECT_TO_ANGEL");
                } else {
                    Tr.info(tc, "SERVER_NOT_AUTHORIZED_TO_CONNECT_TO_ANGEL_NAME", angelName);
                }
            } else if (registerReturnCode == NativeReturnCodes.ANGEL_REGISTER_DRM_SAFM_NOT_APF_AUTHORIZED) {
                Tr.info(tc, "SERVER_SAFM_NOT_APF_AUTHORIZED");
            } else if (registerReturnCode == NativeReturnCodes.ANGEL_REGISTER_DRM_NOT_AUTHORIZED_BBGZSAFM) {
                if (null == angelName) {
                    Tr.info(tc, "ANGEL_NOT_AVAILABLE", registerReturnCode);
                } else {
                    Tr.info(tc, "ANGEL_NOT_AVAILABLE_NAME", angelName, registerReturnCode);
                }
                Tr.info(tc, "SERVER_SAFM_NOT_SAF_AUTHORIZED");
            } else {
                if (null == angelName) {
                    Tr.info(tc, "ANGEL_NOT_AVAILABLE", registerReturnCode);
                } else {
                    Tr.info(tc, "ANGEL_NOT_AVAILABLE_NAME", angelName, registerReturnCode);
                }
            }
        }

        // Populate the OSGi service registry
        populateServiceRegistry();

        // Stop here if we require the angel,
        // but aren't registered with it
        checkAngelRequirement();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(BundleContext bundleContext) {
        // Remove native service representations from service registry
        unregisterOSGiServices();

        // Attempt to deregister from the angel
        if (registeredWithAngel) {
            deregisterServer();
            registeredWithAngel = false;
        }
    }

    /**
     * Check the configuration to determine whether this server
     * is allowed to start without the angel and take it down
     * if the angel is required and not connected
     */
    private void checkAngelRequirement() {
        // Boolean.getBoolean() returns false by default if it
        // can't find a value for the given key. This is fine
        // because our property should default to false anyway.
        String isAngelReqStr = AccessController.doPrivileged(new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty(ANGEL_REQUIRED_KEY);
            }
        });
        isAngelReqStr = (null == isAngelReqStr) ? "false" : isAngelReqStr.trim().toLowerCase();
        boolean isAngelRequired = Boolean.parseBoolean(isAngelReqStr);
        if (!registeredWithAngel && isAngelRequired) {
            Tr.info(tc, "NOT_REGISTERED_WITH_REQUIRED_ANGEL");
            shutdownFramework();
        }
    }

    /**
     * This method is used to stop the root bundle
     * thus bringing down the OSGi framework.
     */
    @FFDCIgnore(Exception.class)
    final void shutdownFramework() {

        try {
            Bundle bundle = bundleContext.getBundle(Constants.SYSTEM_BUNDLE_LOCATION);

            if (bundle != null)
                bundle.stop();
        } catch (Exception e) {
            // do not FFDC this.
            // exceptions during bundle stop occur if framework is already stopping or stopped
        }
    }

    /**
     * Test if the specified file exists using this class's security context.
     *
     * @param file the file to test
     *
     * @return true if the file exists
     */
    boolean fileExists(final File file) {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return file.exists();
            }
        });
    }

    /**
     * Load the module containing the unauthorized native code.
     *
     * @return the &quot;load HFS&quot; return codes as a <code>ServiceResults</code>.
     */
    private ServiceResults loadUnauthorized() {
        File library = new File(WAS_LIB_DIR, UNAUTHORIZED_FUNCTION_MODULE);
        if (!fileExists(library)) {
            Tr.error(tc, "LIBRARY_DOES_NOT_EXIST", library.getAbsolutePath());
        }
        return ntv_loadUnauthorized(library.getAbsolutePath());
    }

    /**
     * Attempt to register this server with the angel and access the authorized
     * code infrastructure.
     *
     * @param angelName The angel name we want to connect to.
     *
     * @return the return code from server registration
     */
    private int registerServer(String angelName) {
        File library = new File(WAS_LIB_DIR, AUTHORIZED_FUNCTION_MODULE);
        if (!fileExists(library)) {
            Tr.error(tc, "LIBRARY_DOES_NOT_EXIST", library.getAbsolutePath());
        }

        return ntv_registerServer(library.getAbsolutePath(), angelName);
    }

    /**
     * Deregister this server and tear down the authorized code infrastructure.
     *
     * @return the deregistration return code
     */
    private int deregisterServer() {
        return ntv_deregisterServer();
    }

    /**
     * Populate the OSGi service registry with information about the native
     * services from the service vector table.
     */
    synchronized void populateServiceRegistry() {
        List<String> permittedServices = new ArrayList<String>();
        List<String> deniedServices = new ArrayList<String>();
        List<String> permittedClientServices = new ArrayList<String>();
        List<String> deniedClientServices = new ArrayList<String>();

        Set<String> permittedProfiles = new TreeSet<String>();
        Set<String> deniedProfiles = new TreeSet<String>();
        Set<String> permittedClientProfiles = new TreeSet<String>();
        Set<String> deniedClientProfiles = new TreeSet<String>();

        getNativeServiceEntries(permittedServices, deniedServices, permittedClientServices, deniedClientServices);

        for (int i = 0; i < permittedServices.size(); i += 2) {
            registerOSGiService(permittedServices.get(i), permittedServices.get(i + 1), true, false);
            permittedProfiles.add(permittedServices.get(i + 1));
        }

        for (int i = 0; i < deniedServices.size(); i += 2) {
            registerOSGiService(deniedServices.get(i), deniedServices.get(i + 1), false, false);
            deniedProfiles.add(deniedServices.get(i + 1));
        }

        for (int i = 0; i < permittedClientServices.size(); i += 2) {
            registerOSGiService(permittedClientServices.get(i), permittedClientServices.get(i + 1), true, true);
            permittedClientProfiles.add(permittedClientServices.get(i + 1));
        }

        for (int i = 0; i < deniedClientServices.size(); i += 2) {
            registerOSGiService(deniedClientServices.get(i), deniedClientServices.get(i + 1), false, true);
            deniedClientProfiles.add(deniedClientServices.get(i + 1));
        }

        for (String profile : permittedProfiles) {
            Tr.info(tc, "AUTHORIZED_SERVICE_AVAILABLE", profile);
        }

        for (String profile : deniedProfiles) {
            Tr.info(tc, "AUTHORIZED_SERVICE_NOT_AVAILABLE", profile);
        }

        for (String profile : permittedClientProfiles) {
            Tr.info(tc, "AUTHORIZED_SERVICE_AVAILABLE", "CLIENT." + profile);
        }

        for (String profile : deniedClientProfiles) {
            Tr.info(tc, "AUTHORIZED_SERVICE_NOT_AVAILABLE", "CLIENT." + profile);
        }

        if (registeredWithAngel) {
            int angelVersion = ntv_getAngelVersion();
            if (angelVersion != -1) {
                Angel angel = new AngelImpl(ntv_getAngelVersion());
                Dictionary<String, String> properties = new Hashtable<String, String>();
                properties.put(Constants.SERVICE_VENDOR, "IBM");
                properties.put(Angel.ANGEL_DRM_VERSION, Integer.toString(angel.getDRM_Version()));

                angelRegistration = bundleContext.registerService(Angel.class, angel, properties);
            }
        }
    }

    /**
     * Register a {@code NativeService} representation with the specified
     * name and indicate whether or not this server is authorized to use it.
     *
     * @param name the service name from the services vector table
     * @param authorizationGroup the name of the SAF authorization group that
     *            controls access to the authorized service
     * @param isAuthorized indication of whether or not this server can use the
     *            specified service
     * @param client indication of whether this is a service that the client calls,
     *            or the server calls (BBGZSCFM vs BBGZSAFM)
     */
    synchronized void registerOSGiService(String name, String authorizationGroup, boolean isAuthorized, boolean client) {
        NativeClientService service = new NativeServiceImpl(name.trim(), authorizationGroup.trim(), isAuthorized, client);
        Dictionary<String, String> properties = new Hashtable<String, String>();

        properties.put(Constants.SERVICE_VENDOR, "IBM");
        properties.put(NativeService.NATIVE_SERVICE_NAME, service.getServiceName());
        properties.put(NativeService.AUTHORIZATION_GROUP_NAME, service.getAuthorizationGroup());
        properties.put(NativeService.IS_AUTHORIZED, Boolean.toString(service.isPermitted()));

        if (client) {
            clientRegistrations.add(bundleContext.registerService(NativeClientService.class, service, properties));
        } else {
            registrations.add(bundleContext.registerService(NativeService.class, service, properties));
        }
    }

    /**
     * Unregister all {@code NativeService} representations from the OSGi
     * service registry.
     */
    synchronized void unregisterOSGiServices() {
        for (ServiceRegistration<NativeService> service : registrations) {
            service.unregister();
        }
        for (ServiceRegistration<NativeClientService> service : clientRegistrations) {
            service.unregister();
        }

        registrations.clear();
        clientRegistrations.clear();

        if (angelRegistration != null) {
            angelRegistration.unregister();
            angelRegistration = null;
        }
    }

    /**
     * Get information about the native services in the authorized load module.
     *
     * @param permittedServices the list to populate with permitted service names.
     *            Each permitted service uses two entries in the list where the first
     *            entry is the name of the service and the next entry is the name of
     *            the authorization group.
     * @param deniedServices the list to populate with denied service names
     *            Each denied service uses two entries in the list where the first
     *            entry is the name of the service and the next entry is the name of
     *            the authorization group.
     * @param permittedClientServices the list to populate with permitted service names.
     *            Each permitted service uses two entries in the list where the first
     *            entry is the name of the service and the next entry is the name of
     *            the authorization group.
     * @param deniedClientServices the list to populate with denied service names
     *            Each denied service uses two entries in the list where the first
     *            entry is the name of the service and the next entry is the name of
     *            the authorization group.
     *
     * @return the number of combined entries in the services lists.
     */
    int getNativeServiceEntries(List<String> permittedServices, List<String> deniedServices, List<String> permittedClientServices, List<String> deniedClientServices) {
        return ntv_getNativeServiceEntries(permittedServices, deniedServices, permittedClientServices, deniedClientServices);
    }

    protected native ServiceResults ntv_loadUnauthorized(String unauthorizedModulePath);

    protected native int ntv_registerServer(String authorizedModulePath, String angelName);

    protected native int ntv_deregisterServer();

    protected native int ntv_getNativeServiceEntries(List<String> permittedServices, List<String> deniedServices, List<String> permittedClientServices,
                                                     List<String> deniedClientServices);

    protected native int ntv_getAngelVersion();
}
