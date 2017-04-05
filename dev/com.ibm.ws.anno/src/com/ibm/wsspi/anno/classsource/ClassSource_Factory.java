/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corporation 2011, 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.ibm.wsspi.anno.classsource;

import com.ibm.ws.anno.classsource.specification.ClassSource_Specification_Container_EJB;
import com.ibm.ws.anno.classsource.specification.ClassSource_Specification_Container_WAR;
import com.ibm.ws.anno.classsource.specification.ClassSource_Specification_Direct_Bundle;
import com.ibm.ws.anno.classsource.specification.ClassSource_Specification_Direct_EJB;
import com.ibm.ws.anno.classsource.specification.ClassSource_Specification_Direct_WAR;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.anno.util.Util_Factory;
import com.ibm.wsspi.anno.util.Util_InternMap;

public interface ClassSource_Factory {
    String getHashText();

    //

    Util_Factory getUtilFactory();

    //

    ClassSource_Exception newClassSourceException(String message);

    ClassSource_Exception wrapIntoClassSourceException(String callingClassName,
                                                       String callingMethodName,
                                                       String message, Throwable th);

    //

    String getCanonicalName(String classSourceName);

    //

    ClassSource_Aggregate createAggregateClassSource(String name)
                    throws ClassSource_Exception;

    //

    // These are mostly for internal use.  Most class sources are created
    // as children of an aggregate class source, and the root aggregate class
    // source is usually created with it's own intern map.

    ClassSource_Aggregate createAggregateClassSource(Util_InternMap internMap, String name)
                    throws ClassSource_Exception;

    ClassSource_MappedDirectory createDirectoryClassSource(Util_InternMap internMap, String name, String dirPath)
                    throws ClassSource_Exception;

    ClassSource_MappedJar createJarClassSource(Util_InternMap internMap, String name, String jarPath)
                    throws ClassSource_Exception;

    ClassSource_ClassLoader createClassLoaderClassSource(Util_InternMap internMap, String name, ClassLoader classLoader)
                    throws ClassSource_Exception;

    ClassSource_MappedContainer createContainerClassSource(Util_InternMap internMap, String name, Container container)
                    throws ClassSource_Exception;

    ClassSource_MappedSimple createSimpleClassSource(Util_InternMap internMap, String name, ClassSource_MappedSimple.SimpleClassProvider provider)
                    throws ClassSource_Exception;

    //

    ClassSource_Specification_Direct_EJB newEJBSpecification();

    ClassSource_Specification_Direct_Bundle newEBASpecification();

    ClassSource_Specification_Direct_WAR newWARSpecification();

    ClassSource_Specification_Container_EJB newEJBContainerSpecification();

    ClassSource_Specification_Container_WAR newWARContainerSpecification();
}
