/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 *
 * Change activity:
 *
 * Reason           Date     Origin   Description
 * ---------------  -------- -------- ------------------------------------------
 *  82924           13/09/12 Kavitha  Version 1.26 from WASX.SIB copied 
 *  85149           15/10/12 Kavitha  com.ibm.websphere.ras package used ,Tr.register 
 *                                    modified , Tr.service entry removed  
 * ===========================================================================
 */


package com.ibm.ws.sib.utils.ras;

import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.utils.UtConstants;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * This class adds the current thread bus & engine name to the front of each
 * message issued by Jetstream. Note that this class does NOT trace via
 * SibTr. That would dangerous seeing as how it's used by SibTr.....
 */
public class SibMessage extends java.util.ListResourceBundle
{
  /**
   * Use SibStackFinder to traverse through the call stack of function to
   * know who is real caller of SIB Message functions. 
   */
  private static SibStackFinder finder;
  
  /**
   * The Listener interface is the interface that those interested in SibMessage need to
   * implement in order for SibMessage to tell them about the messages being generated
   */
  public static interface Listener
  {
    /** An enum for the type of message being generated by SibMessage */
    public enum MessageType {
       /** Audit messages */
       AUDIT,
       /** Error messages (i.e. those indicating a real problem */
       ERROR,
       /** Fatal messages (i.e. those that will result in the death of the server */
       FATAL,
       /** Informational messages (i.e. those indicating no problem, but a possibly interesting event */
       INFO,
       /** Service messages */
       SERVICE,
       /** Warning messages (i.e. those that MAY indicate a problem */
       WARNING };

    /* -------------------------------------------------------------------------- */
    /* message method
    /* -------------------------------------------------------------------------- */
    /**
     * The method called to indicate that a message is being generated by SibMessage
     *
     * @param type   The type of the event
     * @param me     The messaging engine (if any) generating the message
     * @param tc     The trace component generating the message
     * @param msgKey The message key of the message being generated
     * @param objs   The inserts of the message being generated
     * @param formattedMessage An object array holding the formatted message ready for Tr
     */
    public void message(MessageType type, String me, TraceComponent tc, String msgKey, Object objs, Object[] formattedMessage);
  }

  private static class TrListener implements Listener
  {
    /* -------------------------------------------------------------------------- */
    /* message method
    /* -------------------------------------------------------------------------- */
    /**
     * The method called to indicate that a message is being generated by SibMessage
     *
     * @param type   The type of the event
     * @param me     The messaging engine (if any) generating the message
     * @param tc     The trace component generating the message
     * @param msgKey The message key of the message being generated
     * @param objs   The inserts of the message being generated
     * @param formattedMessage A formatted version of the message being generated
     */
    public void message(MessageType type, String me, TraceComponent tc, String msgKey, Object objs, Object[] formattedMessage)
    {
      switch(type)
      {
        case AUDIT  : if (TraceComponent.isAnyTracingEnabled() && myTc.isAuditEnabled()) Tr.audit(myTc, SIB_MESSAGE, formattedMessage);
                      break;
        case ERROR  : Tr.error(myTc, SIB_MESSAGE, formattedMessage);
                      break;
        case FATAL  : Tr.fatal(myTc, SIB_MESSAGE, formattedMessage);
                      break;
        case INFO   : Tr.info(myTc, SIB_MESSAGE, formattedMessage);
                      break; 
        case WARNING: Tr.warning(myTc, SIB_MESSAGE, formattedMessage);
                      break;
      }
    }
  }

   private static final TraceComponent myTc = Tr.register(SibMessage.class, "", "com.ibm.ws.sib.utils.ras.SibMessage");
   private static final String SIB_MESSAGE = "SIB_MESSAGE";

   private final static Object[][] resources = {
      { SIB_MESSAGE, "{0} {1}" }
   };

   /** The proposed list of listeners on SibMessage */
   private static List<Listener> proposedList;
   /** The list of listeners on SibMessage */
   private static volatile List<Listener> listeners = null;
   static
   {
     proposedList = new ArrayList<Listener>();
     proposedList.add(new TrListener());
     listeners = new ArrayList<Listener>(proposedList);
   }

   // Do not reference the SibTr.Suppressor class UNTIL AFTER all the other fields have been initialized
   private static final TraceComponent TRACE_COMPONENT_FOR_CWSIU_MESSAGE_BUNDLE = Tr.register(SibTr.Suppressor.class, "", UtConstants.MSG_BUNDLE);

   /* -------------------------------------------------------------------------- */
   /* addListener method
   /* -------------------------------------------------------------------------- */
   /**
    * Add a new listener to SibMessage.
    *
    * @param l The new listener to be added to SibMessage.
    */
   /* This method is synchronized to ensure that proposedList is updated and used safely */
   public static synchronized void addListener(Listener l)
   {
     proposedList.add(l);
     listeners = new ArrayList<Listener>(proposedList); // Semantics of volatile means that viewers will see a consistent list
   }

   /* -------------------------------------------------------------------------- */
   /* removeListener method
   /* -------------------------------------------------------------------------- */
   /**
    * Remove a new listener to SibMessage.
    *
    * @param l The listener to be removed to SibMessage.
    */
   /* This method is synchronized to ensure that proposedList is updated and used safely */
   public static synchronized void removeListener(Listener l)
   {
     proposedList.remove(l);
     listeners = new ArrayList<Listener>(proposedList); // Semantics of volatile means that viewers will see a consistent list
   }

   /* -------------------------------------------------------------------------- */
   /* getContents method
   /* -------------------------------------------------------------------------- */
   /**
    * Return the list of objects contained in this Resource Bundle
    *
    * @see java.util.ListResourceBundle#getContents()
    * @return the list of objects
    */
   public Object[][] getContents() {
       return resources;
   }

   private static Object[] format (String me, TraceComponent tc, String msgKey, Object objs) {

     String resourceBundle = tc.getResourceBundleName();
     TraceNLS nls = TraceNLS.getTraceNLS(resourceBundle);

     Object[] objs1 = new Object[] {null};

     if (objs != null) {

       if (objs.getClass().isArray()) objs1 = (Object[])objs;
       else                           objs1 = new Object[] {objs};

     }

     // Get the formatted version of the original message from WAS TraceNLS. 
     String formattedMsg = nls.getFormattedMessage(msgKey, objs1, null);
     
     if (formattedMsg.equalsIgnoreCase(msgKey)) {
        //Some problem. Most probably TraceNLS could not load Resource bundle. 
        // Try to load the resource bundle using SibStackFinder.
        return getFormattedMessage(me,resourceBundle,msgKey, objs1,tc);
     }

     return new Object[]{me, formattedMsg};
   }
   
   private static Object[] getFormattedMessage(String me,String resourceBundle,String msgKey,Object[] objs,TraceComponent tc) {
       /** As TraceNLS functions could not load all SIB resource files, Writing
       * our own function. SIBStackFinder class is used
       * to find the original caller and caller's classloader is used to load
       * the resource files. 
       */
      ResourceBundle bundle = getResourceBundle(resourceBundle,Locale.getDefault(),tc);
      
      // If there's no bundle, just return the key
      if (bundle == null) return new Object[] {me, msgKey};
      
      String message = null;
      try {
        message = bundle.getString(msgKey);
      } catch(Exception e) {
        // No FFDC Code needed - catching all exceptions.
      }
      
      // If there's no message, just return the key
      if ((message == null) || (message.equals(""))) return new Object[] {me, msgKey};
      
      // If there are no inserts, just return the message
      if (objs == null) return new Object[] {me, message};
      
      String formattedMsg;
      try
      {
        formattedMsg = MessageFormat.format(message,objs);
      }
      catch(IllegalArgumentException e)
      {
        // No FFDC Code needed - tolerate the failure to insert by just using the message
        formattedMsg = message;
      }
      
      return new Object[]{me, formattedMsg};
   }
   
   
    private static ResourceBundle getResourceBundle(String name, Locale locale, TraceComponent tc)
    {
      ResourceBundle rb = null;
      
      if (finder == null)
        finder = SibStackFinder.getInstance();
      
      final Class<?> aClass = finder.getCaller();
      
      if (aClass != null)
      {
        ClassLoader cl = null;
        try
        {
          cl = (ClassLoader) AccessController.doPrivileged(new PrivilegedExceptionAction<ClassLoader>()
              {
                public ClassLoader run() throws Exception
                {
                  return aClass.getClassLoader();
                }
              });
        }
        catch(PrivilegedActionException e)
        {
          Tr.event(tc, "Unable to get context class loader: " + e.getMessage());
        }
        try
        {
          rb = ResourceBundle.getBundle(name,locale,cl);
        }
        catch(RuntimeException re)
        {
          Tr.event(tc, "Unable to load {0} from {1} (from class {2}) in {3}; caught exception: {4}", new Object[] { name, cl, aClass, locale.toString(), re });
        }
      }
      else
      {
        Tr.event(tc, "Unable to load {0} class was null", new Object[] { name});
      }
      return rb;
    }

   // Entry points from SibTr

   static void audit (String me, TraceComponent tc, String msgKey, Object objs) {
     Object[] formattedMessage = format(me, tc, msgKey, objs);
     for(Listener l : listeners)
       l.message(Listener.MessageType.AUDIT, me, tc, msgKey, objs, formattedMessage);
   }

   static void error (String me, TraceComponent tc, String msgKey, Object objs) {
     Object[] formattedMessage = format(me, tc, msgKey, objs);
     for(Listener l : listeners)
       l.message(Listener.MessageType.ERROR, me, tc, msgKey, objs, formattedMessage);
   }

   static void fatal (String me, TraceComponent tc, String msgKey, Object objs) {
     Object[] formattedMessage = format(me, tc, msgKey, objs);
     for(Listener l : listeners)
       l.message(Listener.MessageType.FATAL, me, tc, msgKey, objs, formattedMessage);
   }

   static void info (String me, TraceComponent tc, String msgKey, Object objs) {
     Object[] formattedMessage = format(me, tc, msgKey, objs);
     for(Listener l : listeners)
       l.message(Listener.MessageType.INFO, me, tc, msgKey, objs, formattedMessage);
   }

   static void service (String me, TraceComponent tc, String msgKey, Object objs) {
     Object[] formattedMessage = format(me, tc, msgKey, objs);
     for(Listener l : listeners)
       l.message(Listener.MessageType.SERVICE, me, tc, msgKey, objs, formattedMessage);
   }

   static void warning (String me, TraceComponent tc, String msgKey, Object objs) {
     Object[] formattedMessage = format(me, tc, msgKey, objs);
     for(Listener l : listeners)
       l.message(Listener.MessageType.WARNING, me, tc, msgKey, objs, formattedMessage);
   }

   static void SuppressableError(SibTr.Suppressor s,String me, TraceComponent tc, String msgKey, Object objs) {
     suppressableOperation(s, me, tc, msgKey, objs, Listener.MessageType.ERROR);
   }

   static void SuppressableInfo(SibTr.Suppressor s,String me, TraceComponent tc, String msgKey, Object objs) {
     suppressableOperation(s, me, tc, msgKey, objs, Listener.MessageType.INFO);
   }

   static void SuppressableWarning(SibTr.Suppressor s,String me, TraceComponent tc, String msgKey, Object objs) {
     suppressableOperation(s, me, tc, msgKey, objs, Listener.MessageType.WARNING);
   }

   private static void suppressableOperation(SibTr.Suppressor s, String me, TraceComponent tc, String msgKey, Object objs, Listener.MessageType messageType) {
     Object[] sibMessageInserts = format(me, tc, msgKey, objs);
     String   actualMessage = (String)sibMessageInserts[1];
     String   messageNumber;

     if (actualMessage != null && actualMessage.length() > 10)
     {
       messageNumber = actualMessage.substring(0, 10);

       // In some cases (when unit testing), the message is not resolved from the bundle and the message number is
       // "Can't find". In these cases use the message key as the number, so we have
       // something useful to look up!
       if (messageNumber.equals("Can't find"))
         messageNumber = msgKey;
     }
     else
     {
       // We don't have a message from the message bundle, so we'll have to use the message key */
       messageNumber = msgKey;
     }

     if (s == null)
     {
       // No suppressor, so always emit the message
       emitOperation(me, tc, msgKey, objs, messageType, sibMessageInserts);
     }
     else
     {
       SibTr.Suppressor.Decision decision = s.suppress(messageNumber,actualMessage);

       decision.emitSuppressedMessagesMessageIfNecessary(TRACE_COMPONENT_FOR_CWSIU_MESSAGE_BUNDLE);

       if (decision.isSuppressThisMessage())
       {
         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) Tr.debug(tc, "Suppressed "+messageType+": "+actualMessage);
       }
       else
       {
         emitOperation(me, tc, msgKey, objs, messageType, sibMessageInserts);
       }

       decision.emitSuppressingFollowingMessagesMessageIfNecessary(TRACE_COMPONENT_FOR_CWSIU_MESSAGE_BUNDLE);
     }
   }

  /* -------------------------------------------------------------------------- */
  /* emitOperation method
  /* -------------------------------------------------------------------------- */
  /**
   * @param me                 The ME on which to emit this message
   * @param tc                 The trace component for this message
   * @param msgKey             The message key for the message
   * @param objs               The objects for this operation
   * @param messageType        The type of operation
   * @param sibMessageInserts  The message inserts
   */
  private static void emitOperation(String me, TraceComponent tc, String msgKey, Object objs, Listener.MessageType messageType, Object[] sibMessageInserts)
  {
    // Tell the interested parties about this non-suppressed message
     for(Listener l : listeners)
       l.message(messageType, me, tc, msgKey, objs, sibMessageInserts);
  }
}
