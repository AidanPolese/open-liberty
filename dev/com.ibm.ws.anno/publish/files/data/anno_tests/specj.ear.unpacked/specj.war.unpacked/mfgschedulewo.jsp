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
        List<String> assemblyIds = (List<String>)request.getAttribute("assemblyIds");
        %>
        <TR>
            <TD bgcolor="#cccccc" width="200"><b>Schedule Work Order</b></TD>
        </TR>
        <TR>
            <TD width="624">
            <FONT color="#cc0000"><%= request.getAttribute("results") %></FONT> 
            <BR>
            <B><p><b> To schedule a work order, please select Assembly ID and enter quantity </b></p></B>
            <HR>
        </TR>
        <FORM METHOD=POST ACTION="app?action=<%= nextAction %>">
     
        <%
        if (assemblyIds != null) {
		   if (assemblyIds.size() > 0) {
           %>
                <TR>
                    <TD align="right" width="624"> <FONT size="-1">Assembly ID</FONT> &nbsp; &nbsp; <FONT size="-1">Location</FONT>&nbsp; &nbsp<FONT size="-1">Quantity</FONT>&nbsp; &nbsp;
                    </TD>
                </TR>
                <TR>
                    <TD align="right" width="624">
                    <SELECT NAME="assemblyId">
		<%
			   String assemblyId; 
			   Iterator iterAssembly = assemblyIds.iterator();
			   while (iterAssembly.hasNext())
			   {
					assemblyId = (String)iterAssembly.next();
		%>
					    <OPTION> <%= assemblyId %>
		<%
				}
		%>
				    </SELECT>
				    <INPUT TYPE=text name="location" value="0" size="4" maxlength="5"> &nbsp; &nbsp;
                    <INPUT TYPE=text name="qty" value="100" size="4" maxlength="5"> &nbsp; &nbsp;
                    <!--  TODO choose from a set of location ids from the database. -->
                    </TD>
                </TR>
                <TR>
                    <TD align="right" width="624">
                    <INPUT TYPE=submit name="submit" value="Schedule"> &nbsp; &nbsp;
                    </TD>
                </TR>
           <%
           } else {
           %>
                <TR>
                    <TD><BR>
				        <FONT color="#cc0000"> No assembly Ids found </font> <BR>
                    </TD>
                </TR>
           <%
           }
        }
        %>
        
        </FORM>        
    </TBODY>
</TABLE>
<%@ include file="mfg_incl_footer.jsp" %>
</BODY>
</HTML>
