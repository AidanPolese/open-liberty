/************** Begin Copyright - Do not add comments here **************
 *
 *  
 * IBM Confidential OCO Source Material
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2012, 2013
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U. S. Copyright Office.
 * 
 * Change History:
 * 
 * Tag          Person          Defect/Feature      Comments
 * ----------   ------          --------------      --------------------------------------------------
 * 04/01/2013   ankit_jain      95574               StackOverflowError occurs while starting the web applications
 * 07/10/2013   ankit_jain      105090              Mask password for all classes in trace for VMM
 * 08/21/2015   rzunzarr        184098              Simplified Object trace output
 * 09/25/2015   rzunzarr        188399              Trace output includes extended properties
 */
package com.ibm.websphere.security.wim.ras;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.wsspi.security.wim.model.Group;
import com.ibm.wsspi.security.wim.model.PersonAccount;

@Trivial
public class WIMTraceHelper {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");//$NON-NLS-1$

    /**
     * The string used for display password in the trace. For security reason, password can not be printed out in the trace or message.
     */
    public final static String DUMMY_VALUE = "****";

    public final static String JAVA_STRING_CLASS = "java.lang.String";

    //Properties
    static HashSet systemProps = new HashSet();

    /**
     * Method takes the JavaBean object and returns InputStream for tracing purpose
     * 
     */
    public static String trace(Object boundData) {

        StringBuffer strBuffer = new StringBuffer();
        //Calling components should not have to handle any exceptions as this is a tracing Utility
        //Return full String or empty value
        try {
            parseObject(boundData, strBuffer);

        } catch (Exception e) {
            // Let FFDC instrumentation catch this exception
        }
        return strBuffer.toString();
    }

    /**
     * @param boundData
     * @param strBuffer
     */
    private static void parseObject(Object boundData, StringBuffer strBuffer) throws Exception {

        try {
            strBuffer.append(boundData.getClass().getName());
            strBuffer.append("=" + LINE_SEPARATOR);
            strBuffer.append("[");

            BeanInfo beanInfo = Introspector.getBeanInfo(boundData.getClass());

            systemProps.add("setAttributes");
            systemProps.add("class");
            systemProps.add("record");
            systemProps.add("recordName");
            systemProps.add("recordShortDescription");
            systemProps.add("propertyAnnotations");
            systemProps.add("objectAnnotations");

            PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();
            processClass(properties, systemProps, strBuffer, boundData, false);

            // ExtendedProperties
            String className = boundData.getClass().getName();
            if ("com.ibm.wsspi.security.wim.model.PersonAccount".equals(className)) {
                Set<String> propertyNames = ((PersonAccount) boundData).getExtendedPropertyNames();
                processExtendedProperties((PersonAccount) boundData, propertyNames, strBuffer);
            }
            else if ("com.ibm.wsspi.security.wim.model.Group".equals(className)) {
                Set<String> propertyNames = ((Group) boundData).getExtendedPropertyNames();
                processExtendedProperties((Group) boundData, propertyNames, strBuffer);
            }

            strBuffer.append("]"); //$NON-NLS-1$
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @param boundData
     * @param propertyNames
     * @param strBuffer
     */
    private static void processExtendedProperties(Group boundData, Set<String> propertyNames, StringBuffer strBuffer) {
        if (propertyNames == null)
            return;

        for (String propName : propertyNames) {
            if (boundData.get(propName) != null) {
                strBuffer.append(propName);
                strBuffer.append("=");
                strBuffer.append(boundData.get(propName));
                strBuffer.append(LINE_SEPARATOR);
            }
        }
    }

    /**
     * @param boundData
     * @param propertyNames
     * @param strBuffer
     */
    private static void processExtendedProperties(PersonAccount boundData, Set<String> propertyNames, StringBuffer strBuffer) {
        if (propertyNames == null)
            return;

        for (String propName : propertyNames) {
            if (boundData.get(propName) != null) {
                strBuffer.append(propName);
                strBuffer.append("=");
                strBuffer.append(boundData.get(propName));
                strBuffer.append(LINE_SEPARATOR);
            }
        }
    }

    /*
     * This method can be called recursively if the Parent Bean contains
     * members that are complex types
     */
    public static void processClass(PropertyDescriptor[] properties, HashSet systemProps, StringBuffer strBuffer, Object boundData, boolean isContainment) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, IntrospectionException {
        //--use beanintrospector to traverse the bean properties 
        Method readMethod = null;
        Method getterMethod = null;
        Object returnValue = null;
        Object objectInsideArray = null;
        String strCurrentProp = null;
        String className = null;
        boolean isNonJavaComplexType = false;

        for (int i = 0; i < properties.length; i++) {

            strCurrentProp = properties[i].getName();

            // Skip properties superTypes and typeName
            if ("superTypes".equals(strCurrentProp) || "typeName".equalsIgnoreCase(strCurrentProp))
                continue;

            if ("password".equalsIgnoreCase(strCurrentProp)) {
                strBuffer.append(strCurrentProp);
                strBuffer.append("=");
                strBuffer.append(DUMMY_VALUE);
                strBuffer.append(LINE_SEPARATOR);
                continue;
            }

            className = properties[i].getPropertyType().getName();

            isNonJavaComplexType = (!className.contains("java.") && !className.contains("javax."));
            if (isNonJavaComplexType && !isContainment && !isJavaPrimitiveType(className)) {

                //Invoke and get the containing classes values
                Method method = properties[i].getReadMethod();

                Object memberClass = method.invoke(boundData, null);

                if (memberClass != null) {
                    BeanInfo beanInfo = Introspector.getBeanInfo(memberClass.getClass());
                    PropertyDescriptor[] propertiesContained = beanInfo.getPropertyDescriptors();
                    //Print the name of the containing class
                    //Added to print just the simple name and not entire package name
                    // strBuffer.append("\t");
                    strBuffer.append(properties[i].getPropertyType().getSimpleName());
                    strBuffer.append("= {" + LINE_SEPARATOR);

                    //If this does not start with java or javax then its a complex type and hence this needs to be broken down further
                    processClass(propertiesContained, systemProps, strBuffer, memberClass, true);
                    // strBuffer.append("\t");
                    strBuffer.append("}" + LINE_SEPARATOR);
                }

                continue; //So that complex type does not get printed

            }

            if (systemProps.contains(strCurrentProp)) {
                continue;
            }

            readMethod = properties[i].getReadMethod();
            getterMethod = boundData.getClass().getMethod(readMethod.getName(), new Class[] {});
            returnValue = getterMethod.invoke(boundData, new Object[] {});

            if (readMethod.getName().startsWith("isSet") && returnValue instanceof Boolean)
                continue;

            if (returnValue != null) {
                boolean isHashSet = false;
                boolean isHashMap = false;
                boolean isList = false;
                int size = 0;

                if (returnValue instanceof HashSet) {
                    isHashSet = true;
                    size = ((HashSet) returnValue).size();
                } else if (returnValue instanceof HashMap) {
                    isHashMap = true;
                    size = ((HashMap) returnValue).size();
                } else if (returnValue instanceof List) {
                    isList = true;
                    size = ((List) returnValue).size();
                }

                if ((isHashSet || isHashMap || isList) && size == 0) {
                    continue;
                } else {
                    if (isContainment)
                        strBuffer.append("\t");

                    strBuffer.append(strCurrentProp);
                    strBuffer.append("="); //$NON-NLS-1$
                }

                //--if it is simple array
                if (strCurrentProp.equalsIgnoreCase("password")) {
                    strBuffer.append("*****");
                }
                else if (returnValue.getClass().isArray()) {

                    int arrayLength = Array.getLength(returnValue);

                    for (int j = 0; j < arrayLength; j++) {
                        objectInsideArray = Array.get(returnValue, j);
                        strBuffer.append(objectInsideArray);
                        strBuffer.append(", ");
                    }
                    strBuffer.deleteCharAt(strBuffer.length() - 2);

                    //--if it is an arraylist	
                } else if (returnValue instanceof List) {

                    List list = (List) returnValue;

                    boolean isListAllStrings = false;

                    for (int k = 0; k < list.size(); k++) {

                        objectInsideArray = list.get(k);

                        isListAllStrings = true;
                        if (k == 0) {
                            strBuffer.append("{"); //Opening curly braces
                        }

                        if (objectInsideArray instanceof PersonAccount || objectInsideArray instanceof Group) {
                            String printString = objectInsideArray.toString();
                            int startIndex = 0;
                            while (printString.indexOf(LINE_SEPARATOR, startIndex) > 0) {
                                strBuffer.append(printString.substring(startIndex, printString.indexOf(LINE_SEPARATOR, startIndex)));
                                strBuffer.append(LINE_SEPARATOR);
                                strBuffer.append("\t");
                                startIndex = printString.indexOf(LINE_SEPARATOR, startIndex) + 2;
                            }

                            strBuffer.append(printString.substring(startIndex));
                        }
                        else
                            strBuffer.append(objectInsideArray);

                        if (k < (list.size() - 1)) { //Don't add a comma after the last item in the List of Strings
                            strBuffer.append(",");
                        } else {
                            strBuffer.append("}");
                        }
                    }
                } else {
                    if ((isHashMap || isHashSet) && size == 0) {
                        //do nothing as its an empty collection
                    } else {
                        strBuffer.append(returnValue);
                    }
                }

                strBuffer.append(LINE_SEPARATOR);
            }
        }
    }

    /*
     * @param String className - the name of the className
     * Checks if this is a part of the Java primitive data types
     */
    public static boolean isJavaPrimitiveType(String className) {
        boolean flag = false;
        if (className.equalsIgnoreCase("byte") || className.equalsIgnoreCase("short") || className.equalsIgnoreCase("int")
            || className.equalsIgnoreCase("long") || className.equalsIgnoreCase("float") || className.equalsIgnoreCase("double")
            || className.equalsIgnoreCase("char") || className.equalsIgnoreCase("boolean")) {
            flag = true;
        }
        return flag;
    }

    /**
     * Return string equivalent of an object array.
     **/
    public static String printObjectArray(Object[] array) {
        if (array == null)
            return null;
        StringBuffer result = new StringBuffer();

        result.append("[");
        for (int i = 0; i < array.length; i++) {
            Object obj = array[i];
            if (obj != null) {
                if (obj instanceof Object[]) {
                    result.append(printObjectArray((Object[]) obj));
                } else {
                    result.append(obj);
                }
            } else {
                result.append("null");
            }
            if (i != array.length - 1)
                result.append(", ");
        }
        result.append("]");
        return result.toString();
    }

    /**
     * Return a string format of the map. If any of the key is a password type field(contains "password") then
     * the value is masked.
     **/
    public static String printMapWithoutPassword(Map<String, Object> map) {

        if (map == null) {
            return "null";
        }

        StringBuffer sb = new StringBuffer();
        for (Iterator<Entry<String, Object>> itr = map.entrySet().iterator(); itr.hasNext();) {
            Entry<String, Object> entry = itr.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if ((key != null) && (key.toLowerCase().indexOf("password") >= 0)) {
                value = DUMMY_VALUE;
            }
            if (itr.hasNext())
                sb.append("[" + key + "=" + value + "],");
            else
                sb.append("[" + key + "=" + value + "]");
        }
        return sb.toString();
    }
}