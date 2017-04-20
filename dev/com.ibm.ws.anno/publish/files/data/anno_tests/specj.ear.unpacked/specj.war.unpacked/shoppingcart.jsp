<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<META http-equiv="Content-Style-Type" content="text/css">
<TITLE>Dealership Shopping Cart</TITLE>
</HEAD>
<BODY bgcolor="#ffffff" link="#000099" vlink="#000099">
<%@ page import="java.util.*, java.math.*, org.spec.jappserver.servlet.helper.*, org.spec.jappserver.ejb.shoppingcart.entity.*, org.spec.jappserver.ejb.orders.entity.Item" session="true" isThreadSafe="true" isErrorPage="false"%>
<jsp:useBean id="shoppingcart" scope="request" type="org.spec.jappserver.ejb.shoppingcart.entity.ShoppingCart" />
<jsp:useBean id="specUtils" class="org.spec.jappserver.servlet.helper.SpecUtils" scope="session"/>
<%@ include file="dealership_incl_header.jsp" %>
<TABLE width="645">
    <TBODY>
        <TR>
            <TD valign="top" width="643">
            <TABLE width="639">
                <TBODY>

                    <TR>
                        <TD colspan="5" bgcolor="#cccccc" width="200"><b>
                        Shopping Cart</b></TD>
                        <TD bgcolor="#cccccc" align="right" width="429"><B>Number of 
                        Vehicle Models in Cart: </B><%= shoppingcart.getItemCount() %></TD>
                    </TR>
                    <TR align="center">
                        <TD colspan="6" width="633">
                        <CENTER>
                        <p></p>
                        </CENTER>
                        <TABLE border="1" style="font-size: smaller">
                            <CAPTION align="top"><b>Items Currently in Cart</b></CAPTION>
                            <TBODY>
                                <TR align="center">
                                    <TD>Cart ID</TD>
                                    <TD>Inventory ID</TD>
                                    <TD>Name</TD>
                                    <TD>Description</TD>
                                    <TD>Discount</TD>                                 
                                    <TD>Purchase Price</TD>
                                    <TD>Quantity</TD> 
                                    <TD>Total</TD>                                   
                                    <TD>Shopping Cart Operation</TD>
                                  </TR>
                                
<%
try{
    Iterator<Item> items = shoppingcart.getItems().iterator();
    int i = 0;
	for (; items.hasNext(); ) {
	    i++;
	    Item item = items.next();
	    int quantity = shoppingcart.getQuantity(item);
	    BigDecimal discountFactor  = item.getDiscount();
	    BigDecimal discountPercent = discountFactor.multiply(SpecUtils.HUNDRED);
	    BigDecimal discountedPrice = item.getPrice().multiply(SpecUtils.ONE.subtract(discountFactor)).setScale(2, BigDecimal.ROUND_DOWN);
		BigDecimal totalPrice = discountedPrice.multiply(new BigDecimal(quantity)).setScale(2, BigDecimal.ROUND_DOWN);
%>
            <TR bgcolor="#fafcb6" align="center">
                <TD><%= i /*get id from session*/ %>&nbsp;</TD>
                <TD><%= item.getId() %>&nbsp;</TD>
                <TD><%= item.getName() %>&nbsp;</TD>
                <TD><%= item.getDescription() %>&nbsp;</TD>
                <TD><%= specUtils.formatNumber(discountPercent) %>%&nbsp;</TD>
				<TD><%= specUtils.formatCurrency(discountedPrice) %>&nbsp;</TD>
                <TD><%= quantity %>&nbsp;</TD>
                <TD><%= specUtils.formatCurrency(totalPrice) %>&nbsp;</TD>
                <TD><B><%= "<A href=\"app?action=remove&cartID="+ item.getId() +"\">remove</A>"%></B>&nbsp;</TD>
            </TR>
<%
}
}catch(Exception e){
    e.printStackTrace();
}
%>                        
                                <TR align="center">
                                    <TD></TD>
                                    <TD></TD>
                                    <TD></TD>
                                    <TD></TD>                           
                                    <TD></TD>
                                    <TD><B>Total</B></TD>
                                    <TD align="center"><%= specUtils.formatCurrency(shoppingcart.getTotalPrice()) %></TD>
                                    <TD align="center" colspan="2"><A href="app?action=purchasecart">purchase now</A> / <BR><A href="app?action=deferorder">open deferred order</A> / <BR><A href="app?action=clearcart">clear cart</A></TD>
                                </TR>
                            </TBODY>
                        </TABLE>
                        <CENTER>
                        <p></p>
                        </CENTER>
                        </TD>
                    </TR>
                    <TR>
                        <TD colspan="6" width="633"></TD>
                    </TR>
               </TBODY>
            </TABLE>
            </TD>
        </TR>
    </TBODY>
</TABLE>
<%@ include file="dealership_incl_footer.jsp" %>
</BODY>
</HTML>