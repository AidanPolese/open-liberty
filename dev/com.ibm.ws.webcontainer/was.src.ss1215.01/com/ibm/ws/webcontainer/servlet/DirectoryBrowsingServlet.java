// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//
//  CHANGE HISTORY
//Defect        Date       Modified By     Description
//--------------------------------------------------------------------------------------
//PK81387      02/03/09    mmulholl        Don't serve WEB-INF and META-INF dorectories
//

package com.ibm.ws.webcontainer.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.ws.webcontainer.webapp.WebAppRequestDispatcher;
import com.ibm.wsspi.webcontainer.WCCustomProperties;


/**
 * DirectoryBrowsingServlet
 *
 * This servlet implements directory browsing behavior.  It is automatically loaded
 * if directory browsing is enabled for a web module.
 *
 * version 1.0 - 07/24/01
 */
public class DirectoryBrowsingServlet extends HttpServlet
{

    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3258125864872195895L;
	private static TraceNLS nls = TraceNLS.getTraceNLS(DirectoryBrowsingServlet.class, "com.ibm.ws.webcontainer.resources.Messages");
	
    /**
     * Handle the GET Method
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        PrintWriter out;
        String  title = "Index of ";
        String dirName = (String)req.getAttribute("com.ibm.servlet.engine.webapp.dir.browsing.path");
        String reqURI = (String)req.getAttribute("com.ibm.servlet.engine.webapp.dir.browsing.uri");

        // PK81387 Start

        File dir = new File(dirName);

        // get path to war directory
        ServletContext context = getServletConfig().getServletContext();        
        String contextRealPath = context.getRealPath("");
        
        int idx=dirName.lastIndexOf(contextRealPath);
               
        if (idx!=-1) {
        	// subtract the war directory from teh reqiested directory
        	String matchString=dirName.substring(idx+contextRealPath.length());
        	
        	matchString=matchString.replace(File.separator,"/");
        	
        	// Ensure matchString starts with "/" so WSUtil.resolveURI processes leading "."s
        	if (!matchString.startsWith("/")) {
        	   matchString="/"+matchString;
        	}
        	
        	// remove a trailing "/" so tthat uriCaseCheck does a vlaid check.
        	if (matchString.endsWith("/")) {
        		matchString=matchString.substring(0, matchString.length()-1);
        	}
        	     
        	// checkWEB-INF unless esposeWebInfoOnDispatch is set and we are in a dispatched request
        	boolean checkWEBINF = !WCCustomProperties.EXPOSE_WEB_INF_ON_DISPATCH || (req.getAttribute(WebAppRequestDispatcher.DISPATCH_NESTED_ATTR)==null) ;
        	
        	try {
        		
        		if (!com.ibm.wsspi.webcontainer.util.FileSystem.uriCaseCheck(dir, matchString,checkWEBINF))
        		{
                    resp.sendError(404, nls.getString("File.not.found", "File not found"));
                    return;
        		}	
        	} catch (java.lang.IllegalArgumentException exc) {
        		// Must be traversing back directories
                resp.sendError(404, nls.getString("File.not.found", "File not found"));
                return;
        	}
         }
        
        // PK81387 End	
        if (!reqURI.endsWith("/"))
            reqURI += '/';

        title += reqURI;

        // make sure we can access it
        if (!dir.canRead())
            resp.sendError(404, nls.getString("File.not.found", "File not found"));

        // set the content type
		// set the content type as UTF-8 as filenames are encoded in UTF-8
        resp.setContentType("text/html; charset=UTF-8");

        // write the output
        out = resp.getWriter();

        out.println("<HTML><HEAD><TITLE>");
        out.println(title);
        out.println("</TITLE></HEAD><BODY>");
        out.println("<H1 align=\"left\">" + title + "</H1>");
        out.println("<HR size=\"3\"><TABLE cellpadding=\"2\"><TBODY><TR bgcolor=\"#d7ffff\">");

        // output the table headers
        out.println("<TH width=\"250\" nowrap><P align=\"left\">Name</P></TH>");
        out.println("<TH width=\"250\" nowrap><P align=\"left\">Last Modified</P></TH>");
        out.println("<TH width=\"150\" nowrap><P align=\"left\">Size</P></TH>");
        out.println("<TH width=\"300\" nowrap><P align=\"left\">Description</P></TH></TR>");

        // output a row for each file in the directory
        fillTableRows(dir, reqURI, out);

        // finish the page
        out.println("</TBODY></TABLE></BODY></HTML>");

        // close it up
        out.close();
    }

    // Pass the POST request to the GET method
    public void doPost (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        doGet(req,resp);
    }

    private void fillTableRows(File dir, String reqURI, PrintWriter out)
    {
        File[] files = dir.listFiles();
        int fc = 0;
        Date date;

        // for each file, output a table row
        while (fc < files.length)
        {
            if (files[fc].isDirectory())
            {
                // it's a directory...make sure it's not a configuration dir
                if (files[fc].getName().equalsIgnoreCase("META-INF") ||
                    files[fc].getName().equalsIgnoreCase("WEB-INF"))
                {
                    fc++;
                    continue;
                }
                
                // not a config dir...put out a table row
                out.println("<TR><TD nowrap>");

                // create the link
                out.println("<A href=\"" + reqURI + files[fc].getName() + "\">");
                out.println("<B>" + files[fc].getName() + "</B></A></TD>");

                // show last modified date
                date = new Date(files[fc].lastModified());
                out.println("<TD nowrap>" + date.toString() + "</TD>");

                // show the dir size (just a dash) and description
                out.println("<TD nowrap>-</TD><TD nowrap>Directory</TD></TR>");
            }
            else
            {
                // it's a file...make sure it's not a jsp type
                if (files[fc].getName().endsWith(".jsp") ||
                    files[fc].getName().endsWith(".jsv") ||
                    files[fc].getName().endsWith(".jsw"))
                {
                    fc++;
                    continue;
                }

                // not a jsp type...put out a file link
                out.println("<TR><TD nowrap>");

                // create the link
                out.println("<A href=\"" + reqURI + files[fc].getName() + "\">");
                out.println(files[fc].getName() + "</A></TD>");

                // show last modified date
                date = new Date(files[fc].lastModified());
                out.println("<TD nowrap>" + date.toString() + "</TD>");

                // show the file size
                out.println("<TD nowrap>" + files[fc].length() + "</TD>");

                out.println("<TD nowrap>File</TD></TR>");
            }

            // increment file counter for while
            fc++;
        }
    }
}
