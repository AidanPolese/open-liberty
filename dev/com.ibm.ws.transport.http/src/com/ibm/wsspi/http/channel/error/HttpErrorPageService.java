// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.http.channel.error;

/**
 * Service used to match error page providers against error conditions on
 * different listening ports.
 * 
 */
public interface HttpErrorPageService {

    /**
     * Register the given page provider for the input listening port.
     * This may fail if another provider is already registered.
     * 
     * @param port
     * @param provider
     * @return boolean - true means it successfully registered
     * @throws IllegalArgumentException
     *             if provider is null or port is non-positive
     */
    boolean register(int port, HttpErrorPageProvider provider);

    /**
     * Attempt to deregister whatever provider is registered for the input
     * port.
     * 
     * @param port
     * @return HttpErrorPageProvider - null if none registered for this port
     * @throws IllegalArgumentException
     *             if the port is non-positive
     */
    HttpErrorPageProvider deregister(int port);

    /**
     * Access the current page provider that is registered for this port. It
     * may return null if no provider exists.
     * 
     * @param port
     * @return HttpErrorPageProvider
     */
    HttpErrorPageProvider access(int port);

}
