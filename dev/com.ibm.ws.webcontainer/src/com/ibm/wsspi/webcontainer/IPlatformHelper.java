// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.wsspi.webcontainer;

public interface IPlatformHelper {

	public abstract Object securityIdentityPush();

	public abstract void securityIdentityPop(Object o);

	public abstract String getServerID();

	//change class to abstract and this method to public static boolean class member?
	public abstract boolean isSyncToThreadPlatform();

	public abstract boolean isDecodeURIPlatform();
	public abstract boolean isTransferToOS();

}