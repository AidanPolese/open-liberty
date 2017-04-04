// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.webcontainer.async;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.wsspi.webcontainer.logging.LoggerFactory;

public class AsyncServletReentrantLock extends ReentrantLock {
	protected static Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.ws.webcontainer.async");
    private static final String CLASS_NAME = "com.ibm.ws.webcontainer.async.AsyncServletReentrantLock";
	private AtomicBoolean isValid = new AtomicBoolean(true);
	public boolean getAndSetIsValid(boolean value){
		boolean ret = isValid.getAndSet(value);
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINEST))
		{
			logger.logp(Level.FINER,CLASS_NAME,"getAndSetIsValid","isValid->"+ret);
		}
		return ret;
	}
}
