/*COPYRIGHT_START***********************************************************
 *
 * IBM Confidential OCO Source Material
 * 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2015
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
 *
 *
 * @(#)file   SessionInvalidatorWithThreadPool.java
 * @(#)version   1.0
 * @(#)date      06/16/15
 *
 *COPYRIGHT_END*************************************************************/
package com.ibm.ws.session;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.ibm.ws.webcontainer.httpsession.SessionMgrComponentImpl;
import com.ibm.wsspi.session.IStore;
import com.ibm.wsspi.session.ITimer;

/*This class is similar to SessionInvalidator but instead uses ScheduledExecutorService to control thread pool
 */

public class SessionInvalidatorWithThreadPool implements ITimer{

    private int _invalInterval = 60; // default to 1 minute
    private long _delay = 0; // default is 0
    private InvalidationTask _invalTask;
    private ScheduledExecutorService _scheduler; 
    private ScheduledFuture<?> _result;

 
    public void start(IStore store, int interval) {
        _invalInterval = interval;
        
        /*Get reference to ScheduledExecutorService from SessionMgrComponentImpl*/
        _scheduler = SessionMgrComponentImpl.INSTANCE.get().getScheduledExecutorService();
        
        _invalTask = new InvalidationTask(store);
        
        /*schedule periodic invalidation task and store in ScheduledFuture object*/
        _result=_scheduler.scheduleWithFixedDelay(_invalTask, _delay * 1000, _invalInterval * 1000, TimeUnit.MILLISECONDS);
        
    }

    public void stop() {
        /*allows the current running task to continue to completion but stops any further tasks from running*/
        _result.cancel(false);
    }
    
    public void setDelay(long invalStart) {
        _delay = invalStart;
    }
 
    protected static class InvalidationTask implements Runnable {

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
