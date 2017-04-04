/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014, 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.rest.handler.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.net.ssl.HttpsURLConnection;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ssl.SSLException;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.rest.handler.internal.TraceConstants;
import com.ibm.wsspi.collective.plugins.CollectivePlugin;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;
import com.ibm.wsspi.rest.handler.RESTHandlerContainer;
import com.ibm.wsspi.rest.handler.RESTRequest;
import com.ibm.wsspi.rest.handler.RESTResponse;
import com.ibm.wsspi.webcontainer.util.RequestUtils;

/**
 * This helper service routes/bridges an incoming request to and from a RESTHandler that resides in another Collective member.
 * 
 * @ibm-spi
 */
@Component(service = { DefaultRoutingHelper.class }, configurationPolicy = ConfigurationPolicy.IGNORE, immediate = true, property = { "service.vendor=IBM" })
public class DefaultRoutingHelper {

    /**
     * Encapsulates legacy constants for JMX connector clients v1, v2 and v3.
     */
    public interface LegacyJMX {
        /**
         * JMC Connector root URI.
         */
        public static final String CONNECTOR_URI = "IBMJMXConnectorREST";

        /**
         * Router prefix URI.
         */
        public static final String ROUTER_URI = CONNECTOR_URI + "/router";

        /**
         * This parameter represents the host name to be used in a routing context.
         */
        public static final String ROUTING_KEY_HOST_NAME = "com.ibm.websphere.jmx.connector.rest.routing.hostName";

        /**
         * This parameter represents the server name to be used in a routing context.
         */
        public static final String ROUTING_KEY_SERVER_NAME = "com.ibm.websphere.jmx.connector.rest.routing.serverName";

        /**
         * This parameter represents the server user directory to be used in a routing context.
         */
        public static final String ROUTING_KEY_SERVER_USER_DIR = "com.ibm.websphere.jmx.connector.rest.routing.serverUserDir";
    }

    private static final TraceComponent tc = Tr.register(DefaultRoutingHelper.class);

    private static final String KEY_COLLECTIVE_PLUGIN = "collectivePlugin";
    private final AtomicServiceReference<CollectivePlugin> collectivePluginRef = new AtomicServiceReference<CollectivePlugin>(KEY_COLLECTIVE_PLUGIN);

    @Activate
    protected void activate(ComponentContext cc, Map<String, Object> props) {
        collectivePluginRef.activate(cc);
    }

    @Deactivate
    protected void deactivate(ComponentContext cc) {
        collectivePluginRef.deactivate(cc);
    }

    @Reference(name = KEY_COLLECTIVE_PLUGIN, service = CollectivePlugin.class)
    protected void setCollectivePlugin(ServiceReference<CollectivePlugin> ref) {
        collectivePluginRef.setReference(ref);
    }

    protected void unsetCollectivePlugin(ServiceReference<CollectivePlugin> ref) {
        collectivePluginRef.unsetReference(ref);
    }

    private CollectivePlugin getCollectivePlugin() {
        CollectivePlugin plugin = collectivePluginRef.getService();
        if (plugin == null) {
            throw new RuntimeException(Tr.formatMessage(tc, "OSGI_SERVICE_ERROR", "CollectivePlugin"));
        }
        return plugin;
    }

    /**
     * The target RESTHandler did not want to provide custom routing, so route the request to it.
     * 
     */
    public void routeRequest(RESTRequest request, RESTResponse response) throws IOException {
        routeRequest(request, response, false);
    }

    /**
     * The target RESTHandler did not want to provide custom routing, so route the request to it.
     * 
     * @param request
     * @param response
     * @param legacyURI whether or not the request is using the legacy /router URI
     */
    @FFDCIgnore({ NoSuchElementException.class })
    public void routeRequest(RESTRequest request, RESTResponse response, boolean legacyURI) throws IOException {
        //Note:  For the initial release of the general router we are just supporting a singleton list,
        //but a lot of this method will be structured to support multi-target routing
        List<RoutingContext> routingContexts = null;

        try {
            routingContexts = getRoutingContext(request);

            if (routingContexts == null) {
                //Legacy case is always just a singleton
                routingContexts = Collections.singletonList(getLegacyRoutingContext(request));
            }

        } catch (IllegalArgumentException iae) {
            response.sendError(400, iae.getMessage());
            return;
        }

        //Log context(s)
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            for (RoutingContext context : routingContexts) {
                Tr.debug("routeRequest", tc, "Host: " + context.hostName + " | ServerUsrDir: " + context.serverUserDir + " | ServerName: " + context.serverName);
            }
        }

        //Route to each target (** see note at beginning of method regarding singleton list support limitation)
        for (RoutingContext context : routingContexts) {
            if (context.serverUserDir == null || context.serverName == null) {
                //This default routing service is only for server-level routing.  If a handler wants to provide
                //host-level routing it must be present on the controller and it must enable the custom routing flag.
                //TODO: Translate message
                throw new IllegalArgumentException("This RESTHandler endpoint does not support host-level routing!");
            }

            //Get the host/port tuple. getHostPortTuple throws NoSuchElementException if the node is does not 
            //exists in the repository
            String[] hostPort;
            try {
                hostPort = getHostPortTuple(context.hostName, context.serverUserDir, context.serverName);
            } catch (NoSuchElementException e) {
                response.sendError(400, e.getMessage());
                return;
            }

            //Build the target URL
            String uri = request.getURI();
            //Legacy calls to routing had /router in the URI, which must be removed before sending to member
            if (legacyURI) {
                uri = uri.replaceFirst(LegacyJMX.ROUTER_URI, LegacyJMX.CONNECTOR_URI);
            }

            String queryString = request.getQueryString();
            if (queryString != null && !queryString.isEmpty()) {
                uri = uri + "?" + queryString;
            }

            URL url = new URL("https://" + hostPort[0] + ":" + hostPort[1] + uri);

            //Setup the outgoing connection
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            //TODO: timeout header?
            boolean doOutput = request.getMethod().equalsIgnoreCase("PUT") || request.getMethod().equalsIgnoreCase("POST");
            connection.setDoInput(true);
            connection.setDoOutput(doOutput);
            connection.setUseCaches(false);
            connection.setRequestMethod(request.getMethod());
            String originalContentType = request.getHeader("Content-Type");
            if (originalContentType != null) {
                connection.setRequestProperty("Content-Type", originalContentType);
            }

            try {
                connection.setSSLSocketFactory(getCollectivePlugin().getSSLContent("memberConnectionConfig").getSocketFactory());
            } catch (SSLException e) {
                throw new RuntimeException(Tr.formatMessage(tc, "SSL_CONTEXT_NOT_AVAILABLE", e.getMessage()));
            }

            //Transfer streams
            //TODO: for multiple targets we'll have to cache the incoming input stream so we can write multiple times
            if (doOutput) {
                copyStream(request.getInputStream(), connection.getOutputStream());
            }

            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                Tr.event("DefaultRoutingHelper", tc, "Routing to target URL: " + url.toString());
            }

            //Trigger the routed call!
            int responseCode = connection.getResponseCode();

            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                Tr.event("DefaultRoutingHelper", tc, "Received responseCode: " + responseCode);
            }

            //Handle the response back to the client
            //TODO: for multiple targets we'll need to aggregate this information
            response.setStatus(responseCode);
            copyResponseHeaders(connection, response);
            response.setContentType(connection.getContentType());

            //Copy the appropriate stream back to the client
            if (connection.getErrorStream() == null) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                    Tr.event("DefaultRoutingHelper", tc, "Copying input stream");
                }
                copyStream(connection.getInputStream(), response.getOutputStream());
            } else {
                if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                    Tr.event("DefaultRoutingHelper", tc, "Copying error stream");
                }
                copyStream(connection.getErrorStream(), response.getOutputStream());
            }
        }
    }

    private void copyResponseHeaders(HttpsURLConnection connection, RESTResponse response) {
        boolean hasMoreFields = true;
        int i = 0;
        while (hasMoreFields) {
            String headerName = connection.getHeaderFieldKey(i);
            String headerValue = connection.getHeaderField(i);

            if (headerName == null) {
                hasMoreFields = false;
            } else {
                response.setResponseHeader(headerName, headerValue);
                i++;
            }
        }
    }

    private long copyStream(InputStream input, OutputStream output) throws IOException {
        if (input != null) {
            ReadableByteChannel inputChannel = null;
            WritableByteChannel outputChannel = null;
            try {
                inputChannel = Channels.newChannel(input);
                outputChannel = Channels.newChannel(output);

                final ByteBuffer buffer = ByteBuffer.allocate(4 * 1024);
                long size = 0;

                buffer.clear();
                while (inputChannel.read(buffer) >= 0 || buffer.position() != 0) {
                    buffer.flip();
                    size += outputChannel.write(buffer);
                    buffer.compact();
                }
                return size;
            } finally {
                if (outputChannel != null) {
                    try {
                        outputChannel.close();
                    } catch (IOException e) {
                    }
                }
                if (inputChannel != null) {
                    try {
                        inputChannel.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        return 0;
    }

    private String[] getHostPortTuple(String host, String userDir, String serverName) throws IllegalArgumentException, IOException {

        Map<String, Object> mapJMXAuth = (Map<String, Object>) getCollectivePlugin().getPrivateServerNode(host, userDir, serverName, "sys.jmx.auth.info");
        String[] hostPort = new String[2];
        Object o = mapJMXAuth.get("jmxHost");
        if (o != null) {
            hostPort[0] = (String) o;
        }
        o = mapJMXAuth.get("jmxPort");
        if (o != null) {
            hostPort[1] = (String) o;
        }

        if (hostPort[0] == null || hostPort[1] == null) {
            //TODO: Translate message
            throw new RuntimeException("Could not fetch the target host and port for server: {" + host + ", " + userDir + ", " + serverName + "}");
        }

        return hostPort;
    }

    /**
     * Quick check for legacy routing context (used from JMX connector)
     */
    public static boolean containsLegacyRoutingContext(RESTRequest request) {
        return request.getHeader(LegacyJMX.ROUTING_KEY_HOST_NAME) != null;
    }

    /**
     * Quick check for multiple routing context, without actually fetching all pieces
     */
    public static boolean containsRoutingContext(RESTRequest request) {
        if (request.getHeader(RESTHandlerContainer.COLLECTIVE_HOST_NAMES) != null) {
            return true;
        }

        //No routing header found, so check query strings
        return getQueryParameterValue(request, RESTHandlerContainer.COLLECTIVE_HOST_NAMES) != null;
    }

    // Use this method for parsing query string for POST. Using RESTRequest's method will read the
    // request's body once and for all.
    public static String getQueryParameterValue(RESTRequest request, String name) {
        if (!"post".equalsIgnoreCase(request.getMethod())) {
            return request.getParameter(name);
        }

        if (request.getQueryString() == null) {
            return null;
        }

        Hashtable params = null;
        try {
            params = RequestUtils.parseQueryString(request.getQueryString());
        } catch (Exception e) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                Tr.event("DefaultRoutingHelper", tc, "Failed to parse the query string:\n Exception: " + e);
            }
            return null;
        }

        String[] values = (String[]) params.get(name);
        String value = null;
        if (values != null && values.length > 0)
        {
            value = values[0];
        }

        return value;
    }

    // Use this method for parsing query string for POST. Using RESTRequest's method will read the
    // request's body once and for all.
    public static String[] getQueryParameterValues(RESTRequest request, String name) {
        if (!"post".equalsIgnoreCase(request.getMethod())) {
            return request.getParameterValues(name);
        }

        if (request.getQueryString() == null) {
            return null;
        }

        Hashtable params = null;
        try {
            params = RequestUtils.parseQueryString(request.getQueryString());
        } catch (Exception e) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                Tr.event("DefaultRoutingHelper", tc, "Failed to parse the query string:\n Exception: " + e);
            }
            return null;
        }

        return (String[]) params.get(name);
    }

    /**
     * This helper method looks for the routing keys in the HTTP headers
     * 
     * @param httpServletRequest of the current request
     * @return a 3-sized String array containing hostName, userDir and serverName respectively, or null if no routing context was found.
     */
    public static RoutingContext getLegacyRoutingContext(RESTRequest request) {
        String targetHost = request.getHeader(LegacyJMX.ROUTING_KEY_HOST_NAME);
        if (targetHost != null) {
            String targetUserDir = request.getHeader(LegacyJMX.ROUTING_KEY_SERVER_USER_DIR);
            String targetServer = request.getHeader(LegacyJMX.ROUTING_KEY_SERVER_NAME);

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug("DefaultRoutingHelper", tc, "Found routing context in headers.  Host:" + targetHost + " | UserDir:" + targetUserDir + " | Server:" + targetServer);
            }

            targetHost = URLDecoder(targetHost);
            targetUserDir = targetUserDir == null ? null : URLDecoder(targetUserDir);
            targetServer = targetServer == null ? null : URLDecoder(targetServer);

            return new RoutingContext(targetHost, null, targetUserDir, targetServer);
        }

        return null;
    }

    public static String URLDecoder(String name) {
        try {
            return URLDecoder.decode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // This shouldn't happen. If it happens, it means JVM doesn't support UTF-8!
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * This helper method looks for the routing keys in the HTTP headers first, and then falls-back into looking at the query string.
     * 
     * @param httpServletRequest of the current request
     * @return a list of routing contexts, or null if none found.
     */
    public static List<RoutingContext> getRoutingContext(RESTRequest request) {
        //Look for headers first
        String targetHosts = request.getHeader(RESTHandlerContainer.COLLECTIVE_HOST_NAMES);
        if (targetHosts != null) {
            String targetInstallDirs = request.getHeader(RESTHandlerContainer.COLLECTIVE_SERVER_INSTALL_DIRS);
            String targetUserDirs = request.getHeader(RESTHandlerContainer.COLLECTIVE_SERVER_USER_DIRS);
            String targetServers = request.getHeader(RESTHandlerContainer.COLLECTIVE_SERVER_NAMES);

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug("DefaultRoutingHelper", tc, "Found routing context in headers.  Hosts:" + targetHosts + " | InstallDirs" + targetInstallDirs + " | UserDirs:"
                                                     + targetUserDirs + " | Servers:" + targetServers);
            }

            //Tokenize the fields
            final String[] hosts = targetHosts.split(",");
            final String[] installDirs = targetInstallDirs == null ? null : targetInstallDirs.split(",");
            final String[] userDirs = targetUserDirs == null ? null : targetUserDirs.split(",");
            final String[] servers = targetServers == null ? null : targetServers.split(",");

            return assembleRoutingContext(hosts, installDirs, userDirs, servers);

        } else {
            //Look for query strings
            if (getQueryParameterValue(request, RESTHandlerContainer.COLLECTIVE_HOST_NAMES) != null) {
                return assembleRoutingContext(getQueryParameterValues(request, RESTHandlerContainer.COLLECTIVE_HOST_NAMES),
                                              getQueryParameterValues(request, RESTHandlerContainer.COLLECTIVE_SERVER_INSTALL_DIRS),
                                              getQueryParameterValues(request, RESTHandlerContainer.COLLECTIVE_SERVER_USER_DIRS),
                                              getQueryParameterValues(request, RESTHandlerContainer.COLLECTIVE_SERVER_NAMES));
            }

            return null;
        }
    }

    private static List<RoutingContext> assembleRoutingContext(String[] hosts, String[] installDirs, String[] userDirs, String[] servers) {
        int userDirsLength = userDirs == null ? 0 : userDirs.length;
        int serversLength = servers == null ? 0 : servers.length;

        //Validate length of parameters, if applicable (don't need to check install dirs..)
        if ((userDirs != null && hosts.length != userDirsLength) || userDirsLength != serversLength) {
            //TODO: Translate 
            throw new IllegalArgumentException("The length of the routing context parameters did not match: Hosts:" + hosts.length + " | UserDirs:" + userDirsLength
                                               + " | Servers:" + serversLength);
        }

        //Build RoutingContext list
        List<RoutingContext> contexts = new ArrayList<RoutingContext>(hosts.length);
        for (int i = 0; i < hosts.length; i++) {
            //UserDir, installDir and ServerName might be null
            String installDir = installDirs == null ? null : installDirs[i].trim();
            String userDir = userDirs == null ? null : userDirs[i].trim();
            String serverName = servers == null ? null : servers[i].trim();
            contexts.add(new RoutingContext(hosts[i].trim(), installDir, userDir, serverName));
        }

        return contexts;
    }

    /**
     * This inner class encapsulates the routing context.
     */
    public static class RoutingContext {
        final public String hostName;
        final public String serverInstallDir;
        final public String serverUserDir;
        final public String serverName;

        public RoutingContext(String hostName, String serverUserDir, String serverName) {
            this(hostName, null, serverUserDir, serverName);
        }

        public RoutingContext(String hostName, String serverInstallDir, String serverUserDir, String serverName) {
            this.hostName = hostName;
            this.serverInstallDir = serverInstallDir;
            this.serverUserDir = serverUserDir;
            this.serverName = serverName;
        }
    }
}
