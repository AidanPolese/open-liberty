/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2008
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.injectionengine;

import java.util.Hashtable;
import java.util.Map;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;

/**
 * Provides a factory which resolves message-destination-refs to message
 * destinations, where a message-destination-link has been provided.
 **/
public class MessageDestinationLinkFactory
{
    private static final String CLASS_NAME = MessageDestinationLinkFactory.class.getName();

    private static final TraceComponent tc = Tr.register
                    (CLASS_NAME,
                     InjectionConfigConstants.traceString,
                     InjectionConfigConstants.messageFile);

    /**
     * Map of message destination bindings per module per application.
     *
     * Map< app, Map< module, Map< link, binding > > >
     **/
    private Map<String, Map<String, Map<String, String>>> ivMessageDestinationLinks = new Hashtable<String, Map<String, Map<String, String>>>(); //d502608

    public MessageDestinationLinkFactory()
    {
        // Empty on purpose
    }

    /**
     * Returns a String that is a jndiName for the specified destination.
     *
     * @param application name of the application containing the ref.
     * @param module name of the module containing the ref.
     * @param link name of the destination in the mdb
     *
     * @return the JndiName the destination is bound to in the namespace.
     **/
    // d449021 //d465081
    public String findDestinationByName(String application,
                                        String module,
                                        String link)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "findDestinationByName : " + application + ":" +
                         module + ":" + link);

        String boundToJndiName = null;

        Map<String, Map<String, String>> appMap = ivMessageDestinationLinks.get(application);

        // if not null check
        if (appMap == null)
        {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                Tr.exit(tc, "findDestinationByName : application not found");
            return null;
        }

        int lindex = link.indexOf('#');
        if (lindex != -1)
        {
            String moduleName = null;
            // Module name may have path information... remove it.          d510405
            int mindex = link.lastIndexOf('/');
            if (mindex > -1 && mindex < lindex)
            {
                moduleName = link.substring(mindex + 1, lindex);
            }
            else
            {
                // If no path was specified, then the referenced module
                // is in the same location. Use first half of the link.
                moduleName = link.substring(0, lindex);
            }
            String newLink = link.substring(lindex + 1);

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(tc, "looking for module : " + moduleName);
            Map<String, String> moduleMap = appMap.get(moduleName);
            if (moduleMap != null)
            {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    Tr.debug(tc, "looking for link : " + newLink);
                boundToJndiName = moduleMap.get(newLink);
            }
        }
        else
        {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(tc, "looking for module : " + module);
            Map<String, String> moduleMap = appMap.get(module);
            if (moduleMap != null)
            {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    Tr.debug(tc, "looking for link : " + link);
                boundToJndiName = moduleMap.get(link);
            }

            // If the link was not found in the current module, loop through
            // all the modules in the application
            if (boundToJndiName == null)
            {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    Tr.debug(tc, "not in specified module, looking in all modules");

                for (Map<String, String> curModuleMap : appMap.values())
                {
                    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                        Tr.debug(tc, "looking for link : " + link);
                    boundToJndiName = curModuleMap.get(link);
                    if (boundToJndiName != null)
                    {
                        break;
                    }
                }
            }
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "findDestinationByName : " + boundToJndiName);

        return boundToJndiName;
    }

    /**
     * Return the map of message destination link names to jndi message
     * destination names per module per application for the server. <p>
     */
    public Map<String, Map<String, Map<String, String>>> getMessageDestinationLinks() //d502608
    {
        return ivMessageDestinationLinks;
    }
}
