package com.ibm.websphere.security.wim.ras_test;
import static org.junit.Assert.*;

import org.junit.Test;

import com.ibm.websphere.security.wim.ras.WIMTraceHelper;

public class WIMTraceHelperTest {

	@Test
	public void testAllBeans() {
		Customer cs = new Customer();
		cs.setFirtName("Sunil");
		cs.setLastName("George");
		cs.setAddress("EGL IBM D Block");
		cs.setPinCode(560066);
		
		PhoneInfo pi = new PhoneInfo();
		pi.setLandLineNo("0808237333");
		pi.setMobileNo("9879787888");
		pi.setStdCode("080");
		
		Geography geo = new Geography();
		geo.setCity("Bangalore");
		geo.setCountry("India");
		geo.setState("Karnataka");
		
		cs.setPi(pi);
		cs.setGeo(geo);
		
		try {
			String trace = WIMTraceHelper.trace(cs);
			assertTrue("address=EGL IBM D Block - not Found", trace.contains("address=EGL IBM D Block"));
			assertTrue("firtName=Sunil - not Found", trace.contains("firtName=Sunil"));
			assertTrue("city=Bangalore -  not Found", trace.contains("city=Bangalore"));
			assertTrue("country=India - not Found", trace.contains("country=India"));
			assertTrue("state=Karnataka - not Found", trace.contains("state=Karnataka"));
			assertTrue("lastName=George - not Found", trace.contains("lastName=George"));
			assertTrue("landLineNo=0808237333 - not Found", trace.contains("landLineNo=0808237333"));
			assertTrue("mobileNo=9879787888 - not Found", trace.contains("mobileNo=9879787888"));
			assertTrue("stdCode=080 - not Found", trace.contains("stdCode=080"));
			assertTrue("mobileNo=9879787888 - not Found", trace.contains("mobileNo=9879787888"));
			assertTrue("pinCode=560066 - not Found", trace.contains("pinCode=560066"));
		} catch (Exception e) {
            String errorMessage = e.getMessage();
            assertEquals("Call completed successfully", true, false + " with " + errorMessage);
		}
	}
}
