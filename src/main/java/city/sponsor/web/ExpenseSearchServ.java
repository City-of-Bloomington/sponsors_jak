package city.sponsor.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.text.*;
import city.sponsor.model.*;
import city.sponsor.list.*;
import city.sponsor.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 *
 */
@WebServlet(urlPatterns = {"/ExpenseSearchServ"})
public class ExpenseSearchServ extends TopServlet{

    static final long serialVersionUID = 35L;	
    static Logger logger = LogManager.getLogger(ExpenseSearchServ.class);
    static NumberFormat cf = NumberFormat.getCurrencyInstance();
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
	    oppt_id="", id="", vendor_id="",
	    date_from="", date_to="", whichDate = "date",            
	    value_from="", value_to="", sortBy="date";

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
	ExpenseList expens = new ExpenseList(debug);
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("sponship_id")) {
		expens.setSponship_id(value);
		sponship_id = value;
	    }
	    else if (name.equals("id")) {
		expens.setId(value);
		id = value;
	    }			
	    else if (name.equals("spon_id")) {
		expens.setSpon_id(value);
		spon_id = value;
	    }
	    else if (name.equals("vendor_id")) {
		expens.setVendor_id(value);
		vendor_id = value;
	    }	
	    else if (name.equals("oppt_id")) {
		expens.setOppt_id(value);
		oppt_id = value;
	    }
	    else if (name.equals("date_from")) {
		if(value.equals("today")){
		    value = Helper.getToday();
		}
		expens.setDate_from(value);
		date_from = value;
	    }	
	    else if (name.equals("date_to")) {
		expens.setDate_to(value);
		date_to = value;
	    }
	    else if (name.equals("value_to")) {
		expens.setValue_to(value);
		value_to = value;
	    }
	    else if (name.equals("value_from")) {
		expens.setValue_from(value);
		value_from = value;
	    }
	    else if (name.equals("sortBy")) {
		expens.setSortBy(value);
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
	expens.setPageSize(pageSize);
	expens.setPageNumber(pageNumber);
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
	    String back = expens.find();
	    if(!back.equals("")){
		message += back;
		logger.error(back);
	    }
	    else{
		int cnt = expens.getCount();
		if(expens.size() == 1){
		    Expense expen = expens.get(0);
		    String str = url+"ExpenseServ?id="+expen.getId();
		    res.sendRedirect(str);
		    return; 
		}
	    }
	}
	VendorList vendors = new VendorList(debug);
	vendors.find();		
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

	out.println("</div>");
	
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("<form name=\"myForm\" method=\"get\" "+
		    " action=\""+url+"ExpenseSearchServ?\""+
		    " onsubmit=\"return validateForm()\">");
	out.println("<input type=\"hidden\" name=\"pageNumber\" value=\""+pageNumber+"\" />");

	out.println("<fieldset><legend>Search Expenses</legend>");
	out.println("<table width=\"90%\" border=\"1\">");
	out.println("<caption>Search Options</caption>");
	out.println("<tr><th><label for=\"id\">Expense ID</label></th>");
	out.println("<td class=\"left\">");		
	out.println("<input name=\"id\" size=\"10\" id=\"id\""+
		    " id=\"10\" value=\""+id+"\" />");
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"spon_name\">Sponsor</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"spon_name\" size=\"30\" id=\"spon_name\""+
		    " maxlength=\"50\" value=\""+spon_name+"\" />");
	out.println("<label for=\"spon_id\">ID</label><input id=\"spon_id\" name=\"spon_id\" value=\""+spon_id+"\" size=\"4\" />");
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"vendor_id\">Vendor</label></th>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"vendor_id\" id=\"vendor_id\">");
	out.println("<option value=\"\">All</option>");
	if(vendors != null && vendors.size() > 0){
	    for(Vendor vv:vendors){
		out.println("<option value=\""+vv.getId()+"\">"+vv);
	    }
	}
	out.println("</select></td></tr>");
	out.println("<tr><th><label for=\"date_from\">Date From</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"date_from\" size=\"10\" id=\"date_from\" "+
		    " maxlength=\"10\" value=\""+date_from+"\" />");
	out.println("&nbsp;&nbsp;");
	out.println("<label for=\"date_to\"> To </label>");
	out.println("<input name=\"date_to\" size=\"10\" id=\"date_to\" "+
		    " maxlength=\"10\" value=\""+date_to+"\" />");
	out.println("(mm/dd/yyyy)");		
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"value_from\">Value From</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"value_from\" size=\"10\" id=\"value_from\" "+
		    " maxlength=\"10\" value=\""+value_from+"\" />");
	out.println("&nbsp;&nbsp;");
	out.println("<label for=\"value_to\"> To </label>");
	out.println("<input name=\"value_to\" size=\"10\" id=\"value_to\" "+
		    " maxlength=\"10\" value=\""+value_to+"\" />");
	out.println("</td></tr>");	
	out.println("<tr><th><label for=\"page_size\">Records Per Page </label></th>");
	out.println("<td class=\"left\">");		
	out.println("<input name=\"pageSize\" size=\"3\" maxlength=\"3\" value=\""+pageSize+"\" id=\"page_size\" /></th></tr>");
	out.println("<tr><th><label for=\"sort_by\">Sort By </label></th>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"sortBy\" id=\"sort_by\">");
	String selected="";
	selected = sortBy.equals("date")?"selected=\"selected\"":"";
	out.println("<option value=\"date\" "+selected+">Date</option>");
	selected = sortBy.equals("date")?"":"selected=\"selected\"";
	out.println("<option value=\"value\" "+selected+">Amount</option>");
	out.println("<select></th>");	
	out.println("</tr>");
	out.println("<tr><td class=\"center\" colspan=\"2\"><input type=\"submit\" "+
		    " name=\"action\" value=\"Submit\" />");
	out.println("</td></tr>");		
	out.println("</table>");							
	out.println("</fieldset>");			
	out.println("</form>");

	if(!action.equals("")){
	    int cnt = expens.size();
	    out.println("<table width=\"100%\" border=\"1\">");
	    out.println("<caption>Found "+cnt+" expenses</caption>");
	    if(cnt == 0){
		out.println("<tr><td class=\"center\">No match found</td></tr>"); 
	    }
	    else{
		out.println("<tr><th>Expense</th>"+
			    "<th>Vendor</th>"+
			    "<th>Date</th>"+
			    "<th>Amount</th>"+
			    "<th>Details</th>"+
			    "</tr>");				
		double total = 0;
		for(Expense expen:expens){
		    out.println("<tr>");
		    out.println("<td><a href=\""+url+"ExpenseServ?id="+expen.getId()+"\">"+expen.getId()+"</a></td>");
		    out.println("<td>"+expen.getVendor()+"</td>");
		    out.println("<td>"+expen.getDate()+"</td>");
		    total += expen.getValueDbl();
		    out.println("<td class=\"money\">"+cf.format(expen.getValueDbl())+"</td>");
		    out.println("<td>"+expen.getDetails()+"</td>");
					
		    out.println("</tr>");
		}
		if(expens.size() > 1){
		    out.println("<tr><td colspan=\"3\" class=\"money\"><b>Total</b></td>");
		    out.println("<td class=\"money\">"+cf.format(total)+"</td>");
		    out.println("<td>&nbsp;</td></tr>");
		}
	    }
	    out.println("</table>");
	}
	PageList pages = expens.buildPages(url+"ExpenseSearchServ?action=Submit");
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






















































