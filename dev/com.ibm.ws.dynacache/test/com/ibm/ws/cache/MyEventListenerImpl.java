// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2003
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache;

import java.util.ArrayList;
import java.util.List;

import com.ibm.websphere.cache.ChangeEvent;
import com.ibm.websphere.cache.ChangeListener;
import com.ibm.websphere.cache.InvalidationEvent;
import com.ibm.websphere.cache.InvalidationListener;

public class MyEventListenerImpl implements InvalidationListener, ChangeListener {
    private int _typeOfEvent;
    private int _expectedEvents;
    private int _eventsReceived = 0;
    private long _start, _end;
    private boolean _finished = false;
    private final Object _listenerId;
    private final List<InvalidationListenerInfo> _events = new ArrayList<InvalidationListenerInfo>(3);

    public MyEventListenerImpl(Object listenerId, int expectedEvents) {
        _listenerId = listenerId;
        _expectedEvents = expectedEvents;
        _eventsReceived = 0;
        _events.clear();
        _start = System.currentTimeMillis();
    }

    public void fireEvent(InvalidationEvent ie) {

        InvalidationListenerInfo info = new InvalidationListenerInfo(ie.getId(), ie.getValue(), ie.getCauseOfInvalidation(), ie.getSourceOfInvalidation(), ie.getCacheName());
        if (ie.getCauseOfInvalidation() == _typeOfEvent) {
            _events.add(info);
            System.out.println("\t ** ListenerId=" + _listenerId + " " + info.toString() + " timestamp=" + ie.getTimeStamp());
        } else {
            System.err.println("\t ** ListenerId=" + _listenerId + " " + info.toString() + " timestamp=" + ie.getTimeStamp());
        }

        try {
            java.lang.Thread.sleep(5);
        } catch (Exception e) {
        }
        if (++_eventsReceived == _expectedEvents) {
            _end = System.currentTimeMillis();
            System.out.println("** Received " + _expectedEvents + " events in " + (_end - _start) + " ms");
            synchronized (this) {
                _finished = true;
            }
        }
    }

    /**
     * This method is invoked when there is a change to a cache entry
     */
    public void cacheEntryChanged(ChangeEvent ce) {

        if (ce.getCauseOfChange() == _typeOfEvent) {
            InvalidationListenerInfo info = new InvalidationListenerInfo(ce.getId(), ce.getValue(), ce.getCauseOfChange(), ce.getSourceOfChange(), ce.getCacheName());
            _events.add(info);
            System.out.println("\t ** ListenerId=" + _listenerId + " " + info.toString() + " timestamp=" + ce.getTimeStamp());
        }

        try {
            java.lang.Thread.sleep(5);
        } catch (Exception e) {
        }
        if (++_eventsReceived == _expectedEvents) {
            _end = System.currentTimeMillis();
            System.out.println("** Received " + _expectedEvents + " events in " + (_end - _start) + " ms");
            synchronized (this) {
                _finished = true;
            }
        }
    }

    public synchronized void waitOnCompletion() {
        long t1 = System.currentTimeMillis();
        long t, t2;
        do {
            try {
                java.lang.Thread.sleep(100);
            } catch (Exception e) {
            }
            t2 = System.currentTimeMillis();
            t = t2 - t1;
            if (t > 20000) {
                _finished = true;
            }
        } while (_finished == false);
        _finished = false;
    }

    public void restart(int expectedEvents) {
        _events.clear();
        _start = System.currentTimeMillis();
        _expectedEvents = expectedEvents;
        _eventsReceived = 0;
        _finished = false;
    }

    public String compare(InvalidationListenerInfo info, int size) {
        if (info == null) {
            if (_events.isEmpty())
                return "";
            else {
                Object[] eArray = _events.toArray();
                for (int i = 0; i < eArray.length; i++) {
                    InvalidationListenerInfo linfo = (InvalidationListenerInfo) eArray[i];
                    System.out.println("** ERROR **" + linfo.toString());
                }
                return "** Error: some events happened - expected no events ";
            }
        }

        if (size != _events.size())
            return "** Error: expected # events = " + size + " but received # events = " + _events.size();

        if (_events.isEmpty())
            return "** Error: No event happened";

        String s = "BAD";
        Object[] eArray = _events.toArray();
        for (int i = 0; i < eArray.length; i++) {
            InvalidationListenerInfo linfo = (InvalidationListenerInfo) eArray[i];
            s = linfo.compare(info);
            if (s.equals(""))
                break;
            else if (!s.startsWith("Id")) {
                break;
            }
        }
        return s;

    }

    public void setTypeOfEvent(int typeOfEvent) {
        _typeOfEvent = typeOfEvent;
    }

}
