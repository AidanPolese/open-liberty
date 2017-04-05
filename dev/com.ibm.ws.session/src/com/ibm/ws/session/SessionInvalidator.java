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
 *  @(#) 1.1 SERV1/ws/code/web.session.core/src/com/ibm/ws/session/SessionInvalidator.java, WASCC.web.session.core, WASX.SERV1, o0901.11 10/13/06 15:52:48 [1/9/09 15:01:28]
 *
 * @(#)file   SessionInvalidator.java
 * @(#)version   1.1
 * @(#)date      10/13/06
 *
 *COPYRIGHT_END*************************************************************/
package com.ibm.ws.session;

import java.util.Timer;
import java.util.TimerTask;

import com.ibm.wsspi.session.IStore;
import com.ibm.wsspi.session.ITimer;

/**
 * @author dettlaff
 * 
 *         default invalidator that does not depend on websphere AlarmManager -
 *         uses java.util.Timer
 */
public class SessionInvalidator implements ITimer {

    private int _invalInterval = 60; // default to 1 minute
    private long _delay = 0; // default is 0
    private Timer _timer;
    private InvalidationTask _invalTask;

    /**
     * Method setStorageInterval
     * <p>
     * 
     * @param interval
     * @see com.ibm.wsspi.session.IStorer#setStorageInterval(int)
     */
    public void start(IStore store, int interval) {
        synchronized (this) {
            _invalInterval = interval;
            _timer = new Timer(true);
            _invalTask = new InvalidationTask(store);
            _timer.schedule(_invalTask, _delay * 1000, _invalInterval * 1000);
        }
    }

    public void stop() {
        _timer.cancel();
    }
    
    //PM74718
    public void setDelay(long invalStart) {
        _delay = invalStart;
    }

    protected class InvalidationTask extends TimerTask {

        /*
         * (non-Javadoc)
         * 
         * @see java.util.TimerTask#run()
         */
        IStore _store;

        public InvalidationTask(IStore store) {
            _store = store;
        }

        public void run() {

            _store.runInvalidation();

        }
    }

}
