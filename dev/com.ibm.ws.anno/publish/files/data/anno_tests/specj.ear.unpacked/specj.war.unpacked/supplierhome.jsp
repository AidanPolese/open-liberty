<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<META http-equiv="Content-Style-Type" content="text/css">
<TITLE>Welcome to SPECjAppServer 2008</TITLE>

</HEAD>
<BODY bgcolor="#ffffff" link="#000099" vlink="#000099">
<%@ page import="java.util.Collection, 
                 java.util.Iterator, 
                 java.util.Map, 
                 java.math.BigDecimal, 
                 org.spec.jappserver.ejb.supplier.entity.*,
                 org.spec.jappserver.servlet.helper.*" 
          session="true" 
          isThreadSafe="true" 
          isErrorPage="false"%>
<jsp:useBean id="suppliers"  type="java.util.Collection<Supplier>" scope="request"/>
<TABLE width="644">
    <TBODY>
     <TR>
       <TD>
		<CENTER>
		<p> <IMG SRC="images/mfg.gif" width="344" height="213"><H2>Supplier Management Page</H2>
           From this page you can review and manipulate Supplier Information		
        </p>
		</CENTER>
	   </TD>
      </TR>
	  <TR>
	   <TD>
         <TABLE border="1" style="font-size: smaller">
             <CAPTION align="top"><b>Supplier List </b></CAPTION>
             <TBODY>
                 <TR align="center">
                     <TD>Id</TD>
                     <TD>Name</TD>
                     <TD>WebService URL</TD>
                     <TD>Reply URL</TD>
                     <TD>Address</TD>
                     <TD>Contact</TD>
                 </TR>    
<%
	for ( Supplier supplier: suppliers ) {
%>
				<TR bgcolor="#fafcb6" align="center">
				    <TD><%= supplier.getId() %>&nbsp;</TD>
				    <TD><%= supplier.getName() %>&nbsp;</TD> 
					<TD><%= supplier.getWebServiceURL() %>&nbsp;</TD> 
				    <TD><%= supplier.getReplyURL() %>&nbsp;</TD> 
				    <TD><%= supplier.getAddress() %>&nbsp;</TD> 
				    <TD><%= supplier.getContact() %>&nbsp;</TD> 
				</TR>
<%
	}
%>                     
          	</TBODY>
          </TABLE>
        </TD>    
      </TR>
      <FORM METHOD=POST ACTION="app">
      <TR>
        <TABLE>
         <TBODY>
	      <TR>
		     <TD align="right"> <FONT size="-1">New WebService URL:</FONT></TD>
		     <TD align="left"><INPUT NAME="supp_ws_url" TYPE=text SIZE="64"/> </TD>
	      </TR>
	      <TR>
		     <TD align="right"><FONT size="-1">New Reply URL:</FONT></TD>
		     <TD align="left" ><INPUT NAME="supp_reply_url" TYPE=text SIZE="64"/></TD>
	      </TR>
	      <TR>
	         <TD align="center" colspan="2">
	         	<INPUT TYPE=submit name="action" value="Set Supplier URLs">&nbsp; &nbsp;
	        </TD>
	      </TR>
	     </TBODY>
	    </TABLE>
      </FORM>
    </TBODY>
</TABLE>
</BODY>
</HTML>
