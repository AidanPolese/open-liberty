// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

package com.ibm.ws.webcontainer.extension;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ibm.ws.webcontainer.webapp.WebApp;
import com.ibm.wsspi.webcontainer.extension.ExtensionProcessor;
import com.ibm.wsspi.webcontainer.metadata.WebComponentMetaData;
import com.ibm.wsspi.webcontainer.servlet.IServletConfig;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;
import com.ibm.wsspi.webcontainer.servlet.IServletWrapper;
import com.ibm.wsspi.webcontainer.servlet.ServletConfigParmMap;


public abstract class WebExtensionProcessor implements ExtensionProcessor
{
	
	protected IServletContext extensionContext;
	
	public WebExtensionProcessor(IServletContext webApp) 
	{
		 this.extensionContext = webApp;
	}
	
	
	public IServletWrapper createServletWrapper(IServletConfig config) throws Exception
	{
		return (((WebApp) extensionContext).getWebExtensionProcessor().createServletWrapper(config));
	}
   

	 /**
	  * Returns the list of patterns (as Strings) conforming with the servlet mappings
	  * as mandated by the servlet spec. The subclasses of this class may override this
	  * method in case they want to supply patterns they want to be associated with.
	  */
    @SuppressWarnings("unchecked")
	 public List getPatternList()
	 {
		 return new ArrayList();
	 }
	
	 /**
	  * A convenience method that creates a ServletConfig object. This also populates
	  * the necessary metaData which enables the Servlet associated with the returned
	  * config to correctly lookup NameSpace entries. It is highly recommended that 
	  * extension processors use this method to create the config objects for the targets
	  * that the processor creates.
	  * 
	  * @param servletName
	  * @return
	  * @throws ServletException
	  */
	public IServletConfig createConfig(String servletName) throws ServletException
	{
		return (((WebApp) extensionContext).getWebExtensionProcessor().createConfig(servletName));
		
	}

    // Begin f269714, LI3477 - ServletConfig creation for Security
    public IServletConfig createConfig(String servletName, ServletConfigParmMap cfgMap) throws ServletException
    {
        return (((WebApp) extensionContext).getWebExtensionProcessor().createConfig(servletName, cfgMap));
        
    }
    // End f269714, LI3477 - ServletConfig creation for Security
	 
	 public boolean isAvailable (String resource){
	 	return true;
	 }

    public String getName(){
    	return "WebExtensionProcessor";
    }
    
    public boolean isInternal(){
    	return false;
    }
    
    @Override
    public WebComponentMetaData getMetaData() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public IServletWrapper getServletWrapper(ServletRequest req,
            ServletResponse resp) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
