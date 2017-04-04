// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.webcontainer;

import com.ibm.ws.container.BaseConfiguration;
import com.ibm.ws.http.Alias;
import com.ibm.ws.http.MimeEntry;
import com.ibm.ws.http.VirtualHost;

public class VirtualHostConfiguration extends BaseConfiguration 
{
	private VirtualHost config;
   
	public VirtualHostConfiguration(String id)
	{
		super (id);
	}
	
	public void setConfig(VirtualHost config)
	{
		this.config = config;
	}
	
	public String getName()
	{
		return config.getName();
	}
	
	public Alias[] getAliases()
	{
		return config.getAliases();
	}
	
	public MimeEntry[] getMimeEntries()
	{
		return config.getMimeEntries();
	}
	
	public String getMimeType(String extension)
	{
		return config.getMimeType(extension);
	}
	
	public String toString()
	{
	    Alias[] aliases = getAliases();
	    StringBuffer buf = new StringBuffer(getName());
	    buf.append('[');
	    for (int i = 0; i < aliases.length; i++)
	    {
	        buf.append(aliases[i].getHostname());
	        buf.append(':');
	        buf.append(aliases[i].getPort());
	        buf.append( (i == (aliases.length - 1)) ? ']' : ',');
	    }
	    
	    return buf.toString();
	}
  
}
