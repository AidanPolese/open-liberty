// NOTE: This is a generated file. Do not edit it directly.
package com.ibm.ws.javaee.ddmodel.commonbnd;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@Component(configurationPid = "com.ibm.ws.javaee.dd.commonbnd.EJBRef",
     configurationPolicy = ConfigurationPolicy.REQUIRE,
     immediate=true,
     property = "service.vendor = IBM")
public class EJBRefComponentImpl implements com.ibm.ws.javaee.dd.commonbnd.EJBRef {
private Map<String,Object> configAdminProperties;
private com.ibm.ws.javaee.dd.commonbnd.EJBRef delegate;
     protected java.lang.String name;
     protected java.lang.String binding_name;

     @Activate
     protected void activate(Map<String, Object> config) {
          this.configAdminProperties = config;
          name = (java.lang.String) config.get("name");
          binding_name = (java.lang.String) config.get("binding-name");
     }

     @Override
     public java.lang.String getName() {
          if (delegate == null) {
               return name == null ? null : name;
          } else {
               return name == null ? delegate.getName() : name;
          }
     }

     @Override
     public java.lang.String getBindingName() {
          if (delegate == null) {
               return binding_name == null ? null : binding_name;
          } else {
               return binding_name == null ? delegate.getBindingName() : binding_name;
          }
     }
     public Map<String,Object> getConfigAdminProperties() {
          return this.configAdminProperties;
     }

     public void setDelegate(com.ibm.ws.javaee.dd.commonbnd.EJBRef delegate) {
          this.delegate = delegate;
     }
}
