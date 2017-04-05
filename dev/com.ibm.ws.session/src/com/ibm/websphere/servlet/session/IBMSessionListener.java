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
 *  @(#) 1.1 SERV1/ws/code/web.session.shell/src/com/ibm/websphere/servlet/session/IBMSessionListener.java, WASCC.web.session, WASX.SERV1, o0901.11 10/13/06 15:50:12 [1/9/09 15:00:58]
 *
 * @(#)file   IBMSessionListener.java
 * @(#)version   1.1
 * @(#)date      10/13/06
 *
 *COPYRIGHT_END*************************************************************/
package com.ibm.websphere.servlet.session;

import javax.servlet.http.HttpSessionListener;

/**
 * The IBMSessionListener interface extends the
 * javax.servlet.http.HttpSessionListener
 * interface of the Servlet API to notify an application that its session has
 * been
 * removed from the server's cache. This could be because:
 * <ul>
 * <li>the session timed out
 * <li>the session was programatically invalidated
 * <li>the session cache is full and this is the least-recently-used session
 * (distributed environment only)
 * </ul>
 * <p>
 * A session will eventually time out on every server that accesses the session,
 * and therefore sessionRemovedFromCache() will be called on all of these
 * servers. sessionDestroyed() is only called during session invalidation, which
 * only happens on one server. Further, the server that invalidates and calls
 * sessionDestroyed() may or may not be the same server that created a session
 * and called sessionCreated().
 * </p>
 * 
 * @ibm-api
 */
public interface IBMSessionListener extends HttpSessionListener {

    public void sessionRemovedFromCache(String sessionId);

}
