package city.sponsor.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import city.sponsor.model.*;
import jakarta.servlet.annotation.WebServlet;
import city.sponsor.list.*;
import city.sponsor.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 */
@WebServlet(urlPatterns = {"/ActionServ","/Action"})
public class ActionServ extends TopServlet{

    static final long serialVersionUID = 12L;	
    static Logger logger = LogManager.getLogger(ActionServ.class);
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
    
	String id="", spon_id="", oppt_id="", sponship_id="", cont_id="",
	    ben_id="";
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
	Action act = new Action(debug);
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")) {
		act.setId(value);
		id = value;
	    }
	    else if (name.equals("spon_id")) {
		act.setSpon_id(value);
	    }
	    else if (name.equals("oppt_id")) {
		act.setOppt_id(value);
	    }
	    else if (name.equals("sponship_id")) {
		act.setSponship_id(value);
	    }
	    else if (name.equals("cont_id")) {
		act.setCont_id(value);
	    }
	    else if (name.equals("ben_id")) {
		act.setBen_id(value);
	    }	
	    else if (name.equals("notes")) {
		act.setNotes(value);
	    }
	    else if (name.equals("date")) {
		act.setDate(value);
	    }
	    else if (name.equals("actionBy")) {
		act.setBy(value);
	    }
	    else if (name.equals("followup")) {
		act.setFollowup(value);
	    }
	    else if (name.equals("status")) {
		act.setStatus(value);
	    }
	    else if (name.equals("m_alert")) {
		act.setAlert(value);
	    }
	    else if (name.equals("alert_date")) {
		act.setAlert_date(value);
	    }
	    else if (name.equals("another_userid")) {
		act.setAnotherUserid(value);
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
	// for debug
	// activeMail = true;
	// 
	if(action.equals("Save") && user.canEdit()){
	    //
	    act.setBy(user.getUserid());
	    String back = act.doSave();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		id = act.getId();
		message += " Saved Successfully ";
		/**
		if(activeMail && act.hasAlert()){
		    try{
			SponScheduler sched =
			    new SponScheduler(user, act, emailStr, debug);
			sched.run();
		    }catch(Exception ex){
			logger.error(ex);
		    }
		}
		*/
	    }
	}
	else if(action.equals("Update") && user.canEdit()){
	    //
	    boolean needAlert = false;
	    String back="";
	    if(act.hasAlert()){
		Action oldAct = new Action(debug, id);
		back = oldAct.doSelect();
		if(back.equals("")){
		    if(!oldAct.hasAlert()) needAlert = true;
		}
	    }
	    back = act.doUpdate();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		message += " Updated Successfully ";
		/**
		if(needAlert && activeMail){
		    try{
			SponScheduler sched =
			    new SponScheduler(user, act, emailStr, debug);
			sched.run();
		    }catch(Exception ex){
			logger.error(ex);
		    }
		}
		*/
	    }
	}
	else if(action.equals("Delete") && user.canDelete()){
	    //
	    String back = act.doDelete();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		message += " Deleted Successfully ";
		act = new Action(debug);
		id = "";
	    }
	}
	else if(action.equals("zoom")){	
	    //
	    String back = act.doSelect();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	}
	Sponsor sponsor      = null;
	Opportunity oppt     = null;
	Sponsorship sponship = null;
	Contact contact = null;
	SponsorshipList sponships = null;
	ContactList contacts = null;
	DonBenType benefit = null;
	sponship_id = act.getSponship_id();
	sponship = act.getSponship();
	contact = act.getContact();
	sponsor = act.getSponsor();
	benefit = act.getBenefit();
	if(sponship != null){
	    sponsor = sponship.getSponsor();
	    oppt = sponship.getOppt();
	    sponship_id = sponship.getId();
	}
	else{
	    sponsor = act.getSponsor();
	    oppt = act.getOppt();
	}
	if(sponsor != null){
	    spon_id = sponsor.getId();
	    contacts = sponsor.getContacts();
	}
	if(oppt != null){
	    oppt_id = oppt.getId();
	    sponships = oppt.getSponsorships();
	}
	if(contacts != null && contacts.size() == 1){
	    contact = contacts.get(0);
	}
	if(contact != null){
	    cont_id = contact.getId();
	}
	if(benefit != null){
	    ben_id = benefit.getId();
	}	
	boolean needSponsor = false, needOppt = false, needContact = false;
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");
	out.println("<script type=\"text/javascript\">");
	out.println("//<![CDATA[  ");
	out.println("  function validateForm(){	                     ");
	out.println("  with(document.myForm){                        ");
	out.println("    if(m_alert.checked){                 ");
	out.println("       if(alert_date.value == ''){                   ");
	out.println("       alert('Alert date is required when alert is set'); ");
	out.println("         alert_date.focus();           ");
	out.println("         return false;			        ");
	out.println("        }                              ");
	out.println("     }                                 ");	
	out.println("     var xx = notes.value;             ");
	out.println("     if(xx.trim() == ''){              ");
	out.println("       alert('Notes field is a required field '); ");
	out.println("       return false;			        ");
	out.println("     }	         		                ");
	out.println("   }	         		                ");	
	//
	// checking dates and numeric values
	// check the numbers
	//
	out.println("  return true;			                ");
	out.println(" }	         		                    ");
	out.println("  function validateDelete2(){	        ");
	out.println("   var x = confirm(\"Are you sure you want to delete this record\");");
	out.println("   if(x){                       ");
	out.println("    document.forms[0].action2.value=\"Delete\"; ");
	out.println("    document.forms[0].submit();   ");
	out.println("    return true;                  ");
	out.println("	}	             		       ");
	out.println("  }	             		       ");
	out.println("  function checkSubmit(){	       ");
	out.println("    var xx = document.myForm.oppt_id.value; ");
	//out.println("    if(xx == ''){ alert('value not set '+xx); ");
	// out.println("      return false;}              ");
	out.println("    document.forms[0].submit();   ");
	out.println("    return true;                  ");
	out.println("	}	             		       ");
	out.println(" //]]>                            ");
	out.println(" </script>				           ");
	//
	// delete startNew
	//
	out.println("<div class=\"center\">");
	if(id.equals("")){
	    out.println("<h2>New Note</h2>");
	}
	else { 
	    out.println("<h2>View/Edit Note</h2>");
	}
	out.println("</div>"); 		
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("<form name=\"myForm\" method=\"post\" id=\"myForm\" "+
		    " action=\""+url+"ActionServ?\""+
		    " onsubmit=\"return validateForm()\" >");
	if(!id.equals("")){
	    out.println("<input type=\"hidden\" name=\"id\" value=\""+id+"\" />");
	    out.println("<input type=\"hidden\" name=\"action2\" value=\"\" />");
	}

	if(!sponship_id.equals("")){
	    out.println("<input type=\"hidden\" id=\"sponship_id\" name=\"sponship_id\" value=\""+sponship_id+"\" />");
	}
	if(contacts == null && !cont_id.equals("")){
	    out.println("<input type=\"hidden\" id=\"cont_id\"  name=\"cont_id\" value=\""+cont_id+"\" />");
	}
	if(sponsor != null){
	    out.println("<input type=\"hidden\"  name=\"spon_id\" value=\""+sponsor.getId()+"\" />");
	}
	if(oppt != null){
	    out.println("<input type=\"hidden\"  name=\"opport_id\" value=\""+oppt.getId()+"\" />");
	}
	if(benefit != null){
	    out.println("<input type=\"hidden\"  name=\"ben_id\" value=\""+benefit.getId()+"\" />");
	}			
	//
	// Add/Edit record
	//
	out.println("<table width=\"90%\" border=\"1\">");
	out.println("<caption>Notes Info</caption>");
	if(sponsor != null){
	    out.println("<tr><th>Sponsor</th><td class=\"left\"><a href=\""+url+"SponsorServ?id="+sponsor.getId()+"\">");
	    out.println(sponsor);
	    out.println("</a></td></tr>");
	}
	else if(id.equals("")){
	    needSponsor = true;
	    out.println("<tr><th>Sponsor</th><td class=\"left\"><input id=\"sponsorName\" size=\"30\" />");
	    out.println("<b>ID</b><input id=\"spon_id\"  name=\"spon_id\" value=\""+spon_id+"\"  size=\"4\" />");			
	    out.println("</td></tr>");
	}
	if(oppt != null){
	    out.println("<tr><th>Opportunity</th><td class=\"left\">");
	    out.println("<a href=\""+url+"OpportServ?id="+oppt.getId()+"\">");
	    out.println(oppt);
	    out.println("</a></td></tr>");
	}
	else if(id.equals("")){
	    needOppt = true;
	    out.println("<tr><th>Opportunity</th><td class=\"left\"><input id=\"opptName\" size=\"30\" />");
	    out.println("<b>ID</b><input id=\"oppt_id\" name=\"oppt_id\" value=\""+oppt_id+"\" size=\"4\" />");			
	    out.println("</td></tr>");
	}
		
	if(sponship_id.equals("") && sponships != null){
	    sponship = sponships.get(0);
	    if(sponships.size() > 1){
		out.println("<tr><th><label for=\"sponsorshi_id\">Sponsorships</th></label><td class=\"left\">");
		out.println("<select name=\"sponship_id\" >");
		out.println("<option value=\"\"></option>");
		for(Sponsorship spsh:sponships){
		    out.println("<option value=\""+spsh.getId()+"\">"+spsh+"</option>");
		}
		out.println("</select></td></tr>");
	    }
	}
	else if(sponship != null){
	    out.println("<tr><th>Sponsorship</th><td class=\"left\">");
	    out.println("<a href=\""+url+"SponsorshipServ?id="+sponship.getId()+"\">");
	    out.println(sponship);
	    out.println("</a></td></tr>");
	}
	if(contact != null){
	    out.println("<tr><th>Contact</th><td class=\"left\">");
	    out.println("<a href=\""+url+"ContactServ?id="+contact.getId()+"\">");
	    out.println(contact.getInfo());
	    out.println("</a></td></tr>");
	}
	else if(contacts != null){
	    if(contacts.size() > 1){
		out.println("<tr><th><label for=\"cont_id\">Contacts</lable></th><td class=\"left\">");
		out.println("<select name=\"cont_id\">");
		out.println("<option value=\"\"></option>");
		for(Contact cont:contacts){
		    out.println("<option value=\""+cont.getId()+"\">"+cont+"</option>");
		}
		out.println("</select></td></tr>");
	    }
	}
	else if(id.equals("")){
	    needContact = true;
	    out.println("<tr><th><label for=\"contName\">Contact Name</label></th><td class=\"left\"><input id=\"contactName\" size=\"30\" />");
	    out.println("<label for\"cont_id\">ID</label><input id=\"cont_id\"  name=\"cont_id\" value=\""+cont_id+"\" size=\"4\" />");			
	    out.println("</td></tr>");
	}	
	if(benefit != null){
	    out.println("<tr><th>Benefit</th><td class=\"left\">");
	    out.println(benefit.getName());
	    out.println("</a></td></tr>");
	}
	out.println("<tr>");
	out.println("<tr><th>Date </th>");		
	out.println("<td class=\"left\">");
	out.println(act.getDate());
	out.println("</td></tr>");
	out.println("<tr><th>Initiated By</th>");		
	out.println("<td class=\"left\">");
	String actBy = act.getByName();
	if(actBy.equals("")){
	    actBy = user.getFullName();
	}
	out.println(actBy);
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"m_alert\">Set Email Notification</label></th>");
	out.println("<td class=\"left\">");
	String checked = act.hasAlert() ? "checked=\"checked\"":"";
	out.println("<input name=\"m_alert\" type=\"checkbox\" "+
		    " value=\"y\" "+checked+" id=\"m_alert\"/><label for=\"alert_date\"> on </label>");
	out.println("<input name=\"alert_date\" size=\"10\" maxlength=\"10\" "+
		    " id=\"alert_date\" value=\""+act.getAlert_date()+"\"/>");
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"another_id\">Notify Another User</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"another_userid\" size=\"10\" maxlength=\"10\" "+
		    " id=\"another_id\" value=\""+act.getAnotherUserid()+"\"/>(make sure this a valid city employee userid)</td></tr>");		
	out.println("<tr><th><label for=\"notes\">Notes </lable></th>");		
	out.println("<td class=\"left\">");
	out.println("<textarea name=\"notes\" id=\"notes\" row=\"5\" cols=\"50\">");
	out.println(act.getNotes());
	out.println("</textarea></td></tr>");
	out.println("<tr><th><label for=\"folllowup\">Followup Date</lable></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"followup\" size=\"10\" maxlength=\"10\" "+
		    " id=\"followup\" value=\""+act.getFollowup()+"\" />");
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"status\">Status</lable></th>");
	out.println("<td class=\"left\">");
	for(String str:Helper.actionStatusArr){
	    String status = act.getStatus();
	    checked = status.equals(str)?"checked=\"checked\"":"";
	    out.println("<input type=\"radio\" name=\"status\" "+checked+
			" value=\""+str+"\" id=\"status\"/>"+str);
	}
	out.println("</td></tr>");
	if(id.equals("")){
	    out.println("<tr><td class=\"center\"><input type=\"submit\" "+
			" name=\"action\" value=\"Save\" />");
	    out.println("</td></tr>");
	}
	else{ // Save, Update
	    out.println("<tr>");
	    if(user.canEdit()){
		out.println("<td><input "+
			    "type=\"submit\" name=\"action\" value=\"Update\" />");
		out.println("</td>");
	    }
	    out.println("<td valign=\"top\"><input type=\"button\" "+
			"onclick=\"document.location='"+url+"ActionServ?spon_id="+spon_id+"&amp;cont_id="+cont_id+"'\" "+
			"value=\"Add Another Task Notes\" />");					
	    if(user.canDelete()){
		out.println("<input type=\"button\" "+
			    " onclick=\"validateDelete2();\" "+
			    " value=\"Delete\" />");
	    }
	    out.println("</td></tr>");
	    out.println("</table>");
	    out.println("</form>");

	    ActionList actl = new ActionList(debug, spon_id, oppt_id, sponship_id);
	    actl.setStatus("Ongoing");
	    actl.find();
	    if(actl != null && actl.size() > 0){
		out.println("<table width=\"90%\" >");
		out.println("<caption>Recent Ongoing Task Notes</caption>");
		for(Action actt:actl){
		    sponship = actt.getSponship();
		    sponsor = actt.getSponsor();
		    oppt = actt.getOppt();
		    contact = actt.getContact();
		    benefit = actt.getBenefit();
		    out.println("<tr><th>Date</th><td class=\"left\"><a href=\""+
				url+"ActionServ?id="+actt.getId()+"\">"+
				actt.getDate()+"</a></td></tr>");
		    out.println("<tr><th>Initiated By</th><td class=\"left\">"+actt.getByName()+"</td></tr>");		
		    if(sponsor != null){
			out.println("<tr><th>Sponsor</th><td class=\"left\">"+sponsor+"</td></tr>");
		    }
		    if(oppt != null){
			out.println("<tr><th>Opportunity</th><td class=\"left\">"+oppt+"</td></tr>");
		    }
		    if(sponship != null){
			out.println("<tr><th>Sponsorship</th><td class=\"left\">"+sponship+"</td></tr>");
		    }
		    if(contact != null){
			out.println("<tr><th>Contact</th><td class=\"left\">"+contact.getInfo()+"</td></tr>");
		    }
		    if(benefit != null){
			out.println("<tr><th>Benefit</th><td class=\"left\">"+benefit.getName()+"</td></tr>");
		    }	
		    String alert_date = actt.hasAlert()?actt.getAlert_date():"";
		    if(!alert_date.equals("")){
			out.println("<tr><th>Has Alert on</th><td class=\"left\">"+alert_date+"</td></tr>");
		    }
		    if(!actt.getAnotherUserid().equals("")){
			out.println("<tr><th>Notify Another User</th><td class=\"left\">"+actt.getAnotherUserid()+"</td></tr>");
		    }							
		    out.println("<tr><th>Notes</th><td class=\"left\">"+actt.getNotes()+"</td></tr>");
		    String follow = actt.getFollowup();
		    if(!follow.equals("")){
			out.println("<tr><th>Followup Date</th><td class=\"left\">"+follow+"</td></tr>");
		    }
		    out.println("<tr><td colspan=\"2\" class=\"center\"><hr width=\"90%\" /></td></tr>");
		}
		out.println("</table>");
	    }
	}
	out.println(Inserts.footer(url));	
	out.println("<script>");
	out.println("//<![CDATA[  ");			
	out.println("  $( \"#followup\" ).datepicker("+Inserts.jqDateStr(url)+"); ");
	out.println("  $( \"#alert_date\" ).datepicker("+Inserts.jqDateStr(url)+"); ");
	if(needContact){
	    out.println(" $(\"#contactName\").autocomplete({ ");
	    out.println("		source: '"+url+"ContactService?format=json&type=lname', ");
	    out.println("		minLength: 2, ");
	    out.println("		select: function( event, ui ) { ");
	    out.println("			if(ui.item){ ");
	    out.println("				$(\"#cont_id\").val(ui.item.id); ");
	    out.println("			} ");
	    out.println("		}  ");
	    out.println("	}); ");
	}	
	if(needSponsor){
	    out.println(" $(\"#sponsorName\").autocomplete({ ");
	    out.println("		source: '"+url+"SponService?format=json&type=orgname', ");
	    out.println("		minLength: 2, ");
	    out.println("		select: function( event, ui ) { ");
	    out.println("			if(ui.item){ ");
	    out.println("				$(\"#spon_id\").val(ui.item.id); ");
	    out.println("               $(\"#myForm\").submit(); ");
	    out.println("			} ");
	    out.println("		}  ");
	    out.println("	}); ");
	}
	if(needOppt){
	    out.println(" $(\"#opptName\").autocomplete({ ");
	    out.println("		source: '"+url+"OpptService?format=json&type=name', ");
	    out.println("		minLength: 2, ");
	    out.println("		select: function( event, ui ) { ");
	    out.println("			if(ui.item){ ");
	    out.println("				$(\"#oppt_id\").val(ui.item.id); ");
	    out.println("               $(\"#myForm\").submit(); ");			
	    out.println("			} ");
	    out.println("		}  ");
	    out.println("	}); ");
	}
	out.println(" //]]>   ");
	out.println("</script>");			
	out.print("<br /><br />");
	out.print("</div>");
	out.print("</body></html>");
	out.flush();
	out.close();
    }

}






















































