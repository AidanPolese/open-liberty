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
 *  @(#) 1.3 SERV1/ws/code/web.session.core/src/com/ibm/wsspi/session/ISessionAffinityManager.java, WASCC.web.session.core, WASX.SERV1, o0901.11 5/30/08 16:34:26 [1/9/09 15:01:32]
 *
 * @(#)file   ISessionAffinityManager.java
 * @(#)version   1.3
 * @(#)date      5/30/08
 *
 *COPYRIGHT_END*************************************************************/
package com.ibm.wsspi.session;

import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * This interface allows the cookie generator to be customized. Depending on
 * whether
 * the session management depends on the presence or absence of affinity, a
 * different
 * cookie generator can be plugged in.
 * 
 */
public interface ISessionAffinityManager {

    // XD vars
    // Constant for the JSessionID
    public final static String JSESSIONID = "JSESSIONID";
    public final static String SIP_HTTP_COOKIE_NAME = "SIP-HTTP-COOKIE";
    public final static String URL_REWRITE_PREFIX = ";jsessionid=";

    public String encodeURL(ServletRequest request, String url, SessionAffinityContext affinityContext, IProtocolAdapter adapter);

    public void setCookie(ServletRequest request, ServletResponse response, SessionAffinityContext affinityContext, IProtocolAdapter adapter, Object session);

    /**
     * Gets whatever default token this app server uses for http session affinity
     * to
     * this server and this server only
     * 
     * @return
     */
    public String getLocalCloneID();

    /**
     * Sets whether URL encoding is configured
     */
    public void setUseURLEncoding(boolean useURLEncoding);

    /**
     * Sets the name to use for the session cookie
     */
    public void setCookieName(String cookieName);

    // end of XD vars
    /**
     * Analyses the provided request object and extracts all the relevant
     * information
     * about affinity details that are available on the request, and visible by
     * using the
     * standard methods available on the ServletRequest object. A protocol
     * specific affinity manager may also be able to use protocol specific
     * extensions,
     * so in HTTP, methods on the HTTPServletRequest class may also be used.
     * <p>
     * 
     * @param ServletRequest
     *            request
     * @return SessionAffinityContext object that stores the information gleaned
     *         from the request.
     */
    public SessionAffinityContext analyzeRequest(ServletRequest request);

    /**
     * Encodes the URL with affinity information (URL rewriting)
     * <p>
     * 
     * @param ServletRequest
     *            request
     * @param String
     *            url
     * @param SessionAffinityContext
     *            affinityContext
     * @return String encoded URL
     */
    public String encodeURL(ServletRequest request, String url, SessionAffinityContext affinityContext);

    /**
     * Sets a cookie on the response object with the affinity details in the
     * affinityContext
     * object.
     * <p>
     * 
     * @param ServletRequest
     *            Incoming request object.
     * @param ServletResponse
     *            response on which the cookie is set
     * @param SessionAffinityContext
     *            affinityContext object that stores the affinity data of the
     *            request and response.
     */
    public void setCookie(ServletRequest request, ServletResponse response, SessionAffinityContext affinityContext, Object session);

    /**
     * If multiple IDs are recieved (for example, multiple JSESSIONID cookies) on
     * a single request, set the
     * next one in the SessionAffinityContext. This allows the session manager to
     * check all input ids.
     * 
     * @param sac
     * @return boolean that indicates whether another ID was present
     */
    public boolean setNextId(SessionAffinityContext sac);

    /**
     * Returns the session id to use for this request. Maybe the SSL session id,
     * the
     * requested session id, or the response session id. The response session id
     * is used
     * if we've been dispatched and another servlet has already generated the
     * response id.
     * 
     * @param req
     * @param sac
     * @return String
     */
    public String getInUseSessionID(ServletRequest req, SessionAffinityContext sac);

    /**
     * Returns the session version to use for this request. The response session
     * version is used
     * if we've been dispatched and another servlet has already set the response
     * version.
     * 
     * @param req
     * @param sac
     * @return String
     */
    public int getInUseSessionVersion(ServletRequest req, SessionAffinityContext sac);

    /**
     * Returns a list of all the cookie values with the session cookie name.
     * 
     * @param request
     * @return List
     */
    public List getAllCookieValues(ServletRequest request);
}
