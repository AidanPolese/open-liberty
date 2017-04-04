// IBM Confidential OCO Source Material
// Copyright IBM Corp. 2006, 2013
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// d392996   EJB3      20060930 leealber : Initial Release
// d408321   EJB3      20061128 leealber : Safe-guarding substring results exception
// d413031   EJB3      20070104 leealber : add comment on possible rootURL forms
// d416151.3 EJB3      20070306 leealber : Extend-scoped support
// d416151.3.7 EJB3    20070501 leealber : Add isAnyTraceEnabled() test
// d416151.3.3 EJB3    20070506 leealber : Messages/FFDC clean up.
// d440322   EJB3      20070510 leealber : Enable Loose Config
// d456716   EJB3      20070906 tkb      : use correct message prefix CWWJP
// d473432.1 EJB3      20071011 leealber : Fix getArchiveName for p.xml URL not in supported package
// d502635   WAS70     20080306 jckrueg  : Decode persistence.xml URL string
// d534924   WAS70     20080708 tkb      : Reduce WCCM overhead, ask mod for scope
// d536681   WAS70     20080715 tkb      : fix 534924 - ask correct mod for scope
// d537753   WAS70     20080722 jckrueg  : Support application editions for XD
// F743-16027 WAS80    20091029 andymc   : Decoupling DeployedApplication/Module for embeddable
// F743-18776
//           WAS80     20100122 bkail    : Use JPAModuleInfo; move puScope logic to WASJPAModuleInfo
// F743-26137
//           WAS80     20100511 bkail    : Embeddable module names now have extensions
// d654520   WAS80     20100527 bkail    : Revert F743-26137 changes
// F743-29629
//           WAS80     20100621 bkail    : Embeddable module names have extensions (again)
// d668837   WAS70     20100908 jckrueg  : Support OSGI FP module names
// d699472   WAS80     20110328 bkail    : Fix embeddable directory-based modules
// d727932.1 WAS85     20120215 tkb      : refactor schema creation for Liberty
// RTC113511 RWAS90    20131009 bkail    : Move logic to SharedJPAComponentImpl
// --------- --------- -------- --------- -----------------------------------------
package com.ibm.ws.jpa.management;

import static com.ibm.ws.jpa.management.JPAConstants.JPA_RESOURCE_BUNDLE_NAME;
import static com.ibm.ws.jpa.management.JPAConstants.JPA_TRACE_GROUP;

import java.io.InputStream;
import java.net.URL;

import javax.xml.validation.Schema;

import org.xml.sax.SAXException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * Persistence.xml handling class.
 */
public abstract class JPAPXml
{
    private static final TraceComponent tc = Tr.register(JPAPXml.class,
                                                         JPA_TRACE_GROUP,
                                                         JPA_RESOURCE_BUNDLE_NAME);

    private final JPAApplInfo ivApplInfo;

    // Jar name where this persistence.xml is defined.
    private String ivArchiveName;

    // Scope specified for this persistence.xml
    private JPAPuScope ivPuScope;

    // Classloader used for the persistence.xml
    private ClassLoader ivClassLoader;

    // URL of the root where the persistence.xml is defined. See JPA spec 6.2
    // The probable URL forms for the root URL are:
    //    jar:file://.../xxxx.ear/xxxx.jar!/                for ejb jar file
    //    file://.../xxxx.war/WEB-INF/classes/              for web app classes directory in WAR file
    //    jar:file://.../xxxx.war/WEB-INF/lib/xxxx.jar!/    for jars in web app library directory
    //    jar:file://.../xxxx.ear/xxxx.jar!/                for persistence archives in application(EAR) root
    //    jar:file://.../xxxx.ear/somelib/xxxx.jar!/        for persistence archives in application(EAR) library directory
    private URL ivRootUrl;

    /**
     * Constructor that initializes common state.
     * 
     * @param appName name of the application where this persistence.xml was found
     * @param archiveName name of the archive where this persistence.xml was found
     * @param scope scope that applies to all persistence units in the archive
     * @param puRoot root of the persistence.xml; location of META-INF directory
     * @param classloader ClassLoader for the archive
     */
    protected JPAPXml(JPAApplInfo applInfo, String archiveName, JPAPuScope scope, URL puRoot, ClassLoader classloader)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "<init> : " + applInfo.getApplName() + ", " + archiveName + ", " + scope + ", " + puRoot);

        ivApplInfo = applInfo;
        ivArchiveName = archiveName;
        ivPuScope = scope;
        ivClassLoader = classloader;
        ivRootUrl = puRoot;
    }

    JPAApplInfo getApplInfo()
    {
        return ivApplInfo;
    }

    /**
     * Returns the name of the containing archive relative to the root of
     * the application (EAR). <p>
     * 
     * For EJB and Web modules, this will be the name of the module; for
     * library jars, this will include the library directory. When located
     * within a jar in a Web module, this will be the name of the Web module.
     */
    String getArchiveName()
    {
        return ivArchiveName;
    }

    /*
     * Getter for this pu scope.
     */
    JPAPuScope getPuScope()
    {
        return ivPuScope;
    }

    /*
     * Getter for class loader.
     */
    public ClassLoader getClassLoader()
    {
        return ivClassLoader;
    }

    /*
     * Getter for this pu's root URL.
     */
    URL getRootURL()
    {
        return ivRootUrl;
    }

    protected abstract InputStream openStream()
                    throws java.io.IOException;

    /**
     * Creates a new schema for the specified persistence xsd.
     * 
     * @throws SAXException if an error occurs
     */
    // d727932.1
    protected abstract Schema newSchema(String xsdName)
                    throws SAXException;

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append('[').append(ivApplInfo.getApplName());
        sb.append(", ").append(ivArchiveName);
        sb.append(", ").append(ivPuScope);
        sb.append(']');
        return sb.toString();
    }
}
