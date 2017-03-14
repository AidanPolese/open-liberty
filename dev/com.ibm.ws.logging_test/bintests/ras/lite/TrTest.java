/*
 * ============================================================================
 * @start_prolog@
 * Version: @(#) 1.7 SERV1/ws/code/ras.lite/unittest/ras/lite/TrTest.java, WAS.ras.lite, WASX.SERV1, kk0826.07 08/06/19 03:27:11 [6/30/08 15:02:52]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  Copyright IBM Corp. 2007
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 *                 061031 vaughton Prep SERV1 version
 * SIB0119.cli.11  070215 vaughton Change message prefix
 * 455868          070731 vaughton Add isMessageIdConversionEnabled method
 * 484111          071126 vaughton NPE inside toString method
 * 485562          071126 vaughton Initialise new FFDC implementation
 * 530532          080618 djvines  Test Untraceable and Traceable
 * ============================================================================
 */

package ras.lite;

import com.ibm.ejs.ras.*;
import junit.framework.TestCase;

public class TrTest extends TestCase {

    static final String className = TrTest.class.getName();
    static final Object[] objs = new Object[]{"p1","p2","p3","p4"};

    // Trace IN, OUT & DEBUG statements

    public void test1() {
        final TraceComponent tc = Tr.register(className, "", "");
        if (tc.isEntryEnabled()) Tr.entry(tc, "test1 entry");
        if (tc.isDebugEnabled()) Tr.debug(tc, "test1 debug");
        if (tc.isEntryEnabled()) Tr.exit(tc, "test1 exit");
    }

    // Use the ras lite message file as our own and obtain formatted and unformatted messages

    public void test2() {
        final TraceComponent tc = Tr.register(className, "", "");
        final TraceNLS nls = TraceNLS.getTraceNLS("com.ibm.ejs.ras.Messages");
        if (tc.isEntryEnabled()) Tr.entry(tc, "test2 entry");
        String msg = nls.getFormattedMessage("BUILDLEVELS_NOT_SAME_CWSJE0001E",objs,"");
        if (tc.isDebugEnabled()) Tr.debug(tc, msg);
        msg = nls.getString("BUILDLEVELS_NOT_SAME_CWSJE0002E");
        if (tc.isDebugEnabled()) Tr.debug(tc, msg);
        if (tc.isEntryEnabled()) Tr.exit(tc, "test2 exit");
    }

    // Use the non-existent message file and reply on default string values

    public void test3() {
        final TraceComponent tc = Tr.register(className, "", "");
        final TraceNLS nls = TraceNLS.getTraceNLS("com.ibm.ejs.ras.MessagesNotExist");
        if (tc.isEntryEnabled()) Tr.entry(tc, "test3 entry");
        String msg = nls.getFormattedMessage("BUILDLEVELS_NOT_SAME_CWSJE0001E",objs,"This is the default string");
        if (tc.isDebugEnabled()) Tr.debug(tc, msg);
        msg = nls.getString("BUILDLEVELS_NOT_SAME_CWSJE0002E","This is the default string");
        if (tc.isDebugEnabled()) Tr.debug(tc, msg);
        msg = nls.getString("BUILDLEVELS_NOT_SAME_CWSJE0002E");
        if (tc.isDebugEnabled()) Tr.debug(tc, msg);
        if (tc.isEntryEnabled()) Tr.exit(tc, "test3 exit");
    }

    // Use the ras lite message file as our own and obtain non-existent formatted and unformatted messages

    public void test4() {
        final TraceComponent tc = Tr.register(className, "", "");
        final TraceNLS nls = TraceNLS.getTraceNLS("com.ibm.ejs.ras.Messages");
        if (tc.isEntryEnabled()) Tr.entry(tc, "test4 entry");
        String msg = nls.getFormattedMessage("BUILDLEVELS_NOT_SAME_CWSJE0001E_NOTEXIST",objs,"This is the default string");
        if (tc.isDebugEnabled()) Tr.debug(tc, msg);
        msg = nls.getString("BUILDLEVELS_NOT_SAME_CWSJE0002ENOTEXIST","This is the default string");
        if (tc.isDebugEnabled()) Tr.debug(tc, msg);
        msg = nls.getString("BUILDLEVELS_NOT_SAME_CWSJE0002ENOTEXIST");
        if (tc.isDebugEnabled()) Tr.debug(tc, msg);
        if (tc.isEntryEnabled()) Tr.exit(tc, "test4 exit");
    }

    // Test all the register methods

    public void test5() {
      TraceComponent tc = Tr.register(className, "", "");
      if (tc.isEntryEnabled()) Tr.entry(tc, "test5 entry");
      tc = Tr.register(Object.class);
      if (tc.isDebugEnabled()) Tr.debug(tc, "tc is for Object.class");
      tc = Tr.register(Object.class.getName());
      if (tc.isDebugEnabled()) Tr.debug(tc, "tc is for Object.class name");
      tc = Tr.register(Object.class,"group");
      if (tc.isDebugEnabled()) Tr.debug(tc, "tc is for Object.class + group name");
      tc = Tr.register(Object.class.getName(),"group");
      if (tc.isDebugEnabled()) Tr.debug(tc, "tc is for Object.class name + group name");
      tc = Tr.register(Object.class,"group","bundle");
      if (tc.isDebugEnabled()) Tr.debug(tc, "tc is for Object.class + group name + bundle name");
      tc = Tr.register(Object.class.getName(),"group","bundle");
      if (tc.isDebugEnabled()) Tr.debug(tc, "tc is for Object.class name + group name + bundle name");
      if (tc.isEntryEnabled()) Tr.exit(tc, "test5 exit");
    }

    // Test component outside enabled trace classes (ras.*=all:notrace.*=off) see build.xml

    public void testSix() {
      TraceComponent tc = Tr.register(className, "", "");
      if (tc.isEntryEnabled()) Tr.entry(tc, "test6 entry");
      TraceComponent tc_off = Tr.register("abc.def.ghi", "", "");
      if (tc.isDebugEnabled()) Tr.debug(tc_off, "test6 =============> should not see this !!!!!!!!!!!!!!!!!");
      tc_off = Tr.register("notrace.all", "", "");
      if (tc.isDebugEnabled()) Tr.debug(tc_off, "test6 =============> should not see this !!!!!!!!!!!!!!!!!");
      if (tc.isEntryEnabled()) Tr.exit(tc, "test6 exit");
    }

    // Test audit trace

   public void test7() {
     TraceComponent tc = Tr.register(className, "", "com.ibm.ejs.ras.Messages");
     if (tc.isEntryEnabled()) Tr.entry(tc, "test7 entry");
     if (tc.isAuditEnabled()) Tr.audit(tc, "BUILDLEVELS_NOT_SAME_CWSJE0002E");
     if (tc.isAuditEnabled()) Tr.audit(tc, "BUILDLEVELS_NOT_SAME_CWSJE0002ENOTEXIST");
     if (tc.isAuditEnabled()) Tr.audit(tc, "BUILDLEVELS_NOT_SAME_CWSJE0002E",objs);
     if (tc.isEntryEnabled()) Tr.exit(tc, "test7 exit");
   }

   // Test group filtering GROUPON*=all:GROUPOFF=off see build.xml

   public void test8() {
     TraceComponent tc = Tr.register(className, "", "com.ibm.ejs.ras.Messages");
     if (tc.isEntryEnabled()) Tr.entry(tc, "test8 entry");
     TraceComponent tc_off = Tr.register("abc.def", "GROUP2", "");
     if (tc.isDebugEnabled()) Tr.debug(tc_off, "test8 GROUP2 =============> should not see this !!!!!!!!!!!!!!!!!");
     tc_off = Tr.register("abc.def", "GROUPOFF", "");
     if (tc.isDebugEnabled()) Tr.debug(tc_off, "test8 GROUPOFF =============> should not see this !!!!!!!!!!!!!!!!!");
     TraceComponent tc_on = Tr.register("abc.def", "GROUPON", "");
     if (tc.isDebugEnabled()) Tr.debug(tc_on, "test8 trace group GROUPON on");
     tc_on = Tr.register("abc.def", "GROUPONPLUS", "");
     if (tc.isDebugEnabled()) Tr.debug(tc_on, "test8 trace group GROUPONPLUS on");
     if (tc.isEntryEnabled()) Tr.exit(tc, "test8 exit");
   }

    // Test debug trace

   public void test9() {
     TraceComponent tc = Tr.register(className, "", "com.ibm.ejs.ras.Messages");
     if (tc.isEntryEnabled()) Tr.entry(tc, "test9 entry");
     if (tc.isDebugEnabled()) Tr.debug(tc, "test9 debug text");
     if (tc.isDebugEnabled()) Tr.debug(tc, "test9 debug text + objects", objs);
     if (tc.isEntryEnabled()) Tr.exit(tc, "test9 exit");
   }

    // Test dump trace

   public void test10() {
     TraceComponent tc = Tr.register(className, "", "com.ibm.ejs.ras.Messages");
     if (tc.isEntryEnabled()) Tr.entry(tc, "test10 entry");
     if (tc.isDumpEnabled()) Tr.dump(tc, "test10 dump text");
     if (tc.isDumpEnabled()) Tr.dump(tc, "test10 dump text + objects", objs);
     if (tc.isEntryEnabled()) Tr.exit(tc, "test10 exit");
   }

    // Test error trace

   public void test11() {
     TraceComponent tc = Tr.register(className, "", "com.ibm.ejs.ras.Messages");
     if (tc.isEntryEnabled()) Tr.entry(tc, "test11 entry");
     if (tc.isErrorEnabled()) Tr.error(tc, "BUILDLEVELS_NOT_SAME_CWSJE0002E");
     if (tc.isErrorEnabled()) Tr.error(tc, "BUILDLEVELS_NOT_SAME_CWSJE0002E",objs);
     if (tc.isEntryEnabled()) Tr.exit(tc, "test11 exit");
   }

    // Test event trace

   public void test12() {
     TraceComponent tc = Tr.register(className, "", "com.ibm.ejs.ras.Messages");
     if (tc.isEntryEnabled()) Tr.entry(tc, "test12 entry");
     if (tc.isEventEnabled()) Tr.event(tc, "BUILDLEVELS_NOT_SAME_CWSJE0002E");
     if (tc.isEventEnabled()) Tr.event(tc, "BUILDLEVELS_NOT_SAME_CWSJE0002E",objs);
     if (tc.isEntryEnabled()) Tr.exit(tc, "test12 exit");
   }

   // Test entry/exit trace

   public void test13() {
     TraceComponent tc = Tr.register(className, "", "com.ibm.ejs.ras.Messages");
     if (tc.isEntryEnabled()) Tr.entry(tc, "test13 entry");
     if (tc.isEntryEnabled()) Tr.entry(tc, "test13 entry + objs",objs);
     if (tc.isEntryEnabled()) Tr.exit(tc, "test13 exit + objs",objs);
     if (tc.isEntryEnabled()) Tr.exit(tc, "test13 exit");
   }

   // Test fatal trace

   public void test14() {
     TraceComponent tc = Tr.register(className, "", "com.ibm.ejs.ras.Messages");
     if (tc.isEntryEnabled()) Tr.entry(tc, "test14 entry");
     if (tc.isFatalEnabled()) Tr.fatal(tc, "BUILDLEVELS_NOT_SAME_CWSJE0002E");
     if (tc.isFatalEnabled()) Tr.fatal(tc, "BUILDLEVELS_NOT_SAME_CWSJE0001E",objs);
     if (tc.isEntryEnabled()) Tr.exit(tc, "test14 exit");
   }

   // Test info trace

   public void test15() {
     TraceComponent tc = Tr.register(className, "", "com.ibm.ejs.ras.Messages");
     if (tc.isEntryEnabled()) Tr.entry(tc, "test15 entry");
     if (tc.isInfoEnabled()) Tr.info(tc, "BUILDLEVELS_NOT_SAME_CWSJE0002E");
     if (tc.isInfoEnabled()) Tr.info(tc, "BUILDLEVELS_NOT_SAME_CWSJE0001E",objs);
     if (tc.isEntryEnabled()) Tr.exit(tc, "test15 exit");
   }

   // Test service trace

   public void test16() {
     TraceComponent tc = Tr.register(className, "", "com.ibm.ejs.ras.Messages");
     if (tc.isEntryEnabled()) Tr.entry(tc, "test16 entry");
     if (tc.isServiceEnabled()) Tr.service(tc, "test16 service");
     if (tc.isServiceEnabled()) Tr.service(tc, "test16 service + objs",objs);
     if (tc.isEntryEnabled()) Tr.exit(tc, "test16 exit");
   }

   // Test uncondEvent trace

   public void test17() {
     TraceComponent tc = Tr.register(className, "", "com.ibm.ejs.ras.Messages");
     if (tc.isEntryEnabled()) Tr.entry(tc, "test17 entry");
     Tr.uncondEvent(tc, "test17 uncondEvent");
     Tr.uncondEvent(tc, "test17 uncondEvent + objs",objs);
     if (tc.isEntryEnabled()) Tr.exit(tc, "test17 exit");
   }

   // Test uncondFormattedEvent trace

   public void test18() {
     TraceComponent tc = Tr.register(className, "", "com.ibm.ejs.ras.Messages");
     if (tc.isEntryEnabled()) Tr.entry(tc, "test18 entry");
     Tr.uncondFormattedEvent(tc, "BUILDLEVELS_NOT_SAME_CWSJE0002E");
     Tr.uncondFormattedEvent(tc, "BUILDLEVELS_NOT_SAME_CWSJE0001E",objs);
     if (tc.isEntryEnabled()) Tr.exit(tc, "test18 exit");
   }

   // Test warning trace

   public void test19() {
     TraceComponent tc = Tr.register(className, "", "com.ibm.ejs.ras.Messages");
     if (tc.isEntryEnabled()) Tr.entry(tc, "test19 entry");
     if (tc.isWarningEnabled()) Tr.warning(tc, "test19 warning");
     if (tc.isWarningEnabled()) Tr.warning(tc, "test19 warning + objs",objs);
     if (tc.isEntryEnabled()) Tr.exit(tc, "test19 exit");
   }

   // Change the traceSpec dynamically

   public void test20() {
     TraceComponent tc = Tr.register(className, "", "com.ibm.ejs.ras.Messages");
     if (tc.isEntryEnabled()) Tr.entry(tc, "test20 entry");
     Tr.setTraceSpec("abc*=all:xyz*=off");
     TraceComponent tc_on = Tr.register("abcdef.ghi");
     if (tc_on.isDebugEnabled()) Tr.debug(tc_on, "test20 debug");
     TraceComponent tc_off = Tr.register("xyzabcdef.ghi");
     if (tc_off.isDebugEnabled()) Tr.debug(tc_off, "test20 debug =============> should not see this !!!!!!!!!!!!!!!!!");
     Tr.setTraceSpec("*");
     if (tc.isEntryEnabled()) Tr.exit(tc, "test20 exit");
   }

   // Write out a lot of data to force maxFileSize to be exceeded (1mb)

   public  void test21() {
     TraceComponent tc = Tr.register(className, "", "com.ibm.ejs.ras.Messages");
     if (tc.isEntryEnabled()) Tr.entry(tc, "test21 entry");
     final String msg = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"; // 100 chars
     for (int i = 0; i < 4096; i++) {
       if (tc.isDebugEnabled()) Tr.debug(tc,i + ":" + msg + msg + msg + msg + msg + msg + msg + msg + msg + msg);
     }
     if (tc.isEntryEnabled()) Tr.exit(tc, "test21 exit");
   }

   // Test isMessageIdConversionEnabled method returns false

   public void test22() {
     assertFalse(TraceNLS.isMessageIdConversionEnabled());
   }

   // Cause an NPE inside the toString of an object being traced

   public void test23() {
     TraceComponent tc = Tr.register(className, "", "com.ibm.ejs.ras.Messages");
     Tr.debug(tc,"test23 debug: Ready to cause NPE", new Inner());
   }

   class Inner {
     public String toString() {
       String a = null;
       String b = a;
       return b.toString();
     }
   }

   //  Test that Tr correctly initialises the new FFDC implementation

   public void test24() {
     TraceComponent tc = Tr.register(className, "", "com.ibm.ejs.ras.Messages");
     com.ibm.ffdc.Manager.Ffdc.log(new Exception("test24"), TrTest.className, "parm1", "parm2");
     com.ibm.ffdc.Manager.Ffdc.log(new Exception("test24"), TrTest.className, "parm3", "parm4", new Obo(), "Text string");
   }

   class Obo {
     static final int obo1 = 1;
     private String obo2 = "2";
   }
   
   // Tests that Untraceable and Traceable work - note as an automated test this merely checks this
   // doesn't crash - a manual inspection of the output is recommended when changing the code....

   public void test25() {
     TraceComponent tc = Tr.register(className, "", "com.ibm.ejs.ras.Messages");
     if (tc.isEntryEnabled()) Tr.entry(tc, "test25 entry", new Object[] { new UntraceableClass(), new TraceableClass(false) });
     if (tc.isEntryEnabled()) Tr.exit(tc, "test25 exit");
     
   }
   
   // Tests that a crashing Traceable works - note as an automated test this merely checks this
   // doesn't crash - a manual inspection of the output is recommended when changing the code....

   public void test26() {
     TraceComponent tc = Tr.register(className, "", "com.ibm.ejs.ras.Messages");
     if (tc.isEntryEnabled()) Tr.entry(tc, "test26 entry", new Object[] { new TraceableClass(true) });
     if (tc.isEntryEnabled()) Tr.exit(tc, "test26 exit");
     
   }
   
   // Tests that arrays of objects (including Untraceable's and Traceable's) work
   // - note as an automated test this merely checks this
   // doesn't crash - a manual inspection of the output is recommended when changing the code....

   public void test27() {
     TraceComponent tc = Tr.register(className, "", "com.ibm.ejs.ras.Messages");
     Object[] array = new Object[] { new Integer(7), new Object[] { new TraceableClass(false), "A", new UntraceableClass(), "B" }, new UntraceableClass(), new TraceableClass(false) };
     if (tc.isEntryEnabled()) Tr.entry(tc, "test27 entry", new Object[] { "Z", array } );
     if (tc.isEntryEnabled()) Tr.exit(tc, "test27 exit");
     
   }
   
   public static class UntraceableClass implements Untraceable
   {
     public String toString()
     {
       return "MUST NOT SEE THIS TEXT";
     }
   }
   
   public static class TraceableClass implements Traceable
   {
     private final boolean throwsException;
     
     public TraceableClass(boolean throwException)
     {
       this.throwsException = throwException;
     }
     
     public String toTraceString()
     {
       if (throwsException)
         throw new ArrayIndexOutOfBoundsException();
       else
         return "LookAtMeIMTraceable";
     }
     
   }
   

}
