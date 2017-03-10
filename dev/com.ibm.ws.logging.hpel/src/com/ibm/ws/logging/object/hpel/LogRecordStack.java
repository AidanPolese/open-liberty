//%Z% %I% %W% %G% %U% [%H% %T%]
/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2011, 2012
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *
 * Change History:
 *
 * Reason           Version        Date       User id     Description
 * ----------------------------------------------------------------------------
 * F1344-49496       8.0        10/24/2011   belyi       Initial MDC code
 * 729954            8.5        03/16/2012   belyi       Introduce StackInfo
 * 725321            8.5        03/21/2012   belyi       Add Thread.getId() into StackInfo and move getters into LogRecordStack itself
 */
package com.ibm.ws.logging.object.hpel;

import java.util.HashMap;
import java.util.Map;

import com.ibm.ejs.ras.hpel.HpelHelper;
import com.ibm.websphere.logging.hpel.LogRecordContext;

/**
 * Utility class to pass MDC values of records published by asynchronous loggers
 * to the handler. Publishing code is responsible for both setting values and cleaning
 * it up afterwards.
 */
public class LogRecordStack {

	/**
	 * Utility class containing thread specific information asynchronous loggers
	 * need to pass to the handler.
	 */
	public static class StackInfo {
		private final Map<String,String> extensions;
		private final int threadId;

		public StackInfo() {
			extensions = collectExtensions();
			threadId = HpelHelper.getIntThreadId();
		}
		
		private static Map<String, String> collectExtensions() {
			 Map<String, String> extensions = new HashMap<String,String>();
			 LogRecordContext.getExtensions(extensions);
			 return extensions;
		}
	}
    
	 private final static ThreadLocal<StackInfo> MDC = new ThreadLocal<StackInfo>();

	 /**
	  * Returns current stack information. It is called by log handler to get
	  * thread specific information which is either passed for the record in a
	  * request or obtained from the thread directly.
	  * 
	  * @return {@link StackInfo} instance setup in the current thread
	  */
	 public static StackInfo getStack() {
		 StackInfo result = MDC.get();
		 return result == null ? new StackInfo() : result;
	 }
	 
	 /**
	  * Returns thread id from the current stack. It is called by log handler
	  * to get thread id which is either passed for the record in a request or
	  * obtained from the thread directly.
	  * 
	  * @return thread id from the stack.
	  */
	 public static int getThreadID() {
		 StackInfo result = MDC.get();
		 return result == null ? HpelHelper.getIntThreadId() : result.threadId;
	 }
	 
	 /**
	  * Returns context extensions from the current stack. It is called by log handler
	  * to get extensions which are either passed for the record in a request or
	  * obtained from the thread directly.
	  * 
	  * @return extensions from the stack.
	  */
	 public static Map<String,String> getExtensions() {
		 StackInfo result = MDC.get();
		 if (result == null) {
			 return StackInfo.collectExtensions();
		 } else {
			 return result.extensions;
		 }
	 }
	 
     /**
      * Sets current stack information. It is called by publishing code for records
      * logged with asynchronous loggers to pass on values effective at the time
      * log request was made.
      * @param stack new {@link StackInfo} to setup in the current thread
      */
	 public static void setStack(StackInfo stack) {
		 MDC.set(stack);
	 }
	 
	 /**
	  * Clears current map from the stack. It is called by publishing code after
	  * all records logged with asynchronous loggers were set to log handler.
	  */
	 public static void clear() {
		 MDC.remove();
	 }
}
