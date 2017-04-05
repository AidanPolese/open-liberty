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
 *  @(#) 1.1 SERV1/ws/code/web.session.shell/src/com/ibm/ws/webcontainer/httpsession/SessionManager.java, WASCC.web.session, WASX.SERV1, o0901.11 10/13/06 15:50:48 [1/9/09 15:01:02]
 *
 * @(#)file   SessionManager.java
 * @(#)version   1.1
 * @(#)date      10/13/06
 *
 *COPYRIGHT_END*************************************************************/

package com.ibm.ws.webcontainer.httpsession;

import com.ibm.ws.session.SessionContextRegistry;
import com.ibm.ws.session.SessionManagerConfig;
import com.ibm.ws.session.SessionStoreService;

/**
 * @author asisin
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface SessionManager {

    void start(SessionContextRegistry scr);

    /**
     * The unique identifier for this server among a group of other servers.
     * 
     * @return a unique server identifier
     */
    public String getCloneID();

    /**
     * Gets the character used to delimit clone IDs in session cookies.
     * Usually either ':' or '+' will be returned.
     * 
     * @return the character used to delimit clone IDs in session cookies
     */
    public char getCloneSeparator();

    /**
     * Gets the value of the "rewriteId" property, used for URL rewriting.
     * 
     * @return the rewriteId, such as jsessionid
     */
    public String getAffinityUrlIdentifier();

    /**
     * Gets the default name of session cookies. Note that this value can be
     * overridden by individual applications at run time. Whenever possible,
     * the exact cookie name should be read from the ServletContext instead
     * of this value.
     * 
     * @return the default name of session cookies
     */
    public String getDefaultAffinityCookie();

    /**
     * Retrieve the session manager configuration information at the web container/server level.
     * 
     * @return session manager configuration information at the web container/server level
     */
    public SessionManagerConfig getServerConfig();

    /**
     * Retrieves the store service associated with this instance.
     * 
     * @return the store service, or null if the service is unavailable or invalid
     */
    public SessionStoreService getSessionStoreService();
    
}
