/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2009, 2013 */
/* The source code for this program is not published or otherwise divested           */
/* of its trade secrets, irrespective of what has been deposited with the            */
/* U.S. Copyright Office.                                                            */
/*                                                                                   */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                     */
/*                                                                                   */
/*  DESCRIPTION:                                                                     */
/*                                                                                   */
/*  Change History:                                                                  */
/*                                                                                   */
/*  YY-MM-DD  Developer  Defect    Description                                       */
/*  --------  ---------  ------    -----------                                       */
/*  09-11-09  johawkes   F743-305.1 EJB 3.1                                          */
/*  13-08-10  slaterpa   752004     TRANSUMMARY trace                                */
/* ********************************************************************************* */
package com.ibm.tx.util.logging;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class Tr
{
    private static Tracer t;

    static
    {
        reinitializeTracer();
    }

    private static String getProperty(final String prop) {
        if (System.getSecurityManager() == null)
            return System.getProperty(prop);
        else
            return AccessController.doPrivileged(new PrivilegedAction<String>() {
                @Override
                public String run() {
                    return System.getProperty(prop);
                }
            });
    }

    public static void reinitializeTracer()
    {
        String tracerClass = getProperty("com.ibm.tx.tracer");

        if (tracerClass == null)
        {
            tracerClass = "com.ibm.ws.tx.util.logging.WASTr";
        }

        try
        {
            t = (Tracer)Class.forName(tracerClass).newInstance();
        }
        catch(Exception e)
        {
            try
            {
                t = (Tracer)Class.forName("com.ibm.tx.jta.util.logging.TxTr").newInstance();
            }
            catch(Exception e1)
            {
                t = null;
                e1.printStackTrace();
            }
        }
        
        if (t == null)
        {       
            t = new Tracer()
            {
    
                public void audit(TraceComponent tc, String s) {
                    // TODO Auto-generated method stub
                    
                }
                
                public void audit(TraceComponent tc, String s, Object o) {
                    // TODO Auto-generated method stub
                    
                }
                
                public void audit(TraceComponent tc, String s, Object[] o) {
                    // TODO Auto-generated method stub
                    
                }
    
                public void debug(TraceComponent tc, String s) {
                    // TODO Auto-generated method stub
                    
                }
                
                public void debug(TraceComponent tc, String s, Object o) {
                    // TODO Auto-generated method stub
                    
                }
                
                public void debug(TraceComponent tc, String s, Object[] o) {
                    // TODO Auto-generated method stub
                    
                }
    
                public void entry(TraceComponent tc, String s) {
                    // TODO Auto-generated method stub
                    
                }
                
                public void entry(TraceComponent tc, String s, Object o) {
                    // TODO Auto-generated method stub
                    
                }
                
                public void entry(TraceComponent tc, String s, Object[] o) {
                    // TODO Auto-generated method stub
                    
                }
    
                public void error(TraceComponent tc, String s) {
                    // TODO Auto-generated method stub
                    
                }
                
                public void error(TraceComponent tc, String s, Object o) {
                    // TODO Auto-generated method stub
                    
                }
                
                public void error(TraceComponent tc, String s, Object[] o) {
                    // TODO Auto-generated method stub
                    
                }
    
                public void event(TraceComponent tc, String s) {
                    // TODO Auto-generated method stub
                    
                }
                
                public void event(TraceComponent tc, String s, Object o) {
                    // TODO Auto-generated method stub
                    
                }
                
                public void event(TraceComponent tc, String s, Object[] o) {
                    // TODO Auto-generated method stub
                    
                }
    
                public void exit(TraceComponent tc, String s) {
                    // TODO Auto-generated method stub
                    
                }
                
                public void exit(TraceComponent tc, String s, Object o) {
                    // TODO Auto-generated method stub
                    
                }
                
                public void exit(TraceComponent tc, String s, Object[] o) {
                    // TODO Auto-generated method stub
                    
                }
    
                public void fatal(TraceComponent tc, String s) {
                    // TODO Auto-generated method stub
                    
                }
                
                public void fatal(TraceComponent tc, String s, Object o) {
                    // TODO Auto-generated method stub
                    
                }
                
                public void fatal(TraceComponent tc, String s, Object[] o) {
                    // TODO Auto-generated method stub
                    
                }
    
                public void info(TraceComponent tc, String s) {
                    // TODO Auto-generated method stub
                    
                }
                
                public void info(TraceComponent tc, String s, Object o) {
                    // TODO Auto-generated method stub
                    
                }
                
                public void info(TraceComponent tc, String s, Object[] o) {
                    // TODO Auto-generated method stub
                    
                }
    
                public TraceComponent register(Class cl, String traceGroup, String nlsFile)
                {
                    // TODO Auto-generated method stub
                    return new TraceComponent()
                    {
                        public Object getData() {
                            // TODO Auto-generated method stub
                            return null;
                        }
    
                        public boolean isDebugEnabled() {
                            // TODO Auto-generated method stub
                            return false;
                        }
    
                        public boolean isEntryEnabled() {
                            // TODO Auto-generated method stub
                            return false;
                        }
                        
                        public boolean isEventEnabled() {
                            // TODO Auto-generated method stub
                            return false;
                        }                   
                        
                        public boolean isWarningEnabled() {
                            // TODO Auto-generated method stub
                            return false;
                        }

                        public void setDebugEnabled(boolean enabled)
                        {
                            // TODO Auto-generated method stub

                        }

                        public void setEntryEnabled(boolean enabled)
                        {
                            // TODO Auto-generated method stub

                        }

                        public void setEventEnabled(boolean enabled)
                        {
                            // TODO Auto-generated method stub

                        }

                        public void setWarningEnabled(boolean enabled)
                        {
                            // TODO Auto-generated method stub

                        }                   
                    };
                }
    
                public void warning(TraceComponent tc, String s) {
                    // TODO Auto-generated method stub
                    
                }
                
                public void warning(TraceComponent tc, String s, Object o) {
                    // TODO Auto-generated method stub
                    
                }
                
                public void warning(TraceComponent tc, String s, Object[] o) {
                    // TODO Auto-generated method stub
                    
                }

                public void initTrace()
                {
                    // TODO Auto-generated method stub
                    
                }

                @Override
                public TraceComponent register(String s, String traceGroup, String nlsFile) {
                   return new TraceComponent()
                   {
                       public Object getData() {
                           // TODO Auto-generated method stub
                           return null;
                       }
   
                       public boolean isDebugEnabled() {
                           // TODO Auto-generated method stub
                           return false;
                       }
   
                       public boolean isEntryEnabled() {
                           // TODO Auto-generated method stub
                           return false;
                       }
                       
                       public boolean isEventEnabled() {
                           // TODO Auto-generated method stub
                           return false;
                       }                   
                       
                       public boolean isWarningEnabled() {
                           // TODO Auto-generated method stub
                           return false;
                       }

                       public void setDebugEnabled(boolean enabled)
                       {
                           // TODO Auto-generated method stub

                       }

                       public void setEntryEnabled(boolean enabled)
                       {
                           // TODO Auto-generated method stub

                       }

                       public void setEventEnabled(boolean enabled)
                       {
                           // TODO Auto-generated method stub

                       }

                       public void setWarningEnabled(boolean enabled)
                       {
                           // TODO Auto-generated method stub

                       }                   
                   };
               }
                
            }; 
        }
        
        t.initTrace();
    }

    public static void audit(TraceComponent tc, String s)
    {
        t.audit(tc, s);
    }

    public static void audit(TraceComponent tc, String s, Object o)
    {
        t.audit(tc, s, o);
    }

    public static void debug(TraceComponent tc, String s)
    {
        t.debug(tc, s);
    }

    public static void debug(TraceComponent tc, String s, Object o)
    {
        t.debug(tc, s, o);
    }

    public static void entry(TraceComponent tc, String s)
    {
        t.entry(tc, s);
    }

    public static void entry(TraceComponent tc, String s, Object o)
    {
        t.entry(tc, s, o);
    }

    public static void error(TraceComponent tc, String s)
    {
        t.error(tc, s);
    }

    public static void error(TraceComponent tc, String s, Object o)
    {
        t.error(tc, s, o);
    }

    public static void event(TraceComponent tc, String s)
    {
        t.event(tc, s);
    }

    public static void event(TraceComponent tc, String s, Object o)
    {
        t.event(tc, s, o);
    }

    public static void exit(TraceComponent tc, String s)
    {
        t.exit(tc, s);
    }

    public static void exit(TraceComponent tc, String s, Object o)
    {
        t.exit(tc, s, o);
    }

    public static void fatal(TraceComponent tc, String s)
    {
        t.fatal(tc, s);
    }

    public static void fatal(TraceComponent tc, String s, Object o)
    {
        t.fatal(tc, s, o);
    }

    public static void info(TraceComponent tc, String s)
    {
        t.info(tc, s);
    }

    public static void info(TraceComponent tc, String s, Object o)
    {
        t.info(tc, s, o);
    }

    public static TraceComponent register(Class cl, String traceGroup, String nlsFile)
    {
        return t.register(cl, traceGroup, nlsFile);
    }
    
    public static TraceComponent register(String string, String summaryTraceGroup, String nlsFile) 
    {
        return t.register(string, summaryTraceGroup, nlsFile);
    }

    public static void warning(TraceComponent tc, String s)
    {
        t.warning(tc, s);
    }

    public static void warning(TraceComponent tc, String s, Object o)
    {
        t.warning(tc, s, o);
    }
}
