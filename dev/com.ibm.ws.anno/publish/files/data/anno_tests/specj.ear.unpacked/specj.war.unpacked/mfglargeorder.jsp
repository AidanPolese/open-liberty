<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<META http-equiv="Content-Style-Type" content="text/css">
<TITLE>Welcome to SPECjAppServer 2008</TITLE>

</HEAD>
<BODY bgcolor="#ffffff" link="#000099" vlink="#000099">
<%@ page import="java.util.Collection, java.util.Iterator, org.spec.jappserver.ejb.mfg.entity.LargeOrder" isThreadSafe="true" session="TRUE" isErrorPage="false"%>
<jsp:useBean id="specUtils" class="org.spec.jappserver.servlet.helper.SpecUtils" scope="session"/>
<%@ include file="mfg_incl_header.jsp" %>

<TABLE width="645">
    <TBODY>
        <TR>
            <TD bgcolor="#cccccc" width="200"><b>Large Orders Information</b></TD>
        </TR>
        <%
        LargeOrder large_order;
        String assemblyId;
        Integer qty;
        String dueDate;
        Integer oLineId;
        Integer salesId;
    
        Collection listLOs = (Collection) request.getAttribute("listLOs");
        
        if (listLOs.size() == 0 ) {
         %>
             <TR>
                 <TD>            
                 <FONT color="#cc0000"><BR> No pending large orders at this time </font>
             </TR>
         <%
        } else {
        %>
        
        
            <TR>
		        <TD width="624"><B><p><b> There are <%= listLOs.size() %> large orders. They are listed below.<BR>
                To schedule an order please click <STRONG> Assembly ID </STRONG> of the order </b></p></B>
                <HR>
            </TR>
            
            <TR>
            <TD width="624">
                <TABLE border="1" style="font-size: smaller">
                <CAPTION align="top"><b>Large Orders </b></CAPTION>
                <TBODY>
                   <TR align="center">
                       <TD>Assembly ID</TD>
                       <TD>Quantity</TD>
                       <TD>Due Date</TD>
                   </TR>

		<% 
		        Iterator iterLargeOrder = listLOs.iterator();
		        while (iterLargeOrder.hasNext())
		        {
		            large_order= (LargeOrder)iterLargeOrder.next();
					assemblyId = large_order.getAssembly().getId();
					qty = large_order.getQty();
					dueDate = specUtils.formatParseableDate(large_order.getDueDate());
					oLineId = large_order.getOrderLineNumber();
					salesId = large_order.getSalesOrderId();
		%>
					
             
                    <TR bgcolor="#fafcb6" align="center">
					<TD> 
					<A HREF="app?action=mfgschedulewo&assemblyId=<%= assemblyId %>&qty=<%= qty %>&dueDate=<%=dueDate %>&oLineId=<%= oLineId %>&salesId=<%= salesId %> ">
					<%= assemblyId %> 
					</A>
					&nbsp;</TD>
					<TD> <%= qty %> &nbsp;</TD>
					<TD> <%= dueDate %> &nbsp;</TD>
                    </TR>
		<%
				}	
		%>
				</TABLE>

        <%
        }
        %>
    
            <BR>
            </TD>
        </TR>
        
        
        
    </TBODY>
</TABLE>
<%@ include file="mfg_incl_footer.jsp" %>
</BODY>
</HTML>
