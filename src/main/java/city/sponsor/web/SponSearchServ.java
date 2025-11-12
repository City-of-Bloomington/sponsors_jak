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
@WebServlet(urlPatterns = {"/SponSearchServ"})
public class SponSearchServ extends TopServlet{

    static final long serialVersionUID = 73L;	

    static Logger logger = LogManager.getLogger(SponSearchServ.class);
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
	String type="",       
	    orgname="",         
	    address="",         
	    city="",            
	    state="",           
	    zip="",             
	    pobox="",           
	    email="",           
	    phone="",           
	    pref_con_time="",   
	    interests="",       
	    target_pop="",      
	    spon_status="",            
	    con_means="",       
	    pref_type_spon="",  
	    acc_manager = "";
	String pageSize = "50", pageNumber = "1";
	//
	// contact variables
	//
	String c_name="",c_address="", prim_cont="",
	    c_city="",c_phone="", cont_id="";
	String fullName = "";
		
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
	SponsorList sponsors = new SponsorList(debug);
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("orgname")) {
		sponsors.setOrgname(value); 
		orgname = value;
	    }
	    else if (name.equals("id")) {
		sponsors.setId(value);
		id = value;
	    }
	    else if (name.equals("c_name")) {
		sponsors.setC_name(value); // we do not send this
		c_name = value;
	    }
	    else if (name.equals("cont_id")) {
		sponsors.setCont_id(value);
		cont_id = value;
	    }
	    else if (name.equals("prim_cont")) {
		sponsors.setPrim_cont(value);
		prim_cont = value;
	    }			
	    else if (name.equals("type")) {
		sponsors.setType(value);
		type = value;
	    }
	    else if (name.equals("address")) {
		sponsors.setAddress(value);
		address = value;
	    }
	    else if (name.equals("c_address")) {
		sponsors.setC_address(value);
		c_address = value;
	    }	
	    else if (name.equals("city")) {
		sponsors.setCity(value);
		city = value;
	    }
	    else if (name.equals("c_city")) {
		sponsors.setC_city(value);
		c_city = value;
	    }
	    else if (name.equals("state")) {
		sponsors.setState(value);
		state = value;
	    }
	    else if (name.equals("zip")) {
		sponsors.setZip(value);
		zip = value;
	    }
	    else if (name.equals("pobox")) {
		sponsors.setPobox(value);
		pobox = value;
	    }
	    else if (name.equals("phone")) {
		sponsors.setPhone(value);
		phone = value;
	    }
	    else if (name.equals("c_phone")) {
		sponsors.setC_phone(value);
		c_phone = value;
	    }	
	    else if (name.equals("interests")){
		sponsors.setInterests(value);
		interests = value;
	    }
	    else if (name.equals("target_pop")) {
		sponsors.setTarget_pop(value);
		target_pop = value;
	    }
	    else if (name.equals("spon_status")) {
		sponsors.setSpon_status(value);
		spon_status = value;
	    }			
	    else if (name.equals("con_means")) {
		sponsors.setCon_means(value);
		con_means = value;
	    }
	    else if (name.equals("pref_type_spon")) {
		sponsors.setPref_type_spon(value);
		pref_type_spon = value;
	    }			
	    else if (name.equals("acc_manager")) {
		sponsors.setAcc_manager(value);
		acc_manager = value;
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
	sponsors.setPageSize(pageSize);
	sponsors.setPageNumber(pageNumber);
	//
	SponOptionList conTimes = new SponOptionList(debug, "pref_con_time");
	SponOptionList conMeans = new SponOptionList(debug, "con_means");
	SponOptionList interestList = new SponOptionList(debug, "interests");
	SponOptionList targetPops = new SponOptionList(debug, "target_pop");
	SponOptionList reasons = new SponOptionList(debug, "reasons");
	SponOptionList prefTypes = new SponOptionList(debug, "pref_type_spon");		
	OrgTypeList orgTypes = new OrgTypeList(debug);
	UserList managers = new UserList(debug);
	managers.setManager("y");
	int count = 0;
	if(true){
	    String back = conTimes.find();
	    back += conMeans.find();
	    back += orgTypes.find();
	    back += interestList.find();
	    back += targetPops.find();
	    back += reasons.find();
	    back += prefTypes.find();
	    back += managers.find();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    if(!action.equals("")){
		back = sponsors.find();
		if(!back.equals("")){
		    message += back;
		    logger.error(back);
		}
		else{
		    count = sponsors.size();
		}
				
		if(count == 1){
		    Sponsor spon = sponsors.get(0);
		    String str = url+"SponsorServ?id="+spon.getId();
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
	out.println("<h2>Search Sponsors</h2>");
	out.println("</div>"); 		
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("<form name=\"myForm\" method=\"get\" "+
		    " action=\""+url+"SponSearchServ?\""+
		    " onsubmit=\"return validateForm()\">");
	out.println("<input type=\"hidden\" name=\"pageNumber\" value=\""+pageNumber+"\" />");
	out.println("<fieldset><legend>Search Options</legend>");

	out.println("<table width=\"90%\" border=\"1\">");
	out.println("<caption>");
	out.println("Business</caption>");		
	out.println("<tr><th><label for=\"spon_status\">Sponsorship Status</label></th>");
	out.println("<th><label for=\"acc_manager\">Account Manager</label></th>");
	out.println("<th><label for=\"type\">Industry Category</label></th>");
	out.println("</tr>");
	out.println("<tr>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"spon_status\" id=\"spon_status\">");
	out.println("<option value=\"\">All</option>");	
	for(String status:Helper.statusArr){
	    String selected = "";
	    if(spon_status.equals(status)) selected="selected=\"selected\"";
	    out.println("<option "+selected+" >"+status+"</option>");			
	}
	out.println("</select></td>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"acc_manager\" id=\"acc_manager\">");
	out.println("<option value=\"\">All</option>");	
	for(User muser:managers){
	    String selected = "";
	    if(muser.getUserid().equals(acc_manager)) selected = "selected=\"selected\" "; 
	    out.println("<option value=\""+muser.getUserid()+"\" "+selected+">"+muser+"</option>");			
	}
	out.println("</select></td>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"type\" id=\"type\">");
	out.println("<option value=\"\">All</option>");
	for(Type tt:orgTypes){
	    String selected = "";
	    out.println("<option value=\""+tt.getId()+"\" "+selected+">"+tt+"</option>");			
	}
	out.println("</select></td>");		
	out.println("</tr>");
	out.println("<tr><th><label for=\"orgname\">Business Name</label></th>");
	out.println("<th><label for=\"id\">ID</label></th>");
	out.println("<th><label for=\"addr\">Address</label></th>");
	out.println("</tr>");
	out.println("<tr>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"orgname\" id=\"orgname\" "+
		    " size=\"30\" maxlength=\"50\" value=\""+orgname+"\" />");
	out.println("</td><td class=\"left\">");
	out.println("<input name=\"id\" id=\"id\" size=\"10\" maxlength=\"10\" "+
		    " value=\""+id+"\" />");
	out.println("</td><td class=\"left\">");
	out.println("<input name=\"address\" size=\"20\" maxlength=\"30\" "+
		    " value=\""+address+"\" id=\"addr\" />");
	out.println("</td></tr>");	
	out.println("<tr><th><label for=\"city\">City </label></th>");
	out.println("<th><label for=\"state\">State</label></th>");
	out.println("<th><label for=\"zip\">Zip code</label></th></tr>");
	out.println("<tr><td class=\"left\">");	
	out.println("<input name=\"city\" size=\"10\" maxlength=\"30\" "+
		    "id=\"city\" value=\""+city+"\" />");
	out.println("</td><td class=\"left\">");
	out.println("<input name=\"state\" size=\"2\" maxlength=\"2\" "+
		    "id=\"state\" value=\""+state+"\" />");
	out.println("</td><td class=\"left\">");
	out.println("<input name=\"zip\" size=\"10\" maxlength=\"10\" "+
		    "id=\"zip\" value=\""+zip+"\" />");
	out.println("</td></tr>");
	//
	// contact info
	out.println("<tr><th><label for=\"phone\">Phone</label></th>");
	out.println("<th><label for=\"focus\">Community Focus</label></th>");
	out.println("<th><label for=\"target\">Target Market</label></th></tr>");	
	out.println("<tr><td class=\"left\">");
	out.println("<input name=\"phone\" size=\"12\" maxlength=\"20\" value=\""+phone+"\" id=\"phone\" /></td>");		
	out.println("<td class=\"left\">");
	out.println("<select name=\"interests\" id=\"focus\">");
	out.println("<option value=\"\">All</option>");
	for(String str: interestList){
	    String selected="";
	    if(interests.indexOf(str) > -1){
		selected="selected=\"selected\"";
	    }
	    out.println("<option value=\""+str+"\" "+selected+">"+str+"</option>");
	}			
	out.println("<select></td>");	
	out.println("<td class=\"left\">");
	out.println("<select name=\"target_pop\" id=\"target\">");
	out.println("<option value=\"\">All</option>");
	for(String str: targetPops){
	    String selected="";
	    if(target_pop.indexOf(str) > -1){
		selected="selected=\"selected\"";
	    }
	    out.println("<option value=\""+str+"\" "+selected+">"+str+"</option>");
	}			
	out.println("<select>");	
	out.println("</td></tr>");
	out.println("</table>");
	//
	// contact table
	out.println("<table width=\"90%\" border=\"1\">");	
	out.println("<caption>Contacts Related</caption>");			
	out.println("<tr><th><label for=\"c_name\">Name</label></th>");
	out.println("<th><label for=\"cont_id\">ID</label></th>");
	out.println("<th><label for=\"prim_cont\">Primary Contact</label></th>");
	out.println("</tr>");		
	out.println("<tr><td class=\"left\">");
	//
	// we do not send this, instead we send the ID
	out.println("<input id=\"c_name\" name=\"c_name\" size=\"20\" maxlength=\"50\" value=\""+c_name+"\" /></td>");		
	out.println("<td class=\"left\">");
	out.println("<input name=\"cont_id\" id=\"cont_id\" size=\"4\" maxlength=\"6\" value=\""+cont_id+"\" /></td>");
	out.println("<td class=\"left\">");
	String checked = (!prim_cont.equals(""))?"checked=\"checked\"":"";
	out.println("<input name=\"prim_cont\" type=\"checkbox\" value=\"y\" "+checked+" id=\"prim_cont\" /></td>");
	out.println("</tr>");
	out.println("<tr><th><label for=\"c_addr\">Address</label></th>");
	out.println("<th><label for=\"c_city\">City</label></th>");
	out.println("<th><label for=\"c_phone\">Phone</label></th>");
	out.println("</tr>");
	out.println("<tr><td class=\"left\">");
	out.println("<input name=\"c_address\" size=\"12\" maxlength=\"20\" value=\""+c_address+"\" id=\"c_addr\" /></td>");		
	out.println("<td class=\"left\">");
	out.println("<input name=\"c_city\" size=\"12\" maxlength=\"20\" value=\""+c_city+"\" id=\"c_city\"/></td>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"c_phone\" size=\"12\" maxlength=\"20\" value=\""+c_phone+"\" id=\"c_phone\" /></td>");
	out.println("</tr>");
	out.println("<tr><th><label for=\"pageSize\">Records Per Page</label> ");
	out.println("<input name=\"pageSize\" size=\"3\" maxlength=\"3\" value=\""+pageSize+"\" id=\"pageSize\" /></th>");
	out.println("<th><label for=\"sortBy\">Sort By </label>");
	out.println("<select name=\"sortBy\" id=\"sortBy\">");
	out.println("<option value=\"orgname\">Name</option>");
	out.println("<option value=\"address\">Address</option>");
	out.println("<select></th></tr>");	
	out.println("</table></td></tr>");
	out.println("<tr><td class=\"center\"><input type=\"submit\" "+
		    " name=\"action\" value=\"Submit\" />");
	out.println("</td></tr>");
	out.println("</table>");
	out.println("</fieldset>");			
	out.println("</form>");

	if(!action.equals("")){
	    if(count == 0){
		out.println("<p class=\"center\">No match found</p>"); 
	    }
	    else{
		out.println("<table width=\"90%\"><tr><td align=\"center\">");
		out.println("<table width=\"100%\" border=\"1\">");
		out.println("<caption>Found "+count+" Sponsor(s)</caption>");			
		out.println("<tr>"+
			    "<th>Org. Name</th>"+
			    "<th>Address </th>"+
			    "<th>Type</th>"+
			    "<th>Status</th>"+
			    "<th>Website Address</th>"+
			    "</tr>");
		for(Sponsor sp:sponsors){
		    out.println("<tr>");
		    out.println("<td><a href=\""+url+
				"SponsorServ?action=zoom&amp;"+
				"id="+sp.getId()+"\">"+
				sp.getOrgname()+"</a></td>");
		    out.println("<td>"+sp.getAddress()+
				"</td>");
		    out.println("<td>"+sp.getTypeName()+
				"</td>");
		    out.println("<td>"+sp.getSpon_status()+
				"</td>");
		    out.println("<td>"+sp.getWebsite()+
				"</td>");						
		    out.println("</tr>");
		}
		out.println("</table>");
		out.println("</td></tr></table>");
		PageList pages = sponsors.buildPages(url+"SponSearchServ?action=Submit");
		String all = pages.getPagesStr();
		if(!all.equals("")){
		    out.println("<center>"+all+"</center>");
		}						
	    }
	}
	out.println(Inserts.footer(url));		
	out.println("<script>");
	out.println("  $( \"#date_from\" ).datepicker(); ");
	out.println("  $( \"#date_to\" ).datepicker(); ");
	out.println("                                  ");
	out.println(" $(\"#orgname\").autocomplete({ ");
	out.println("		source: '"+url+"SponService?format=json&type=orgname', ");
	out.println("		minLength: 2, ");
	out.println("		select: function( event, ui ) { ");
	out.println("			if(ui.item){ ");
	out.println("				$(\"#id\").val(ui.item.id); ");
	out.println("			} ");
	out.println("		}  ");
	out.println("	}); ");
	out.println(" $(\"#fullName\").autocomplete({ ");
	out.println("		source: '"+url+"ContactService?format=json&type=lname', ");
	out.println("		minLength: 2, ");
	out.println("		select: function( event, ui ) { ");
	out.println("			if(ui.item){ ");
	out.println("				$(\"#cont_id\").val(ui.item.id); ");
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






















































