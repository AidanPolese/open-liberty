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
package com.ibm.ws.jmx.fat.attach;

import java.io.IOException;
import java.util.Properties;

/**
 *
 */
public interface VirtualMachineProxy {

    public void detach() throws IOException;

    public String id();

    public Properties getAgentProperties() throws IOException;

    public Properties getSystemProperties() throws IOException;

    public void loadAgent(String agent) throws IOException;

    public void loadAgent(String agent, String options) throws IOException;

    public void loadAgentLibrary(String agentLibrary) throws IOException;

    public void loadAgentLibrary(String agentLibrary, String options) throws IOException;

    public void loadAgentPath(String agentPath) throws IOException;

    public void loadAgentPath(String agentPath, String options) throws IOException;

    public String toString();

}
