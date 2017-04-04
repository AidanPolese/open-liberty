/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2009, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.util;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.ScheduleExpression;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;
import com.ibm.ejs.util.Util;

public abstract class ObjectCopier
{
    private static final TraceComponent tc = Tr.register(ObjectCopier.class, "EJBContainer", "com.ibm.ejs.container.container");

    /**
     * Returns true if this copier uses an ORB that has local copies
     * disabled.
     */
    public abstract boolean isNoLocalCopies();

    /**
     * Make a copy of an object by writing it out to stream and reading it
     * back again. This will make a "deep" copy of the object.
     * 
     * @param obj the object to be copied
     * @return a copy of the object
     * @throws RuntimeException if the copy fails
     */
    protected abstract Serializable copySerializable(Serializable obj);

    /**
     * Make a copy of an object by writing it out to stream and reading it
     * back again. This will make a "deep" copy of the object.
     * 
     * This method is optimized to not copy immutable objects.
     * 
     * @param obj the object to be copied, or null
     * @return a copy of the object
     * @throws RuntimeException if the object cannot be serialized
     */
    public Serializable copy(Serializable obj)
    {
        final boolean isTraceOn = TraceComponent.isAnyTracingEnabled();
        if (isTraceOn && tc.isDebugEnabled())
            Tr.debug(tc, "copy : " + Util.identity(obj));

        // -----------------------------------------------------------------------
        // Optimize copyObject by special casing null, immutable objects,
        // and primitive arrays.  All of these can be handled much more
        // efficiently than performing a 'deep' copy.                    d154342.7
        // -----------------------------------------------------------------------

        if (obj == null)
        {
            return obj;
        }

        Class<?> objType = obj.getClass();

        // if the object is a primitive wrapper class, then return it.
        if ((objType == String.class) ||
            (objType == Integer.class) ||
            (objType == Long.class) ||
            (objType == Boolean.class) ||
            (objType == Byte.class) ||
            (objType == Character.class) ||
            (objType == Float.class) ||
            (objType == Double.class) ||
            (objType == Short.class))
        {
            // Yes, so do nothing...
            return obj;
        }

        Class<?> componentType = objType.getComponentType();

        // If this is an array of primitives take a clone instead of deep copy
        if (componentType != null && componentType.isPrimitive())
        {
            if (componentType == boolean.class)
                return ((boolean[]) obj).clone();
            if (componentType == byte.class)
                return ((byte[]) obj).clone();
            if (componentType == char.class)
                return ((char[]) obj).clone();
            if (componentType == short.class)
                return ((short[]) obj).clone();
            if (componentType == int.class)
                return ((int[]) obj).clone();
            if (componentType == long.class)
                return ((long[]) obj).clone();
            if (componentType == float.class)
                return ((float[]) obj).clone();
            if (componentType == double.class)
                return ((double[]) obj).clone();
        }
        // End d154342.7

        if (isTraceOn && tc.isDebugEnabled())
            Tr.debug(tc, "copy : making a deep copy");

        return copySerializable(obj);
    }

    /**
     * Make a copy of a ScheduleExpression object.
     * 
     * @param schedule the object to be copied
     * @return a copy of the object
     */
    public ScheduleExpression copy(ScheduleExpression schedule)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "copy ScheduleExpression: " + Util.identity(schedule));

        // 'schedule' could be a subclass of ScheduleExpression, so only
        // use the base class constructor if not a subclass.          F743-21028.3
        if (schedule.getClass() == ScheduleExpression.class)
        {
            return copyBase(schedule); // 632906
        }

        return (ScheduleExpression) copySerializable(schedule);
    }

    /**
     * Make a copy of a javax.ejb.ScheduleExpression portion of the object.
     * 
     * We do not want to assume anything about how the methods are implemented
     * in the ScheduleExpression, which can be extended by user code. This
     * method is used in formatting a ScheduleExpression for the findEJBTimers
     * command, so it must be in a common, expected format.
     * 
     * @param schedule the object to be copied
     * @return a copy of the object
     */
    public static ScheduleExpression copyBase(ScheduleExpression schedule) // d632906
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "copy copyBase: " + Util.identity(schedule));

        // 'schedule' could be a subclass of ScheduleExpression.
        // Use only the base class constructor.

        return new ScheduleExpression()
                        .start(schedule.getStart() == null ? null : new Date(schedule.getStart().getTime()))
                        .end(schedule.getEnd() == null ? null : new Date(schedule.getEnd().getTime()))
                        .timezone(schedule.getTimezone())
                        .second(schedule.getSecond())
                        .minute(schedule.getMinute())
                        .hour(schedule.getHour())
                        .dayOfMonth(schedule.getDayOfMonth())
                        .dayOfWeek(schedule.getDayOfWeek())
                        .month(schedule.getMonth())
                        .year(schedule.getYear());

    }
}
