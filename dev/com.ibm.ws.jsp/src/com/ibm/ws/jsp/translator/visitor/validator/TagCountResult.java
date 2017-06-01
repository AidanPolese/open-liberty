package com.ibm.ws.jsp.translator.visitor.validator;

import java.util.Map;

import com.ibm.ws.jsp.translator.visitor.JspVisitorResult;

public class TagCountResult extends JspVisitorResult {
    private Map countMap = null;
    
    protected TagCountResult(String jspVisitorId, Map countMap) {
        super(jspVisitorId);
        this.countMap = countMap;
    }

    public Map getCountMap() {
        return countMap;
    }
}
