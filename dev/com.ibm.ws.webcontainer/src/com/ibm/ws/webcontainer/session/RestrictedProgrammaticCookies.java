package com.ibm.ws.webcontainer.session;

public class RestrictedProgrammaticCookies {

    String name=null;
    String domain=null;
    String path=null;
    
    public RestrictedProgrammaticCookies(String n, String d, String p) {
        name=n;
        domain=d;
        path=p;        
    }
    
    public String getName() {
        return name;
    }
    
    public String getDomain() {
        return domain;
    }
    
    public String getPath() {
        return path;
    }
    
}
