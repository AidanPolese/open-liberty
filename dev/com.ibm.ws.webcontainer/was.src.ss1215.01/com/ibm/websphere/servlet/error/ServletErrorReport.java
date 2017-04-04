// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//CHANGE HISTORY
//Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//        313551         10/13/05     ekoonce             modify getExceptionType
//        317164         10/25/05     ekoonce             exception type not being set
//        321589         11/07/05     todkap              infinite loop when finding exception type ServletErrorReport.    WASCC.web.webcontainer
//        327424         11/29/05     ekoonce             exception type not being set correctly
//        PM03788        01/12/10     anupag              provide option for unencoded HTML message
//


package com.ibm.websphere.servlet.error;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;

import com.ibm.ws.webcontainer.servlet.DefaultErrorReporter;


/**
 * @ibm-api
 * 
 * This class allows users to customize their error pages by extending this class
 * and then supplying them to the standard error handling routines. 
 * 
 * Represents an error reported by a servlet.
 * Servlets can report errors to the server 2 ways:
 * 1. servlets can explicitly call HttpServletResponse.sendError
 * 2. servlets can throw uncaught Exceptions.
 */
public class ServletErrorReport extends ServletException
{
    
        /**
         * Comment for <code>serialVersionUID</code>
         */
        private static final long serialVersionUID = 4048795645651465012L;
        /**
     * The request-scoped attribute name that is used to retrieve a servlet error report.<BR>
     * <B>attribute name:</B> ErrorReport
     */
    public static final String ATTRIBUTE_NAME = "ErrorReport";
    private String _servletName;
    private int _statusCode;

    public ServletErrorReport()
    {
        super();
    }
    /**
     * Constructs a new ServletErrorReport with the specified message.
     *
     * @param message Message of exception
     */

    public ServletErrorReport(String message)
    {
        super(message);
    }

    /**
     * Constructs a new ServletErrorReport with the specified message
     * and root cause.
     *
     * @param message Message of exception
     * @param rootCause Exception that caused this exception to be raised
     */

    public ServletErrorReport(String message, Throwable rootCause)
    {
        super(message, rootCause);
    }

    /**
     * Constructs a new ServletErrorReport with the specified message
     * and root cause.
     *
     * @param rootCause Exception that caused this exception to be raised
     */

    public ServletErrorReport(Throwable rootCause)
    {
        super(rootCause);
    }

    /**
     * Returns a detailed message about the error.
     */
    public String getMessage()
    {
        return DefaultErrorReporter.encodeChars(super.getMessage());          // 96236
    }
    // Defect 211450 begin
    /**
     * Returns an unencoded detailed message about the error.
     */
    public String getUnencodedMessage()
    {
       return super.getMessage();
    }

    /**
     * Returns a detailed message about the error in HTML.
     */
    public String getMessageAsHTML()
    {
       return "Error " + getErrorCode() + ": " + getMessage();
    }
    // Defect 211450 end
	
    //PM03788 Start   
    /**
     * Returns a detailed message about the error in HTML.
     */
    public String getUnencodedMessageAsHTML()
    {
   	  return "Error " + getErrorCode() + ": " + getUnencodedMessage();              	   	
    }
    //PM03788 End
	
    
    /**
     * Returns the stack trace as a string.
     */
    public String getStackTraceAsString()
    {
        return getStackTrace(this);
    }


    /**
     * Return the error code associated with this error.
     */
    public int getErrorCode()
    {
        return _statusCode;
    }

    /**
     * Returns the name of the servlet that reported the error.
     */
    public String getTargetServletName()
    {
        return _servletName;
    }

    /**
     * Subclasses can use this method to set the error code.
     */
    public void setErrorCode(int sc)
    {
        _statusCode = sc;
    }

    /**
     * Subclasses can use this method to set the status code.
     */
    protected void setTargetServletName(String servletName)
    {
        _servletName = servletName;
    }

    protected String getStackTrace(Throwable th)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        th.printStackTrace(pw);
        pw.flush();
        return sw.toString();

    }
    
        /**
          * Returns the fully-qualified class name of the exception.
          *
          */
    public String getExceptionType()
    {
        String exceptionType = null;
        Throwable rootCause = this;
        
        do{
            Throwable temp = ((ServletException)rootCause).getRootCause();
            if(temp != null) {
                rootCause = temp;
            }
            else{
                break;
            }
        }while (rootCause instanceof ServletException);

        if(rootCause != null){
            exceptionType = rootCause.getClass().getName();
        }
        else{
            exceptionType = this.getClass().getName();
        }

        return exceptionType;
    }
    
    /**
     * Returns the java.lang.Class of the root cause.
     */
    public Class getExceptionClass()
    {
    	Class exceptionClass = null;
        Throwable rootCause = this;
        
        do{
            Throwable temp = ((ServletException)rootCause).getRootCause();
            if(temp != null) {
                rootCause = temp;
            }
            else{
                break;
            }
        }while (rootCause instanceof ServletException);

        if(rootCause != null){
            exceptionClass = rootCause.getClass();
        }
        else{
            exceptionClass = this.getClass();
        }

        return exceptionClass;
    }
    
    /*
    public static void main (String args[]){
        ServletErrorReport report = new ServletErrorReport (new FileNotFoundException() );
        System.out.println("*********************************************************");
        System.out.println("ServletErrorReport.getExceptionType() --> [" + report.getExceptionType() +"]");
        System.out.println("ServletErrorReport.getExceptionType() --> [" + report.getExceptionType2() +"]");
        ServletErrorReport report2 = new ServletErrorReport (report );
        System.out.println("*********************************************************");
        System.out.println("ServletErrorReport.getExceptionType() --> [" + report2.getExceptionType() +"]");
        System.out.println("ServletErrorReport.getExceptionType() --> [" + report2.getExceptionType2() +"]");
        ServletErrorReport report3 = new ServletErrorReport ();
        System.out.println("*********************************************************");
        System.out.println("ServletErrorReport.getExceptionType() --> [" + report3.getExceptionType() +"]");
        System.out.println("ServletErrorReport.getExceptionType() --> [" + report3.getExceptionType2() +"]");
        
    }
    
    public String getExceptionType2()
    {
        String exceptionType = null;
        //Defect 89871
        ServletException e = this;//(ServletException)getRootCause();
        if (e != null)
        {        // This check is not necessary
            Stack errorStack = new Stack();
            errorStack.push(e);

            while (e != null)
            {
                Throwable th = e.getRootCause();
                if (th instanceof ServletException)
                {
                    e = (ServletException)th;
                } else
                {
                    e = null;
                }

                if (th != null)
                {
                    errorStack.push(th);
                }
            }

            try
            {
                Throwable th = (Throwable)errorStack.pop();
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                th.printStackTrace(pw);
                String stringizedStack = sw.toString();

                int end = -1;
                if ((th.getMessage()) != null)
                {
                    end = stringizedStack.indexOf(":");
                } else
                {
                    end = stringizedStack.indexOf("\n");
                }

                if (end != -1)
                {
                    exceptionType = stringizedStack.substring(0, end);
                    exceptionType = exceptionType.trim();
                }
            } catch (EmptyStackException ex)
            {
              com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(ex, "com.ibm.ws.webcontainer.webapp.WebAppErrorReport.getExceptionType", "186", this);
            }
        }

        return exceptionType;
    }
    */
    
    
    
    
}
