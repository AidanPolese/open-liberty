<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<META http-equiv="Content-Style-Type" content="text/css">
<TITLE>Welcome to SPECjAppServer 2008</TITLE>

</HEAD>
<BODY bgcolor="#ffffff" link="#000099" vlink="#000099">
<%@ page import="java.util.*" isThreadSafe="true" session="TRUE" isErrorPage="false"%>
<%@ include file="mfg_incl_header.jsp" %>

<TABLE width="645">
    <TBODY>
        <%
        String nextAction = (String)request.getAttribute("nextAction");
        %>
        <TR>
            <TD bgcolor="#cccccc" width="200"><b>Check for Larger Orders</b></TD>
        </TR>
        <TR>
            <TD width="624">
            <FONT color="#cc0000"><%= request.getAttribute("results") %></FONT> 
            <BR>
            <B><p><b> Specify the location at which the work order should be processed</b></p></B>
            <HR>
        </TR>
        <TR>
			<TD>
        		<FORM METHOD=POST ACTION="app?action=<%= nextAction %>">
			    	  <!--  TODO choose from a set of location ids from the database. -->
				    <INPUT TYPE=text name="location" value="0" size="4" maxlength="5"> &nbsp; &nbsp;
                    <INPUT TYPE=submit name="submit" value="Schedule"> &nbsp; &nbsp;
                </FORM>  
            </TD>
        </TR>
    </TBODY>
</TABLE>
<%@ include file="mfg_incl_footer.jsp" %>
</BODY>
</HTML>
