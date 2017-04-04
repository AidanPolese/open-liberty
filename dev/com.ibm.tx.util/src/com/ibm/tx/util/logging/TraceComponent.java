package com.ibm.tx.util.logging;

public interface TraceComponent
{
    public boolean isEntryEnabled();

    public boolean isEventEnabled();

    public boolean isDebugEnabled();

    public void setWarningEnabled(boolean enabled);

    public void setEntryEnabled(boolean enabled);

    public void setEventEnabled(boolean enabled);

    public void setDebugEnabled(boolean enabled);

    public boolean isWarningEnabled();

    public Object getData();
}
