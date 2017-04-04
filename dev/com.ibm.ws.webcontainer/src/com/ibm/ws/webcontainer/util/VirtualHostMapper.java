//IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//

//Code added as part of LIDB 2283-4
//
//CHANGE HISTORY
//Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//        254437                      todkap              virtual host matching should be a case-insensitive process    WAS.utils
//        323294         11/11/05     todkap              allow virtual hosts to use same host/port combinations    WAS.webcontainer
//        328144         12/01/05     todkap              61FVT: app fails to start with mapping clash    WASCC.web.webcontainer
//        PK62387        05/15/08     mmolden             PERFORMANCE OVERHEAD FROM 
//       PK66137        05/20/08     mmolden(jebergma)   FAILED TO LOAD WEBAPP

package com.ibm.ws.webcontainer.util;

import com.ibm.ws.webcontainer.core.RequestMapper;
import com.ibm.wsspi.webcontainer.RequestProcessor;
import com.ibm.wsspi.webcontainer.servlet.IExtendedRequest;

/**
 * @author asisin
 *
 */

public class VirtualHostMapper extends com.ibm.ws.util.VirtualHostMapper implements RequestMapper
{	
	/**
	 * @param vHostKey - the alias:port string
	 * @return RequestProcessor
	 */
	public RequestProcessor map(String vHostKey)
	{
        return (RequestProcessor) super.getMapping(vHostKey.toLowerCase());
	}
	
	public void addMapping(String vHostKey, Object target) {
		super.addMapping(vHostKey.toLowerCase(), target);
	}
	public Object getMapping(String vHostKey) {
		return super.getMapping(vHostKey.toLowerCase());
	}
	public void removeMapping(String vHostKey) {
		super.removeMapping(vHostKey.toLowerCase());
	}
	public Object replaceMapping(String vHostKey, Object target) throws Exception {
		return super.replaceMapping(vHostKey.toLowerCase(), target);
	}
	public boolean exists(String vHostKey) {
		return super.exists(vHostKey.toLowerCase());
	}
    
    public boolean exactMatchExists(String vHostKey) {
        return super.exactMatchExists(vHostKey.toLowerCase());
    }

    //PK65158
    protected Object findExactMatch(String vHostKey){
    	return super.findExactMatch(vHostKey.toLowerCase());
    }
    
    /**
	 * @see com.ibm.ws.core.RequestMapper#map(IWCCRequest)
	 */
	public RequestProcessor map(IExtendedRequest req)
	{
		return null;
	}
}
