package com.ibm.ws.fat.util.browser;

/**
 * Thread-safe counter
 * 
 * @author Tim Burns
 *
 */
public class Counter {

	protected int count;
	
	/**
	 * Primary Constructor
	 */
	public Counter() {
		this.count = 0;
	}
	
	/**
	 * Increment this instance by one
	 * 
	 * @return
	 */
	public synchronized int next() {
		this.count++;
		return this.count;
	}
}
