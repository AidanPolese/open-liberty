/*COPYRIGHT_START***********************************************************
 *
 * IBM Confidential OCO Source Material
 * 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2012
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
 *  @(#) 1.1 SERV1/ws/code/session.shell/src/com/ibm/ws/session/WsManualSessionStorer.java, WAS.session, WASX.SERV1, ff1146.05 10/13/06 16:08:07 [11/21/11 18:33:12]
 *
 * @(#)file   WsManualSessionStorer.java
 * @(#)version   1.1
 * @(#)date      10/13/06
 *
 *COPYRIGHT_END*************************************************************/
package com.ibm.ws.session;

import com.ibm.wsspi.session.ISession;
import com.ibm.wsspi.session.IStorer;

public class ManualSessionStorer implements IStorer {

    /**
     * Method storeSession
     * <p>
     * 
     * @param session
     * @see com.ibm.wsspi.session.IStorer#storeSession(com.ibm.wsspi.session.ISession)
     */
    public void storeSession(ISession session) {
        session.flush(true); // only cache last accessed times in manual mode
    }

    /**
     * Method storeSession
     * <p>
     * 
     * @param session
     * @param boolean
     * @see com.ibm.wsspi.session.IStorer#storeSession(com.ibm.wsspi.session.ISession)
     */
    public void storeSession(ISession session, boolean usesCookies) {
        session.flush(true); // only cache last acccessed times in manual mode
    }

    /**
     * Method setStorageInterval
     * <p>
     * 
     * @param interval
     * @see com.ibm.wsspi.session.IStorer#setStorageInterval(int)
     */
    public void setStorageInterval(int interval) {

    }

}
