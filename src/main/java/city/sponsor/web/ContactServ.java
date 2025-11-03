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
@WebServlet(urlPatterns = {"/ContactServ"})
public class ContactServ extends TopServlet{

    static final long serialVersionUID = 27L;	
    static Logger logger = LogManager.getLogger(ContactServ.class);
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
    
	String id="", spon_id="";
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
	String phone_add[] = {"","",""};
	String phone_type[]= {"","",""};		
	Sponsor sponsor = null;
	Contact contact = new Contact(debug);
	Enumeration<String> values = req.getParameterNames();
	String [] vals, del_phones;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")) {
		contact.setId(value);
		id = value;
	    }
	    if (name.equals("spon_id")) {
		contact.setSpon_id(value);
		spon_id = value;
	    }
	    else if (name.equals("lname")) {
		contact.setLname(value);
	    }
	    else if (name.equals("fname")) {
		contact.setFname(value);
	    }
	    else if (name.equals("occupation")) {
		contact.setOccupation(value);
	    }
	    else if (name.equals("address")) {
		contact.setAddress(value);
	    }
	    else if (name.equals("city")) {
		contact.setCity(value);
	    }
	    else if (name.equals("state")) {
		contact.setState(value);
	    }
	    else if (name.equals("zip")) {
		contact.setZip(value);
	    }
	    else if (name.equals("pobox")) {
		contact.setPobox(value);
	    }
	    else if (name.equals("email")) {
		contact.setEmail(value);
	    }
	    else if (name.equals("notes")) {
		contact.setNotes(value);
	    }
	    else if (name.equals("primary")) {
		contact.setPrimary(value);
	    }	
	    else if (name.equals("pref_con_time")) {
		contact.setPref_con_time(vals);
	    }
	    else if (name.equals("con_means")) {
		contact.setCon_means(vals);
	    }
	    else if (name.equals("del_phones")) {
		contact.setDel_phones(vals);
	    }
	    else if (name.equals("phone_1")) {
		phone_add[0]=value;
	    }
	    else if (name.equals("phone_2")) {
		phone_add[1]=value;
	    }
	    else if (name.equals("phone_3")) {
		phone_add[2]=value;
	    }
	    else if (name.equals("phone_type_1")) {
		phone_type[0]=value;
	    }
	    else if (name.equals("phone_type_2")) {
		phone_type[1]=value;
	    }
	    else if (name.equals("phone_type_3")) {
		phone_type[2]=value;
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
	    action ="zoom";
	}
	// 
	if(action.equals("Save") && user.canEdit()){
	    //
	    String back = contact.doSave();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		message += " Saved Successfully ";
		id = contact.getId();
	    }
	}
	else if(action.equals("Update") && user.canEdit()){
	    //
	    String back = contact.doUpdate();
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
	    String back = contact.doSelect();
	    if(back.equals("")){
		spon_id = contact.getSpon_id();
	    }
	    back = contact.doDelete();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		message += " Deleted Successfully ";
		id="";
		contact = new Contact(debug);
		contact.setSpon_id(spon_id);
	    }
	}
	else if(action.equals("zoom")){	
	    //
	    String back = contact.doSelect();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		spon_id = contact.getSpon_id();
	    }
	}
	ContTimeTypeList conTimes = new ContTimeTypeList(debug);		
	SponOptionList conMeans = new SponOptionList(debug, "con_means");
	if(true){
	    String back = conTimes.find();
	    back += conMeans.find();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    sponsor = contact.getSponsor();
	    //
	    // for a new contact we extract some info from
	    // the sponsor
	    if(id.equals("")){
		contact.poplateFromSponsor(sponsor);
	    }
	    else{
		contact.addPhones(phone_add, phone_type);
	    }
	}
	PhoneList phones = contact.getPhones();		
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
	    out.println("<h2>New Contact</h2>");
	}
	else { 
	    out.println("<h2>View/Edit Contact "+id+"</h2>");
	}
	out.println("</div>"); 		
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("<form name=\"myForm\" method=\"post\" "+
		    " action=\""+url+"ContactServ?\""+
		    " onsubmit=\"return validateForm()\">");
	out.println("<fieldset><legend>Contact Info</legend>");
	if(!id.equals("")){
	    out.println("<input type=\"hidden\" name=\"id\" value=\""+id+"\" />");
	    out.println("<input type=\"hidden\" name=\"action2\" value=\"\" />");
	}
	if(!spon_id.equals("")){
	    out.println("<input type=\"hidden\" name=\"spon_id\" value=\""+spon_id+"\" />");
	}	
	out.println("<table border=\"1\" width=\"90%\">");
	out.println("<tr><td class=\"center\">");
	//
	// Add/Edit record
	//
	out.println("<table width=\"100%\">");
	out.println("<tr><td class=\"center title\">");
	out.println("Contact Info</td></tr>");
	out.println("<tr><td>");
	out.println("<table width=\"100%\">");
	out.println("<tr><th>Last Name</th><th>First Name</th><th>Title</th>");
	out.println("</tr>");
	out.println("<tr>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"lname\" "+
		    " size=\"25\" maxlength=\"50\" value=\""+
		    Helper.replaceSpecialChars(contact.getLname())+"\" />");
	out.println("</td><td class=\"left\">");
	out.println("<input name=\"fname\" size=\"25\" maxlength=\"30\" "+
		    " value=\""+contact.getFname()+"\" />");
	out.println("</td><td class=\"left\">");
	out.println("<input name=\"occupation\" size=\"30\" maxlength=\"70\" "+
		    " value=\""+contact.getOccupation()+"\" /></td>");	
		
	out.println("</tr>");
	out.println("<tr><th colspan=\"2\">Address</th><th>Email</th></tr>");
	out.println("<tr><td colspan=\"2\" class=\"left\">");	
	out.println("<input name=\"address\" size=\"50\" maxlength=\"70\" "+
		    " value=\""+contact.getAddress()+"\" />");
	out.println("</td>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"email\" size=\"30\" maxlength=\"50\" value=\""+contact.getEmail()+"\" /></td>");
	out.println("</tr>");
	out.println("<tr><th>City </th><th>State</th><th>Zip code</th></tr>");
	out.println("<tr><td class=\"left\">");	
	out.println("<input name=\"city\" size=\"10\" maxlength=\"30\" "+
		    " value=\""+contact.getCity()+"\" />");
	out.println("</td><td class=\"left\">");
	out.println("<input name=\"state\" size=\"2\" maxlength=\"2\" "+
		    " value=\""+contact.getState()+"\" />");
	out.println("</td><td class=\"left\">");
	out.println("<input name=\"zip\" size=\"10\" maxlength=\"10\" "+
		    " value=\""+contact.getZip()+"\" />");
	out.println("</td></tr>");
	out.println("</table></td></tr>");
	//
	// phones table
	//
	out.println("<tr><td align=\"center\"><table width=\"60%\">");
	out.println("<caption><b>Phones</b></caption>");
	out.println("<tr><th>Delete</th><th>Phone #</th><th>Type</th><th></th></tr>");
	int nps=0;
	if(phones != null){
	    for(Phone pp:phones){
		out.println("<tr>");
		out.println("<td>"+(++nps)+" - <input type=\"checkbox\" name=\"del_phones\" value=\""+pp.getId()+"\" />*</td>");
		out.println("<td>"+pp.getNumber()+"</td>");
		out.println("<td>"+pp.getType()+"</td>");
		out.println("<td>&nbsp;</td>");
		out.println("</tr>");
	    }
	}
	out.println("<tr><td colspan=\"4\" align=\"center\"><b>Add new phones </b></td></tr>");
	for(int jj=1;jj<4;jj++){
	    out.println("<tr>");
	    out.println("<td>"+(nps+jj)+" - </td>");
	    out.println("<td><input name=\"phone_"+jj+"\" value=\"\" size=\"20\" maxlength=\"20\" /></td>");
	    out.println("<td><select name=\"phone_type_"+jj+"\" >");
	    out.println("<option></option>");
	    for(String str:Phone.phoneTypes){
		out.println("<option>"+str+"</option>");
	    }
	    out.println("</select></td>");
	    out.println("<td>&nbsp;</td>");
	    out.println("</tr>");
	}	
	out.println("</table></td></tr>");
	//		
	out.println("<tr><td><table>");
	out.println("<tr><th>&nbsp;</th></tr>");
	if(sponsor != null){
	    out.println("<tr><th>Sponsor</th><td class=\"left\">");
	    out.println("<a href=\""+url+"SponsorServ?id="+sponsor.getId()+"\">"+
			sponsor.getOrgname()+"</a></td></tr>");
	}
	String checked = contact.isPrimary() ? "checked=\"checked\"":"";
	out.println("<tr><th>Contact Level</th>");
	out.println("<td class=\"left\">");
	out.println("<input type=\"checkbox\" name=\"primary\" value=\"y\" "+checked+" />Primary</td></tr>");
	out.println("<tr><th valign=\"top\">Contact Time</th><td class=\"left\">");
	int jj=1;
	for(Type tt: conTimes){
	    checked="";
	    if(contact.getPref_con_time().indexOf(":"+tt.getId()+":") > -1){
		checked="checked=\"checked\"";
	    }
	    out.println("<input type=\"checkbox\" name=\"pref_con_time\" value=\""+tt.getId()+"\" "+checked+" />"+tt);
	    jj++;
	    if(jj > 8) {
		out.println("<br />");
		jj = 1;
	    }
	}
	out.println("</td></tr>");
	out.println("<tr><th>&nbsp;</th></tr>");
	out.println("<tr><th valign=\"top\">Preferred Contact Method</th>");
	out.println("<td class=\"left\">");
	jj = 1;
	for(String str: conMeans){
	    checked="";
	    if(contact.getCon_means().indexOf(str) > -1){
		checked="checked=\"checked\"";
	    }
	    out.println("<input type=\"checkbox\" name=\"con_means\" value=\""+str+"\" "+checked+" />"+str);
	    jj++;
	    if(jj > 8) {
		out.println("<br />");
		jj = 1;
	    }
	}
	out.println("</td></tr>");
	out.println("<tr><th>&nbsp;</th></tr>");
	out.println("<tr><th valign=\"top\">Notes</th>");
	out.println("<td class=\"left\">");
	out.println("<textarea name=\"notes\" rows=\"4\" cols=\"60\">");
	out.println(contact.getNotes());
	out.println("</textarea></td></tr>");
	out.println("</table></td></tr>");
	if(id.equals("")){
	    out.println("</table></td></tr>");							
	    if(user.canEdit()){
		out.println("<tr><td class=\"center\"><input type=\"submit\" "+
			    " name=\"action\" value=\"Save\" />");
		out.println("</td></tr>");
	    }
	    out.println("</table>");
	    out.println("</fieldset>");			
	    out.println("</form>");
	}
	else{ // Save, Update
	    out.println("<tr><td valign=\"top\" class=\"center\">");
	    out.println("<table width=\"100%\" border=\"1\"><tr><td>");
	    out.println("<table width=\"100%\">");	
	    out.println("<tr>");
	    if(user.canEdit()){
		out.println("<td><input "+
			    "type=\"submit\" name=\"action\" value=\"Update\" />");
		out.println("</td>");
	    }
	    out.println("<td><input type=\"button\" value=\"Add Another Contact\" "+
			"onclick=\"document.location='"+url+"ContactServ?spon_id="+spon_id+"'\" />");
	    out.println("</td>");
	    out.println("<td valign=\"top\"><input type=\"button\" "+
			"onclick=\"document.location='"+url+"ActionServ?spon_id="+spon_id+"&amp;cont_id="+id+"'\" "+
			"value=\"Add Task Notes\" />");			
	    if(user.canDelete()){
		out.println("<td>");
		out.println("<input type=\"button\" "+
			    " onclick=\"validateDelete2();\" "+
			    " value=\"Delete\" />");
		out.println("</td>");
	    }
	    out.println("</tr>");
			
	    out.println("</table></td></tr>");
	    out.println("</table></td></tr>");			
	    out.println("</table>");
	    out.println("</td></tr></table>");	
	    out.println("</fieldset>");
	    out.println("</form>");
	}
	if(sponsor != null){
	    ContactList contacts = sponsor.getContacts();
	    if(contacts != null && contacts.size() > 0){
		Helper.writeContacts(out, contacts, url);
	    }
	}
	ActionList actl = new ActionList(debug, spon_id, null, null, id);
	actl.find();
	if(actl != null && actl.size() > 0){
	    out.println("<table width=\"90%\" border=\"1\">");
	    out.println("<caption>Recent Notes </caption>");
	    out.println("<tr><td align=\"center\">");
	    out.println("<table width=\"100%\" >");
	    jj = 0;
	    for(Action actt:actl){
		Sponsorship sponship = actt.getSponship();
		sponsor = actt.getSponsor();
		Opportunity oppt = actt.getOppt();
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
		String alert_date = actt.hasAlert()?actt.getAlert_date():"";
		if(!alert_date.equals("")){
		    out.println("<tr><th>Has Alert on</th><td class=\"left\">"+alert_date+"</td></tr>");
		}				
		out.println("<tr><th>Notes</th><td class=\"left\">"+actt.getNotes()+"</td></tr>");
		String follow = actt.getFollowup();
		if(!follow.equals("")){
		    out.println("<tr><th>Followup Date</th><td class=\"left\">"+follow+"</td></tr>");
		}
		String status = actt.getStatus();
		out.println("<tr><th>Status</th><td class=\"left\">"+status+"</td></tr>");	
		out.println("<tr><td colspan=\"2\" class=\"center\"><hr width=\"80%\" /></td></tr>");
		jj++;
		if(jj > Helper.maxActionCount) break;
	    }
	    out.println("</table>");
	    out.println("</td></tr></table>");
	}		
	out.print("<br /><br />");
	out.print("</div>");
	out.print("</body></html>");
	out.flush();
	out.close();
    }

}






















































