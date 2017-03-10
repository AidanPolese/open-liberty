// %Z% %I% %W% %G% %U% [%H% %T%]
/**
 * COMPONENT_NAME: WAS.ras
 *
 * ORIGINS: 27         (used for IBM originated files)
 *
 * IBM Confidential OCO Source Material
 * 5639-D57, 5630-A36, 5630-A37, 5724-D18 (C) COPYRIGHT International Business Machines Corp. 1999,2000,2001,2002
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * DESCRIPTION:
 *
 * Change History:
 *
 * Reason    Version  Date        User id     Description
 * ----------------------------------------------------------------------------
 * LIDB1241    6.0    12-29-2003  dbourne     JSR47
 *
 */

package com.ibm.ws.logging.object.hpel;

import java.util.ResourceBundle;
import java.util.logging.Level;

public interface ILogRecord {
	Level getLevel();
	String getLoggerName();
	String getMessage();
	long getMillis();
	Object[] getParameters();
	ResourceBundle getResourceBundle();
	String getResourceBundleName();
	long getSequenceNumber();
	String getSourceClassName();
	String getSourceMethodName();
	int getThreadID();
	Throwable getThrown();
	void setLevel(Level level);
	void setLoggerName(String name);
	void setMessage(String message);
	void setMillis(long millis);
	void setParameters(Object parameters[]);
	void setResourceBundle(ResourceBundle bundle);
	void setResourceBundleName(String name);
	void setSequenceNumber(long seq);
	void setSourceClassName(String sourceClassName);
	void setSourceMethodName(String sourceMethodName);
	void setThreadID(int threadID);
	void setThrown(Throwable thrown);
}