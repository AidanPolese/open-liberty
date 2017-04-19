//IBM Confidential OCO Source Material
//5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
package com.ibm.ws.cache.servlet;

import com.ibm.ws.ffdc.FFDCFilter;
import javax.servlet.http.HttpServletResponse;

public class CharacterEncodingSideEffect
    implements ResponseSideEffect
{

    public String toString()
    {
        StringBuffer sb = new StringBuffer("Character Encoding side effect: \n\t");
        sb.append("Character Encoding: ").append(characterEncoding).append("\n");
        return sb.toString();
    }

    public CharacterEncodingSideEffect(String charEnc)
    {
        characterEncoding = null;
        characterEncoding = charEnc;
    }

    public void performSideEffect(HttpServletResponse response)
    {
        try
        {
            if(response instanceof CacheProxyResponse)
            {
                CacheProxyResponse cpr = (CacheProxyResponse)response;
                if(!cpr._gotWriter && !cpr._gotOutputStream && !cpr.getResponse().isCommitted())
                    cpr.setCharacterEncoding(characterEncoding);
            }
        }
        catch(IllegalStateException ex)
        {
            FFDCFilter.processException(ex, "com.ibm.ws.cache.servlet.ContentTypeSideEffect.performSideEffect", "71", this);
        }
    }

    private static final long serialVersionUID = 0xf8bdef22fd0be42aL;
    private String characterEncoding;
}

