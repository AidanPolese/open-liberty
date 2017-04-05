package com.ibm.ws.objectManager.utils;

/*
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2013
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 *
 * Change activity:
 *
 *  Reason           Date    Origin     Description
 * --------------- -------- -------- ------------------------------------------
 *  607710         21/08/09 gareth    Add isAnyTracingEnabled() check around trace
 * ============================================================================
 */

/**
 * @author Andrew_Banks
 * 
 *         Produce First Failure Data Capture.
 *         This should be subclassed and loaded from com.ibm.ws.objectManager.utils.impl.FFDC.
 *         The output is sent to printWriter, unless subClassed.
 * 
 */
public class FFDC {
    static final Class cclass = FFDC.class;
    static Trace trace = Utils.traceFactory.getTrace(cclass,
                                                     UtilsConstants.MSG_GROUP_UTILS);

    public java.io.PrintWriter printWriter = null;

    // public java.io.PrintWriter printWriter = new java.io.PrintWriter(System.out,true);

    /**
     * Create a platform specific FFDC instance.
     * 
     * @return FFDC instance loaded.
     */
    public static FFDC getFFDC() {
        return (FFDC) Utils.getImpl("com.ibm.ws.objectManager.utils.FFDCImpl",
                                    new Class[0],
                                    new Object[0]);
    } // getFFDC().

    /**
     * Process an exception for a static class.
     * 
     * @param sourceClass of the source object.
     * @param methodName where the FFDC is being requested from.
     * @param throwable the cause of the exception.
     * @param probe identifying the source of the exception.
     */
    public void processException(Class sourceClass,
                                 String methodName,
                                 Throwable throwable,
                                 String probe) {
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.entry(cclass,
                        "processException",
                        new Object[] { sourceClass,
                                      methodName,
                                      throwable,
                                      probe });
        print(null,
              sourceClass,
              methodName,
              throwable,
              probe,
              null,
              null);

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.exit(cclass,
                       "processException");

    } // processException().               

    /**
     * Process an exception for a static class.
     * 
     * @param sourceClass of the source object.
     * @param methodName where the FFDC is being requested from.
     * @param throwable the cause of the exception.
     * @param probe identifying the source of the exception.
     * @param objects to be dumped in addition to the source.
     */
    public void processException(Class sourceClass,
                                 String methodName,
                                 Throwable throwable,
                                 String probe,
                                 Object[] objects) {
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.entry(cclass,
                        "processException",
                        new Object[] { sourceClass,
                                      methodName,
                                      throwable,
                                      probe,
                                      objects });

        print(null,
              sourceClass,
              methodName,
              throwable,
              probe,
              null,
              objects);

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.exit(cclass,
                       "processException");

    } // processException().           

    /**
     * Process an exception for a non static class.
     * 
     * @param source object requesting the FFDC.
     * @param sourceClass of the source object.
     * @param methodName where the FFDC is being requested from.
     * @param throwable the cause of the exception.
     * @param probe identifying the source of the exception.
     */
    public void processException(Object source,
                                 Class sourceClass,
                                 String methodName,
                                 Throwable throwable,
                                 String probe) {
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.entry(cclass,
                        "processException",
                        new Object[] { source,
                                      sourceClass,
                                      methodName,
                                      throwable,
                                      probe });
        print(source,
              sourceClass,
              methodName,
              throwable,
              probe,
              null,
              null);

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.exit(cclass,
                       "processException");
    } // processException().

    /**
     * Process an exception for a non static class.
     * 
     * @param source object requesting the FFDC.
     * @param sourceClass of the source object.
     * @param methodName where the FFDC is being requested from.
     * @param throwable the cause of the exception.
     * @param probe identifying the source of the exception.
     * @param objects to be dumped in addition to the source.
     */
    public void processException(Object source,
                                 Class sourceClass,
                                 String methodName,
                                 Throwable throwable,
                                 String probe,
                                 Object[] objects) {
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.entry(cclass,
                        "processException",
                        new Object[] { source,
                                      sourceClass,
                                      methodName,
                                      throwable,
                                      probe,
                                      objects });
        print(source,
              sourceClass,
              methodName,
              throwable,
              probe,
              null,
              objects);

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.exit(cclass,
                       "processException");
    } // processException().   

    /**
     * Process an exception for a non static class.
     * 
     * @param source object requesting the FFDC.
     * @param sourceClass of the source object.
     * @param methodName where the FFDC is being requested from.
     * @param throwable the cause of the exception.
     * @param probe identifying the source of the exception.
     * @param fileInformation describing the source file.
     * @param objects to be dumped in addition to the source.
     */
    public void processException(Object source,
                                 Class sourceClass,
                                 String methodName,
                                 Throwable throwable,
                                 String probe,
                                 String fileInformation,
                                 Object[] objects) {
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.entry(cclass,
                        "processException",
                        new Object[] { source,
                                      sourceClass,
                                      methodName,
                                      throwable,
                                      probe,
                                      objects });
        print(source,
              sourceClass,
              methodName,
              throwable,
              probe,
              fileInformation,
              objects);

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.exit(cclass,
                       "processException");
    } // processException().   

    /**
     * Print the FFDC to the printWriter if it is not null.
     * 
     * @param source object requesting the FFDC.
     * @param sourceClass of the source object.
     * @param methodName where the FFDC is being requested from.
     * @param throwable the cause of the exception.
     * @param probe identifying the source of the exception.
     * @param fileInformation describing the source file.
     * @param object Object or Object[] or null object(s) to be dumped in addition to the source.
     */
    void print(Object source,
               Class sourceClass,
               String methodName,
               Throwable throwable,
               String probe,
               String fileInformation,
               Object object) {

        // Protect against printWriter being cleared while we are in here.
        java.io.PrintWriter localPrintWriter = printWriter;
        if (localPrintWriter != null) {
            localPrintWriter.println("========================FFDC Start==========================");
            if (source != null)
                localPrintWriter.println("Source=" + source);
            localPrintWriter.println("Source=" + sourceClass + ":" + methodName + " probe=" + probe + "\n throwable=" + throwable);
            localPrintWriter.println("FileInflormation:" + fileInformation);
            print(source,
                  localPrintWriter);

            if (object == null)
                ;
            else if (object instanceof Object[]) {
                Object[] objects = (Object[]) object;
                for (int i = 0; i < objects.length; i++) {
                    localPrintWriter.println("------------------------------------------------------------");
                    print(objects[i],
                          localPrintWriter);
                } // for...  
            } else {
                localPrintWriter.println("------------------------------------------------------------");
                print(object,
                      localPrintWriter);
            }
            localPrintWriter.println();
            throwable.printStackTrace(localPrintWriter);
            localPrintWriter.println("=========================FFDC End===========================");
            localPrintWriter.flush();
        } // if (localPrintWriter != null).
    } // print().  

    /**
     * Dump an Object to a java.io.PrintWriter.
     * 
     * @param object to be dumped to printWriter
     * @param printWriter to dump the Object to.
     */
    void print(Object object,
               java.io.PrintWriter printWriter) {
        if (object instanceof Printable) {
            ((Printable) object).print(printWriter);
        } else {
            printWriter.print(object);
        } // if (object instanceof Printable).
    } // print().

    /**
     * @return java.io.PrintWriter the printWriter.
     */
    public java.io.PrintWriter getPrintWriter() {
        return printWriter;
    }

    /**
     * @param printWriter The printWriter to set.
     */
    public void setPrintWriter(java.io.PrintWriter printWriter) {
        this.printWriter = printWriter;
    }
} // class FFDC.
