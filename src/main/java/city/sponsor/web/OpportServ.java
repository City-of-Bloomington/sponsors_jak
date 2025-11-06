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
@WebServlet(urlPatterns = {"/OpportServ"})
public class OpportServ extends TopServlet{

    static final long serialVersionUID = 51L;	
    static Logger logger = LogManager.getLogger(OpportServ.class);
    /**
     * @param req
     * @param res
     */
    
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	doPost(req,res);
    }
    /**
     * @link #doGetost
     */

    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	String id="", spon_id="", event_id="";
	boolean success=true;
	//
	String message="", action="";

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
	Opportunity opport = new Opportunity(debug);
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")) {
		opport.setId(value);
		id = value;
	    }
	    else if (name.equals("event_id")) {
		opport.setEvent_id(value);
		event_id=value;
	    }
	    else if (name.equals("name")) {
		opport.setName(value);
	    }
	    else if (name.equals("start_date")) {
		opport.setStart_date(value);
	    }
	    else if (name.equals("end_date")) {
		opport.setEnd_date(value);
	    }	
	    else if (name.equals("season_id")) {
		opport.setSeason_id(value);
	    }
	    else if (name.equals("year")) {
		opport.setYear(value);
	    }
	    else if (name.equals("program_area")) {
		opport.setProgram_area(value);
	    }
	    else if (name.equals("park_cont")) {
		opport.setPark_cont(value);
	    }
	    else if (name.equals("instructions")) {
		opport.setInstructions(value);
	    }
	    else if (name.equals("lead")) {
		opport.setLead(value);
	    }
	    else if (name.equals("end_date")) {
		opport.setEnd_date(value);
	    }
	    else if (name.equals("action")){ 
		action = value;  
	    }
	    else if (name.equals("action2")){
		if(!value.equals(""))
		    action = value;  
	    }	
	}
	if(!id.equals("") && action.equals("")){
	    action = "zoom";
	}
	// 
	if(action.equals("Save") && user.canEdit()){
	    //
	    String back = opport.doSave();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		message += " Saved Successfully ";
		id = opport.getId();
	    }
	}
	else if(action.equals("Update") && user.canEdit()){
	    //
	    String back = opport.doUpdate();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		message += " Updated Successfully ";
	    }
	}
	else if(action.equals("Delete") && user.canDelete()){
	    //
	    String back = opport.doDelete();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		id = "";
		opport = new Opportunity(debug);
		message += " Deleted Successfully ";
				
	    }
	}
	else if(action.equals("zoom")){	
	    //
	    String back = opport.doSelect();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	}
	EventList events = new EventList(debug);
	SponOptionList progAreas = new SponOptionList(debug, "program_area");
	SponOptionList conMeans = new SponOptionList(debug, "con_means");
	SeasonTypeList seasons = new SeasonTypeList(debug);
		
	if(true){
	    String back = progAreas.find();
	    back += conMeans.find();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    back += seasons.find();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    back += events.find();
	    if(!back.equals("")){
		message += back;
		success = false;
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
	out.println("  function validateDelete2(){	        ");
	out.println("   var x = confirm(\"Are you sure you want to delete this record\");");
	out.println("   if(x){                       ");
	out.println("    document.forms[0].action2.value=\"Delete\"; ");
	out.println("    document.forms[0].submit();   ");
	out.println("    return true;                    ");
	out.println("	}	             		     ");
	out.println("  }	             		     ");			
	out.println(" //]]>   ");
	out.println(" </script>				        ");
	//
	// delete startNew
	//
	out.println("<div class=\"center\">");
	if(id.equals("")){
	    out.println("<h2>New Opportunity</h2>");
	}
	else { 
	    out.println("<h2>View/Edit Opportunity "+id+"</h2>");
	}
	out.println("</div>"); 		
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("<form name=\"myForm\" method=\"post\" "+
		    " action=\""+url+"OpportServ?\""+
		    " onsubmit=\"return validateForm()\">");

	if(!id.equals("")){
	    out.println("<input type=\"hidden\" name=\"id\" value=\""+id+"\" />");
	    out.println("<input type=\"hidden\" name=\"event_id\" value=\""+opport.getEvent_id()+"\" />");		
	    out.println("<input type=\"hidden\" name=\"action2\" value=\"\" />");
	}
	//
	// Add/Edit record
	//
	out.println("<table width=\"90%\" border=\"1\">");
	out.println("<caption>Opportunity Info</caption>");	
	out.println("<tr><td class=\"center title\">");
	out.println("<tr><th><label for=\"event_id\">Event</label></th>");
	out.println("<td class=\"left\">");			
	out.println("<select name=\"event_id\">");
	out.println("<option value=\"\">Pick Event</option>");
	for(Type event: events){
	    String selected="";
	    if(opport.getEvent_id().equals(event.getId())){
		selected="selected=\"selected\"";
	    }
	    out.println("<option value=\""+event.getId()+"\" "+selected+">"+event+"</option>");
	}
	out.println("</select>");
	out.println("</td></tr>");
	/**
	 * we need the old opport name to replace with event name
	 */
	if(!opport.hasEvent()){
	    out.println("<tr><th>Opportunity</th>");
	    out.println("<td colspan=\"3\" class=\"left\">");
	    out.println("<input name=\"name\" "+
			" size=\"70\" maxlength=\"70\" value=\""+
			Helper.replaceSpecialChars(opport.getName())+"\" />");
	    out.println("</td></tr>");
	}
	out.println("<tr><th><label for=\"season_id\">Season</label></th>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"season_id\" id=\"season_id\">");
	out.println("<option value=\"\"></option>");
	for(Type tt: seasons){
	    String selected="";
	    if(opport.getSeason_id().equals(tt.getId())){
		selected="selected=\"selected\"";
	    }
	    out.println("<option value=\""+tt.getId()+"\" "+selected+">"+tt+"</option>");
	}
	out.println("</select></td></tr>");
	
	out.println("<tr><th><label for=\"year\">Year</label></th><td class=\"left\">");
	out.println("<select name=\"year\" id=\"year\">");
	out.println("<option value=\"\"></option>");
	for(int yy: Helper.getYearList()){
	    String selected="";
	    if(opport.getYear().equals(""+yy)){
		selected="selected=\"selected\"";
	    }
	    out.println("<option value=\""+yy+"\" "+selected+">"+yy+"</option>");
	}
	out.println("</select>");
	out.println("</td></tr>");
	out.println("<tr>");
	out.println("<th><label for=\"start_date\">Start Date</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"start_date\" id=\"start_date\" size=\"10\" maxlength=\"10\" "+
		    " value=\""+opport.getStart_date()+"\" />");
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"end_date\">End Date</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"end_date\" id=\"end_date\" size=\"10\" maxlength=\"10\" "+
		    " value=\""+opport.getEnd_date()+"\" />");
	out.println("</td>");
	out.println("</tr>");
	out.println("<tr>");
	out.println("<th><label for=\"area\">Program Area</label></th>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"program_area\" id=\"area\">");
	out.println("<option value=\"\"></option>");
	for(String str: progAreas){
	    String selected="";
	    if(opport.getProgram_area().equals(str)){
		selected="selected=\"selected\"";
	    }
	    out.println("<option value=\""+str+"\" "+selected+">"+str+"</option>");
	}
	out.println("</select></td>");
	out.println("</tr>");
	out.println("<tr>");
	out.println("<th><label for=\"instr\">Special Instructions</label></th>");
	out.println("<td class=\"left\">");
	out.println("<textarea name=\"instructions\" rows=\"5\" id=\"instr\" cols=\"70\">");
	out.println(opport.getInstructions());
	out.println("</textarea></td>");
	out.println("</tr>");	
	if(id.equals("")){
	    out.println("<tr><td colspan=\"2\" class=\"center\"><input type=\"submit\" "+
			" name=\"action\" value=\"Save\" />");
	    out.println("</td></tr>");
	}
	else{ // Save, Update
	    out.println("<tr>");
	    out.println("<td><input "+
			"type=\"submit\" name=\"action\" value=\"Update\" />");
	    out.println("</td>");
	    if(user.canDelete()){
		out.println("<td>");
		out.println("<input type=\"button\" "+
			    " onclick=\"validateDelete2();\" "+
			    " value=\"Delete\" />");
		out.println("</td>");
	    }
	    out.println("</tr>");
	    out.println("<tr>");
	    out.println("<td><input type=\"button\" value=\"New Sponsorship\" "+
			"onclick=\"document.location='"+url+"SponsorshipServ?oppt_id="+id+"'\" />");
	    out.println("</td>");
	    out.println("<td><input type=\"button\" value=\"Add Notes\" "+
			"onclick=\"document.location='"+url+"ActionServ?oppt_id="+id+"'\" />");
	    out.println("</td>");	

	    out.println("</tr>");
	}
	out.println("</table>");
	out.println("</form>");
	if(!id.equals("")){
	    SponsorshipList donors = new SponsorshipList(debug, id, null);
	    String back = donors.find();
	    if(donors.size() > 0){
		Helper.writeSponsorships(out, donors, url);				
	    }
	    ActionList actl = new ActionList(debug, null, id, null);
	    actl.find();
	    if(actl != null && actl.size() > 0){
		Helper.writeActionsVertical(out, actl, url);
	    }			
	}
	out.println(Inserts.footer(url));
	out.println("<script>");
	out.println("//<![CDATA[  ");					
	out.println("  $( \"#start_date\" ).datepicker("+Inserts.jqDateStr(url)+"); ");
	out.println("  $( \"#end_date\" ).datepicker("+Inserts.jqDateStr(url)+"); ");
	out.println("                                  ");
	out.println(" //]]>   ");		
	out.println("</script>");			
	//
	// include the partners here
	//
	out.print("<br /><br />");
	out.print("</div>");
	out.print("</body></html>");
	out.flush();
	out.close();
    }

}






















































