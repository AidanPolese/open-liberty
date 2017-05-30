package componenttest.topology.utils;

public class Java9Helper {

    // TODO: This class should be removed when design issue 240720 is resolved and implemented

    public static final String JAVA_9_ARGS = " --add-modules java.se.ee" +
    // Exports
                                             " --add-exports jdk.management.agent/jdk.internal.agent=ALL-UNNAMED" + // for JMX Agent in LocalConnectorActivator
                                             " --add-exports java.base/jdk.internal.vm=ALL-UNNAMED" + // for JMX VMSupport in LocalConnectorActivator
                                             " --add-exports java.base/sun.security.action=ALL-UNNAMED" + // for com.ibm.crypto.ibmkeycert.jar
                                             " --add-exports java.naming/com.sun.jndi.ldap=ALL-UNNAMED" + // for com.ibm.ws.jndi.internal.WASInitialContextFactoryBuilder
                                             " --add-exports jdk.attach/sun.tools.attach=ALL-UNNAMED" + // for collective controller dump action: com.ibm.ws.kernel.boot.internal.commands.HotSpotJavaDumperImpl$VirtualMachine.remoteDataDump(HotSpotJavaDumperImpl.java:342)
                                             // Reads
                                             " --add-reads java.base=ALL-UNNAMED" +
                                             " --add-reads java.logging=ALL-UNNAMED" +
                                             " --add-reads java.management=ALL-UNNAMED" +
                                             " --add-reads java.naming=ALL-UNNAMED" +
                                             " --add-reads java.rmi=ALL-UNNAMED" +
                                             " --add-reads java.sql=ALL-UNNAMED" +
                                             " --add-reads java.xml=ALL-UNNAMED" +
                                             // Opens
                                             " --add-opens java.base/java.util=ALL-UNNAMED" +
                                             " --add-opens java.base/java.lang=ALL-UNNAMED" + // needed for EJB Container's ClassDefiner.defineClass
                                             " --add-opens java.base/java.util.concurrent=ALL-UNNAMED" +
                                             " --add-opens java.base/java.io=ALL-UNNAMED" +
                                             " --add-opens java.naming/javax.naming.spi=ALL-UNNAMED" + // see defect 241221
                                             " --add-opens java.naming/javax.naming=ALL-UNNAMED" + // for ejbcontainer.ejb2x_fat/S[FL]RemoteTest (yoko setAccessible calls)
                                             " --add-opens java.rmi/java.rmi=ALL-UNNAMED" + // for ejbcontainer.ejb2x_fat/S[FL]RemoteTest (yoko setAccessible calls)
                                             " --add-opens java.sql/java.sql=ALL-UNNAMED" + // for ejbcontainer.ejb2x_fat/S[FL]RemoteTest (yoko setAccessible calls)
                                             " --add-opens java.xml.bind/javax.xml.bind=ALL-UNNAMED" + // for jpa.container_fat to use MOXy and JPARS
                                             " --add-opens java.management/javax.management=ALL-UNNAMED" + // for com.ibm.ws.management.j2ee.client_fat_java7:TestMEJBqueryNames/TestMEJBgetAttribute (yoko setAccessible calls)
                                             " --add-opens java.base/java.lang.reflect=ALL-UNNAMED" + // for com.ibm.ws.transport.iiop_fat:testResolvable/testResolvableThatThrows (yoko setAccessible calls)
                                             " --add-opens java.desktop/java.awt.image=ALL-UNNAMED" + // for com.ibm.ws.transport.iiop_fat:testResolvable/testResolvableThatThrows (yoko setAccessible calls)
                                             " --add-opens java.base/java.security=ALL-UNNAMED"; // for com.ibm.ws.transport.iiop_fat:testResolvable/testResolvableThatThrows (yoko accessible calls)
}
