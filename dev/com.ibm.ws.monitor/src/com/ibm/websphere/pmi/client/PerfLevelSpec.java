// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
/** 
 * History:
 * CMVC 86523: create the file - wenjian, 09/18/2000
 * 
 * The class is the descriptor for setting/getting instrumentation level of 
 * Pmi modules.
 *  
 */

package com.ibm.websphere.pmi.client;

/**
 * @deprecated As of 6.0, PMI Client API is replaced with
 *             JMX interface and MBean StatisticsProvider model.
 *             PMI CpdCollection data structure is replaced by J2EE
 *             Performance Data Framework defined in
 *             <code>javax.management.j2ee.statistics</code> package.
 * 
 *             <p>
 *             The <code> PerfLevelSpec </code> is WebSphere 4.0 interface used to represent the PMI module
 *             instrumentation level. This interface is replaced by <code>com.ibm.websphere.pmi.stat.StatLevelSpec</code>.
 * 
 * @ibm-api
 */
public interface PerfLevelSpec extends java.io.Serializable {

    /**
     * Get the path of the PerfLevelSpec.
     * It has preleading root "pmi".
     */
    public String[] getPath();

    /**
     * Get the path without root "pmi"
     * It should look like module.instance....
     */
    public String[] getShortPath();

    /**
     * Returns 0 if same
     */
    public int comparePath(PerfLevelSpec otherDesc);

    /**
     * Returns 0 if same
     */
    public int comparePath(String[] otherPath);

    /**
     * Returns true if it's path is a subpath of otherDesc
     */
    public boolean isSubPath(PerfLevelSpec otherDesc);

    public boolean isSubPath(String[] otherPath);

    /**
     * Get module name in the path
     */
    public String getModuleName();

    /**
     * Get submodule name in the path
     */
    public String getSubmoduleName();

    /**
     * Get instrumentation level for the path
     */
    public int getLevel();

    /**
     * Set instrumentation level for the path
     */
    public void setLevel(int level);
}
