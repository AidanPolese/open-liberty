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
 *  @(#) 1.3 SERV1/ws/code/web.session.shell/src/com/ibm/websphere/servlet/session/IBMApplicationSession.java, WASCC.web.session, WASX.SERV1, o0901.11 10/21/08 10:02:10 [1/9/09 15:00:57]
 *
 * @(#)file   IBMApplicationSession.java
 * @(#)version   1.3
 * @(#)date      10/21/08
 *
 *COPYRIGHT_END*************************************************************/
package com.ibm.websphere.servlet.session;

import java.util.Collection;
import java.util.Iterator;

/**
 * The IBMApplicationSession interface is used for SIP/HTTP Converged
 * Applications
 * as well as for sharing session data in http applications.
 * HTTP Servlets can call session.getIBMApplicationSession, whereas SIP Servlets
 * call session.getApplicationSession. In a converged application, both of these
 * calls will return the same SipApplicationSession object.
 * 
 * @ibm-api
 */

public interface IBMApplicationSession {

    /**
     * Encodes the ID of this IBMApplicationSession into the specified SIP URI.
     * 
     * @param URI
     *            object expected to be an instance of javax.servlet.sip.URI
     */
    public void encodeURI(Object URI);

    /**
     * Encodes the ID of this IBMApplicationSession into the specified HTTP URI.
     * 
     * @param URI
     *            string representation of the http uri
     * @return string with encoded application session id added
     */
    public String encodeURI(String URI);

    /**
     * Returns the object bound with the specified name in this session, or null
     * if no object is bound under the name.
     * 
     * @param name
     *            name of the attribute to retrieve
     */
    public Object getAttribute(String name);

    /**
     * Returns an Iterator over the String objects containing the names of all the
     * objects bound to this session.
     */
    public Iterator getAttributeNames();

    /**
     * Returns the time when this session was created, measured in milliseconds
     * since midnight January 1, 1970 GMT.
     */
    public long getCreationTime();

    /**
     * Returns a string containing the unique identifier assigned to this session.
     */
    public String getId();

    /**
     * Returns the last time an event occurred on this application session.
     */
    public long getLastAccessedTime();

    /**
     * Returns an Iterator over all "protocol" sessions associated with this
     * application session.
     */
    public Iterator getSessions();

    /**
     * Returns an Iterator over the "protocol" session objects associated of the
     * specified protocol
     * associated with this application session.
     * 
     * @param protocol
     *            string representation of protocol, either "SIP" or "HTTP"
     */
    public Iterator getSessions(String protocol);

    /**
     * Returns all active timers associated with this application session.
     */
    public Collection getTimers();

    /**
     * Invalidates this application session.
     */
    public void invalidate();

    /**
     * Removes the object bound with the specified name from this session.
     * 
     * @param name
     *            name of the attribute to remove
     */
    public void removeAttribute(String name);

    /**
     * Binds an object to this session, using the name specified.
     * 
     * @param name
     *            name of the attribute to set
     * @param attribute
     *            value of the attribute to set
     */
    public void setAttribute(String name, Object attribute);

    /**
     * Sets the time of expiry for this application session.
     * 
     * @param deltaMinutes
     *            the number of minutes that the lifetime of this
     *            IBMApplicationSession is extended
     * @return actual number of minutes the lifetime of this session is extended,
     *         or 0 if it wasn't extended
     */
    public int setExpires(int deltaMinutes);

    /**
     * Forces replication of application session changes
     */
    public void sync();


}
