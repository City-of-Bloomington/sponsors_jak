package city.sponsor.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import city.sponsor.model.*;
import city.sponsor.list.*;
import city.sponsor.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 *
 */
@WebServlet(urlPatterns = {"/PaymentSearchServ"})
public class PaymentSearchServ extends TopServlet{

    static final long serialVersionUID = 58L;	
    static Logger logger = LogManager.getLogger(PaymentSearchServ.class);
    /**
     * @param req
     * @param res
     */
    
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
	doGet(req,res);
    }
    /**
     * @link #doGetost
     */

    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
    
	boolean success=true;
	//
	String message="", action="";
	String            
	    spon_id="", sponship_id="",       
	    oppt_id="", id="", hasBalance="",
	    date_from="", date_to="", whichDate = "dueDate",            
	    value_from="", value_to="", sortBy="dueDate";

	String pageSize = "50", pageNumber = "1";
		
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	User user = null;
		
	HttpSession session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"Login";
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"Login";
	    res.sendRedirect(str);
	    return; 
	}
	PaymentList payments = new PaymentList(debug);
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("sponship_id")) {
		payments.setSponship_id(value);
		sponship_id = value;
	    }
	    else if (name.equals("id")) {
		payments.setId(value);
		id = value;
	    }			
	    else if (name.equals("spon_id")) {
		payments.setSpon_id(value);
		spon_id = value;
	    }
	    else if (name.equals("oppt_id")) {
		payments.setOppt_id(value);
		oppt_id = value;
	    }
	    else if (name.equals("date_from")) {
		if(value.equals("today")){
		    value = Helper.getToday();
		}
		payments.setDate_from(value);
		date_from = value;
	    }	
	    else if (name.equals("date_to")) {
		payments.setDate_to(value);
		date_to = value;
	    }
	    else if (name.equals("value_to")) {
		payments.setValue_to(value);
		value_to = value;
	    }
	    else if (name.equals("value_from")) {
		payments.setValue_from(value);
		value_from = value;
	    }
	    else if (name.equals("hasBalance")) {
		if(!value.equals(""))
		    payments.hasBalance();
		hasBalance = value;
	    }
	    else if (name.equals("sortBy")) {
		payments.setSortBy(value);
		sortBy = value;
	    }	
	    else if (name.equals("pageSize")) {
		pageSize = value;
	    }
	    else if (name.equals("pageNumber")) {
		pageNumber = value;
	    }			
	    else if (name.equals("action")){ 
		action = value;  
	    }
	}
	payments.setPageSize(pageSize);
	payments.setPageNumber(pageNumber);
	//
	String spon_name = "", opport_name="", details="";
	if(!spon_id.equals("")){
	    Sponsor sp = new Sponsor(debug, spon_id);
	    String back = sp.doSelect();
	    if(back.equals(""))
		spon_name = sp.getOrgname();
	}
	if(!oppt_id.equals("")){
	    Opportunity op = new Opportunity(debug, oppt_id);
	    String back = op.doSelect();
	    if(back.equals(""))
		opport_name = op.getInfo();
	}
	if(!sponship_id.equals("")){
	    Sponsorship sp = new Sponsorship(debug, sponship_id);
	    String back = sp.doSelect();
	    if(back.equals(""))
		details = sp.getDetails();
	}
	if(!action.equals("")){
	    String back = payments.find();
	    if(!back.equals("")){
		message += back;
		logger.error(back);
	    }
	    else{
		int cnt = payments.getCount();
		if(payments.size() == 1){
		    Payment pay = payments.get(0);
		    String str = url+"PaymentServ?id="+pay.getId();
		    res.sendRedirect(str);
		    return; 
		}
	    }
	}
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");
	out.println("<script type=\"text/javascript\">");
	out.println("//<![CDATA[  ");
	out.println("  function validateForm(){	                     ");
	out.println("  with(document.myForm){                        ");
	//
	// checking dates and numeric values
	// check the numbers
	//
	out.println("  return true;			        ");
	out.println(" }}	         		        ");
	out.println(" //]]>   ");
	out.println(" </script>				        ");
	//
    	// delete startNew
	//
	out.println("<div class=\"center\">");
	out.println("<h2>Search Payments</h2>");
	out.println("</div>"); 		
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("<form name=\"myForm\" method=\"get\" "+
		    " action=\""+url+"PaymentSearchServ?\""+
		    " onsubmit=\"return validateForm()\">");
	out.println("<input type=\"hidden\" name=\"pageNumber\" value=\""+pageNumber+"\" />");

	out.println("<fieldset><legend>Search</legend>");
	out.println("<table border=\"1\" width=\"90%\">");
	out.println("<tr><td class=\"center\">");
	//
	// Add/Edit record
	//
	out.println("<table width=\"100%\">");
	out.println("<tr><th>Payment ID</th>");
	out.println("<td class=\"left\">");		
	out.println("<input name=\"id\" size=\"10\" id=\"id\""+
		    " id=\"10\" value=\""+id+"\" />");
	out.println("</td></tr>");
	out.println("<tr><th>Sponsor</th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"spon_name\" size=\"30\" id=\"spon_name\""+
		    " maxlength=\"50\" value=\""+spon_name+"\" />");
	out.println("<b>ID</b><input id=\"spon_id\" name=\"spon_id\" value=\""+spon_id+"\" size=\"4\" />");
	out.println("</td></tr>");
	out.println("<tr><th>Payment has</th>");
	out.println("<td class=\"left\">");
	if(!hasBalance.equals("")) hasBalance="checked=\"checked\"";
	out.println("<input name=\"hasBalance\" type=\"checkbox\" "+hasBalance+
		    " value=\"y\" />");
	out.println("balance</td></tr>");		
	out.println("<tr><th>Due Date From</th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"date_from\" size=\"10\" id=\"date_from\" "+
		    " maxlength=\"10\" value=\""+date_from+"\" />");
	out.println("&nbsp;&nbsp;");
	out.println("<b> To </b>");
	out.println("<input name=\"date_to\" size=\"10\" id=\"date_to\" "+
		    " maxlength=\"10\" value=\""+date_to+"\" />");
	out.println("(mm/dd/yyyy)");		
	out.println("</td></tr>");
	out.println("<tr><th>Value From</th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"value_from\" size=\"10\" id=\"value_from\" "+
		    " maxlength=\"10\" value=\""+value_from+"\" />");
	out.println("&nbsp;&nbsp;");
	out.println("<b> To </b>");
	out.println("<input name=\"value_to\" size=\"10\" id=\"value_to\" "+
		    " maxlength=\"10\" value=\""+value_to+"\" />");
	out.println("</td></tr>");	
	out.println("<tr><th>Records Per Page </th>");
	out.println("<td class=\"left\">");		
	out.println("<input name=\"pageSize\" size=\"3\" maxlength=\"3\" value=\""+pageSize+"\" /></th></tr>");
	out.println("<tr><th>Sort By </th>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"sortBy\">");
	String selected="";
	selected = sortBy.equals("dueDate")?"selected=\"selected\"":"";
	out.println("<option value=\"dueDate\" "+selected+">Due Date</option>");
	selected = sortBy.equals("dueDate")?"":"selected=\"selected\"";
	out.println("<option value=\"value\" "+selected+">Payment Amount</option>");
	out.println("<select></th>");	
	out.println("</tr></table></td></tr>");
	out.println("<tr><td class=\"center\"><input type=\"submit\" "+
		    " name=\"action\" value=\"Submit\" />");
	out.println("</td></tr>");		
	out.println("</table></td></tr>");							
	out.println("</fieldset>");			
	out.println("</form>");

	if(!action.equals("")){
	    int cnt = payments.size();
	    out.println("<table width=\"90%\"><tr><td align=\"center\">");
	    out.println("<table width=\"100%\" border=\"1\">");
	    out.println("<caption>Found "+cnt+" payments</caption>");
	    if(cnt == 0){
		out.println("<tr><td class=\"center\">No match found</td></tr>"); 
	    }
	    else{
		out.println("<tr>"+
			    "<th>Payment</th>"+
			    "<th>Sponsor</th>"+
			    "<th>Sponsorship</th>"+
			    "<th>Due Date</th>"+
			    "<th>Value </th>"+
			    "<th>Balance </th>"+
			    "</tr>");
				
		for(Payment pay:payments){
		    out.println("<tr>");
		    String all = "&nbsp;";
		    Sponsorship sponship = pay.getSponship();					
		    Sponsor sponsor = null;
		    if(sponship != null){
			sponsor = sponship.getSponsor();
		    }
		    out.println("<td><a href=\""+url+
				"PaymentServ?action=zoom&amp;"+
				"id="+pay.getId()+"\">"+
				pay.getId()+"</a></td>");
		    if(sponsor == null){
			out.println("<td>&nbps;</td>");
		    }
		    else{
			out.println("<td><a href=\""+url+
				    "SponsorServ?action=zoom&amp;"+
				    "id="+sponsor.getId()+"\">"+
				    sponsor.getOrgname()+"</a></td>");
		    }
		    if(sponship == null){
			out.println("<td>&nbps;</td>");
		    }
		    else{
			out.println("<td><a href=\""+url+
				    "SponsorshipServ?action=zoom&amp;"+
				    "id="+sponship.getId()+"\">"+
				    sponship.getDetails()+"</a></td>");
		    }	
		    out.println("<td>"+pay.getDueDate()+
				"</td>");
		    out.println("<td>$"+pay.getValue()+
				"</td>");
		    out.println("<td>$"+pay.getBalance()+
				"</td>");					

		    out.println("</tr>");
		}
	    }
	    out.println("</table>");
	    out.println("</td></tr></table>");
	}
	PageList pages = payments.buildPages(url+"PaymentSearchServ?action=Submit");
	String all = pages.getPagesStr();
	if(!all.equals("")){
	    out.println("<center>"+all+"</center>");
	}
	out.println(Inserts.footer(url));
	out.println("<script>");
	out.println("  $( \"#date_from\" ).datepicker(); ");
	out.println("  $( \"#date_to\" ).datepicker(); ");
	out.println("                                  ");
	out.println(" $(\"#spon_name\").autocomplete({ ");
	out.println("		source: '"+url+"SponService?format=json&type=orgname', ");
	out.println("		minLength: 2, ");
	out.println("		select: function( event, ui ) { ");
	out.println("			if(ui.item){ ");
	out.println("				$(\"#spon_id\").val(ui.item.id); ");
	out.println("			} ");
	out.println("		}  ");
	out.println("	}); ");
	out.println("</script>");			
	out.print("<br /><br />");
	out.print("</div>");
	out.print("</body></html>");
	out.flush();
	out.close();
    }

}






















































