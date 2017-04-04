// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//CHANGE HISTORY
//Defect        Date        Modified By         Description
//--------------------------------------------------------------------------------------
// 353142       03/07/06      todkap            CodeReview: discarding of pooled objects and WCCRequest channel    WASCC.web.webcontainer    

package com.ibm.wsspi.webcontainer;


public interface IPoolable {
    
    public void destroy();

}
