// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//  CHANGE HISTORY
//Defect        Date        Modified By         Description
//--------------------------------------------------------------------------------------
//F1179-17167.9 11/03/09    utle          	Add removePrivateAttribute() method

package com.ibm.ws.webcontainer.srt;

import java.util.Enumeration;

public interface IPrivateRequestAttributes
{
    public Object getPrivateAttribute(String name);
    
    @SuppressWarnings("unchecked")
    public Enumeration getPrivateAttributeNames();

    public void setPrivateAttribute(String name, Object value);
    
    public void removePrivateAttribute(String name);
}
