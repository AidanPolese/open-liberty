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
package componenttest.custom.junit.runner;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;

/**
 * 
 *Junit seems to only apply the last filter, so we have this compound filter
 *to allow us to use multiple filters.
 * 
 *If any of the filters returns false then the test is removed from the run list.
 * 
 */
public class CompoundFilter extends Filter {

    private final Filter[] filters;

    CompoundFilter(Filter[] filters) {
        this.filters = filters;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.junit.runner.manipulation.Filter#describe()
     */
    @Override
    public String describe() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.junit.runner.manipulation.Filter#shouldRun(org.junit.runner.Description)
     */
    @Override
    public boolean shouldRun(Description arg0) {
        boolean shouldRun = true;
        for (Filter f : filters) {
            shouldRun &= f.shouldRun(arg0);
        }
        return shouldRun;
    }

}
