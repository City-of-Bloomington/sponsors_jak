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
@WebServlet(urlPatterns = {"/SponshipSearchServ"})
public class SponshipSearchServ extends TopServlet{

    static final long serialVersionUID = 77L;	
    static Logger logger = LogManager.getLogger(SponshipSearchServ.class);
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
    
	String id="";
	boolean success=true;
	//
	String message="", action="";
	String            
	    spon_id="", don_type="",        
	    oppt_id="", sponship_id="",         
	    details="",            
	    date_from="", date_to="",             
	    terms_from ="", terms_to="",          
	    value_from="", value_to="",           
	    spon_level="", pay_type="";           
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
	SponshipList sponships = new SponshipList(debug);
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("details")) {
		// sponships.setDetails(value);
		details = value;
	    }
	    else if (name.equals("pay_type")) {
		sponships.setPay_type(value);
		pay_type = value;
	    }
	    else if (name.equals("don_type")) {
		sponships.setDon_type(value);
		don_type = value;
	    }
	    else if (name.equals("date_from")) {
		sponships.setDate_from(value);
		date_from = value;
	    }	
	    else if (name.equals("date_to")) {
		sponships.setDate_to(value);
		date_to = value;
	    }
	    else if (name.equals("value_from")) {
		sponships.setValue_from(value);
		value_from = value;
	    }	
	    else if (name.equals("value_to")) {
		sponships.setValue_to(value);
		value_to = value;
	    }
	    else if (name.equals("terms_from")) {
		sponships.setTerms_from(value);
		terms_from = value;
	    }	
	    else if (name.equals("terms_to")) {
		sponships.setTerms_to(value);
		terms_to = value;
	    }				
	    else if (name.equals("spon_level")) {
		sponships.setSpon_level(value);
		spon_level = value;
	    }
	    else if (name.equals("pay_type")) {
		sponships.setPay_type(value);
		pay_type = value;
	    }
	    else if (name.equals("spon_id")) {
		sponships.setSpon_id(value);
		spon_id = value;
	    }
	    else if (name.equals("sponship_id")) {
		sponships.setId(value);
		sponship_id = value;
	    }
	    else if (name.equals("oppt_id")) {
		sponships.setOppt_id(value);
		oppt_id = value;
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
	sponships.setPageSize(pageSize);
	sponships.setPageNumber(pageNumber);
	//
	String spon_name = "", opport_name="";
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
		opport_name = op.getName();
	}
	if(!action.equals("")){
	    String back = sponships.find();
	    if(!back.equals("")){
		message += back;
		logger.error(back);
	    }
	    else{
		if(sponships.size() == 1){
		    Sponsorship sps = sponships.get(0);
		    String str = url+"SponsorshipServ?id="+sps.getId();
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
	out.println("<h2>Search Sponsorships</h2>");
	out.println("</div>"); 		
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("<form name=\"myForm\" method=\"get\" "+
		    " action=\""+url+"SponshipSearchServ?\""+
		    " onsubmit=\"return validateForm()\">");
	out.println("<input type=\"hidden\" name=\"pageNumber\" value=\""+pageNumber+"\" />");



	//
	out.println("<table width=\"90%\" border=\"1\">");
	out.println("<caption>Search</caption>");	
	out.println("<tr><th><label for=\"opport_name\">Opportunity</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"opport_name\" size=\"30\" id=\"opport_name\""+
		    " maxlength=\"50\" value=\""+opport_name+"\" />");
	out.println("<label for=\"oppt_id\">ID</label><input id=\"oppt_id\" name=\"oppt_id\" size=\"4\" value=\""+oppt_id+"\" />");		
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"spon_name\">Sponsor</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"spon_name\" size=\"30\" id=\"spon_name\""+
		    " maxlength=\"50\" value=\""+spon_name+"\" />");
	out.println("<label for=\"spon_id\">ID</label><input id=\"spon_id\" name=\"spon_id\" size=\"4\" value=\""+spon_id+"\" />");				
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"details\">Sponsorship</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"details\" size=\"30\" id=\"details\""+
		    " maxlength=\"50\" value=\""+details+"\" />");
	out.println("<b>ID</b><input id=\"sponship_id\" name=\"sponship_id\" size=\"4\" value=\""+sponship_id+"\" />");		
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"pay_type\">Payment Option</label></th>");
	out.println("<td class=\"left\">");
	for(String str:Helper.payTypeArr){
	    String checked="";
	    if(pay_type.equals(str)) checked="checked=\"checked\"";
	    out.println("<input type=\"radio\" id=\"pay_type\" name=\"pay_type\" value=\""+str+"\" "+checked+" />"+str);
	}
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"don_type\">Sponsorship Type</label></th>");	
	out.println("<td class=\"left\">");
	out.println("<select name=\"don_type\" id=\"don_type\">");
	out.println("<option value=\"\"></option>");
	for(String str: Helper.donTypeArr){
	    String selected="";
	    if(don_type.equals(str)){
		selected="selected=\"selected\"";
	    }
	    out.println("<option value=\""+str+"\" "+selected+">"+str+"</option>");
	}
	out.println("</select>");
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"level\">Sponsorship Level</label></th>");	
	out.println("<td class=\"left\">");
	out.println("<select name=\"spon_level\" id=\"level\">");
	out.println("<option value=\"\"></option>");
	for(int i=0;i< Helper.sponLevelIdArr.length;i++){
	    String str = Helper.sponLevelIdArr[i];
	    String str2 = Helper.sponLevelArr[i];
	    String selected="";
	    if(spon_level.equals(str)){
		selected="selected=\"selected\"";
	    }
	    out.println("<option value=\""+str+"\" "+selected+">"+str2+"</option>");
	}		
	out.println("</select>");
	out.println("</td></tr>");			
	out.println("<tr><th><label for=\"date_from\">Payment Start Date From</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"date_from\" size=\"10\" id=\"date_from\" "+
		    " maxlength=\"10\" value=\""+date_from+"\" />");
	out.println("&nbsp;&nbsp;");
	out.println("<label for=\"date_to\"> To </label>");
	out.println("<input name=\"date_to\" size=\"10\" id=\"date_to\" "+
		    " maxlength=\"10\" value=\""+date_to+"\" />");
	out.println("(mm/dd/yyyy)");		
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"pageSize\">Records Per Page </label></th>");
	out.println("<td class=\"left\">");		
	out.println("<input name=\"pageSize\" size=\"3\" maxlength=\"3\" value=\""+pageSize+"\" /></th></tr>");
	out.println("<tr><th><label for=\"sortBy\">Sort By </label></th>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"sortBy\" id=\"sortBy\">");
	out.println("<option value=\"details\">Sponsorship</option>");
	out.println("<option value=\"start_date\">Start Date</option>");
	out.println("<select></th></td>");	
	out.println("<tr><td colspan=\"2\" class=\"center\"><input type=\"submit\" "+
		    " name=\"action\" value=\"Submit\" />");
	out.println("</td></tr>");		
	out.println("</table>");
	out.println("</form>");

	if(!action.equals("")){
	    int cnt = sponships.size();
	    out.println("<table width=\"90%\" border=\"1\">");
	    out.println("<caption>Found "+cnt+" Sponsorships</caption>");
	    if(cnt == 0){
		out.println("<tr><td class=\"center\">No match found</td></tr>"); 
	    }
	    else{
		out.println("<tr><th>"+
			    "<th>Sponsor</th>"+
			    "<th>Opportunity </th>"+
			    "<th>Sponsorship</th>"+
			    "<th>Sponsorship Type</th>"+
			    "<th>Payment Start Date</th>"+
			    "<th>Requested Amount</th>"+
			    "<th>Sponsorship Level</th>"+
			    "<th>Misc Notes</th>"+										
			    "</tr>");
		for(Sponsorship sp:sponships){
		    out.println("<tr>");
		    Opportunity oppt = sp.getOpportunity();
		    Sponsor sponsor = sp.getSponsor();
		    if(sponsor == null){
			out.println("<td>&nbps;</td>");
		    }
		    else{
			out.println("<td><a href=\""+url+
				    "SponsorServ?action=zoom&amp;"+
				    "id="+sponsor.getId()+"\">"+
				    sponsor.getOrgname()+"</a></td>");
		    }
		    if(oppt == null){
			out.println("<td>&nbps;</td>");
		    }
		    else{
			out.println("<td><a href=\""+url+
				    "OpportServ?action=zoom&amp;"+
				    "id="+oppt.getId()+"\">"+
				    oppt.getName()+"</a></td>");
		    }
		    String str = sp.getDetails();
		    if(str.equals("")){
			str = sp.getId();
		    }
		    out.println("<td><a href=\""+url+
				"SponsorshipServ?action=zoom&amp;"+
				"id="+sp.getId()+"\">"+
				str+"</a></td>");
		    out.println("<td>"+sp.getDon_type()+
				"</td>");						
		    out.println("<td>"+sp.getStart_date()+
				"</td>");
		    out.println("<td>"+sp.getTotalValue()+
				"</td>");
		    out.println("<td>"+sp.getSpon_level()+
				"</td>");
		    out.println("<td>"+sp.getNotes()+
				"</td>");										
		    out.println("</tr>");
		}
	    }
	    out.println("</table>");
	    PageList pages = sponships.buildPages(url+"OpportSearchServ?action=Submit");
	    String all = pages.getPagesStr();
	    if(!all.equals("")){
		out.println("<center>"+all+"</center>");
	    }
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
	out.println(" $(\"#opport_name\").autocomplete({ ");
	out.println("		source: '"+url+"OpptService?format=json&type=name', ");
	out.println("		minLength: 2, ");
	out.println("		select: function( event, ui ) { ");
	out.println("			if(ui.item){ ");
	out.println("				$(\"#oppt_id\").val(ui.item.id); ");
	out.println("			} ");
	out.println("		}  ");
	out.println("	}); ");
	out.println(" $(\"#details\").autocomplete({ ");
	out.println("		source: '"+url+"SponshipService?format=json&type=name', ");
	out.println("		minLength: 2, ");
	out.println("		select: function( event, ui ) { ");
	out.println("			if(ui.item){ ");
	out.println("				$(\"#sponship_id\").val(ui.item.id); ");
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






















































