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
@WebServlet(urlPatterns = {"/InvoiceSearchServ"})
public class InvoiceSearchServ extends TopServlet{

    static final long serialVersionUID = 44L;	
    static Logger logger = LogManager.getLogger(InvoiceSearchServ.class);
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
	InvoiceList invoices = new InvoiceList(debug);
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("sponship_id")) {
		invoices.setSponship_id(value);
		sponship_id = value;
	    }
	    else if (name.equals("id")) {
		invoices.setId(value);
		id = value;
	    }			
	    else if (name.equals("spon_id")) {
		invoices.setSpon_id(value);
		spon_id = value;
	    }
	    else if (name.equals("oppt_id")) {
		invoices.setOppt_id(value);
		oppt_id = value;
	    }
	    else if (name.equals("date_from")) {
		invoices.setDate_from(value);
		date_from = value;
	    }	
	    else if (name.equals("date_to")) {
		invoices.setDate_to(value);
		date_to = value;
	    }
	    else if (name.equals("whichDate")) {
		invoices.setWhichDate(value);
		whichDate = value;
	    }	
	    else if (name.equals("value_to")) {
		invoices.setValue_to(value);
		value_to = value;
	    }
	    else if (name.equals("value_from")) {
		invoices.setValue_from(value);
		value_from = value;
	    }
	    else if (name.equals("hasBalance")) {
		if(!value.equals(""))
		    invoices.hasBalance();
		hasBalance = value;
	    }
	    else if (name.equals("sortBy")) {
		invoices.setSortBy(value);
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
	invoices.setPageSize(pageSize);
	invoices.setPageNumber(pageNumber);
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
	    String back = invoices.find();
	    if(!back.equals("")){
		message += back;
		logger.error(back);
	    }
	    else{
		int cnt = invoices.getCount();
		if(invoices.size() == 1){
		    Invoice inv = invoices.get(0);
		    String str = url+"InvoiceServ?id="+inv.getId();
		    res.sendRedirect(str);
		    return; 
		}
	    }
	}
	//
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
	out.println("<h2>Search Invoices</h2>");
	out.println("</div>"); 		
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("<form name=\"myForm\" method=\"get\" "+
		    " action=\""+url+"InvoiceSearchServ?\""+
		    " onsubmit=\"return validateForm()\">");
	out.println("<input type=\"hidden\" name=\"pageNumber\" value=\""+pageNumber+"\" />");

	out.println("<fieldset><legend>Search</legend>");
	out.println("<table border=\"1\" width=\"90%\">");
	out.println("<tr><td class=\"center\">");
	//
	// Add/Edit record
	//
	out.println("<table width=\"100%\">");
	out.println("<tr><th>ID (Invoice Number)</th>");
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
	out.println("<tr><th>Invoice has</th>");
	out.println("<td class=\"left\">");
	if(!hasBalance.equals("")) hasBalance="checked=\"checked\"";
	out.println("<input name=\"hasBalance\" type=\"checkbox\" "+hasBalance+
		    " value=\"y\" />");
	out.println("balance</td></tr>");		
	out.println("<tr><th>Specify Date </th>");
	out.println("<td class=\"left\">");
	String checked = whichDate.equals("dueDate")?"checked=\"checked\"":"";
	out.println("<input name=\"whichDate\" type=\"checkbox\" "+
		    " "+checked+" value=\"dueDate\" />Due Date");
	checked = whichDate.equals("dueDate")?"":"checked=\"checked\"";
	out.println("<input name=\"whichDate\" type=\"checkbox\" "+
		    " "+checked+" value=\"invoiceDate\" />Invoice Date");
	out.println("</td></tr>");
	out.println("<tr><th>Date From</th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"date_from\" size=\"10\" id=\"date_from\" "+
		    " maxlength=\"10\" value=\""+date_from+"\" />");
	out.println("&nbsp;&nbsp;");
	out.println("<b> To </b>");
	out.println("<input name=\"date_to\" size=\"10\" id=\"date_to\" "+
		    " maxlength=\"10\" value=\""+date_to+"\" />");
	out.println("(mm/dd/yyyy)");		
	out.println("</td></tr>");
	out.println("<tr><th>Total Amount From</th>");
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
	out.println("<option value=\"total\" "+selected+">Payment Amount</option>");
	out.println("<select></th>");	
	out.println("</tr></table></td></tr>");
	out.println("<tr><td class=\"center\"><input type=\"submit\" "+
		    " name=\"action\" value=\"Submit\" />");
	out.println("</td></tr>");		
	out.println("</table></td></tr>");							
	out.println("</fieldset>");			
	out.println("</form>");

	if(!action.equals("")){
	    int cnt = invoices.size();
	    out.println("<table width=\"90%\"><tr><td align=\"center\">");
	    out.println("<table width=\"100%\" border=\"1\">");
	    out.println("<caption>Found "+cnt+" invoices</caption>");
	    if(cnt == 0){
		out.println("<tr><td class=\"center\">No match found</td></tr>"); 
	    }
	    else{
		out.println("<tr>"+
			    "<th>Invoice</th>"+
			    "<th>Sponsor</th>"+
			    "<th>Invoice Date</th>"+							
			    "<th>Due Date</th>"+
			    "<th>Total </th>"+
			    "<th>Balance </th>"+
			    "<th>Receipt</th>"+
			    "</tr>");
				
		for(Invoice inv:invoices){
		    out.println("<tr>");
		    String all = "&nbsp;";
		    Sponsor sponsor = inv.getSponsor();
		    ReceiptList receipts = inv.getReceipts();
		    String voided=inv.isVoided()?"Voided":"";
		    out.println("<td><a href=\""+url+
				"InvoiceServ?action=zoom&amp;"+
				"id="+inv.getId()+"\">"+
				inv.getId()+" "+voided+"</a></td>");
		    if(sponsor == null){
			out.println("<td>&nbps;</td>");
		    }
		    else{
			out.println("<td><a href=\""+url+
				    "SponsorServ?action=zoom&amp;"+
				    "id="+sponsor.getId()+"\">"+
				    sponsor.getOrgname()+"</a></td>");
		    }
		    out.println("<td>"+inv.getInvoiceDate()+
				"</td>");					
		    out.println("<td>"+inv.getDueDate()+
				"</td>");
		    out.println("<td>$"+inv.getTotal()+
				"</td>");
		    out.println("<td>$"+inv.getInvoiceBalance()+
				"</td>");					

		    if(receipts != null){
			for(Receipt rpt:receipts){
			    all += "<a href=\""+url+"ReceiptServ?id="+rpt.getId()+"\">$"+rpt.getValue()+"</a>&nbsp;";
			}
		    }
		    out.println("<td>"+all+"</td>");
		    out.println("</tr>");
		}
	    }
	    out.println("</table>");
	    out.println("</td></tr></table>");
	}
	PageList pages = invoices.buildPages(url+"InvoiceSearchServ?action=Submit");
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






















































