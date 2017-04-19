<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<META http-equiv="Content-Style-Type" content="text/css">
<TITLE>Dealership Home</TITLE>

</HEAD>
<BODY bgcolor="#ffffff" link="#000099" vlink="#000099">
<%@ page import="java.util.Collection,
				 java.math.BigDecimal, 
				 org.spec.jappserver.ejb.orders.entity.*, 
				 org.spec.jappserver.servlet.helper.*" session="true" isThreadSafe="true" isErrorPage="false"%>
<jsp:useBean id="customerData" type="org.spec.jappserver.ejb.orders.entity.Customer" scope="request" />
<jsp:useBean id="inventory" type="Collection<CustomerInventory>" scope="request"/>
<jsp:useBean id="specUtils" class="org.spec.jappserver.servlet.helper.SpecUtils" scope="session"/>
<%@ include file="dealership_incl_header.jsp" %>
<TABLE width="645">
    <TBODY>
        <TR>
            <TD valign="top" width="377">
            <TABLE width="384">
                <TBODY>
                    <TR>
                        <TD colspan="3"><B>Welcome &nbsp;<%= customerData.getFirstName() + " " + customerData.getLastName() %>,</B></TD>
                    </TR>
                    <TR>
                        <TD width="133"></TD>
                        <TD width="22"></TD>
                        <TD width="212"></TD>
                    </TR>
                    <TR>
                        <TD colspan="3" align="left" bgcolor="#cccccc"><B> Your User Statistics </B></TD>
                    </TR>
                    <TR>
                        <TD align="right" valign="top" width="133">account ID:<BR>
                        account created:<BR>
                        session created:<BR>
                        </TD>
                        <TD width="22"></TD>
                        <TD align="left" width="212"> <%= customerData.getId() %><BR>
                        <%=  specUtils.formatDate(customerData.getSince()) %><BR>
                        <%= (java.util.Date) session.getAttribute("sessionCreationDate") %><BR>
                        </TD>
                    </TR>
                    <TR>
                        <TD width="133"></TD>
                        <TD width="22"></TD>
                        <TD width="212"></TD>
                    </TR>
                    <TR>
                        <TD colspan="3" bgcolor="#cccccc"> <B>Your Account Summary </B></TD>
                    </TR>
                    <TR>
                        <TD align="right" valign="top" width="133">cash balance:<BR>
                        number of vehicles:<BR>
                        total value of vehicles:<BR>
                        sum of cash/vehicles:<BR>
                        credit limit:<BR>
                        credit rating:<BR>
                        <HR>
                        </TD>
                        <TD width="22"></TD>
                        <TD align="left" valign="top" width="212">
                        <%
                        	BigDecimal balance = customerData.getBalance();
                        	int numberOfVehicles = 0;
                        	BigDecimal total = SpecUtils.ZERO;
                        	for( CustomerInventory cidb: inventory ){
                        		numberOfVehicles += cidb.getQuantity();
                        		total = total.add(cidb.getTotalCost());
                           	}
							BigDecimal creditLimit = customerData.getCreditLimit();
							String creditRating = customerData.getCredit();
                        %>
                        <%= specUtils.formatCurrency(balance) %><BR>
                        <%= numberOfVehicles%><BR>
                        <%= specUtils.formatCurrency(total) %><BR>
                        <%= specUtils.formatCurrency(total.add(balance)) %><BR>
                        <%= specUtils.formatCurrency(creditLimit)%><BR>
                        <%= creditRating%><BR>

                        <HR>
                        </TD>
                    </TR>
                </TBODY>
            </TABLE>
            </TD>
            <TD align="center" valign="top" bgcolor="#ffffff" width="244">
            <jsp:include page="loginSummary.jsp" />

            <BR>
            </TD>
        </TR>
    </TBODY>
</TABLE>
<%@ include file="dealership_incl_footer.jsp" %>
</BODY>
</HTML>