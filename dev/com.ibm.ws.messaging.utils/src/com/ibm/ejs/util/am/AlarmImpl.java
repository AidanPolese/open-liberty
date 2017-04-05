package com.ibm.ejs.util.am;

import java.util.concurrent.ScheduledFuture;

public class AlarmImpl implements Alarm {

	private ScheduledFuture future = null;

	AlarmImpl(ScheduledFuture future) {
		this.future = future;
	}

	public void cancel() {
		future.cancel(true);
	}
}
