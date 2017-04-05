/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 2005 */
/* The source code for this program is not published or otherwise divested           */
/* of its trade secrets, irrespective of what has been deposited with the            */
/* U.S. Copyright Office.                                                            */
/*                                                                                   */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                     */
/*                                                                                   */
/* DESCRIPTION:                                                                      */
/*                                                                                   */
/* Change History:                                                                   */
/*                                                                                   */
/* Date      Programmer  Defect         Description                                  */
/* --------  ----------  ------         -----------                                  */
/* 05/01/05  mdobbie     LIDB3603       Creation                                     */
/*                                                                                   */
/* ********************************************************************************* */
package com.ibm.ws.recoverylog.spi;

import java.util.HashMap;

import com.ibm.tx.util.alarm.Alarm;
import com.ibm.tx.util.alarm.AlarmListener;
import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;

//------------------------------------------------------------------------------
//Class: RecoveryLogService
//------------------------------------------------------------------------------
/**
* RLSSuspendTokenManager class manages the pairing of suspend/resume calls through
* the use of RLSSuspendTokens.   Also, in the event of a suspend token timing out i.e.
* a corresponding resume call has not been made within the alloted time for that suspend
* token, then the RLSSuspendTokenManager listens for any timeout events and as a result
* can trigger the resumption of the RecoveryLogService 
* 
*/
class RLSSuspendTokenManager implements AlarmListener {

    /**
     * WebSphere RAS TraceComponent registration
     */
    private static final TraceComponent tc = Tr.register(RLSSuspendTokenManager.class, TraceConstants.TRACE_GROUP, TraceConstants.NLS_FILE);
    
    /**
     * Single instance of the RLSSuspendTokenManager
     */
    private static RLSSuspendTokenManager _instance = new RLSSuspendTokenManager();
   
    /**
     * Map used to associate a RLSSuspendToken tokens with an Alarm 
     */
    private HashMap _tokenMap;
    
	private RLSSuspendTokenManager()
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "RLSSuspendTokenManager");
        
        // Instantiate the token map
        _tokenMap = new HashMap();
        
        if (tc.isEntryEnabled()) Tr.exit(tc, "RLSSuspendTokenManager", this);
    }
    
    /**
     * Returns the single instance of the RLSSuspendTokenManager
     */
    static RLSSuspendTokenManager getInstance()
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "getInstance");
        if (tc.isEntryEnabled()) Tr.exit(tc, "getInstance", _instance);
    	return _instance;
    }
    
    /**
     * Registers that a suspend call has been made on the RecoveryLogService and generates
     * a unique RLSSuspendToken which must be passed in to registerResume to cancel this
     * suspend operation  
     * 
     * @param timeout the  value in seconds in which a corresponding resume call is expected
     * @return A unique token
     */
    RLSSuspendToken registerSuspend(int timeout)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "registerSuspend", new Integer(timeout));
        
    	// Generate the suspend token
        // RLSSuspendToken token = new RLSSuspendTokenImpl();
        RLSSuspendToken token = Configuration.getRecoveryLogComponent()
            .createRLSSuspendToken(null);
        
        // Alarm reference
        Alarm alarm = null;
        
    	// For a timeout value greater than zero, we create an alarm
        // A zero timeout value indicates that this suspend operation will
        // never timeout, hence no alarm is required
        if (timeout > 0)
        {
            // Create an alarm
            // alarm = AlarmManager.createNonDeferrable(((long)timeout) * 1000L, this, token);
            alarm = Configuration.getAlarmManager().
                scheduleAlarm(timeout * 1000L, this, token);
            
            if (tc.isEventEnabled()) Tr.event(tc, "Alarm has been created for this suspend call", alarm);
        }
        
        synchronized(_tokenMap)
        {
        	// Register the token and the alarm with the token map
        	// bearing in mind that this alarm could be null
        	_tokenMap.put(token, alarm);
        }
        
        if (tc.isEntryEnabled()) Tr.exit(tc, "registerSuspend", token);
        
        // Return the generated token
    	return token;
    }
    
    public void alarm(Object object)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "alarm", object);
        
        // The alarm should only be fired with an RLSSuspendTokenImpl
    	// RLSSuspendTokenImpl tokenImpl = (RLSSuspendTokenImpl) object;
        
        synchronized(_tokenMap)
        {
            // Lookup token in the map
            if (_tokenMap.containsKey(object /*tokenImpl*/))
            {
                // Remove the entry from the map
            	_tokenMap.remove(object /*tokenImpl*/);
            	
            	Tr.info(tc, "CWRLS0022_RLS_SUSPEND_TIMEOUT", object /*tokenImpl*/);
            }
        }
        
        // Notify the RecoveryLogService that a suspend timed out
        RLSControllerImpl.notifyTimeout();
        
        if (tc.isEntryEnabled()) Tr.exit(tc, "alarm");
    }
    
    /**
     * Cancels the suspend request that returned the matching RLSSuspendToken.
     * The suspend call's alarm, if there is one, will also be cancelled
     * 
     * In the event that the suspend token is null or not recognized
     * throws an RLSInvalidSuspendTokenException
     * 
     * @param token
     * @throws RLSInvalidSuspendTokenException
     */
    void registerResume(RLSSuspendToken token) throws RLSInvalidSuspendTokenException
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "registerResume", token);
        
        if (token != null && _tokenMap.containsKey(token))
        {
            // Cast the token to its actual type
        	// RLSSuspendTokenImpl tokenImpl = (RLSSuspendTokenImpl) token;
            
            synchronized(_tokenMap)
            {
                // Remove the token and any associated alarm from the map
                Alarm alarm = (Alarm) _tokenMap.remove(token /*tokenImpl*/);
                
                // This suspend token is still active - check if
                // it has an alarm associated with it, and if so, cancel
                if (alarm != null)
                {
                    alarm.cancel();
                }
            }
        }
        else
        {
        	// Supplied token is null or could not be found in token map
            if (tc.isEventEnabled()) Tr.event(tc, "Throw RLSInvalidSuspendTokenException - suspend token is not recognised");
            if (tc.isEntryEnabled()) Tr.exit(tc, "registerResume", "RLSInvalidSuspendTokenException");
        	throw new RLSInvalidSuspendTokenException();
        }
        
        if (tc.isEntryEnabled()) Tr.exit(tc, "registerResume");
    }
    
    /**
     * Returns true if there are no tokens in the map
     * indicating that no active suspends exist.
     * @return
     */
    boolean isResumable()
    {
    	if (tc.isEntryEnabled()) Tr.entry(tc, "isResumable");
        
        boolean isResumable = true;
        
        synchronized(_tokenMap)
        {
            if (!_tokenMap.isEmpty())
            {
            	isResumable = false;
            }
        }
        
        if (tc.isEntryEnabled()) Tr.exit(tc, "isResumable", new Boolean(isResumable));
        
        return isResumable;
    }
}
