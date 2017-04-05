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
 *  @(#) 1.4 SERV1/ws/code/web.session.shell/src/com/ibm/ws/session/WasHttpSessionObserver.java, WASCC.web.session, WASX.SERV1, uu1050.39 5/7/10 15:21:54 [12/22/10 17:06:13]
 *
 * @(#)file   WasHttpSessionObserver.java
 * @(#)version   1.4
 * @(#)date      5/7/10
 *
 *COPYRIGHT_END*************************************************************/
package com.ibm.ws.session;

import java.util.ArrayList;
import java.util.logging.Level;

import javax.servlet.http.HttpSessionListener;

import com.ibm.websphere.servlet.session.IBMSessionListener;
import com.ibm.ws.session.http.HttpSessionObserver;
import com.ibm.ws.session.utils.LoggingUtil;
import com.ibm.wsspi.session.IProtocolAdapter;
import com.ibm.wsspi.session.ISession;

public class WasHttpSessionObserver extends HttpSessionObserver {

    // For logging.
    private static final String methodClassName = "WasHttpSessionObserver";

    // boolean to tell us if we should scan to listeners to find the
    // IBMSessionListener
    // and then invoke call sessionRemovedFromCache. This boolean provides a
    // performance improvement.
    private boolean doesContainIBMSessionListener = false;

    /*
     * constructor
     */
    public WasHttpSessionObserver(ArrayList listeners, IProtocolAdapter adapter) {
        super(listeners, adapter);
        // listeners is an empty ArrayList and therefore we can't set the
        // doesContainIBMSessionListener variable here.
        // setDoesContainIBMSessionListener(boolean) should be called
    }
    
    // Should call the new HttpSessionObserver constructor
    public WasHttpSessionObserver(ArrayList listeners, ArrayList idListeners, IProtocolAdapter adapter) {
        super(listeners, idListeners, adapter);
        // listeners is an empty ArrayList and therefore we can't set the
        // doesContainIBMSessionListener variable here.
        // setDoesContainIBMSessionListener(boolean) should be called
    }

    /*
     * Handles the IBM Extension to the HttpSessionListener, IBSessionListener
     * Calls apps sessionRemovedFromCache method
     */
    public void sessionCacheDiscard(Object value) {
        HttpSessionListener listener = null;

        if (doesContainIBMSessionListener) {
            for (int i = _sessionListeners.size() - 1; i >= 0; i--) {
                listener = (HttpSessionListener) _sessionListeners.get(i);
                if (listener != null && listener instanceof IBMSessionListener) {
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && LoggingUtil.SESSION_LOGGER_CORE.isLoggable(Level.FINE)) {
                        LoggingUtil.SESSION_LOGGER_CORE.logp(Level.FINE, methodClassName, "sessionCacheDiscard", "Calling sessionCacheDiscard on listener:" + listener);
                    }
                    ((IBMSessionListener) listener).sessionRemovedFromCache(((ISession) value).getId());
                }
            }
        }
    }

    /*
     * Setter for doesContainIBMSessionListener boolean.
     */
    public void setDoesContainIBMSessionListener(boolean value) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && LoggingUtil.SESSION_LOGGER_CORE.isLoggable(Level.FINE)) {
            LoggingUtil.SESSION_LOGGER_CORE.logp(Level.FINE, methodClassName, "setDoesContainIBMSessionListener", "" + value);
        }
        doesContainIBMSessionListener = value;
    }

    /*
     * Getter for doesContainIBMSessionListener variable
     */
    public boolean getDoesContainIBMSessionListener() {
        return doesContainIBMSessionListener;
    }
}
