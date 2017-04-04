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
 *  @(#) 1.1 SERV1/ws/code/web.session.core/src/com/ibm/wsspi/session/ISessionObserver.java, WASCC.web.session.core, WASX.SERV1, o0901.11 10/13/06 15:53:35 [1/9/09 15:01:33]
 *
 * @(#)file   ISessionObserver.java
 * @(#)version   1.1
 * @(#)date      10/13/06
 *
 *COPYRIGHT_END*************************************************************/
package com.ibm.wsspi.session;

public interface ISessionObserver {

    /**
     * Method sessionCreated
     * <p>
     * 
     * @param session
     */
    public void sessionCreated(ISession session);

    /**
     * Method sessionAccessed
     * <p>
     * 
     * @param session
     */
    public void sessionAccessed(ISession session);

    public void sessionAccessUnknownKey(Object key);

    /**
     * Method sessionDestroyed
     * <p>
     * 
     * @param session
     */
    public void sessionDestroyed(ISession session);

    public void sessionDestroyedByTimeout(ISession session);

    /**
     * Method sessionReleased
     * <p>
     * 
     * @param session
     */
    public void sessionReleased(ISession session);

    /**
     * Method sessionFlushed
     * <p>
     * 
     * @param session
     */
    public void sessionFlushed(ISession session);

    /**
     * Method sessionDidActivate
     * <p>
     * 
     * @param session
     */
    public void sessionDidActivate(ISession session);

    /**
     * Method sessionWillPassivate
     * <p>
     * 
     * @param session
     */
    public void sessionWillPassivate(ISession session);

    /**
     * Method getId
     * <p>
     * 
     * @return String
     */
    public String getId();

    /**
     * @param session
     */
    public void sessionAffinityBroke(ISession session);

    /**
     * @param value
     */
    public void sessionCacheDiscard(Object value);

    /**
     * @param value
     */
    public void sessionLiveCountInc(Object value);

    /**
     * @param value
     */
    public void sessionLiveCountDec(Object value);
    
    /**
     * Method sessionCreated
     * <p>
     * 
     * @param oldId
     * @param session
     */
    public void sessionIdChanged(String oldId, ISession session);

}