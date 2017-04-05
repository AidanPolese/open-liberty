package com.ibm.ws.objectManager;

import com.ibm.ws.objectManager.utils.Trace;
import com.ibm.ws.objectManager.utils.Tracing;
import com.ibm.ws.objectManager.utils.UtilsException;

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
 *
 * Change activity:
 *
 *  Reason           Date    Origin     Description
 * --------------- -------- -------- ------------------------------------------
 *  251161         07/04/05 gareth    Add ObjectManager code to CMVC
 *  343689         04/04/06 gareth    Modify trace output cont.
 *  607710         21/08/09 gareth    Add isAnyTracingEnabled() check around trace
 * ============================================================================
 */

/**
 * A subclass of ObjectManagerException is thrown whenever an ObjectManager error occurs.
 */
public abstract class ObjectManagerException
                extends UtilsException
{
    private static final Class cclass = ObjectManagerException.class;
    private static Trace trace = ObjectManager.traceFactory.getTrace(cclass,
                                                                     ObjectManagerConstants.MSG_GROUP_EXCEPTIONS);

    /**
     * Construct a new objectManagerException object.
     * 
     * @param source in which the exception is raised, or Class if static.
     * @param exceptionClass of the ObjectManagerException subclass.
     */
    protected ObjectManagerException(Object source,
                                     Class exceptionClass) {
        super(ObjectManager.nls.format(exceptionClass.getName().substring(exceptionClass.getName().lastIndexOf(".") + 1) + "_info"));

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled()) {
            trace.entry(this,
                        cclass,
                        "<init>",
                        new Object[] { source,
                                      exceptionClass });
            trace.exit(this, cclass, "<init>");
        } // if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled()).
    } // ObjectManagerException().

    /**
     * Construct a new objectManagerException object.
     * 
     * @param source Object in which the exception is raised, or Class if static.
     * @param exceptionClass of the ObjectManagerException subclass.
     * @param insert to be insterted into the message.
     */
    protected ObjectManagerException(Object source,
                                     Class exceptionClass,
                                     Object insert) {
        super(ObjectManager.nls.format(exceptionClass.getName().substring(exceptionClass.getName().lastIndexOf(".") + 1) + "_info", insert));

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled()) {
            trace.entry(this,
                        cclass,
                        "<init>",
                        new Object[] { source,
                                      exceptionClass,
                                      insert });
            trace.exit(this, cclass, "<init>");
        } // if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled()).   
    } // ObjectManagerException().

    /**
     * Construct a new objectManagerException object.
     * 
     * @param source in which the exception is raised, or Class if static.
     * @param exceptionClass of the ObjectManagerException subclass.
     * @param inserts to be insterted into the message.
     */
    protected ObjectManagerException(Object source,
                                     Class exceptionClass,
                                     Object[] inserts) {
        super(ObjectManager.nls.format(exceptionClass.getName().substring(exceptionClass.getName().lastIndexOf(".") + 1) + "_info", inserts));

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled()) {
            trace.entry(this, cclass, "<init>"
                        , new Object[] { source, exceptionClass, inserts });
            trace.exit(this, cclass, "<init>");
        } // if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled()).
    } // ObjectManagerException().

    /**
     * Construct a new objectManagerException object for a linked exception.
     * 
     * @param source in which the exception is raised, or Class if static.
     * @param exceptionClass of the ObjectManagerException subclass.
     * @param throwable linked to this exception.
     */
    protected ObjectManagerException(Object source,
                                     Class exceptionClass,
                                     Throwable throwable) {
        super(ObjectManager.nls.format(exceptionClass.getName().substring(exceptionClass.getName().lastIndexOf(".") + 1) + "_info")
              , throwable);

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled()) {
            trace.entry(this, cclass, "<init>"
                        , new Object[] { source, exceptionClass, throwable });
            trace.exit(this, cclass, "<init>");
        } // if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled()).
    } // ObjectManagerException().

    /**
     * Construct a new objectManagerException object for a linked exception.
     * 
     * @param source in which the exception is raised, or Class if static.
     * @param exceptionClass of the ObjectManagerException subclass.
     * @param throwable linked to this exception.
     * @param insert to be insterted into the message.
     */
    protected ObjectManagerException(Object source,
                                     Class exceptionClass,
                                     Throwable throwable,
                                     Object insert) {
        super(ObjectManager.nls.format(exceptionClass.getName().substring(exceptionClass.getName().lastIndexOf(".") + 1) + "_info", insert)
              , throwable);

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled()) {
            trace.entry(this, cclass, "<init>"
                        , new Object[] { source, exceptionClass, throwable, insert });
            trace.exit(this, cclass, "<init>");
        } // if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
    } // ObjectManagerException().

    /**
     * Construct a new objectManagerException object for a linked exception.
     * 
     * @param source in which the exception is raised, or Class if static.
     * @param exceptionClass of the ObjectManagerException subclass.
     * @param throwable linked to this exception.
     * @param inserts to be insterted into the message.
     */
    protected ObjectManagerException(Object source,
                                     Class exceptionClass,
                                     Throwable throwable,
                                     Object[] inserts) {
        super(ObjectManager.nls.format(exceptionClass.getName().substring(exceptionClass.getName().lastIndexOf(".") + 1) + "_info", inserts)
              , throwable);

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled()) {
            trace.entry(this, cclass, "<init>"
                        , new Object[] { source, exceptionClass, throwable, inserts });
            trace.exit(this, cclass, "<init>");
        } // if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled()).  
    } // ObjectManagerException().

} // class ObjectManagerException.
