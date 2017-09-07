/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.microprofile.health;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import org.eclipse.microprofile.health.HealthCheckResponse.State;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.spi.HealthCheckResponseProvider;
import com.ibm.ws.microprofile.health.impl.HealthCheckResponseImpl;
import com.ibm.ws.microprofile.health.impl.HealthCheckResponseBuilderImpl;
import com.ibm.ws.microprofile.health.spi.impl.HealthCheckResponseProviderImpl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ibm.websphere.ras.Tr;


public class HealthCheckResponseTest {    
   
    /**
     * 
     */
    @Test(expected = IllegalStateException.class)
    public void testResponseNamed() throws Exception {    
        final String name = "success-test";             
        
        HealthCheckResponseBuilder builder = HealthCheckResponse.named(name);
        HealthCheckResponse response = builder.build();
        String testName = response.getName();
        
        assertEquals(name, testName);        
    }
    
    /**
     * 
     */
    @Test(expected = IllegalStateException.class)
    public void testResponseBuilder() throws Exception {    
        final String name = "success-test";
               
        HealthCheckResponseBuilder builder = HealthCheckResponse.builder();
        builder = builder.name(name);
        HealthCheckResponse response = builder.build();
        String testName = response.getName();
        
        assertEquals(name, testName);        
    }
    
    /**
     * 
     */
    @Test
    public void testSetProvider() throws Exception {    
        final String name = "success-test";
        
        HealthCheckResponseProvider provider = new HealthCheckResponseProviderImpl();
        HealthCheckResponse.setResponseProvider(provider);
        HealthCheckResponseBuilder builder = provider.createResponseBuilder();
        builder.name(name);        
        HealthCheckResponse response = builder.build();
        String testName = response.getName();
        
        assertEquals(name, testName);        
    }
  
    
    
    /**
     * 
     */
    @Test
    public void testGetNameWithResponse() throws Exception {        
        final String name = "success-test";
               
        HealthCheckResponse response = new HealthCheckResponseImpl(name, null, null);
        String testName = response.getName();
        
        assertEquals(name, testName);
    }
    
    /**
     * 
     */
    @Test
    public void testGetNameWithProvider() throws Exception {    
        final String name = "success-test";
               
        HealthCheckResponseProvider provider = new HealthCheckResponseProviderImpl();
        HealthCheckResponseBuilder builder = provider.createResponseBuilder();
        builder = builder.name(name);
        HealthCheckResponse response = builder.build();
        String testName = response.getName();
        
        assertEquals(name, testName);        
    }
    
    /**
     * 
     */
    @Test
    public void testGetNameWithBuilder() throws Exception {       
        final String name = "success-test";
        
        HealthCheckResponseBuilder builder = new HealthCheckResponseBuilderImpl();
        builder = builder.name(name);
        HealthCheckResponse response = builder.build();
        String testName = response.getName();
        
        assertEquals( name, testName);        
    }

    /**
     * 
     */
    @Test
    public void testWithDataWithString() throws Exception {       
        final String name = "success-test";
               
        Map<String, Object> attribute = new HashMap<String, Object>();
        attribute.put("first-key", "first-value");
        final Optional<Map<String, Object>> data = Optional.of(attribute);           
        
        HealthCheckResponseBuilder builder = new HealthCheckResponseBuilderImpl();
        builder = builder.withData("first-key", "first-value");
        HealthCheckResponse response = builder.build();
        Optional<Map<String, Object>> testData = (Optional<Map<String, Object>>) response.getData();
        
        assertEquals( data, testData);        
       
    }
    
    /**
     * 
     */
    @Test
    public void testGetDataWithString2() throws Exception {        
        final String name = "success-test";
              
        Map<String, Object> attribute = new HashMap<String, Object>();
        attribute.put("first-key", "first-value");
        final Optional<Map<String, Object>> data = Optional.of(attribute);         
        
        HealthCheckResponse response = new HealthCheckResponseImpl(name, null, data);      
        Optional<Map<String, Object>> testData = (Optional<Map<String, Object>>) response.getData();        
        assertEquals( data, testData);       
    }
    
    /**
     * 
     */
    @Test
    public void testWithDataWithLong() throws Exception {        
        final String name = "success-test";
               
        Map<String, Object> attribute = new HashMap<String, Object>();
        attribute.put("first-key", (long)15000);
        final Optional<Map<String, Object>> data = Optional.of(attribute);           
        
        HealthCheckResponseBuilder builder = new HealthCheckResponseBuilderImpl();
        builder = builder.withData("first-key", 15000);
        HealthCheckResponse response = builder.build();
        Optional<Map<String, Object>> testData = (Optional<Map<String, Object>>) response.getData();
        
        assertEquals( data, testData);              
    }
    
    /**
     * 
     */
    @Test
    public void testGetDataWithLong2() throws Exception {        
        final String name = "success-test";
              
        Map<String, Object> attribute = new HashMap<String, Object>();
        attribute.put("first-key", 15000);
        final Optional<Map<String, Object>> data = Optional.of(attribute);          
        
        HealthCheckResponse response = new HealthCheckResponseImpl(name, null, data);        
        Optional<Map<String, Object>> testData = (Optional<Map<String, Object>>) response.getData();
        
        assertEquals( data, testData);              
    }

    /**
     * 
     */
    @Test
    public void testGetDataWithBooleanUp() throws Exception {    
        final String name = "success-test";
               
        Map<String, Object> attribute = new HashMap<String, Object>();
        attribute.put("first-key", false);
        final Optional<Map<String, Object>> data = Optional.of(attribute);          
        
        HealthCheckResponseBuilder builder = new HealthCheckResponseBuilderImpl();
        builder = builder.withData("first-key", false);
        HealthCheckResponse response = builder.build();
        Optional<Map<String, Object>> testData = (Optional<Map<String, Object>>) response.getData();
        
        assertEquals( data, testData);         
    }
    
    /**
     * 
     */
    @Test
    public void testGetDataWithBooleanDown() throws Exception {
        final String name = "success-test";
        
        Map<String, Object> attribute = new HashMap<String, Object>();
        attribute.put("first-key", false);
        final Optional<Map<String, Object>> data = Optional.of(attribute);        
        
        HealthCheckResponse response = new HealthCheckResponseImpl(name, null, data);        
        Optional<Map<String, Object>> testData = (Optional<Map<String, Object>>) response.getData();  
        
        assertEquals( data, testData);            
    }

    /**
     * 
     */
    @Test
    public void testGetStateUp() throws Exception {
      
      final String name = "success-test";
      final State state = State.UP;
      
      HealthCheckResponseBuilder builder = new HealthCheckResponseBuilderImpl();
      builder = builder.state(true);
      HealthCheckResponse response = builder.build();
      State testState = (State) response.getState();
            
      assertEquals( state, testState);     
    }
  
    /**
     * 
     */
    @Test
    public void testGetStateDown() throws Exception {
      
      final String name = "success-test";
      final State state = State.DOWN;        
      
      HealthCheckResponse response = new HealthCheckResponseImpl(name, state, null);      
      State testState = (State) response.getState();      
      assertEquals( state, testState);        
    }
  
    /**
     * 
     */
    @Test
    public void testUp() throws Exception {
      
      final String name = "success-test";
      final State state = State.UP;
      
      HealthCheckResponseBuilder builder = new HealthCheckResponseBuilderImpl();
      builder = builder.up();
      HealthCheckResponse response = builder.build();
      State testState = (State) response.getState();
      
      assertEquals( state, testState);     
    }   
  

}
