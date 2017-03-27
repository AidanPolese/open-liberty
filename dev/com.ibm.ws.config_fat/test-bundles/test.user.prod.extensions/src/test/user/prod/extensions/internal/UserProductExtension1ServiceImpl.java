/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package test.user.prod.extensions.internal;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

import test.user.prod.extensions.UserProductExtension1;

/**
 * User Product Extension Service.
 */
public class UserProductExtension1ServiceImpl implements UserProductExtension1 {

    Long attribute1;
    String attribute2;

    /**
     * Declarative Services method to deactivate this component.
     * Best practice: this should be a protected method, not public or private
     * 
     * @param context context for this component
     */
    protected void activate(BundleContext bundleContext, Map<String, Object> properties) {
        attribute1 = (Long) properties.get("attribute1");
        attribute2 = (String) properties.get("attribute2");
    }

    /**
     * Declarative Services method to deactivate this component.
     * Best practice: this should be a protected method, not public or private
     * 
     * @param context context for this component
     */
    protected void deactivate(ComponentContext context) {}

    /*
     * (non-Javadoc)
     * 
     * @see test.prod.extensions.Product1#sayHello(java.lang.String)
     */
    @Override
    public String sayHello(String input) {
        return "you.said." + input + ".i.say." + input + ".back";
    }

    /*
     * (non-Javadoc)
     * 
     * @see test.prod.extensions.Product1#getAttribute1()
     */
    @Override
    public Long getAttribute1() {
        return attribute1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see test.prod.extensions.Product1#getAttribute1()
     */
    @Override
    public String getAttribute2() {
        return attribute2;
    }
}
