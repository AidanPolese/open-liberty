// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//CHANGE HISTORY
//Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//        340018         01/20/06     mmolden             java.lang.StringIndexOutOfBoundsException in webcontainer
//        PK35828        04/18/07     pmdinh              Inconsistent behavior for URI with trailing '/' character
//        PK47397        06/21/07     pmdinh              Incorrect pathInfo if multi servlet mappings with similar URI's
//        PK39337        06/28/07     sartoris            ServletPath and PathInfo not returning proper values based on mapping
//        498796         02/29/07     mmolden             70FVT i7 SIP encode http URI                                                                                        
//        PK80340        02/25/09     mmulholl            Set path element correctly for a default servlet mapping.
//        PM06111        02/08/10     mmulholl            Add support for URI Matching with string keys  

//Code added as part of LIDB 2283-4
package com.ibm.wsspi.webcontainer.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.ws.util.ClauseNode;
import com.ibm.ws.webcontainer.webapp.WebAppDispatcherContext;
import com.ibm.wsspi.webcontainer.logging.LoggerFactory;
import com.ibm.wsspi.webcontainer.servlet.IExtendedRequest;
import com.ibm.ws.webcontainer.servlet.ServletWrapper;                  // PK80340
import com.ibm.ws.webcontainer.WebContainer;                            // PK80340
import com.ibm.wsspi.webcontainer.WCCustomProperties;                   // PK80340

public class URIMatcher extends com.ibm.ws.util.URIMatcher
{   
	private static Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.wsspi.webcontainer.util");
	private static final String CLASS_NAME="com.ibm.wsspi.webcontainer.util.URIMatcher";
	
    private static String SLASH_STAR = "/*";
    public URIMatcher()
    {
        super();
    }
    
    public URIMatcher(boolean scalable)
    {
        super (scalable,true);    //PM06111
    }
    
	/**
	 * Method match.
	 * 
	 * Exactly the same logic as the above method, but this method will
	 * populate the request with the servletPath, and pathInfo.
	 * 
	 * @param req
	 * @return RequestProcessor
	 */
	public Object match(IExtendedRequest req)
	{
		ClauseNode currentNode = root;
		ClauseNode prevNode = root;
		ClauseNode starNode = null;
		ClauseNode temp = null;
       
        
		WebAppDispatcherContext dispatchContext = (WebAppDispatcherContext)req.getWebAppDispatcherContext();
		String uri = dispatchContext.getRelativeUri().trim();
		
		//		PK39337 - start
		// set default to true
		dispatchContext.setPossibleSlashStarMapping(true);
		//PK39337 - end
		
		/*DO NOT DELETE THESE COMMENTS. They should be introduced in v7.
						 * Fixes the case where we have two servlets A and B.
						 * A is mapped to /servletA/*
						 * B is mapped to /servletA/servletB
						 * path info for /servletA/servletB/pathinfo returns /pathinfo, should be /servletB/pathinfo*/
		int lastStarLoc = 0;
		
		int prevLoc = 0;
		int jsessionIndex = uri.indexOf(';');
		int duplicateJSessionIndex = -1;
		boolean exact = true;

		int len = uri.length();
		//Begin-498796-FVT i7 SIP encode http URI
        int dot = -1;
        if (jsessionIndex==-1){
        	dot = uri.lastIndexOf('.');		//PK35828
        }
        else {
        	dot = uri.substring(0, jsessionIndex).lastIndexOf('.');		//PK35828
        }
        
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
        	logger.logp(Level.FINE,CLASS_NAME,"match","uri->"+uri);
        	logger.logp(Level.FINE,CLASS_NAME,"match","jsessionIndex->"+jsessionIndex+", index Of Dot For Extension Mapping-->"+dot);
        }
        
        //End-498796-FVT i7 SIP encode http URI
		int hash = 0;
		char c;
		StringBuilder clause = new StringBuilder();    // PM06111
		for ( int i=1; i < len; i++ )
		{
			c = uri.charAt(i);
			if ( c == '/' )
			{
				// match the clause we've constructed thus far
				//
				currentNode = currentNode.traverse(clause.toString());    // PM06111

				if ( currentNode == null )
				{
					exact = false;
					break;
				}
				//begin PK35828
				else if ((i == (len - 1)) && (dot != -1) && (jsessionIndex == -1 || dot < jsessionIndex)){
					if ((currentNode.traverse(starString)) == null){        // PM06111
						exact = false;
						break;
					}
				} //end PK35828
				else
				{
					temp = prevNode.traverse(starString);    // PM06111
					if ( temp != null )
					{
						/*DO NOT DELETE THESE COMMENTS. They should be introduced in v7.
						 * Fixes the case where we have two servlets A and B.
						 * A is mapped to /servletA/*
						 * B is mapped to /servletA/servletB
						 * path info for /servletA/servletB/pathinfo returns /pathinfo, should be /servletB/pathinfo*/
						lastStarLoc = prevLoc;
						starNode = temp;
					}
					prevNode = currentNode;
					prevLoc = i;
				}

                clause.delete(0, clause.length());    // PM06111
			}
			else if ( i == (len - 1) )
			{
				// last character
			    clause.append(c);     // PM06111

//				  prevLoc = i;
				currentNode = currentNode.traverse(clause.toString());    // PM06111

				if ( currentNode == null )
				{
					exact = false;
				}
			}
			else if (c == ';')
			{
		        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
		        	logger.logp(Level.FINE,CLASS_NAME,"match","found a semicolon");
		        	}
			    // the following characters are part of pathInfo
			    currentNode = currentNode.traverse(clause.toString());    // PM06111
			    
			    if (currentNode == null)
			        exact = false;
			    
			    duplicateJSessionIndex = i;
			    break;
			}
			else
			{
            	// PM06111 construct clause as we go
            	clause.append(c);
			}
		}

		Object target = null;
		if ( exact )
		{
			target = currentNode.getTarget();
	        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
	        	logger.logp(Level.FINE,CLASS_NAME,"match","found exact target-->"+target);
	        }
			if ( target != null )
			{
				//Begin 313358, 61FVT:req.getPathInfo() returns incorrect values for spl chars
				String servletPath = (jsessionIndex == -1) ? uri : uri.substring(0, jsessionIndex);
				String pathInfo = (jsessionIndex == -1) ? null : uri.substring(jsessionIndex);
				if (servletPath.endsWith(SLASH_STAR)){
					servletPath = servletPath.substring(0,servletPath.length()-2);
					pathInfo = SLASH_STAR;
				}
				
				//PK39337 - start
				if (prevLoc > 0) {
					dispatchContext.setPossibleSlashStarMapping(false);
				}
				//PK39337 - end
				
				dispatchContext.setPathElements(servletPath,pathInfo);
				//End 313358, 61FVT:req.getPathInfo() returns incorrect values for spl chars
				return target; 
			}

			temp = currentNode.traverse(starString);    // PM06111
			if (temp != null)
			{
				target = temp.getTarget();
				if (target != null)
				{
					//PK39337 - start
					if (prevLoc > 0) {
						dispatchContext.setPossibleSlashStarMapping(false);
					}
					//PK39337 - end
					
					dispatchContext.setPathElements((jsessionIndex == -1) ? uri : uri.substring(0, jsessionIndex), (jsessionIndex == -1) ? null : uri.substring(jsessionIndex));
					return target;
				}
			}
		}

		// check for /* at the parent
		temp = prevNode.traverse(starString);    // PM06111

		if ( temp != null )
		{
	        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
	        	logger.logp(Level.FINE,CLASS_NAME,"match","found star node-->"+temp);
	        	}
			starNode = temp;
			/*DO NOT DELETE THESE COMMENTS. They should be introduced in v7.
			 * Fixes the case where we have two servlets A and B.
			 * A is mapped to /servletA/*
			 * B is mapped to /servletA/servletB
			 * path info for /servletA/servletB/pathinfo returns /pathinfo, should be /servletB/pathinfo */
			lastStarLoc = prevLoc;
		}

		if ( starNode != null && starNode != defaultNode)
		{
			// found a <clauses>/* that matched
			/*DO NOT DELETE THESE COMMENTS. They should be introduced in v7.
			 * Fixes the case where we have two servlets A and B.
			 * A is mapped to /servletA/*
			 * B is mapped to /servletA/servletB
			 * path info for /servletA/servletB/pathinfo returns /pathinfo, should be /servletB/pathinfo*/
			
			//PK39337 - start
	        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
	        	logger.logp(Level.FINE,CLASS_NAME,"match","starNode exists and is not default node");
	        }
			if (prevLoc > 0) {
				dispatchContext.setPossibleSlashStarMapping(false);
			}
			//PK39337 - end
			
			dispatchContext.setPathElements(uri.substring(0, lastStarLoc), uri.substring(lastStarLoc));
			//dispatchContext.setPathElements(uri.substring(0, prevLoc), uri.substring(prevLoc));
			return starNode.getTarget();
		}

		// extension matching
		//
		//int dot = uri.lastIndexOf(".");
		if (dot != -1 && (jsessionIndex==-1 ||dot < jsessionIndex))
		{
	        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
	        	logger.logp(Level.FINE,CLASS_NAME,"match","looking for extension mapping");
	        	}
	        String extensionStr = (jsessionIndex == -1) ? uri.substring(dot+1) : uri.substring(dot + 1, jsessionIndex);
			target = extensions.get(extensionStr);
			if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
	        	logger.logp(Level.FINE,CLASS_NAME,"match","extensionStr-->"+extensionStr+", target-->"+target);
	        	}
			if (target != null)
			{
				//PK39337 - start
				if (prevLoc > 0) {
					dispatchContext.setPossibleSlashStarMapping(false);
				}
				//PK39337 - end

				dispatchContext.setPathElements((jsessionIndex == -1) ? uri : uri.substring(0, jsessionIndex), (jsessionIndex == -1) ? null : uri.substring(jsessionIndex));
				return target;            	
			}
		}

		// hit the defaultNode "/*"
		if (starNode != null)
		{
			//PK39337 - start
			dispatchContext.setPossibleSlashStarMapping(true);
			//PK39337 - end
			
			// PK80340 Start
			Object starTarget = starNode.getTarget();
			
			if (WCCustomProperties.ENABLE_DEFAULT_SERVLET_REQUEST_PATH_ELEMENTS && (starTarget instanceof ServletWrapper) && ((ServletWrapper)starTarget).isDefaultServlet() )
			{
				dispatchContext.setPathElements(uri, null);
			} else {
				dispatchContext.setPathElements("", uri);				
			}
			
			return starTarget;      
			// PK80340 End
		}
		// not found
		return null;
	}
}
