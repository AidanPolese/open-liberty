<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<META http-equiv="Content-Style-Type" content="text/css">
<TITLE>Vehicle Order information</TITLE>

</HEAD>
<BODY bgcolor="#ffffff" link="#000099" vlink="#000099">
<%@ page import="java.util.Iterator, 
				 java.util.Map, 
				 java.math.BigDecimal, 
				 org.spec.jappserver.ejb.orders.entity.*,
				 org.spec.jappserver.servlet.helper.*" session="true" isThreadSafe="true" isErrorPage="false"%>
<jsp:useBean id="results" scope="request" type="java.lang.String" />
<jsp:useBean id="order" scope="request" type="org.spec.jappserver.ejb.orders.entity.Order" />
<jsp:useBean id="specUtils" class="org.spec.jappserver.servlet.helper.SpecUtils" scope="session"/>
<%@ include file="dealership_incl_header.jsp" %>        
<TABLE width="645">
    <TBODY>
        <TR>
            <TD>
            <TABLE width="100%">
                <TBODY>
                    <TR>
                        <TD></TD>
                    </TR>
                    <% 
 if ( order != null )
 {
                    %>
                    <TR>
                        <TD align="left" bgcolor="#cccccc"><b>New Order</b></TD>
                    </TR>
                    <TR>
                        <TD align="left"><FONT color="#cc0000"><B><BR>
                        Order <%=order.getId()%></B> <%=results%> </FONT><BR>
                        <BR>
                        <FONT color="#000000">Order <FONT color="#000000"><B><%=order.getId()%></B></FONT> details:</FONT></TD>
                    </TR>
                    <TR>
                        <TD align="center">
                        <TABLE border="1" style="font-size: smaller" width="633">                            
                            <TBODY>
                                <TR align="center" bgcolor="#fafcb6">
                                    <TD colspan=2>Order ID: <%= order.getId() %></TD>
                                    <TD colspan=2>Entry Date: <%= specUtils.formatDate(order.getEntryDate()) %></TD>
                                    <TD colspan=2><A HREF=<%= "\"app?action=cancelorder&orderID="+order.getId()+"\"" %>>cancel order</A>&nbsp;</TD>
                                </TR>
                                <TR bgcolor="#fafcb6">
                                	<TD>&nbsp;</TD>
                                	<TD colspan=5>Order Line Items</TD>
                                </TR>
                                <TR align="center">
                                	<TD bgcolor="#fafcb6">ID</TD>
                                	<TD>Name</TD>
                                	<TD>Description</TD>
                                	<TD>Quantity</TD>
                                	<TD>Unit Price</TD>
                                	<TD>Total</TD>	
                                </TR>
<% 
try{	
	
	for (OrderLine oldb: order.getOrderLines()){
		Item itemdb = oldb.getItem();
		BigDecimal itemPrice = itemdb.getPrice().multiply((SpecUtils.ONE.subtract(itemdb.getDiscount()))).setScale(2, BigDecimal.ROUND_DOWN);
		BigDecimal currentTotal = itemPrice.multiply(new BigDecimal(oldb.getQuantity()));
%>
                                <TR align="center">
                                    <TD bgcolor="#fafcb6"><%= oldb.getPK().getNumber() %>&nbsp;</TD>
                                    <TD><%= itemdb.getName() %>&nbsp;</TD>
                                    <TD><%= itemdb.getDescription() %>&nbsp;</TD>
                                    <TD><%= oldb.getQuantity() %>&nbsp;</TD>
                                    <TD><%= specUtils.formatCurrency(itemPrice) %>&nbsp;</TD>
                                    <TD><%= specUtils.formatCurrency(currentTotal) %>&nbsp;</TD>
                                </TR>
                                <% 
	}
}
catch (Exception e)
{
     e.printStackTrace();
     System.out.println("order.jsp: error displaying user holdings");
}
				%>
                                <TR bgcolor="#fafcb6" align="center">
                                    <TD>&nbsp;</TD>
                                    <TD>Ord Status: <%= order.getStatus() %></TD>
                                    <TD>&nbsp;</TD>
                                    <TD>Discount: <%= specUtils.formatNumber(order.getDiscount()) %>%</TD>
                                    <TD>&nbsp;</TD>
                                    <TD><%= specUtils.formatCurrency(order.getTotal()) %>&nbsp;</TD>
                                </TR>
                            </TBODY>
                        </TABLE>                        </TD>
                    </TR>
<% 
 }
 %>
                </TBODY>
            </TABLE>
            </TD>
        </TR>
    </TBODY>
</TABLE>
<%@ include file="dealership_incl_footer.jsp" %>
</BODY>
</HTML>
