// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//

package com.ibm.wsspi.webcontainer.metadata;

import com.ibm.wsspi.webcontainer.webapp.WebAppConfig;

/**
 * 
 * 
 * 
 * The metadata associated a WebModule runtime object.
 * 
 * @ibm-private-in-use
 *
 */
 public interface WebModuleMetaData 
 {
	   /**
	    * Returns if the webmodule associated with this metaData object is 
	    * atleast compliant with the Servlet 2.3 specification
	    * @return
	    */
	   boolean isServlet23OrHigher();
	   
	   /**
	    * Returns the WebAppConfig associated with the webModule that this 
	    * metaData object is attached to.
	    * @return
	    */
	   WebAppConfig getConfiguration();
	   
	   /**
	    * Gets the Module level JSP specific metaData that is associated with 
	    * the module to which this metaData object is attached to.
	    * 
	    * NOTE: This method must only be invoked by JSP Container implementations 
	    * who had earlier pushed the securityMetaData objects into this metaData 
	    * during metadata creation.
	    * @return
	    */
	   BaseJspComponentMetaData getJspComponentMetadata();
	   
	   /**
	    * Call to push the JSP specific metaData into this metaData object
	    * at metaData creation time. This is typically done by JSP container
	    * implementations, only to retrieve it again at the time when the 
	    * component is invoked at runtime.
	    * @param metaData
	    */
	   void setJspComponentMetadata(BaseJspComponentMetaData metaData);
	   
	/**
	 * Returns the securityMetaData object attached to the module associated
	 * with this metaData object
	 * 
	 * NOTE: This method must only be invoked by security providers who had
	 * earlier pushed the securityMetaData objects into this metaData during metadata
	 * creation.
	 * @return
	 */
     Object getSecurityMetaData();
	   
	/**
	 * Call to push the securityMetaData into this metaData object at metaData
	 * creation time. This is typically done by security providers, who push 
	 * security constraint representations for the component associated with 
	 * this metaData object, only to retrieve it again at the time when the 
	 * component is invoked at runtime.
	 * @param metaData
	 */
     void setSecurityMetaData(Object metaData);
     
     /**
      * Call to set the cookieName in use for this web module.  This can be 
      * retrieved by other components which need to use this name for routing.
      */
     void setSessionCookieNameInUse(String cookieName);
     
     /**
      * Call to retrieve the cookieName in use for this web module.  This can be 
      * called by other components which need to use this name for routing.
      */     
     String getSessionCookieNameInUse();
     
     /*
      * Methods to associate a WebCollaboratorComponentMetaData object with a ModuleMetaData
      * object. The WebContainer wlll use the WebCollaboratorComponentMetaData when calling
      * preInvoke on the collaborators,
      */
     public void setCollaboratorComponentMetaData(WebCollaboratorComponentMetaData wcmd);
     	
     public WebCollaboratorComponentMetaData getCollaboratorComponentMetaData();

 }


