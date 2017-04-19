// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5655-N01, 5733-W60. (C) COPYRIGHT International Business Machines Corp. 1997, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
// $PQ91098 (248534) H28W601, 20050302,PDEK; Rollup of ESI Invalidator fix
// $265641 (248534) H28W601, 20050408,PDEK; Call readInt() on all platforms
package com.ibm.ws.cache.servlet;

import java.io.IOException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.cache.RealTimeDaemon;

public class ESIProcessorKeepAliveDaemon extends RealTimeDaemon{

	ESIProcessor _esiProc = null;
	
	private static final TraceComponent tc = 
		Tr.register(ESIProcessorKeepAliveDaemon.class, "WebSphere Dynamic Cache", "com.ibm.ws.cache.resources.dynacache");
	
	protected ESIProcessorKeepAliveDaemon(long timeInterval) { //interval in milliseconds
		super(timeInterval);
		if (tc.isDebugEnabled()){
			Tr.debug(tc, "Spawned ESIProcessorKeepAliveDaemon with a frequency of "+ timeInterval+" ms");
		}
	}
	
	void setESIProcessor (ESIProcessor esiP){
		_esiProc = esiP;
	}
	
	/**
	 * Send a keep alive message i.e. getPID message to the plugin at 
	 * the specified timeinterval to keep the connection alive.
	 */
	@Override
	protected void wakeUp(long startDaemonTime, long startWakeUpTime) {
		
		if (tc.isDebugEnabled()){
			Tr.debug(tc, "sending a keep alive initPID to the plugin for "+_esiProc);
		}
		
		try {
			if (null != _esiProc){
				_esiProc.initPID();
			}			
		} catch (IOException e) {			
			Tr.debug(tc, "Error encountered when writing a keep alive initPID message to the plugin for "+_esiProc);
			_esiProc = null;
		}		
	}
	
	public void stop(){		
		if (tc.isDebugEnabled()){
			Tr.debug(tc, "stop called on the ESIProcessorKeepAliveDaemon");
		}
		super.stop();
	}
	
}
