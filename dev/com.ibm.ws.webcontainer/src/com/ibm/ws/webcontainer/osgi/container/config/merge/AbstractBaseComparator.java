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
 */
package com.ibm.ws.webcontainer.osgi.container.config.merge;

import java.util.Iterator;
import java.util.List;

import com.ibm.ws.container.service.config.ServletConfigurator.MergeComparator;
import com.ibm.ws.javaee.dd.common.Description;
import com.ibm.ws.javaee.dd.common.Property;

public abstract class AbstractBaseComparator<T> implements MergeComparator<T> {

    protected boolean compareList(List<T> list1, List<T> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        if (list1.size() > 0) {
            Iterator<T> iter1 = list1.iterator();
            for (T t2 : list2) {
                if (iter1.hasNext()) {
                    T t1 = iter1.next();
                    if (!t1.equals(t2)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    
    
    protected boolean compareDescriptions(List<Description> list1, List<Description> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        if (list1.size() > 0) {
            Iterator<Description> o1Iterator = list1.iterator();
            for (Description o2Description : list2) {
                if (o1Iterator.hasNext()) {
                    Description o1Description = o1Iterator.next();
                    if (!o1Description.getValue().equals(o2Description.getValue())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    protected boolean compareProperties(List<Property> list1, List<Property> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        if (list1.size() > 0) {
            Iterator<Property> o1Iterator = list1.iterator();
            for (Property o2Property : list2) {
                if (o1Iterator.hasNext()) {
                    Property o1Property = o1Iterator.next();
                    if (!o1Property.getName().equals(o2Property.getName()) || !o1Property.getValue().equals(o2Property.getValue())) {
                        return false;
                    }
                }
            }/*
              * Map<String, String> o1Properties = new HashMap<String, String>();
              * for (Property property : list1) {
              * o1Properties.put(property.getName(), property.getValue());
              * }
              * for (Property property : list2) {
              * String o1Value = o1Properties.get(property.getName());
              * if (o1Value == null || !o1Value.equals(property.getValue())) {
              * return false;
              * }
              * }
              */
        }
        return true;
    }
}
