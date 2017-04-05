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
 *  @(#) 1.4 SERV1/ws/code/web.session.core/src/com/ibm/wsspi/session/ISessionStateObserver.java, WASCC.web.session.core, WASX.SERV1, o0901.11 5/30/08 16:34:28 [1/9/09 15:01:33]
 *
 * @(#)file   ISessionStateObserver.java
 * @(#)version   1.4
 * @(#)date      5/30/08
 *
 *COPYRIGHT_END*************************************************************/
package com.ibm.wsspi.session;

public interface ISessionStateObserver {

    /**
     * Method sessionAttributeSet
     * <p>
     * 
     * @param session
     * @param key
     * @param oldValue
     * @param newValue
     */
    public void sessionAttributeSet(ISession session, Object key, Object oldValue, Boolean oldIsListener, Object newValue, Boolean newIsListener);

    /**
     * Method sessionAttributeRemoved
     * <p>
     * 
     * @param session
     * @param key
     * @param value
     */
    public void sessionAttributeRemoved(ISession session, Object key, Object value, Boolean oldIsBindingListener);

    // XD methods
    public void sessionAttributeSet(ISession session, Object key, Object oldValue, Object newValue);

    public void sessionAttributeRemoved(ISession session, Object key, Object value);

    /**
     * Method sessionAttributeAccessed
     * <p>
     * 
     * @param session
     * @param key
     * @param value
     */
    public void sessionAttributeAccessed(ISession session, Object key, Object value);

    /**
     * Method sessionUserNameSet
     * <p>
     * 
     * @param session
     * @param oldUserName
     * @param newUserName
     */
    public void sessionUserNameSet(ISession session, String oldUserName, String newUserName);

    /**
     * Method sessionLastAccessTimeSet
     * <p>
     * 
     * @param session
     * @param old
     * @param newaccess
     */
    public void sessionLastAccessTimeSet(ISession session, long old, long newaccess);

    /**
     * Method sessionMaxInactiveTimeSet
     * <p>
     * 
     * @param session
     * @param old
     * @param newval
     */
    public void sessionMaxInactiveTimeSet(ISession session, int old, int newval);

    /**
     * Method sessionExpiryTimeSet
     * <p>
     * 
     * @param session
     * @param old
     * @param newone
     *            TODO Do we even support the setting of a last Expiry time?
     */
    public void sessionExpiryTimeSet(ISession session, long old, long newone);

    /**
     * Method getId
     * <p>
     * 
     * @return String
     */
    public String getId();

}