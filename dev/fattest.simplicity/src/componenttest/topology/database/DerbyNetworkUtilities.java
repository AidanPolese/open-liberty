/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package componenttest.topology.database;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.derby.drda.NetworkServerControl;

/**
 * Provides methods for starting and stopping Derby Network.
 * The server is checked a number of times to be sure it has started,
 * otherwise the exception returned from the ping is thrown.
 */
public class DerbyNetworkUtilities {

    static final int NUMBER_OF_PINGS = 10;
    static final int SLEEP_BETWEEN_PINGS = 10000;
    // TODO serverName and portNumber are hard coded, 
    // but in the future should be taken from bootstrapping.properties if specified
    static final String serverName = "localhost";
    static final int portNumber = 1527;

    static public void startDerbyNetwork() throws UnknownHostException, Exception {
        NetworkServerControl serverControl =
                        new NetworkServerControl(InetAddress.getByName(serverName), portNumber);
        serverControl.start(new PrintWriter(System.out));
        for (int i = 1; i <= NUMBER_OF_PINGS; i++) {
            try {
                System.out.println("Attempt " + i + " to see if Derby Network server started");
                serverControl.ping();
                break;
            } catch (Exception ex) {
                if (i == NUMBER_OF_PINGS) {
                    System.out.println("Derby Network server failed to start");
                    ex.printStackTrace();
                    throw ex;
                }
                Thread.sleep(SLEEP_BETWEEN_PINGS);
            }
        }
    }

    static public void stopDerbyNetwork() throws Exception {
        NetworkServerControl serverControl =
                        new NetworkServerControl(InetAddress.getByName(serverName), portNumber);
        serverControl.shutdown();
    }

}
