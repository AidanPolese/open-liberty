//IBM Confidential OCO Source Material
//   5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//   The source code for this program is not published or otherwise divested
//   of its trade secrets, irrespective of what has been deposited with the
//   U.S. Copyright Office.
//
// Feature LIDB3292-43 "Integrate AMM with webcontainer" 2007/10/12 cjhoward

package com.ibm.ws.jsp.taglib.annotation;

import java.util.EventListener;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.jsp.tagext.JspTag;

import com.ibm.wsspi.webcontainer.facade.ServletContextFacade;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;

public abstract class AnnotationHandler {
     private static HashMap<IServletContext, AnnotationHandler>
          tagAnnotationHandlers = new HashMap<IServletContext,
          AnnotationHandler>();
     
     private IServletContext context;
     
     protected AnnotationHandler () {
     }
     
     public static synchronized AnnotationHandler getInstance
          (ServletContext context) {
          IServletContext servletContext;
          AnnotationHandler tagAnnotationHandler;
          
          // Find the underlying IServletContext for the passed in context
          // first.
          
          if (context instanceof ServletContextFacade) {
               servletContext = ((ServletContextFacade)
                    context).getIServletContext();
          }
          
          else {
               servletContext = (IServletContext) context;
          }
          
          tagAnnotationHandler = AnnotationHandler.tagAnnotationHandlers.get
               (servletContext);
          
          if (tagAnnotationHandler == null) {
               tagAnnotationHandler = AnnotationHandler.createInstance();
               
               tagAnnotationHandler.setServletContext (servletContext);
               
               AnnotationHandler.tagAnnotationHandlers.put (servletContext,
                    tagAnnotationHandler);
          }
          
          return tagAnnotationHandler;
     }
     
     public static synchronized AnnotationHandler removeAnnotationHandler(ServletContext context) {
         AnnotationHandler rc = null;
         if (AnnotationHandler.tagAnnotationHandlers!=null) {
             IServletContext servletContext;
             if (context instanceof ServletContextFacade) {
                 servletContext = ((ServletContextFacade)
                      context).getIServletContext();
             } else {
                 servletContext = (IServletContext) context;
             }
             rc = AnnotationHandler.tagAnnotationHandlers.remove(servletContext);
         }
         return rc;
     }
     
     protected IServletContext getServletContext () {
          return this.context;
     }
     
     protected void setServletContext (IServletContext context) {
          this.context = context;
     }
     
     public abstract void doPostConstructAction (JspTag tag);
     
     public abstract void doPostConstructAction (EventListener listener);
     
     public abstract void doPreDestroyAction (JspTag tag);
     
     private static AnnotationHandler createInstance () {
          String implClassName = System.getProperty
               (AnnotationHandler.class.getName());
          AnnotationHandler instance = null;
          
          try {
               instance = (AnnotationHandler) Class.forName
                    (implClassName).newInstance();
          }
          
          catch (Exception e) {
               instance = new DefaultAnnotationHandler();
          }
          
          return instance;
     }
}
