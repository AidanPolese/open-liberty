// 1.5, 2/10/05
//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
package com.ibm.ws.cache.servlet;


public class UnexternalizablePageException
extends Exception
{
    private static final long serialVersionUID = -3204643255606053569L;
    
    public
    UnexternalizablePageException(String message)
    {
        super(message);
    }
}
