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
package componenttest.aries;

import java.io.IOException;
import java.net.HttpURLConnection;

import com.ibm.ws.topology.exceptions.TopologyException;
import com.ibm.ws.topology.helper.Topology;
import componenttest.topology.impl.LibertyServer;

/**
 *
 */
public interface AriesTopologyForLiberty extends Topology {

    public LibertyServer getServer();

    public void startLibertyServer() throws Exception;

    public String getServerRoot();

    public void addExpectedBundle(String bundleName);

    /**
     * Some bundles log that they have finished execution of some code multiple
     * times, for example each time a reference to some resource is injected.
     * This method ensures that the given bundle has logged that it has finished
     * execution the expected number of times.
     * 
     * @param bundleName bundle name to look for in the list of finished bundles
     * @param nTimes number of times the bundle should log that it has finished
     */
    public void addExpectedBundle(String bundleName, int nTimes);

    /**
     * This version is internal and takes a boolean for the special case of checking whether we are
     * ready to being a component test (i.e. the server is started).
     * 
     * @param customTimeout
     * @param checkingReady
     * @return boolean indicating if all tests completed
     * @throws InterruptedException
     * @throws TopologyException
     */
    public boolean checkForComponentTestFinish(long customTimeout)
                    throws InterruptedException, TopologyException;

    /**
     * This method checks if we are ready to start installing and running component tests.
     * It does this by checking if the commong logging bundle has reported as as finished.
     * The logging bundle reports finished when its init-method of loggingActive is run.
     * 
     * @param customTimeout
     * @throws Exception
     */
    public void checkReady(long customTimeout) throws InterruptedException, TopologyException;

    public void setIBR(String IBRDir);

    public HttpURLConnection getConnection(String urlAddress) throws IOException;

}
