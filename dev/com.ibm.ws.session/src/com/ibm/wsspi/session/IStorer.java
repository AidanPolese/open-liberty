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
 *  @(#) 1.1 SERV1/ws/code/web.session.core/src/com/ibm/wsspi/session/IStorer.java, WASCC.web.session.core, WASX.SERV1, o0901.11 10/13/06 15:53:44 [1/9/09 15:01:34]
 *
 * @(#)file   IStorer.java
 * @(#)version   1.1
 * @(#)date      10/13/06
 *
 *COPYRIGHT_END*************************************************************/
package com.ibm.wsspi.session;

/**
 * Analogous to ILoader, IStorer is the "write" side version of interacting
 * with an external store.
 * <p>
 * 
 */
public interface IStorer {

    /**
     * This method is called when a session's UOW has completed and hence
     * any updated state can be sent to the external store (either synchronously
     * or asynchronously). <br>
     * <br>
     * This is equivalent to the call storeSession(session, false);
     * <p>
     * 
     * @param ISession
     *            session: The session to be transferred to the external store
     */
    public void storeSession(ISession session);

    /**
     * This method is called when a session's UOW has completed and hence
     * any updated state can be sent to the external store (either synchronously
     * or asynchronously). <br>
     * 
     * @param ISession
     *            session: The session to be transferred to the external store
     * @param boolean usesCookies: set to true or false depending on whether this
     *        request sessionid
     *        was derived from a cookie or not.
     */
    public void storeSession(ISession session, boolean usesCookies);

    /**
     * If this particular store is sending out updates asynchronously, the
     * interval used to wait between updates can be set with this method. <br>
     * 
     * @param int interval: The time is seconds to wait between sending out of
     *        updates
     */
    public void setStorageInterval(int interval);

}
