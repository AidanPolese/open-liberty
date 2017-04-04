/*COPYRIGHT_START***********************************************************
 *
 * IBM Confidential OCO Source Material
 * 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *   IBM DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE, INCLUDING
 *   ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *   PURPOSE. IN NO EVENT SHALL IBM BE LIABLE FOR ANY SPECIAL, INDIRECT OR
 *   CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF
 *   USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
 *   OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE
 *   OR PERFORMANCE OF THIS SOFTWARE.
 *
 *  @(#) 1.3 SERV1/ws/code/web.session.core/src/com/ibm/wsspi/session/IProtocolAdapter.java, WASCC.web.session.core, WASX.SERV1, o0901.11 5/30/08 16:34:35 [1/9/09 15:01:32]
 *
 * @(#)file   IProtocolAdapter.java
 * @(#)version   1.3
 * @(#)date      5/30/08
 *
 *COPYRIGHT_END*************************************************************/
package com.ibm.wsspi.session;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

/**
 * Interface that lists methods that may be called to adapt a session to one
 * of two protocols HTTP and SIP. An implementation for adaptation to HTTP
 * is currently available.
 */
public interface IProtocolAdapter {

    // XD methods
    /**
     * Constant for WAS extensions (IBMSession, IBMSessionExt) to HttpSession
     * that allow for IBMApplicationSession
     */
    public final static Integer HTTP_EXT = new Integer(3);

    /**
     * Returns an adapted version of a session, adapted to a certain
     * protocol. This will be a javax.servlet.http.HttpSession object for HTTP.
     * 
     * @return Object adapted version of the ISession.
     */
    public Object adapt(ISession session, Integer protocol);

    /**
     * Returns an adapted version of a session, adapter to a certain protocol.
     * This method was added to help deal with converged, multi-protocol sessions.
     * 
     * @param session
     * @param protocol
     * @param request
     * @param context
     * @return
     */
    public Object adapt(ISession session, Integer protocol, ServletRequest request, ServletContext context);

    /**
     * This method allows for the returning of a correlator ID to be used during
     * multi-protocol scenarios or partition/hot failover based routing scenarios
     * 
     */
    public Object getCorrelator(ServletRequest request, Object session);

    public Integer getProtocol(Object session);

    // end of XD methods

    // ----------------------------------------
    // Public Members
    // ----------------------------------------
    /**
     * HTTP Protocol Constant, used to adaptation to HTTP.
     */
    public final static Integer HTTP = new Integer(1);

    /**
     * SIP Protocol Constant, used for adaptation to SIP.
     */
    public final static Integer SIP = new Integer(2);

    // ----------------------------------------
    // Public Methods
    // ----------------------------------------

    /**
     * Returns an adapted version of a session, adapted to the protocol of the
     * implementing class.
     * This will be a javax.servlet.http.HttpSession object for HTTP.
     * 
     * @return Object adapted version of the ISession.
     */
    public Object adapt(ISession session);

    public Object adaptToAppSession(ISession session);

}