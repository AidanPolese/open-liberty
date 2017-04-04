// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.webcontainer;

import com.ibm.wsspi.webcontainer.IPlatformHelper;

public class PlatformHelper implements IPlatformHelper {
	/* (non-Javadoc)
	 * @see com.ibm.ws.webcontainer.IPlatformHelper#securityIdentityPush()
	 */
	public Object securityIdentityPush() {return null;}
	/* (non-Javadoc)
	 * @see com.ibm.ws.webcontainer.IPlatformHelper#securityIdentityPop(java.lang.Object)
	 */
	public void securityIdentityPop(Object o) {}
	/* (non-Javadoc)
	 * @see com.ibm.ws.webcontainer.IPlatformHelper#getServerID()
	 */
	public String getServerID() {return null;}
	/* (non-Javadoc)
	 * @see com.ibm.ws.webcontainer.IPlatformHelper#isSyncToThreadPlatform()
	 */
	public boolean isSyncToThreadPlatform (){
    	return false;
    }
    /* (non-Javadoc)
	 * @see com.ibm.ws.webcontainer.IPlatformHelper#isDecodeURIPlatform()
	 */
    public boolean isDecodeURIPlatform (){
    	return true;
    }
	public boolean isTransferToOS() {
		// TODO Auto-generated method stub
		return false;
	}
}
