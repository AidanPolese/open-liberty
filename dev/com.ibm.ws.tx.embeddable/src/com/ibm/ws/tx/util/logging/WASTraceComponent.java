package com.ibm.ws.tx.util.logging;

import com.ibm.ejs.ras.TraceComponent;

public class WASTraceComponent implements com.ibm.tx.util.logging.TraceComponent
{
    private TraceComponent _tc;

    public WASTraceComponent(TraceComponent tc)
    {
        _tc = tc;
        
    }

    public Object getData()
    {
        return _tc;
    }

    public boolean isDebugEnabled()
    {
        return TraceComponent.isAnyTracingEnabled() && _tc.isDebugEnabled();
    }

    public boolean isEntryEnabled()
    {
        return TraceComponent.isAnyTracingEnabled() && _tc.isEntryEnabled();
    }

    public boolean isEventEnabled()
    {
        return TraceComponent.isAnyTracingEnabled() && _tc.isEventEnabled();
    }

    public boolean isWarningEnabled()
    {
        return TraceComponent.isAnyTracingEnabled() && _tc.isWarningEnabled();
    }

	public void setDebugEnabled(boolean arg0)
	{
		// Not required in WAS version
		
	}

	public void setEntryEnabled(boolean arg0)
	{
		// Not required in WAS version
		
	}

	public void setEventEnabled(boolean arg0)
	{
		// Not required in WAS version
		
	}

	public void setWarningEnabled(boolean arg0)
	{
		// Not required in WAS version
		
	}
}
