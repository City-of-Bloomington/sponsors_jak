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
@WebServlet(urlPatterns = {"/TaskSearchServ"})
public class TaskSearchServ extends TopServlet{

    static final long serialVersionUID = 84L;	
    static Logger logger = LogManager.getLogger(TaskSearchServ.class);
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
	    oppt_id="", id="", cont_id="", actionBy="",  
	    date_from="", date_to="", dateType = "date",            
	    sortBy="date", status="";

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
	ActionList actions = new ActionList(debug);
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("sponship_id")) {
		actions.setSponship_id(value);
		sponship_id = value;
	    }
	    else if (name.equals("spon_id")) {
		actions.setSpon_id(value);
		spon_id = value;
	    }
	    else if (name.equals("oppt_id")) {
		actions.setOppt_id(value);
		oppt_id = value;
	    }
	    else if (name.equals("cont_id")) {
		actions.setCont_id(value);
		cont_id = value;
	    }
	    else if (name.equals("date_from")) {
		actions.setDate_from(value);
		date_from = value;
	    }	
	    else if (name.equals("date_to")) {
		actions.setDate_to(value);
		date_to = value;
	    }
	    else if (name.equals("whichDate")) {
		actions.setDateType(value);
		dateType = value;
	    }	
	    else if (name.equals("sortBy")) {
		actions.setSortBy(value);
		sortBy = value;
	    }
	    else if (name.equals("actionBy")) {
		actions.setActionBy(value);
		actionBy = value;
	    }	
	    else if (name.equals("status")) {
		actions.setStatus(value);
		status = value;
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
	UserList users = new UserList(debug);
	users.setManager("y");
	users.find();
	actions.setPageSize(pageSize);
	actions.setPageNumber(pageNumber);
	//
	String spon_name = "", opport_name="", details="", fullName="";
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
	if(!cont_id.equals("")){
	    Contact ct = new Contact(debug, cont_id);
	    String back = ct.doSelect();
	    if(back.equals(""))
		fullName = ct.getFullName();
	}
	if(!action.equals("")){
	    String back = actions.find();
	    if(!back.equals("")){
		message += back;
		logger.error(back);
	    }
	    else{
		int cnt = actions.getCount();
		if(actions.size() == 1){
		    Action act = actions.get(0);
		    String str = url+"ActionServ?id="+act.getId();
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
	out.println("<h2>Search Notes</h2>");
	out.println("</div>"); 		
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("<form name=\"myForm\" method=\"get\" "+
		    " action=\""+url+"TaskSearchServ?\""+
		    " onsubmit=\"return validateForm()\">");
	out.println("<input type=\"hidden\" name=\"pageNumber\" value=\""+pageNumber+"\" />");
	out.println("<table border=\"1\" width=\"90%\">");
	out.println("<caption>Search Options</caption>");
	out.println("<tr><th><label for=\"spon_name\">Sponsor</label></th>");
	out.println("<td><input name=\"spon_name\" size=\"30\" id=\"spon_name\""+
		    " maxlength=\"50\" value=\""+spon_name+"\" />");
	out.println("<label for=\"spon_id\">ID</label><input id=\"spon_id\" name=\"spon_id\" value=\""+spon_id+"\" size=\"4\" />");		
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"opport_name\">Opportunity</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"opport_name\" size=\"30\" id=\"opport_name\""+
		    " maxlength=\"50\" value=\""+opport_name+"\" />");
	out.println("<label for=\"oppt_id\">ID</label><input id=\"oppt_id\" name=\"oppt_id\" value=\""+oppt_id+"\" size=\"4\" />");		
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"details\">Sponsorship</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"details\" size=\"30\" id=\"details\""+
		    " maxlength=\"50\" value=\""+details+"\" />");
	out.println("<label for=\"sponship_id\">ID</label><input id=\"sponship_id\" name=\"sponship_id\" value=\""+sponship_id+"\" size=\"4\" />");		
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"fullName\">Contact</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"fullName\" size=\"30\" id=\"fullName\""+
		    " maxlength=\"50\" value=\""+fullName+"\" />");
	out.println("<label for=\"cont_id\">ID</label><input id=\"cont_id\" name=\"cont_id\" value=\""+cont_id+"\" size=\"4\" />");			
	out.println("</td></tr>");		
	out.println("<tr><th>Specify Date </th>");
	out.println("<td class=\"left\">");
	String checked = dateType.equals("date")?"checked=\"checked\"":"";
	out.println("<input name=\"date\" type=\"radio\" id=\"date\" "+
		    " "+checked+" value=\"date\" /><label for=\"date\">Date</label>");
	checked = dateType.equals("followup")?"checked=\"checked\"":"";
	out.println("<input name=\"whichDate\" type=\"radio\" "+
		    " "+checked+" value=\"followup\" id=\"fullowup\" /><label for=\"followup\">Followup Date</label>");
	out.println("</td></tr>");
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
	out.println("<tr><th>Status</th>");
	out.println("<td class=\"left\">");
	for(String str:Helper.actionStatusArr){
	    checked = status.equals(str)?"checked=\"checked\"":"";
	    out.println("<input type=\"radio\" name=\"status\" "+checked+
			" id=\""+str+"\" value=\""+str+"\" /><label for=\""+str+"\">"+str+"</label>");
	}
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"actionBy\">Initiated by</label></th>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"actionBy\" id=\"actionBy\">");	
	out.println("<option value=\"\">All</option>");
	if(users != null && users.size() > 0){
	    for(User usrr:users){
		String selected = "";
		if(actionBy.equals(usrr.getUserid())) selected="selected=\"selected\"";
		out.println("<option value=\""+usrr.getUserid()+"\" "+selected+">"+usrr.getFullName()+"</option>");
	    }
	}
	out.println("</select></td></tr>");	
	out.println("<tr><th><label for=\"pageSize\">Records Per Page </label></th>");
	out.println("<td class=\"left\">");		
	out.println("<input name=\"pageSize\" size=\"3\" maxlength=\"3\" value=\""+pageSize+"\" id=\"pageSize\" /></th></tr>");
	out.println("<tr><th><label for=\"sortBy\">Sort By </label></th>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"sortBy\" id=\"sortBy\">");
	String selected="";
	selected = sortBy.equals("date")?"selected=\"selected\"":"";
	out.println("<option value=\"date\" "+selected+">Date</option>");
	selected = sortBy.equals("date")?"":"selected=\"selected\"";
	out.println("<option value=\"followup\" "+selected+">Followup Date</option>");
	out.println("</select></td></tr>");	
	out.println("<tr><td class=\"center\" colspan=\"2\"><input type=\"submit\" "+
		    " name=\"action\" value=\"Submit\" />");
	out.println("</td></tr>");		
	out.println("</table>");							
	out.println("</form>");
	if(!action.equals("")){
	    Helper.writeActions(out, actions, url);
	    PageList pages = actions.buildPages(url+"TaskSearchServ?action=Submint");
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






















































