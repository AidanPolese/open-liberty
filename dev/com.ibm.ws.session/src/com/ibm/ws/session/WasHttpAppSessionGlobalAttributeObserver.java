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
 *  @(#) 1.1 SERV1/ws/code/web.session.shell/src/com/ibm/ws/session/WasHttpAppSessionGlobalAttributeObserver.java, WASCC.web.session, WASX.SERV1, o0901.11 10/1/07 16:39:01 [1/9/09 15:01:00]
 *
 * @(#)file   WasHttpAppSessionGlobalAttributeObserver.java
 * @(#)version   1.1
 * @(#)date      10/1/07
 *
 *COPYRIGHT_END*************************************************************/

package com.ibm.ws.session;

import java.util.ArrayList;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.ibm.ws.session.http.HttpSessionAttributeObserver;
import com.ibm.wsspi.session.IProtocolAdapter;
import com.ibm.wsspi.session.ISession;

/**
 * This implementation of the ISessionStateObserver drives the
 * sessionAttributeListener invocation
 * mechanism by registering for callbacks with the base session manager.
 */
public class WasHttpAppSessionGlobalAttributeObserver extends HttpSessionAttributeObserver {

    private static final String methodClassName = "WasHttpAppSessionAttributeObserver";

    // ----------------------------------------
    // Public Constructor
    // ----------------------------------------
    public WasHttpAppSessionGlobalAttributeObserver(ArrayList listeners, IProtocolAdapter adapter) {
        super(listeners, adapter);
    }

    // ----------------------------------------
    // Public Methods
    // ----------------------------------------

    /**
     * Method sessionAttributeSet
     * <p>
     * 
     * @see com.ibm.wsspi.session.ISessionStateObserver#sessionAttributeSet(com.ibm.wsspi.session.ISession, java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public void sessionAttributeSet(ISession session, Object name, Object oldValue, Boolean oldIsListener, Object newValue, Boolean newIsListener) {

        HttpSession httpsession = (HttpSession) _adapter.adapt(session);
        HttpSessionBindingEvent addEvent = null; // only init if necessary
        HttpSessionBindingEvent replaceEvent = null; // only init this if
                                                     // necessary..done below

        // do binding listeners first to be consistent with v6.1
        if ((oldValue != null) && (oldIsListener.booleanValue())) {
            replaceEvent = new HttpSessionBindingEvent(httpsession, (String) name, oldValue);
            // if (oldValue instanceof HttpSessionBindingListener)
            ((HttpSessionBindingListener) oldValue).valueUnbound(replaceEvent);
        }
        if ((newValue != null) && (newIsListener.booleanValue())) { // (newValue instanceof HttpSessionBindingListener) ) {
            addEvent = new HttpSessionBindingEvent(httpsession, (String) name, newValue);
            ((HttpSessionBindingListener) newValue).valueBound(addEvent);
        }
    }

    /**
     * Method sessionAttributeRemoved
     * <p>
     * 
     * @see com.ibm.wsspi.session.ISessionStateObserver#sessionAttributeRemoved(com.ibm.wsspi.session.ISession, java.lang.Object, java.lang.Object)
     */
    public void sessionAttributeRemoved(ISession session, Object name, Object value, Boolean oldIsBindingListener) {
        HttpSessionAttributeListener listener = null;
        HttpSessionBindingEvent event = null;
        HttpSession httpsession = (HttpSession) _adapter.adapt(session);

        event = new HttpSessionBindingEvent(httpsession, (String) name, value);
        if (value instanceof HttpSessionBindingListener) {
            if (oldIsBindingListener.booleanValue()) {
                ((HttpSessionBindingListener) value).valueUnbound(event);
            }
        }
    }

}
