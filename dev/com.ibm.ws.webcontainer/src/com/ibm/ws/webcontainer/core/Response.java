// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//CHANGE HISTORY
//Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//        PK11772        11/29/05      todkap             STOREDRESPONSE CLASS FAILS WITH MESSAGE SRV.8.2: RESPONSEWRAPPE    WAS.webcontainer
//        LIDB4408-1     02/22/06      todkap             LIDB4408-1 web container changes to limit pooling
//        340473         04/11/06      ekoonce            Expose getStatusCode
//        LIDB3518-1.1   06-23-07      mmolden            ARD


package com.ibm.ws.webcontainer.core;

import javax.servlet.ServletException;
import javax.servlet.ServletResponse;

import com.ibm.websphere.servlet.response.IResponse;



public interface Response extends ServletResponse
{
	public void start();
	
	public void finish() throws ServletException;
	
	public void initForNextResponse(IResponse res);
    
    //public int getStatusCode();  //340473
    
    public void destroy();
}
