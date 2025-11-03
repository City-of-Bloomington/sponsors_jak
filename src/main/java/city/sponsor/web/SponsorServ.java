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
@WebServlet(urlPatterns = {"/SponsorServ"})
public class SponsorServ extends TopServlet{

    static final long serialVersionUID = 80L;	
    static Logger logger = LogManager.getLogger(SponsorServ.class);
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
    
	String id="", spon_id = ""; // for adding sponsor link
	boolean success=true;
	//
	String message="", action="";

	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	User user = null;
	String phone_add[] = {"","",""};
	String phone_type[]= {"","",""};
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
	Sponsor sponsor = new Sponsor(debug);
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	String [] del_addr = null, del_spons = null;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")) {
		sponsor.setId(value);
		id = value;
	    }
	    else if (name.equals("orgname")) {
		sponsor.setOrgname(value);
	    }
	    else if (name.equals("type")) {
		sponsor.setType(value);
	    }
	    else if (name.equals("address")) {
		sponsor.setAddress(value);
	    }
	    else if (name.equals("city")) {
		sponsor.setCity(value);
	    }
	    else if (name.equals("state")) {
		sponsor.setState(value);
	    }
	    else if (name.equals("zip")) {
		sponsor.setZip(value);
	    }
	    else if (name.equals("pobox")) {
		sponsor.setPobox(value);
	    }
	    else if (name.equals("bni")) {
		sponsor.setBni(value);
	    }
	    else if (name.equals("chapter")) {
		sponsor.setChapter(value);
	    }
	    else if (name.equals("referral_from")) {
		sponsor.setReferral_from(value);
	    }						
	    else if (name.equals("spon_link")) {
		sponsor.setSpon_link(value);
	    }			
	    else if (name.equals("email")) {
		sponsor.setEmail(value);
	    }
	    else if (name.equals("pref_con_time")) {
		sponsor.setPref_con_time_arr(vals); // array
	    }
	    else if (name.equals("interests")){
		sponsor.setInterests_arr(vals);
	    }
	    else if (name.equals("target_pop")) {
		sponsor.setTarget_pop_arr(vals);
	    }
	    else if (name.equals("spon_status")) {
		sponsor.setSpon_status(value);
	    }			
	    else if (name.equals("con_means")) {
		sponsor.setCon_means_arr(vals);
	    }
	    else if (name.equals("fax")) {
		sponsor.setFax(value);
	    }
	    else if (name.equals("notes")) {
		sponsor.setNotes(value);
	    }
	    else if (name.equals("facebook")) {
		sponsor.setFacebook(value);
	    }
	    else if (name.equals("instagram")) {
		sponsor.setInstagram(value);
	    }						
	    else if (name.equals("acc_manager")) {
		sponsor.setAcc_manager(value);
	    }	
	    else if (name.equals("website")) {
		sponsor.setWebsite(value);
	    }
	    else if (name.equals("del_phones")) {
		sponsor.setDel_phones(vals);
	    }
	    else if (name.equals("del_spons")) {
		del_spons = vals;
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
	    else if (name.equals("spon_id")){
		if(!value.equals(""))
		    sponsor.setSpon_id(value);  
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
	    String back = sponsor.doSave();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		message += " Saved Successfully ";
		id = sponsor.getId();
	    }
	}
	else if(action.equals("Update") && user.canEdit()){
	    //
	    String back = sponsor.doUpdate();
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
	    String back = sponsor.doDelete();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		message += " Deleted Successfully ";
		sponsor = new Sponsor(debug);
		sponsor = new Sponsor(debug);
		id = "";
	    }
	}
	else if(action.equals("zoom")){	
	    //
	    String back = sponsor.doSelect();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	}
	if(!id.equals("")){
	    // adding new phones
	    sponsor.addPhones(phone_add, phone_type);
	    if(del_spons != null){
		sponsor.deleteLinks(del_spons);
	    }
	}
	PhoneList phones = sponsor.getPhones();
	BudgetCycleTypeList cycles = new BudgetCycleTypeList(debug);
	SponOptionList conMeans = new SponOptionList(debug, "con_means");
	SponOptionList interests = new SponOptionList(debug, "interests");
	SponOptionList targetPops = new SponOptionList(debug, "target_pop");
	OrgTypeList orgTypes = new OrgTypeList(debug);
	UserList managers = new UserList(debug);
	managers.setManager("y");
	if(true){
	    String back = cycles.find();
	    back += conMeans.find();
	    back += orgTypes.find();
	    back += interests.find();
	    back += targetPops.find();
	    back += managers.find();
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
	out.println("  var x = orgname.value;                        ");
	out.println("  if(x == '') {                                 ");
	out.println("     alert('business name is required');        ");
	out.println("     orgname.focus();                           ");
	out.println("     return false;                              ");
	out.println("   }	         		        ");
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
	    out.println("<h2>New Sponsor</h2>");
	}
	else { 
	    out.println("<h2>View/Edit Sponsor "+id+"</h2>");
	}
	out.println("</div>"); 		
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("<form name=\"myForm\" method=\"post\" "+
		    " action=\""+url+"SponsorServ?\""+
		    " onsubmit=\"return validateForm()\">");
	out.println("<fieldset><legend>Sponsor Info</legend>");
	if(!id.equals("")){
	    out.println("<input type=\"hidden\" name=\"id\" value=\""+id+"\" />");
	    out.println("<input type=\"hidden\" name=\"action2\" value=\"\" />");
	}			
	out.println("<input type=\"hidden\" id=\"spon_id\" name=\"spon_id\" value=\"\" />");
	out.println("<table border=\"1\" width=\"90%\">");
	out.println("<tr><td class=\"center\">");
	//
	// Add/Edit record
	//
	out.println("<table width=\"100%\">");
	out.println("<tr><td class=\"center title\">");
	out.println("Business </td></tr>");
	out.println("<tr><td>");
	out.println("<table width=\"100%\">");
	out.println("<tr><th>Sponsorship Status</th><th>Account Manager</th>");
	out.println("</tr>");
	out.println("<tr>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"spon_status\">");
	for(String status:Helper.statusArr){
	    String selected = "";
	    if(sponsor.getSpon_status().equals(status)){
		selected = "selected=\"selected\"";
	    }
	    out.println("<option "+selected+">"+status+"</option>");			
	}
	out.println("</select></td>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"acc_manager\">");
		
	for(User muser:managers){
	    String selected = "";
	    if(sponsor.getAcc_manager().equals(muser.getUserid())){
		selected = "selected=\"selected\"";
	    }
	    out.println("<option value=\""+muser.getUserid()+"\" "+selected+">"+muser+"</option>");			
	}
	out.println("</select></td>");
	out.println("</tr>");
	out.println("<tr><th>Business Name</th><th>Industry Category</th>");
	out.println("</tr>");
	out.println("<tr>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"orgname\" "+
		    " size=\"50\" maxlength=\"50\" value=\""+
		    Helper.replaceSpecialChars(sponsor.getOrgname())+"\" />");
	out.println("</td><td class=\"left\">");
	out.println("<select name=\"type\">");
	out.println("<option value=\"\"></option>");
	for(Type type:orgTypes){
	    String selected = "";
	    if(sponsor.getType().equals(type.getId())){
		selected = "selected=\"selected\"";
	    }
	    out.println("<option value=\""+type.getId()+"\" "+selected+">"+type+"</option>");			
	}
	out.println("</select></td>");
	out.println("</tr>");
	out.println("<tr><th>Address</th><th>City</th></tr>");
	out.println("<tr><td class=\"left\">");	
	out.println("<input name=\"address\" size=\"50\" maxlength=\"70\" "+
		    " value=\""+sponsor.getAddress()+"\" />");
	out.println("</td><td class=\"left\">");
	out.println("<input name=\"city\" size=\"10\" maxlength=\"30\" "+
		    " value=\""+sponsor.getCity()+"\" />");
	out.println("</td></tr>");		
	out.println("<tr><th>State</th><th>Zip code</th></tr>");
	out.println("<tr><td class=\"left\">");	
	out.println("<input name=\"state\" size=\"2\" maxlength=\"2\" "+
		    " value=\""+sponsor.getState()+"\" />");
	out.println("</td><td class=\"left\">");
	out.println("<input name=\"zip\" size=\"10\" maxlength=\"10\" "+
		    " value=\""+sponsor.getZip()+"\" />");
	out.println("</td></tr>");
	out.println("</table></td></tr>");
	//
	// contact info
	out.println("<tr><td class=\"center title\">Business Details</td></tr>");		
	out.println("<tr><td><table width=\"100%\">");
	out.println("<tr><th>Fax</th><th>Email</th><th>Website</th></tr>");
	out.println("<tr><td class=\"left\">");
	out.println("<input name=\"fax\" size=\"12\" maxlength=\"20\" value=\""+sponsor.getFax()+"\" /></td>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"email\" size=\"30\" maxlength=\"50\" value=\""+sponsor.getEmail()+"\" /></td>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"website\" size=\"50\" maxlength=\"70\" value=\""+sponsor.getWebsite()+"\" /></td>");
	out.println("</tr>");
	out.println("<tr><th colspan=\"2\">Facebook</th><th>Instagram</th></tr>");
	out.println("<tr><td class=\"left\" colspan=\"2\">");			
	out.println("<input name=\"facebook\" size=\"50\" maxlength=\"150\" value=\""+sponsor.getFacebook()+"\" /></td>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"instagram\" size=\"70\" maxlength=\"150\" value=\""+sponsor.getInstagram()+"\" /></td>");
	out.println("</tr>");				
	out.println("<tr><td class=\"left\">");				
	out.println("<tr><th colspan=\"3\">&nbsp;</th></tr>");
	out.println("</table></td></tr>");
				
	out.println("<tr><th align=\"center\"><table width=\"50%\">");
	out.println("<caption><b>Phones</b></caption>");
	int nps = 0;
	if(phones != null){
	    out.println("<tr><th>Delete</th><th>Phone #</th><th>Type</th><th></th></tr>");			
	    for(Phone pp:phones){
		out.println("<tr>");
		out.println("<td>"+(++nps)+" - <input type=\"checkbox\" name=\"del_phones\" value=\""+pp.getId()+"\" /></td>");
		out.println("<td>"+pp.getNumber()+"</td>");
		out.println("<td>"+pp.getType()+"</td>");
		out.println("<td>&nbsp;</td>");
		out.println("</tr>");
	    }
	}
	out.println("<tr><td colspan=\"4\" align=\"center\"><b>Add new phones </b></td></tr>");
	out.println("<tr><th></th><th>Phone #</th><th>Type</th><th></th></tr>");			
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
	out.println("</table></th></tr>");
	out.println("<tr><td><table>");
	out.println("<tr><th>&nbsp;</th></tr>");		
	out.println("<tr><th valign=\"top\">Budget Cycle</th><td class=\"left\">");
	String checked="";
	int jj=1;
	for(Type tt: cycles){
	    checked="";
	    if(sponsor.getPref_con_time().indexOf(":"+tt.getId()+":") > -1){
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
	    if(sponsor.getCon_means().indexOf(str) > -1){
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
	out.println("<tr><th valign=\"top\">Community Focus</th>");
	out.println("<td class=\"left\">");
	jj = 1;
	for(String str: interests){
	    checked="";
	    if(sponsor.getInterests().indexOf(str) > -1){
		checked="checked=\"checked\"";
	    }
	    out.println("<input type=\"checkbox\" name=\"interests\" value=\""+str+"\" "+checked+" />"+str);
	    jj++;
	    if(jj > 4) {
		out.println("<br />");
		jj = 1;
	    }
	}
	out.println("</td></tr>");
	out.println("<tr><th>&nbsp;</th></tr>");
	out.println("<tr><th valign=\"top\">Target Market</th>");
	out.println("<td class=\"left\">");
	jj = 1;
	for(String str: targetPops){
	    checked="";
	    if(sponsor.getTarget_pop().indexOf(str) > -1){
		checked="checked=\"checked\"";
	    }
	    out.println("<input type=\"checkbox\" name=\"target_pop\" value=\""+str+"\" "+checked+">"+str);
	    jj++;
	    if(jj > 6) {
		out.println("<br />");
		jj = 1;
	    }
	}
	out.println("</td></tr>");
	out.println("<tr><th>&nbsp;</th></tr>");
	List<Sponsor> sponsors = sponsor.getSponsorLinks();
	if(sponsors != null){
	    out.println("<tr><th valign=\"top\">Business Links</th>");
	    out.println("<td class=\"left\">");
	    for(Sponsor spon:sponsors){
		out.println("<input type=\"checkbox\" name=\"del_spons\" value=\""+spon.getId()+"\" />* <a href=\""+url+"SponsorServ?id="+spon.getId()+"\">"+spon+"</a><br />");
	    }
	    out.println("</td></tr>");
	}
	out.println("<tr><th valign=\"top\">Add Business Link</th>");
	out.println("<td class=\"left\">");
	out.println("<input id=\"spon_link\" value=\"\" size=\"70\" maxlength=\"80\" />");
	out.println("</td></tr>");
	out.println("<tr>");
	out.println("<tr><th>Chapter</th><td class=\"left\"><input name=\"chapter\" value=\""+sponsor.getChapter()+"\" size=\"50\" maxlength=\"80\" /> ");
	checked = sponsor.getBni().equals("") ? "":"checked=\"checked\"";
	out.println("<input type=\"checkbox\" name=\"bni\" value=\"y\" "+checked+" /><b>BNI Member</b></td></tr>");
	out.println("<tr><th>Referral From</th><td class=\"left\"><input name=\"referral_from\" value=\""+sponsor.getReferral_from()+"\" size=\"70\" maxlength=\"80\" /></td></tr>");
				
	out.println("<th valign=\"top\">Notes</th>");
	out.println("<td class=\"left\"><textarea name=\"notes\" rows=\"3\" cols=\"70\">");
	out.println(sponsor.getNotes());
	out.println("</textarea>");
	out.println("</td></tr>");
	out.println("</table></td></tr>");		
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
	    out.println("<td><input type=\"button\" value=\"Add Contacts\" "+
			"onclick=\"document.location='"+url+"ContactServ?spon_id="+id+"'\" />");
	    out.println("</td>");
	    out.println("<td valign=\"top\"><input type=\"button\" "+
			"onclick=\"document.location='"+url+"ActionServ?spon_id="+id+"'\" "+
			"value=\"Add Note\" />");
	    out.println("</td>");
	    out.println("<td valign=\"top\"><input type=\"button\" "+
			"onclick=\"document.location='"+url+"SponsorshipServ?spon_id="+id+"'\" "+
			" value=\"New Sponsorship\" />");	
	    out.println("</td>");
	    out.println("<td valign=\"top\"><input type=\"button\" "+
			"onclick=\"document.location='"+url+"SponFileServ?spon_id="+id+"'\" "+
			" value=\"Upload Files\" />");	
	    out.println("</td>");					
	    out.println("<td valign=\"top\"><input type=\"button\" "+
			"onclick=\"document.location='"+url+"InvoiceServ?spon_id="+id+"'\" "+
			" value=\"New Invoice\" />");
	    out.println("</td>");

	    if(user.canDelete()){
		out.println("<td>");
		out.println("<input type=\"button\" "+
			    " onclick=\"validateDelete2();\" "+
			    " value=\"Delete\" />");
		out.println("</td>");
	    }
	    out.println("</tr></table></td></tr>");
	    out.println("</table></td></tr>");			
	    out.println("</table>");
	    out.println("</td></tr></table>");	
	    out.println("</fieldset>");
	    out.println("</form>");
	}
	//
	if(!id.equals("")){
	    ContactList contacts = new ContactList(debug, id);
	    String back = contacts.find();
	    if(!back.equals("")){
		message += back;
		logger.error(back);
	    }
	    else if(contacts.size() > 0){
		Helper.writeContacts(out, contacts, url);
	    }
	    ActionList actl = new ActionList(debug, id, null, null);
	    actl.find();
	    if(actl != null && actl.size() > 0){
		Helper.writeActionsVertical(out, actl, url);
	    }			
	    SponsorshipList donors = new SponsorshipList(debug, null, id);
	    back = donors.find();
	    if(!back.equals("")){
		message += back;
		logger.error(back);
	    }
	    if(donors.size() > 0){
		Helper.writeSponsorships(out, donors, url);
	    }
	}
	out.println(Inserts.footer(url));	
	out.println("<script>");
	out.println("//<![CDATA[  ");				
	out.println("                                  ");
	out.println(" $(\"#spon_link\").autocomplete({ ");
	out.println("		source: '"+url+"SponService?format=json&type=orgname', ");
	out.println("		minLength: 2, ");
	out.println("		select: function( event, ui ) { ");
	out.println("			if(ui.item){ ");
	out.println("				$(\"#spon_id\").val(ui.item.id); ");
	out.println("			} ");
	out.println("		}  ");
	out.println("	}); ");
	out.println(" //]]>   ");		
	out.println("</script>");				
	out.print("<br /><br />");
	out.print("</div>");
	out.print("</body></html>");
	out.flush();
	out.close();
    }
    /**
       select count(d.id),sum(a.labor),concat(d.deptName,' ',IFNULL(d.busCat,'')) as name from requests r,actions a,departments d where a.id=r.id and d.dept=r.dept and year(r.reqDate)=2018 group by name;


    */
}






















































